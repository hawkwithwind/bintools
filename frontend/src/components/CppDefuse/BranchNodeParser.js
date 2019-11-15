const ExpressionNodeParser = require('./ExpressionNodeParser').ExpressionNodeParser;

const BranchNodeType = {
    FUNCTION: 'function',
    DO: 'do',
    WHILE: 'while',
    IF: 'if',
    ELSE: 'else',
    ELSEIF: 'elseif',
    SWITCH: 'switch',
    CASE: 'case',
    BREAK: 'break',
    GOTO: 'goto',
    TEXT: 'text',
    LABEL: 'label'
};

class BranchNode {
    constructor(type) {
        this.type = type;
        this.condition = null;
        this.single = true;
        this.lines = [];

        this.parent = null;
        this.children = [];

        this.funcDeclare = null;

        this.parsedCondition = null;
        this.parsedVars = null;
        this.parsedLines = null;
        this.switchParsedCondition = null;

        this.nextNode = null;
        this.trueNode = null;
        this.falseNode = null;

        if (type === 'case') {
            this.single = false;
        }
    }

    commitLines() {
        if (!this.lines.length) {
            return;
        }

        const textNode = new BranchNode('text');
        textNode.lines = this.lines;
        this.addChild(textNode);

        this.lines = [];
    }

    addChild(childNode) {
        this.children.push(childNode);
        childNode.parent = this;
    }

    getLastChild() {
        return this.children && this.children.length && this.children[this.children.length - 1];
    }

    print() {
        var cache = [];
        const str = JSON.stringify(this, function(key, value) {
            if (typeof value === 'bigint') {
                return value.toString(16);
            }
            else if (typeof value === 'object' && value !== null) {
                if (cache.indexOf(value) !== -1) {
                    // Duplicate reference found, discard key
                    return "CIRCULAR";
                }
                // Store value in our collection
                cache.push(value);
            }

            return value;
        }, "  ");
        cache = null;

        console.log(str);
    }

    /**
     * @param handler, if handler return true, continue process children recursively.
     */
    visit(handler) {
        if (handler(this)) {
            this.children.forEach(childNode => {
                childNode.visit(handler);
            })
        }
    }

    toString() {
        const indentString = "  ";

        function _toStringLines (node) {
            let ret = [];

            const retAppendChildren= () => {

                const retAppendChildrenLines = (childrenLines) => {
                    childrenLines.forEach((line) => {
                        ret.push(indentString + line);
                    });
                };

                node.children.forEach(child => {
                    retAppendChildrenLines(_toStringLines(child));
                });
            };

            const parsedConditionToString  = (condition) => {
                const ret = condition.toString();

                if (/^\(.*\)$/.exec(ret)) {
                    return ret;
                }
                else {
                    return `(${ret})`;
                }
            };

            switch (node.type) {
                case BranchNodeType.FUNCTION:
                    ret.push(node.funcDeclare);
                    ret.push("{");

                    retAppendChildren();

                    ret.push("}");
                    break;

                case BranchNodeType.DO:
                    ret.push("do");
                    ret.push("{");

                    retAppendChildren();

                    ret.push("}");
                    ret.push(`while ${parsedConditionToString(node.parsedCondition)}`);
                    break;

                case BranchNodeType.WHILE:
                    ret.push(`while ${parsedConditionToString(node.parsedCondition)}`);

                    !node.single && ret.push("{");

                    retAppendChildren();

                    !node.single && ret.push("}");
                    break;

                case BranchNodeType.IF:
                    ret.push(`if ${parsedConditionToString(node.parsedCondition)}`);
                    !node.single && ret.push("{");

                    retAppendChildren();

                    !node.single && ret.push("}");
                    break;

                case BranchNodeType.ELSE:
                    ret.push('else');
                    !node.single && ret.push("{");

                    retAppendChildren();

                    !node.single && ret.push("}");
                    break;

                case BranchNodeType.ELSEIF:
                    ret.push(`else if ${parsedConditionToString(node.parsedCondition)}`);
                    !node.single && ret.push("{");

                    retAppendChildren();

                    !node.single && ret.push("}");
                    break;

                case BranchNodeType.SWITCH:
                    ret.push(`switch ${parsedConditionToString(node.parsedCondition)}`);

                    ret.push("{");

                    retAppendChildren();

                    ret.push("}");
                    break;

                case BranchNodeType.CASE:
                    ret.push(`case ${node.parsedCondition.toString()}:`);

                    retAppendChildren();
                    break;

                case BranchNodeType.BREAK:
                    ret.push("break;");
                    break;

                case BranchNodeType.GOTO:
                    ret.push(`goto ${node.condition};`);
                    break;

                case BranchNodeType.TEXT:
                    node.parsedLines.forEach((parsedLine, index) => {
                        let line = parsedLine.toString();
                        if (/^\(.*\)$/.exec(line)) {
                            line = line.substring(1, line.length - 1);
                        }

                        ret.push(`${line};`);
                    });
                    break;

                case BranchNodeType.LABEL:
                    ret.push(`${node.condition}:`)
                    break;

                default:
                    console.error("unknown branch node type");
            }

            return ret;
        }

        const lines = _toStringLines(this);
        return lines.join('\n');
    }

    // getNextSiblingNodes(when) {
    //     if (!this.parent) {
    //         return [];
    //     }
    //
    //     const children = this.parent.children;
    //     const index = children.indexOf(this);
    //     if (index === -1) {
    //         return [];
    //     }
    //
    //     const ret = [];
    //
    //     for (let j = index + 1; j < children.length; ++j) {
    //         const n = children[j];
    //         if (when(n)) {
    //             ret.push(n);
    //         }
    //         else {
    //             break;
    //         }
    //     }
    //
    //     return ret;
    // }
    //
    //
    // getNextSiblingNode() {
    //     let once = true;
    //
    //     const ret = this.getNextSiblingNodes((n) => {
    //         let r = once;
    //
    //         once = false;
    //
    //         return r;
    //     });
    //
    //     return ret[0];
    // }
}

class BranchNodeParser {
    constructor() {
        this.nodeStack = [];
        this.topNode = null;

        this.labelNodeCache = {};

        this.graphRootNode = null;

        this.conditionalRegexMap = {
            [BranchNodeType.DO]: /do/,
            [BranchNodeType.WHILE]: /while\s*\((.*)\).*?(;)?/,
            [BranchNodeType.ELSEIF]: /else if\s*\((.*)\)/,
            [BranchNodeType.IF]: /if\s*\((.*)\)/,
            [BranchNodeType.ELSE]: /else/,
            [BranchNodeType.SWITCH]: /switch\s*\((.*)\)/,
            [BranchNodeType.CASE]: /case\s*([^:]+)\s*:/,
            [BranchNodeType.BREAK] : /break/,
            "{": /{/,
            "}": /}/,
            [BranchNodeType.GOTO]: /goto\s*(.*?)\s*;/
        };
    }

    parse(codeText) {
        const lines = this._tokenize(codeText);

        const firstLine = lines.shift();
        const rootNode = new BranchNode(BranchNodeType.FUNCTION);
        rootNode.funcDeclare = firstLine;
        this._pushNode(rootNode);

        lines.forEach((line, index) => {
            const ret = this._testConditionNode(line);
            if (!ret) {
                this.topNode.lines.push(line.trim());

                if (this.topNode.single) {
                    this._popNode();
                }

                return;
            }

            const type = ret[0];
            const condition = ret[1];
            const commaTerminator = ret[2];

            switch (type) {
                case "{":
                    this.topNode.single = false;
                    return;

                case "}":
                    this._popNode();
                    return;

                case BranchNodeType.BREAK:
                    if (this.topNode.type === BranchNodeType.CASE) {
                        this._pushNode(new BranchNode(BranchNodeType.BREAK));
                        this._popNode();

                        // end case
                        this._popNode();
                    }
                    else if (
                        this.topNode.type === BranchNodeType.IF ||
                        this.topNode.type === BranchNodeType.ELSEIF ||
                        this.topNode.type === BranchNodeType.ELSE
                    ) {
                        this._pushNode(new BranchNode(BranchNodeType.BREAK));
                        this._popNode();

                        if (this.topNode.single) {
                            // end if
                            this._popNode();
                        }
                    }
                    else {
                        this._terminate("illegal break", line, index);
                    }

                    return;

                case BranchNodeType.GOTO:
                    if (this.topNode.type === BranchNodeType.CASE) {
                        const gotoNode = new BranchNode(BranchNodeType.GOTO);
                        gotoNode.condition = condition;
                        this._pushNode(gotoNode);
                        this._popNode();

                        // end case
                        this._popNode();
                    }
                    else if (
                        this.topNode.type === BranchNodeType.IF ||
                        this.topNode.type === BranchNodeType.ELSEIF ||
                        this.topNode.type === BranchNodeType.ELSE
                    ) {
                        const gotoNode = new BranchNode(BranchNodeType.GOTO);
                        gotoNode.condition = condition;
                        this._pushNode(gotoNode);
                        this._popNode();

                        if (this.topNode.single) {
                            // end if
                            this._popNode();
                        }
                    }
                    else {
                        this._terminate("illegal goto", line, index);
                    }
                    return;

                case BranchNodeType.WHILE:
                    // 只有 do while 时候，while 后 才会有 comma
                    if (commaTerminator) {
                        const siblingNode = this.topNode.getLastChild();
                        if (siblingNode.type !== BranchNodeType.DO) {
                            this._terminate("illegal while with comma", line, index);
                        }

                        siblingNode.condition = condition;

                        return;
                    }
            }

            const newNode = new BranchNode(type);
            newNode.condition = condition;
            this._pushNode(newNode);
        });

        this._parseLabel(rootNode);
        this._parseCondition(rootNode);
        this._parseLines(rootNode);

        return rootNode;
    }

    /**
     * 假的 tokenize， 为了节约时间只是，只是简单处理了注释
     * @param codeText
     * @private
     */
    _tokenize(codeText) {
        return codeText.split('\n').map((line) => {
            // 只处理了 // 注释，因为 ida 伪代码只有这种形式注释
            const index = line.indexOf("//");
            if (index !== -1) {
                return line.substring(0, index);
            }
            else {
                return line;
            }
        }).filter(line => line && line.length);
    }

    _pushNode(node) {
        if (this.topNode) {
            this.nodeStack.push(this.topNode);
        }

        if (this.nodeStack.length) {
            const preNode = this.nodeStack[this.nodeStack.length - 1];
            preNode.commitLines();
        }

        this.topNode = node;
    }

    _popNode() {
        if (!this.topNode) {
            return;
        }

        const preNode = this.nodeStack.pop();
        if (preNode) {
            preNode.addChild(this.topNode);
        }

        this.topNode.commitLines();
        this.topNode = preNode;
    }

    _terminate(desc, line = null, lineIndex = null) {
        if (line && lineIndex) {
            console.error(`${desc}, line #${lineIndex + 2}, ${line}`);
            process.exit(1);
        }
        else {
            console.error(`${desc}`);
            process.exit(2);
        }
    }

    _testConditionNode(line) {
        for (const name in this.conditionalRegexMap) {
            const m = this.conditionalRegexMap[name].exec(line);
            if (m) {
                return [name, m[1], m[2]];
            }
        }

        return null;
    };

    __processTextNodeLineWithRegex(rootNode, regex, handler) {
        rootNode.visit((node) => {
            if (node.type === BranchNodeType.TEXT) {
                let targetLineFound = false;

                const newChildren = [];

                let tmpLines = [];
                const commitTmpLines = () => {
                    if (!tmpLines.length) {
                        return;
                    }

                    const newNode = new BranchNode("text");
                    newNode.lines = tmpLines;
                    newChildren.push(newNode);

                    tmpLines = [];
                };

                node.lines.forEach((line, index)  => {
                    const m = regex.exec(line);
                    if (m) {
                        commitTmpLines();

                        const newNode = new BranchNode();
                        handler(newNode, m);
                        newChildren.push(newNode);

                        targetLineFound = true;
                    } else {
                        tmpLines.push(line);
                    }

                    // last one
                    if (index === node.lines.length - 1) {
                        commitTmpLines()
                    }
                });

                if (targetLineFound && newChildren.length) {
                    if (node.parent) {
                        const index = node.parent.children.indexOf(node);
                        node.parent.children.splice(index, 1, ...newChildren)
                    }
                    else {
                        console.error("unexpected null parent");
                    }
                }

                return false;
            } else {
                return true;
            }
        });
    }

    _parseLabel(rootNode) {
        const labelRegex = /(LABEL_\d+?):/;

        this.__processTextNodeLineWithRegex(rootNode, labelRegex, (newNode, matches) => {
            newNode.type = BranchNodeType.LABEL;
            newNode.condition = matches[1];

            this.labelNodeCache[newNode.condition] = newNode;
        });
    }
    _parseCondition(rootNode) {
        const conditionNodeTypes = [
            BranchNodeType.DO,
            BranchNodeType.WHILE,
            BranchNodeType.IF,
            BranchNodeType.ELSEIF,
            BranchNodeType.SWITCH,
            BranchNodeType.CASE
        ];

        rootNode.visit((node) => {
            if (conditionNodeTypes.indexOf(node.type) !== -1) {
                const parser = new ExpressionNodeParser();
                const nodes = parser.parse(node.condition);
                node.parsedCondition = nodes[nodes.length - 1];
            }

            return true;
        });
    }
    _parseLines(rootNode) {
        rootNode.visit((node) => {
            if (node.type === BranchNodeType.TEXT) {
                node.parsedLines = [];
                node.parsedVars = {};

                const parseVarDeclarations = () => {
                    node.lines.some((line) => {
                        const parser = new ExpressionNodeParser();
                        const parsedLine = parser.parseDeclareVar(line);
                        if (!parsedLine) {
                            return true;
                        }
                        node.parsedLines.push(parsedLine);

                        // add to parsed var meta
                        const val = parsedLine.val;
                        node.parsedVars[val['name']] = val['type'];

                        return false;
                    });
                };
                parseVarDeclarations();

                for (let i = node.parsedLines.length; i < node.lines.length; ++i) {
                    const line = node.lines[i];

                    const parser = new ExpressionNodeParser();
                    const ret = parser.parse(line);
                    const parsedLine = ret[ret.length - 1];
                    node.parsedLines.push(parsedLine);
                }
            }

            return true;
        });
    }

    _buildBranchGraph (node) {
        console.log(`> _linkBranchGraph: ${node.type} ${node.condition}`);
        const linkChildren = (node) => {
            const children = node.children;
            if (!children || !children.length) {
                return [null, () => {}];
            }

            let headNode = null;
            let preLinkTail = null;

            let index = 0;
            while (index < node.children.length) {
                const child = node.children[index];
                const ret = this._buildBranchGraph(child);
                if (!ret) {
                    index++;
                    continue;
                }

                const retNode = ret[0];
                const linkTail = ret[1];

                if (!headNode) {
                    headNode = retNode;
                }

                preLinkTail&& preLinkTail(retNode);
                preLinkTail = linkTail;

                // 把 if 后面的 else if、else 也一同处理掉
                if (child.type === BranchNodeType.IF) {
                    const ifLinkExitTailList = [ret[2]];

                    let j = index + 1;
                    while (j < node.children.length) {
                        const childSibling = node.children[j];
                        if (childSibling.type === BranchNodeType.ELSEIF || childSibling.type === BranchNodeType.ELSE) {
                            const ret = this._buildBranchGraph(childSibling);
                            const retNode = ret[0];
                            const linkTail = ret[1];

                            // else： ret[2] 为空
                            ret[2] && ifLinkExitTailList.push(ret[2]);

                            preLinkTail(retNode);
                            preLinkTail = linkTail;
                        }
                        else {
                            break;
                        }

                        j++;
                    }

                    const oldPreLinkTail = preLinkTail;
                    preLinkTail = (tailNode) => {
                        ifLinkExitTailList.forEach(link => {
                            link(tailNode);
                        });

                        oldPreLinkTail(tailNode);
                    };

                    index = j;
                }

                // 把 case 后面 case 也 一同处理掉。处理方式和 if else 类似，多一个 break 处理
                else if (child.type === BranchNodeType.CASE) {
                    const caseLinkExitTailList = [ret[2]];

                    let j = index + 1;
                    while (j < node.children.length) {
                        const childSibling = node.children[j];
                        if (childSibling.type === BranchNodeType.CASE) {
                            const ret = this._buildBranchGraph(childSibling);
                            const retNode = ret[0];
                            const linkTail = ret[1];

                            // 如果没有 break， ret[2] 为空
                            ret[2] && caseLinkExitTailList.push(ret[2]);

                            preLinkTail(retNode);
                            preLinkTail = linkTail;
                        }
                        else {
                            break;
                        }

                        j++;
                    }

                    const oldPreLinkTail = preLinkTail;
                    preLinkTail = (tailNode) => {
                        caseLinkExitTailList.forEach(link => {
                            link(tailNode);
                        });

                        oldPreLinkTail(tailNode);
                    };

                    index = j;
                }

                else {
                    index++;
                }
            }

            return [headNode, preLinkTail];
        };

        const ret = linkChildren(node);
        const childrenHeadNode = ret[0];
        const childrenLinkTail = ret[1];

        switch (node.type) {
            case BranchNodeType.FUNCTION:
                return ret;

            case BranchNodeType.DO:
            case BranchNodeType.WHILE:
                node.trueNode = childrenHeadNode;
                childrenLinkTail(node);

                return [node, (tailNode) => {
                    node.falseNode = tailNode;

                    node._breakNodes && node._breakNodes.forEach((breakNode) => {
                        breakNode.nextNode = tailNode;
                    });
                }];

            case BranchNodeType.IF:
            case BranchNodeType.ELSEIF:
                node.trueNode = childrenHeadNode;
                // if elseif false node 和 childNode 需要分开设置
                return [node, (falseNode) => {
                    node.falseNode = falseNode;
                }, (exitNode) => {
                    childrenLinkTail(exitNode);
                }];

            case BranchNodeType.ELSE:
                return ret;

            case BranchNodeType.SWITCH:
                return ret;

            case BranchNodeType.CASE:
                node.switchParsedCondition = node.parent.parsedCondition;
                node.trueNode = childrenHeadNode;

                if (node._break) {
                    return [node, (falseNode) => {
                        node.falseNode = falseNode;
                    }, (exitNode) => {
                        childrenLinkTail(exitNode);
                    }];
                }
                else {
                    return [node, (falseNode) => {
                        node.falseNode = falseNode;
                        childrenLinkTail(falseNode);
                    }];
                }

            case BranchNodeType.BREAK:
                // case break
                if (node.parent.type === BranchNodeType.CASE) {
                    node.parent._break = true;
                }
                // 循环的 break
                else  {
                    return [node, (_) => {
                        // break 的next 直接连接上最近循环的 falseNode

                        let parent = node.parent;
                        while (parent) {
                            if (parent.type === BranchNodeType.DO || parent.type === BranchNodeType.WHILE) {
                                if (!parent._breakNodes) {
                                    parent._breakNodes = [];
                                }

                                parent._breakNodes.push(node);
                                break;
                            }

                            parent = parent.parent;
                        }
                    }];
                }
                return;

            case BranchNodeType.GOTO:
                const targetLabelNode = this.labelNodeCache[node.condition];
                if (!targetLabelNode) {
                    this._terminate("can not find label for goto: " + node.condition);
                }

                node.nextNode = targetLabelNode;
                return [node, (_) => {
                    // do nothing
                }];

            case BranchNodeType.TEXT:
            case BranchNodeType.LABEL:
                return [node, (trailNode) => {
                    node.nextNode = trailNode;
                }];
        }

        return null;
    }

    buildBranchGraph(rootNode) {
        const ret = this._buildBranchGraph(rootNode);
        return ret && ret[0];
    }
}

exports.BranchNodeType = BranchNodeType;
exports.BranchNode = BranchNode;
exports.BranchNodeParser = BranchNodeParser;