const fs = require('fs');

const content = fs.readFileSync("./didAddDyldImage_104B8768C.cpp", {encoding: 'utf8'});

const ExprNodeType = {
    VAR: 'var',
    CONST: 'const',
    BINARY_OPERATOR: 'binary_operator',
    UNARY_OPERATOR: 'unary_operator',
    FUNCTION: 'function',
    BRACE: '()',
    GROUP: 'group'
};

class ExprNode {
    constructor (type, val = null) {
        this.type = type;
        this.val = val;
        this.children = null;
        this.decorator = null;
    }
}

class ExprNodeParser {
    parse (condition) {
        const binaryOperators = [ '+', '-', '>', '<', '>=', '<=', '==', '!=','*', '/', '%', '<<', '>>', '&&', '||', '&', '|', '^'];
        const unaryOperators = ['!', '~']; // ignore ++ --
        const operatorsPriority = {
            "(": 0,
            '++': 1,
            '--': 1,
            '!': 1,
            '~': 1,
            '*': 2,
            '/': 2,
            '%': 2,
            '+': 3,
            '-': 3,
            '<<': 4,
            '>>': 4,
            '>': 5,
            '<': 5,
            '>=': 5,
            '<=': 5,
            '==': 6,
            '!=': 6,
            '^': 7,
            '&': 8,
            '|': 9,
            '&&': 10,
            '||': 11,
            ',': 100,
        };

        const splitReg = () => {
            const operators = ['\\(', '\\)', '\\{', '\\}', '\\+', '\\+\\+', '\\-', '\\-\\-', '\\*', '/', '%', '=', '==', '!=', '>', '>=', '<', '<=', '\\+=', '\\-=', '\\*=', '/=', '%=', '<<=', '>>=', '&=', '^=', '\\|=', '\\!', '~', '\\^', '&', '&&', '\\|', '\\|\\|', '>>', '<<', '\\?', '\\:\\:', '\\:', '\\,', ';'];

            operators.sort((a, b) => {
                return a.length === b.length ? 0 : ((a.length > b.length) ? -1 : 1);
            });

            let regStr = operators.map((op => {
                return `(${op})`;
            })).join("|");

            return new RegExp(`\\s|${regStr}`, 'g');
        };
        const regex = splitReg();

        const tokens = condition.split(regex).filter(c => c && c.length);

        const isBinaryOperator = (token) => {
            return binaryOperators.indexOf(token) !== -1;
        };
        const isUnaryOperator = (token) => {
            return unaryOperators.indexOf(token) !== -1;
        };
        const isOperator = (token) => {
            return isBinaryOperator(token) || isUnaryOperator(token) || token === ',' || token === '('  || token === ')';
        };

        const isNodeOperatorPrior = (nodeA, nodeB) => {
            return operatorsPriority[nodeA.val] <= operatorsPriority[nodeB.val];
        };

        const parseTokens = (tokens, tokenIndex, stopToken) => {
            const operatorStack = [];
            let nodeStack = [];

            const commitOperatorStack = (until, finalRound = false) => {
                while (operatorStack.length) {
                    const topOperator = operatorStack[operatorStack.length - 1];
                    if (until(topOperator)) {
                        operatorStack.pop();

                        if (isBinaryOperator(topOperator.val)) {
                            const tmp = nodeStack.splice(nodeStack.length - 3, 3);
                            topOperator.children = [tmp[0], tmp[2]];
                            nodeStack.push(topOperator);
                        }
                        else if (isUnaryOperator(topOperator.val)) {
                            const tmp = nodeStack.splice(nodeStack.length - 2, 2);
                            topOperator.children = tmp[1];
                            nodeStack.push(topOperator)
                        }
                        else {
                            break;
                            // skip
                        }
                    }
                    else {
                        break;
                    }
                }

                // 已经没有没有 operator，但是 node 中仍然有多个，那么将这些 node 合并起来。比如 (unsigned int)
                if (finalRound && !operatorStack.length && nodeStack.length > 1) {
                    let allVar = true;
                    nodeStack.forEach(node => {
                        if (node.type !== ExprNodeType.VAR) {
                            allVar = false;
                        }
                    });

                    if (allVar) {
                        const newNode = new ExprNode(ExprNodeType.VAR);
                        newNode.val = nodeStack.map((node) => node.val).join(" ");
                        nodeStack = [newNode];
                    }
                    // else {
                    //     const groupNode = new ExprNode(ExprNodeType.GROUP);
                    //     groupNode.children = nodeStack;
                    //     nodeStack = [groupNode];
                    // }
                }
            };

            while (tokenIndex < tokens.length && !(stopToken && tokens[tokenIndex] === stopToken)) {
                const t = tokens[tokenIndex];

                if (t === '(') {
                    const ret = parseTokens(tokens, tokenIndex + 1, ')');

                    const preNode = nodeStack[nodeStack.length - 1];

                    // func ()
                    if (preNode && preNode.type === ExprNodeType.VAR) {
                        preNode.type = ExprNodeType.FUNCTION;
                        preNode.children = ret[0];
                    }
                    else {
                        const children = ret[0];
                        let signalOperationExpr = false;

                        // 删除没必要的 brance
                        if(children.length === 1) {
                            const child = children[0];
                            if (child.type !== ExprNodeType.VAR) {
                                nodeStack.push(child);
                                signalOperationExpr = true;
                            }
                        }

                        if (!signalOperationExpr) {
                            const braceNode = new ExprNode(ExprNodeType.BRACE);
                            braceNode.children = ret[0];
                            nodeStack.push(braceNode);
                        }
                    }

                    tokenIndex = ret[1];
                }

                else if (isBinaryOperator(t)) {
                    const binaryNode = new ExprNode(ExprNodeType.BINARY_OPERATOR, t);

                    commitOperatorStack((topOperator) => {
                        // 先处理优先级大的
                        return isNodeOperatorPrior(topOperator, binaryNode);
                    });

                    operatorStack.push(binaryNode);
                    nodeStack.push(binaryNode);
                }
                else if (isUnaryOperator(t)) {
                    const unaryNode = new ExprNode(ExprNodeType.UNARY_OPERATOR, t)
                    operatorStack.push(unaryNode);
                    nodeStack.push(unaryNode);
                }

                else if (t === ',') {
                    commitOperatorStack((topOperator) => {
                        return !!topOperator;
                    });
                }
                else {
                    let node = null;

                    const decimalNumberReg = /^[-+]?[0-9]+(\.[0-9]+)?$/;
                    const decimalMatches = decimalNumberReg.exec(t);
                    if (decimalMatches) {
                        node = new ExprNode(ExprNodeType.CONST, parseInt(t));
                    }
                    else {
                        const hexNumberReg = /^0([xX])[0-9a-fA-F]+$/;
                        const hexMatches = hexNumberReg.exec(t);
                        if (hexMatches) {
                            node = new ExprNode(ExprNodeType.CONST, parseInt(t, 16));
                        }
                        else {
                            const stringReg = /^['"](\w+)['"]$/;
                            const stringMatches = stringReg.exec(t);
                            if (stringMatches) {
                                node = new ExprNode(ExprNodeType.CONST, stringMatches[1]);
                            }
                            else {
                                node = new ExprNode(ExprNodeType.VAR, t);
                            }
                        }
                    }

                    if (!node) {
                        console.error("nil node");
                    }


                    const preNode = nodeStack[nodeStack.length - 1];
                    if (preNode && preNode.type === ExprNodeType.BRACE) {
                        nodeStack.pop();
                        node.decorator = preNode;
                    }

                    nodeStack.push(node);
                }

                tokenIndex++;
            }

            commitOperatorStack((topOperator) => {
                return !!topOperator;
            }, true);

            return [nodeStack, tokenIndex];
        };

        const ret = parseTokens(tokens, 0, null);
        const rootNode = ret[0];
        return rootNode;
    };
}

const ASTNodeType = {
    DO: 'do',
    WHILE: 'while',
    IF: 'if',
    ELSE: 'else',
    ELSEIF: 'elseif',
    SWITCH: 'switch',
    CASE: 'case',
    BREAK: 'break',
    CURLY_BRACE_L: '{',
    CURLY_BRACE_R: '}',
    GOTO: 'goto',
    TEXT: 'text',
    LABEL: 'label'
};

class ASTNode {
    constructor(type) {
        this.type = type;
        this.condition = null;
        this.parsedCondition = null;
        this.single = true;
        this.lines = [];

        this.parent = null;
        this.children = [];

        if (type === 'case') {
            this.single = false;
        }
    }

    commitLines() {
        if (!this.lines.length) {
            return;
        }

        const textNode = new ASTNode('text');
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
}

class ASTNodeParser {
    constructor() {
        this.astNodeStack = [];
        this.currentNode = null;

        this.labelNodeCache = {};

        this.conditionalRegexMap = {
            [ASTNodeType.DO]: /do/,
            [ASTNodeType.WHILE]: /while\s*\((.*)\).*?(;)?/,
            [ASTNodeType.ELSEIF]: /else if\s*\((.*)\)/,
            [ASTNodeType.IF]: /if\s*\((.*)\)/,
            [ASTNodeType.ELSE]: /else/,
            [ASTNodeType.SWITCH]: /switch\s*\((.*)\)/,
            [ASTNodeType.CASE]: /case\s*([^:]+)\s*:/,
            [ASTNodeType.BREAK] : /break/,
            [ASTNodeType.CURLY_BRACE_L]: /\{/,
            [ASTNodeType.CURLY_BRACE_R]: /\}/,
            [ASTNodeType.GOTO]: /goto\s*(.*?)\s*;/
        };
    }

    parse(codeText) {
        const lines = this._tokenize(codeText);

        const firstLine = lines.shift();
        const rootNode = new ASTNode(firstLine);
        this._pushNode(rootNode);

        lines.forEach((line, index) => {
            const ret = this._testConditionNode(line);
            if (!ret) {
                this.currentNode.lines.push(line);

                if (this.currentNode.single) {
                    this._popNode();
                }

                return;
            }

            const type = ret[0];
            const condition = ret[1];
            const commaTerminator = ret[2];

            switch (type) {
                case ASTNodeType.CURLY_BRACE_L:
                    this.currentNode.single = false;
                    return;

                case ASTNodeType.CURLY_BRACE_R:
                    this._popNode();
                    return;

                case ASTNodeType.BREAK:
                    if (this.currentNode.type === ASTNodeType.CASE) {
                        // end case
                        this._popNode();
                    }
                    else if (
                        this.currentNode.type === ASTNodeType.IF ||
                        this.currentNode.type === ASTNodeType.ELSEIF ||
                        this.currentNode.type === ASTNodeType.ELSE
                    ) {
                        this._pushNode(new ASTNode(ASTNodeType.BREAK));
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

                case ASTNodeType.GOTO:
                    if (this.currentNode.type === ASTNodeType.CASE) {
                        const gotoNode = new ASTNode(ASTNodeType.GOTO);
                        gotoNode.condition = condition;
                        this._pushNode(gotoNode);
                        this._popNode();

                        // end case
                        this._popNode();
                    }
                    else if (
                        this.currentNode.type === ASTNodeType.IF ||
                        this.currentNode.type === ASTNodeType.ELSEIF ||
                        this.currentNode.type === ASTNodeType.ELSE
                    ) {
                        const gotoNode = new ASTNode(ASTNodeType.GOTO);
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

                case ASTNodeType.WHILE:
                    // 只有 do while 时候，while 后 才会有 comma
                    if (commaTerminator) {
                        const siblingNode = this.currentNode.getLastChild();
                        if (siblingNode.type !== ASTNodeType.DO) {
                            this._terminate("illegal while with comma", line, index);
                        }

                        siblingNode.condition = condition;

                        return;
                    }
            }

            const newNode = new ASTNode(type);
            newNode.condition = condition;
            this._pushNode(newNode);
        });

        this._parseLabel(rootNode);
        this._linkLabel(rootNode);
        this._parseCondition(rootNode);

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
            this.astNodeStack.push(this.currentNode);
        }

        if (this.astNodeStack.length) {
            const preNode = this.astNodeStack[this.astNodeStack.length - 1];
            preNode.commitLines();
        }

        this.currentNode = node;

        // console.debug(`> push: ${this.currentNode.type} ${this.currentNode.condition || ""}`);
    }

    _popNode() {
        if (!this.currentNode) {
            return;
        }

        const preNode = this.astNodeStack.pop();
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

    _visitNode(node, visit) {
        if (!node) {
            return;
        }

        if (!visit(node)) {
            node.children.forEach(childNode => {
                this._visitNode(childNode, visit);
            })
        }
    }

    __processTextNodeLineWithRegex(rootNode, regex, handler) {
        this._visitNode(rootNode, (node) => {
            if (node.type === ASTNodeType.TEXT) {
                let targetLineFound = false;

                const newChildren = [];

                let tmpLines = [];
                const commitTmpLines = () => {
                    if (!tmpLines.length) {
                        return;
                    }

                    const newNode = new ASTNode("text");
                    newNode.lines = tmpLines;
                    newChildren.push(newNode);

                    tmpLines = [];
                };

                node.lines.forEach((line, index)  => {
                    const m = regex.exec(line);
                    if (m) {
                        commitTmpLines();

                        const newNode = new ASTNode();
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

                return true;
            }

            return false;
        });
    }

    _parseLabel(rootNode) {
        const labelRegex = /(LABEL_\d+?):/;

        this.__processTextNodeLineWithRegex(rootNode, labelRegex, (newNode, matches) => {
            newNode.type = ASTNodeType.LABEL;
            newNode.condition = matches[1];

            this.labelNodeCache[newNode.condition] = newNode;
        });
    }

    _linkLabel(rootNode) {
        this._visitNode(rootNode,  (node) => {
            if (node.type === ASTNodeType.GOTO) {
                const targetLabelNode = this.labelNodeCache[node.condition];
                if (!targetLabelNode) {
                    this._terminate("can not find label for goto: " + node.condition);
                }

                //注意：实现细节， label 是 goto 的 child， 但是并不是 goto 反过来并不是 label 的 parent, 有问题了再说。
                node.children = [targetLabelNode];

                return true;
            }

            return false;
        })
    }

    _parseCondition(rootNode) {
        const conditionNodeTypes = [
            ASTNodeType.DO,
            ASTNodeType.WHILE,
            ASTNodeType.IF,
            ASTNodeType.ELSEIF,
            ASTNodeType.SWITCH,
        ];

        this._visitNode(rootNode, (node) => {
            if (conditionNodeTypes.indexOf(node.type) !== -1) {
                const parser = new ExprNodeParser();
                node.parsedCondition = parser.parse(node.condition);
                return true;
            }
            return false;
        });
    }
}

function process(content) {
    const parser = new ASTNodeParser();
    const rootNode = parser.parse(content);
    rootNode.print();
}


process(content);