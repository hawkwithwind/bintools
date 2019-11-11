const fs = require('fs');

const content = fs.readFileSync("./didAddDyldImage_104B8768C.cpp", {encoding: 'utf8'});

// function tokenize(str) {
//     let result = [];
//
//     const splitReg = () => {
//         const operators = ['\\(', '\\)', '\\{', '\\}', '\\+', '\\+\\+', '\\-', '\\-\\-', '\\*', '/', '%', '=', '==', '!=', '>', '>=', '<', '<=', '\\+=', '\\-=', '\\*=', '/=', '%=', '<<=', '>>=', '&=', '^=', '\\|=', '\\!', '~', '\\^', '&', '&&', '\\|', '\\|\\|', '>>', '<<', '\\?', '\\:\\:', '\\:', '\\,', '\\;'];
//
//         operators.sort((a, b) => {
//             return a.length === b.length ? 0 : ((a.length > b.length) ? -1 : 1);
//         });
//
//         let regStr = operators.map((op => {
//             return `(${op})`;
//         })).join("|");
//
//         return new RegExp(`\\s|${regStr}`, 'g');
//     };
//
//     const regex = splitReg();
//
//
//     const lines = str.split('\n');
//     for (let line of lines) {
//         const commentIndex = line.indexOf("//");
//         if (-1 !== commentIndex) {
//             line = line.substring(0, commentIndex);
//         }
//
//         const lineTokens = line.split(regex).filter(c => c && c.length);
//         result = result.concat(lineTokens);
//     }
//
//     return result;
// }

// const tokens = tokenize(content);

class ASTNode {
    constructor(type) {
        this.type = type;
        this.condition = null;
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
            'do': /do/,
            'while': /while\s*\((.*)\).*?(;)?/,
            'elseif': /else if\s*\((.*)\)/,
            'if': /if\s*\((.*)\)/,
            'else': /else/,
            'switch': /switch\s*\((.*)\)/,
            'case': /case\s*([^:]+)\s*:/,
            'break' : /break/,
            "{": /\{/,
            "}": /\}/,
            "goto": /goto\s*(.*?)\s*;/
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
                case "{":
                    this.currentNode.single = false;
                    return;

                case "}":
                    this._popNode();
                    return;

                case "break":
                    if (this.currentNode.type === 'case') {
                        // end case
                        this._popNode();
                    }
                    else if (this.currentNode.type === 'if' || this.currentNode.type === 'elseif' || this.currentNode.type === 'else') {
                        this._pushNode(new ASTNode("break"));
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

                case 'goto':
                    if (this.currentNode.type === 'case') {
                        const gotoNode = new ASTNode("goto");
                        gotoNode.condition = condition;
                        this._pushNode(gotoNode);
                        this._popNode();

                        // end case
                        this._popNode();
                    }
                    else if (this.currentNode.type === 'if' || this.currentNode.type === 'elseif' || this.currentNode.type === 'else') {
                        const gotoNode = new ASTNode("goto");
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

                case 'while':
                    // 只有 do while 时候，while 后 才会有 comma
                    if (commaTerminator) {
                        const siblingNode = this.currentNode.getLastChild();
                        if (siblingNode.type !== 'do') {
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

    __processTextNodeLineWithRegex(rootNode, regex, handler) {
        const visit = (node) => {
            if (node.type === 'text') {
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
            } else {
                node.children.forEach(childNode => {
                    visit(childNode);
                })
            }
        };

        visit(rootNode);
    }

    _parseLabel(rootNode) {
        const labelRegex = /(LABEL_\d+?):/;

        this.__processTextNodeLineWithRegex(rootNode, labelRegex, (newNode, matches) => {
            newNode.type = "label";
            newNode.condition = matches[1];

            this.labelNodeCache[newNode.condition] = newNode;
        });
    }

    _linkLabel(rootNode) {
        const visit = (node) => {
            if (node.type === 'goto') {
                const targetLabelNode = this.labelNodeCache[node.condition];
                if (!targetLabelNode) {
                    this._terminate("can not find label for goto: " + node.condition);
                }

                //注意：实现细节， label 是 goto 的 child， 但是并不是 goto 反过来并不是 label 的 parent, 有问题了再说。
                node.children = [targetLabelNode];
            }
            else {
                node.children.forEach(childNode => {
                    visit(childNode);
                })
            }
        };

        visit(rootNode);
    }
}

function process(content) {
    const parser = new ASTNodeParser();
    const rootNode = parser.parse(content);



    rootNode.print();
}

process(content);