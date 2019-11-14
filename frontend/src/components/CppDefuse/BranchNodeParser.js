const ExpressionNodeParser = require('./ExpressionNodeParser');

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
            if (typeof value === 'object' && value !== null) {
                if (cache.indexOf(value) !== -1) {
                    // Duplicate reference found, discard key
                    return "PRINTED";
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
}

class BranchNodeParser {
    constructor() {
        this.nodeStack = [];
        this.currentNode = null;

        this.labelNodeCache = {};

        this.conditionalRegexMap = {
            [BranchNodeType.DO]: /do/,
            [BranchNodeType.WHILE]: /while\s*\((.*)\).*?(;)?/,
            [BranchNodeType.ELSEIF]: /else if\s*\((.*)\)/,
            [BranchNodeType.IF]: /if\s*\((.*)\)/,
            [BranchNodeType.ELSE]: /else/,
            [BranchNodeType.SWITCH]: /switch\s*\((.*)\)/,
            [BranchNodeType.CASE]: /case\s*([^:]+)\s*:/,
            [BranchNodeType.BREAK] : /break/,
            "{": /\{/,
            "}": /\}/,
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
                this.currentNode.lines.push(line.trim());

                if (this.currentNode.single) {
                    this._popNode();
                }

                return;
            }

            const type = ret[0];
            const condition = ret[1];
            const commaTerminator = ret[2];

            switch (type) {
                case "{":
                    this.currentNode.single = false;
                    return;

                case "}":
                    this._popNode();
                    return;

                case BranchNodeType.BREAK:
                    if (this.currentNode.type === BranchNodeType.CASE) {
                        this._pushNode(new BranchNode(BranchNodeType.BREAK));
                        this._popNode();

                        // end case
                        this._popNode();
                    }
                    else if (
                        this.currentNode.type === BranchNodeType.IF ||
                        this.currentNode.type === BranchNodeType.ELSEIF ||
                        this.currentNode.type === BranchNodeType.ELSE
                    ) {
                        this._pushNode(new BranchNode(BranchNodeType.BREAK));
                        this._popNode();

                        if (this.currentNode.single) {
                            // end if
                            this._popNode();
                        }
                    }
                    else {
                        this._terminate("illegal break", line, index);
                    }

                    return;

                case BranchNodeType.GOTO:
                    if (this.currentNode.type === BranchNodeType.CASE) {
                        const gotoNode = new BranchNode(BranchNodeType.GOTO);
                        gotoNode.condition = condition;
                        this._pushNode(gotoNode);
                        this._popNode();

                        // end case
                        this._popNode();
                    }
                    else if (
                        this.currentNode.type === BranchNodeType.IF ||
                        this.currentNode.type === BranchNodeType.ELSEIF ||
                        this.currentNode.type === BranchNodeType.ELSE
                    ) {
                        const gotoNode = new BranchNode(BranchNodeType.GOTO);
                        gotoNode.condition = condition;
                        this._pushNode(gotoNode);
                        this._popNode();

                        if (this.currentNode.single) {
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
                        const siblingNode = this.currentNode.getLastChild();
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
        this._linkLabel(rootNode);
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
        if (this.currentNode) {
            this.nodeStack.push(this.currentNode);
        }

        if (this.nodeStack.length) {
            const preNode = this.nodeStack[this.nodeStack.length - 1];
            preNode.commitLines();
        }

        this.currentNode = node;

        // console.debug(`> push: ${this.currentNode.type} ${this.currentNode.condition || ""}`);
    }

    _popNode() {
        if (!this.currentNode) {
            return;
        }

        const preNode = this.nodeStack.pop();
        if (preNode) {
            preNode.addChild(this.currentNode);
        }

        this.currentNode.commitLines();
        // console.debug(`> pop: ${this.currentNode.type} ${this.currentNode.condition || ""}`);

        this.currentNode = preNode;
    }

    _getCurrentSiblingNode() {
        if (!this.currentNode) {
            return null;
        }

        if (this.currentNode.children.length) {
            return this.currentNode.children[this.currentNode.children.length - 1];
        }
        else {
            return null;
        }
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

    _linkLabel(rootNode) {
        rootNode.visit((node) => {
            if (node.type === BranchNodeType.GOTO) {
                const targetLabelNode = this.labelNodeCache[node.condition];
                if (!targetLabelNode) {
                    this._terminate("can not find label for goto: " + node.condition);
                }

                //注意：实现细节， label 是 goto 的 child， 但是并不是 goto 反过来并不是 label 的 parent, 有问题了再说。
                node.children = [targetLabelNode];

                return false;
            }
            else {
                return true;
            }
        })
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
}

module.exports = BranchNodeParser;