const fs = require('fs');

const content = fs.readFileSync("./didAddDyldImage_104B8768C.cpp", {encoding: 'utf8'});

const {isType, addType} = (() => {
    const baseTypeList = [
        'void',

        'bool',

        'char',
        'signed char',
        'unsigned char',

        'wchar_t',

        'short',
        'signed short',
        'unsigned short',
        'signed short int',
        'unsigned short int',

        'unsigned',
        'signed',
        'int',
        'signed int',
        'unsigned int',
        'long int',
        'signed long int',
        'unsigned long int',

        'long',
        'signed long',
        'unsigned long',
        'long long',
        'signed long long',
        'unsigned long long',
        'signed long long int',
        'long long int',
        'unsigned long long int',

        'float',
        'double',
        'long double',

        // ida

        '__int8',
        'signed __int8',
        'unsigned __int8',
        '__int16',
        'signed __int16',
        'unsigned __int16',
        '__int32',
        'signed __int32',
        'unsigned __int32',
        '__int64',
        'signed __int64',
        'unsigned __int64',
        '__int128',
        'signed __int128',
        'unsigned __int128',

        'uint',
        'uchar',
        'ushort',
        'ulong',
        'int8',
        'sint8',
        'uint8',
        'int16',
        'sint16',
        'uint16',
        'int32',
        'sint32',
        'uint32',
        'int64',
        'sint64',
        'uint64',

        '_BYTE',
        '_WORD',
        '_DWORD',
        '_QWORD',
        '_LONGLONG'

    ];
    const typeDict = {};
    baseTypeList.forEach(type => {
        typeDict[type] = true;
    });

    return {
        isType: (type) => {
            type = type.split('*')[0];
            return typeDict[type];
        },

        addType: (type) => {
            type = type.split('*')[0].trim();
            typeDict[type] = true;
        }
    };
})();

const ExpressionNodeType = {
    TYPE: 'type',
    VAR: 'var',
    NUMBER: 'number',
    STRING: 'string',
    BINARY_OPERATOR: 'binary_operator',
    UNARY_OPERATOR: 'unary_operator',
    FUNCTION_CALL: 'function_call',
    BRACE: '()',
    DECLARE_VAR: "declare_var"
};

class ExpressionNode {
    constructor (type, val = null) {
        this.type = type;
        this.val = val;
        this.children = [];
        this.castType = null;   // 转换类型
        this._committed = false;  // node 一旦 commit 了，就不能在进行符号计算了，比如 "a, &b" 不能是 "a & b"
    }

    isVarOrConstantNode () {
        return this.type === ExpressionNodeType.STRING || this.type === ExpressionNodeType.NUMBER;
    }

    isBinaryOperatorNode() {
        return this.type === ExpressionNodeType.BINARY_OPERATOR;
    }

    isUnaryOperatorNode() {
        return this.type === ExpressionNodeType.UNARY_OPERATOR;
    }

    isOperatorNode() {
        return this.isBinaryOperatorNode() || this.isUnaryOperatorNode();
    };

    isTypeBraceNode() {
        if (this.type !== ExpressionNodeType.BRACE) {
            return false;
        }

        const typeNode = this.children && this.children[0];
        return typeNode && typeNode.type === ExpressionNodeType.TYPE;
    }

    // 这个 node 是否是已经完整的 node。比如加法，如果已经有两个 child 了，那个这个表达式已经完整。
    isCompleted() {
        let ret = false;

        switch (this.type) {
            case ExpressionNodeType.VAR:
            case ExpressionNodeType.NUMBER:
            case ExpressionNodeType.STRING:
                ret = true;
                break;

            case ExpressionNodeType.BINARY_OPERATOR:
                ret = this.children && this.children.length === 2;
                break;

            case ExpressionNodeType.UNARY_OPERATOR:
                ret = this.children && this.children.length === 2;
                break;

            case ExpressionNodeType.FUNCTION_CALL:
            case ExpressionNodeType.BRACE:
                ret = true;
                break;
        }

        return ret;
    }

    toString() {
        let ret = null;

        let castStr = "";

        if (this.castType) {
            castStr = this.castType.toString();
        }

        switch (this.type) {
            case ExpressionNodeType.TYPE:
                const printPointer = (node) => {
                    if (!node.children || !node.children.length) {
                        return ""
                    }

                    const pointerNode = node.children[0];
                    return pointerNode.val + printPointer(pointerNode);
                };

                let pointerStr = printPointer(this);
                if (pointerStr.length) {
                    pointerStr = " " + pointerStr;
                }

                ret = this.val + pointerStr;

                break;
            case ExpressionNodeType.VAR:
            case ExpressionNodeType.NUMBER:
            case ExpressionNodeType.STRING:
                ret = this.type === ExpressionNodeType.NUMBER ? `0x${this.val.toString(16)}` : this.val;
                break;

            case ExpressionNodeType.BINARY_OPERATOR:
                ret = `(${this.children[0].toString()} ${this.val} ${this.children[1].toString()})`;
                break;

            case ExpressionNodeType.UNARY_OPERATOR:
                if (this.val === 'cast') {
                    ret = `(${this.children[0].toString()})`;
                }
                else {
                    ret = `${this.val}${this.children[0].toString()}`;
                }

                break;

            case ExpressionNodeType.FUNCTION_CALL:
                ret = `${this.val}(${this.children.map(n => n.toString()).join(", ")})`;
                break;

            case ExpressionNodeType.BRACE:
                ret = `(${this.children.map(n => n.toString()).join(", ")})`;
                break;

            default:
                console.error(`Unkown node type:${this.type}`);
        }

        return castStr + ret;
    }
}

class ExpressionNodeParser {
    parse (expression) {
        const binaryOperators = [ '+', '-', '>', '<', '>=', '<=', '==', '!=','*', '/', '%', '<<', '>>', '&&', '||', '&', '|', '^', '=', '.', '->'];
        const unaryOperators = ['!', '~', '++', '--']; // ++ -- 只支持前缀模式
        const operatorsPriority = {
            '.': -1,
            '->': -1,
            "(": 0,
            '++': 1,
            '--': 1,
            '!': 1,
            '~': 1,
            'cast': 1,
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
            '=': 12
        };
        const operators = [
            '\\(',
            '\\)',
            '\\{',
            '\\}',
            '\\+',
            '\\+\\+',
            '\\-',
            '\\-\\-',
            '\\*',
            '/',
            '%',
            '=',
            '==',
            '!=',
            '>',
            '>=',
            '<',
            '<=',
            '\\+=',
            '\\-=',
            '\\*=',
            '/=',
            '%=',
            '<<=',
            '>>=',
            '&=',
            '^=',
            '\\|=',
            '\\!',
            '~',
            '\\^',
            '&',
            '&&',
            '\\|',
            '\\|\\|',
            '>>',
            '<<',
            '\\,',
            ';',
            '\\->',
            '\\.'
        ];

        const splitReg = () => {

            operators.sort((a, b) => {
                return a.length === b.length ? 0 : ((a.length > b.length) ? -1 : 1);
            });

            let regStr = operators.map((op => {
                return `(${op})`;
            })).join("|");

            return new RegExp(`\\s|${regStr}`, 'g');
        };
        const regex = splitReg();

        const tokens = expression.split(regex).filter(c => c && c.length);

        const isBinaryOperator = (token) => {
            return binaryOperators.indexOf(token) !== -1;
        };
        const isUnaryOperator = (token) => {
            return unaryOperators.indexOf(token) !== -1;
        };

        const parseTokens = (tokens, tokenIndex, stopToken) => {
            const operatorStack = [];
            let nodeStack = [];

            const commitOperatorStack = (until) => {
                while (operatorStack.length) {
                    const topOperator = operatorStack[operatorStack.length - 1];
                    if (until(topOperator)) {
                        operatorStack.pop();

                        if (topOperator.isBinaryOperatorNode()) {
                            const tmp = nodeStack.splice(nodeStack.length - 3, 3);
                            topOperator.children = [tmp[0], tmp[2]];
                            nodeStack.push(topOperator);
                        }
                        else if (topOperator.isUnaryOperatorNode()) {
                            if (topOperator.val === 'cast') {
                                const tmp = nodeStack.splice(nodeStack.length - 2, 2);
                                const node = tmp[1];
                                node.castType = tmp[0];

                                nodeStack.push(node);
                            }
                            else {
                                const tmp = nodeStack.splice(nodeStack.length - 2, 2);
                                topOperator.children = [tmp[1]];
                                nodeStack.push(topOperator)
                            }
                        }
                    }
                    else {
                        break;
                    }
                }
            };

            while (tokenIndex < tokens.length && !(stopToken && tokens[tokenIndex] === stopToken)) {
                const t = tokens[tokenIndex];

                if (t === '(') {
                    const ret = parseTokens(tokens, tokenIndex + 1, ')');

                    const preNode = nodeStack[nodeStack.length - 1];

                    // func ()
                    if (preNode && !preNode._committed && preNode.type === ExpressionNodeType.VAR) {
                        preNode.type = ExpressionNodeType.FUNCTION_CALL;
                        preNode.children = ret[0];
                    }
                    else {
                        const children = ret[0];

                        // 删除没必要的 brace,
                        if(children.length === 1) {
                            const node = children[0];
                            // 如果是 (xxxx) 返回 brace 类型 node
                            if (node.type !== ExpressionNodeType.TYPE) {
                                nodeStack.push(node);
                            }
                            else {
                                // 将类型转换当作一种 unary 操作符
                                const unaryNode = new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR);
                                unaryNode.val = 'cast';
                                unaryNode.children = [node];

                                operatorStack.push(unaryNode);
                                nodeStack.push(unaryNode);
                            }
                        }
                        else {
                            const braceNode = new ExpressionNode(ExpressionNodeType.BRACE);
                            braceNode.children = ret[0];
                            nodeStack.push(braceNode);
                        }
                    }

                    tokenIndex = ret[1];
                }
                else if (isBinaryOperator(t)) {
                    const preNode = nodeStack[nodeStack.length - 1];
                    let isNormalBinaryOperator = true;

                    // * 有三种意思：乘法、指针类型、计算目标地址
                    if (t === "*") {
                        // 指针类型
                        if (preNode && (preNode.type === ExpressionNodeType.TYPE)){
                            preNode.children.push(new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR, t));
                            isNormalBinaryOperator = false;
                        }
                        // 计算目标地址
                        else if (!preNode ||
                            preNode._committed ||
                            preNode.isTypeBraceNode()||
                            (preNode.isOperatorNode() && !preNode.isCompleted())) {
                            operatorStack.push(new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR, t));
                            nodeStack.push(new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR, t));
                            isNormalBinaryOperator = false;
                        }
                        // 乘法
                        else {
                        }
                    }

                    // & 有两种意思：与操作符，取地址
                    else if (t === '&') {
                        // 取地址
                        if (!preNode ||
                            preNode._committed ||
                            preNode.isTypeBraceNode() ||
                            (preNode.isOperatorNode() && !preNode.isCompleted())) {
                            operatorStack.push(new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR, t));
                            nodeStack.push(new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR, t));
                            isNormalBinaryOperator = false;
                        }
                        // 与操作符
                        else {
                        }
                    }

                    if (isNormalBinaryOperator) {
                        const binaryNode = new ExpressionNode(ExpressionNodeType.BINARY_OPERATOR, t);

                        commitOperatorStack((topOperator) => {
                            // 先处理优先级大的
                            return operatorsPriority[topOperator.val] <= operatorsPriority[binaryNode.val];
                        });

                        operatorStack.push(binaryNode);
                        nodeStack.push(new ExpressionNode(ExpressionNodeType.BINARY_OPERATOR, t));
                    }
                }
                else if (isUnaryOperator(t)) {
                    operatorStack.push(new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR, t));
                    nodeStack.push(new ExpressionNode(ExpressionNodeType.UNARY_OPERATOR, t));
                }

                // 需要区分："a & b" 和 "a, &b"
                else if (t === ',') {
                    commitOperatorStack((topOperator) => {
                        return !!topOperator;
                    });

                    const topNode = nodeStack[nodeStack.length - 1];
                    topNode._committed = true;
                }

                else if (t === ';') {
                    commitOperatorStack((topOperator) => {
                        return !!topOperator;
                    });

                    const topNode = nodeStack[nodeStack.length - 1];
                    topNode._committed = true;
                }

                else if (isType(t)) {
                    const preNode = nodeStack[nodeStack.length - 1];
                    if (preNode && preNode.type === ExpressionNodeType.TYPE) {
                        preNode.val += ` ${t}`;
                    }
                    else {
                        nodeStack.push(new ExpressionNode(ExpressionNodeType.TYPE, t));
                    }
                }

                else {
                    let node = null;

                    const decimalNumberReg = /^[-+]?[0-9]+(\.[0-9]+)?L*$/;
                    const decimalMatches = decimalNumberReg.exec(t);
                    if (decimalMatches) {
                        node = new ExpressionNode(ExpressionNodeType.NUMBER, parseInt(t));
                    }
                    else {
                        const hexNumberReg = /^0([xX])[0-9a-fA-F]+L*$/;
                        const hexMatches = hexNumberReg.exec(t);
                        if (hexMatches) {
                            node = new ExpressionNode(ExpressionNodeType.NUMBER, parseInt(t, 16));
                        }
                        else {
                            const stringReg = /^['"](\w+)['"]$/;
                            const stringMatches = stringReg.exec(t);
                            if (stringMatches) {
                                node = new ExpressionNode(ExpressionNodeType.STRING, stringMatches[1]);
                            }
                            else {
                                node = new ExpressionNode(ExpressionNodeType.VAR, t);
                            }
                        }
                    }

                    if (!node) {
                        console.error("nil node");
                    }

                    nodeStack.push(node);
                }

                tokenIndex++;
            }

            commitOperatorStack((topOperator) => {
                return !!topOperator;
            });

            return [nodeStack, tokenIndex];
        };

        const ret = parseTokens(tokens, 0, null);
        const rootNode = ret[0];
        return rootNode;
    };

    // 对于函数定义单独parse
    parseDeclareVar(line) {
        // 移除注释
        line = line.split('//')[0].trim();

        let name = null;
        let type = null;

        const varRegex = /^[^=]+? \**([\w+-:]+)+(\[\d+\])?;/
        let m = varRegex.exec(line);
        if (m) {
            name = m[1];

            const index = line.indexOf(name);
            type = line.substring(0, index);
        }
        else {
            const funcRegex = /^[^=]+? \(\w*\s*\**\s*([\w+-:]+)\)\(\);$/;
            m = funcRegex.exec(line);

            if (m) {
                name = m[1];
                type = 'function';
            }
        }


        if (name) {
            return {
                type: type,
                name: name,
            }
        }
        else {
            return null;
        }
    }
}

const BranchNodeType = {
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

class BranchNode {
    constructor(type) {
        this.type = type;
        this.condition = null;
        this.parsedCondition = null;
        this.single = true;
        this.lines = [];

        this.parent = null;
        this.children = [];

        this.parsedVars = null;

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
            [BranchNodeType.CURLY_BRACE_L]: /\{/,
            [BranchNodeType.CURLY_BRACE_R]: /\}/,
            [BranchNodeType.GOTO]: /goto\s*(.*?)\s*;/
        };
    }

    parse(codeText) {
        const lines = this._tokenize(codeText);

        const firstLine = lines.shift();
        const rootNode = new BranchNode(firstLine);
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
                case BranchNodeType.CURLY_BRACE_L:
                    this.currentNode.single = false;
                    return;

                case BranchNodeType.CURLY_BRACE_R:
                    this._popNode();
                    return;

                case BranchNodeType.BREAK:
                    if (this.currentNode.type === BranchNodeType.CASE) {
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

        this._parseVarDeclarations(rootNode);
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

    _parseVarDeclarations(rootNode) {
        const lineNode = rootNode.children[0];
        if (lineNode.type !== BranchNodeType.TEXT) {
            return;
        }

        const varDict = {};

        lineNode.lines.some((line) => {
            const parser = new ExpressionNodeParser();
            const ret = parser.parseDeclareVar(line);
            if (!ret) {
                return true;
            }

            varDict[ret['name']] = ret['type'];

            return false;
        });

        if (Object.keys(varDict).length) {
            rootNode.parsedVars = varDict;

            Object.keys(varDict).forEach(name => {
                const type = varDict[name];
                addType(type);
            });
        }
    }
}

function testExpressionParser() {
    const test = (expresion) => {
        const parser = new ExpressionNodeParser();
        const ret = parser.parse(expresion);
        console.log(ret.toString());
    };

    test('(void *)(a + b)');
    test('sub_10100F444(v27, (__int64)&v19, (__int128 *)&v19);');
    test('(unsigned int*)a');
    test('*((_BYTE *)v14 + v30) = 0;')
}

function process(content) {
    const parser = new BranchNodeParser();
    const rootNode = parser.parse(content);

    const parsedConditions = collectParsedConditions(rootNode);
    console.log(parsedConditions);
}

function collectParsedConditions(rootNode) {
    const result = [];

    rootNode.visit((node) => {
        if (node.parsedCondition) {
            result.push(node.parsedCondition.toString());
        }

        return true;
    });

    return result;
}

// process(content);

testExpressionParser();