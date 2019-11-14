const {isType: isType, addType} = (() => {
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
            if (type.indexOf('std::__1::') !== -1) {
                return true;
            }

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

    isFunctionNameNode() {
        return this.type === ExpressionNodeType.VAR || this.type === ExpressionNodeType.TYPE;
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
            castStr = `(${this.castType.toString()})`;
        }

        switch (this.type) {
            case ExpressionNodeType.DECLARE_VAR:
                ret = `${this.val.type} ${this.val.name}`;
                break;

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
                ret = this.type === ExpressionNodeType.NUMBER ? `0x${this.val.toString(16).toUpperCase()}` : this.val;
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
    preProcess(expression) {
        // operator new 转换为 operator_new
        return expression.replace(/operator (\w+)/g, 'operator_$1')
    }

    parse (expression) {
        expression = this.preProcess(expression);

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
                                node.castType = tmp[0].children[0];

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
                    if (preNode && !preNode._committed && preNode.isFunctionNameNode()) {
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

                    const decimalNumberReg = /^([-+]?[0-9]+(\.[0-9]+)?)L*$/;
                    const decimalMatches = decimalNumberReg.exec(t);
                    if (decimalMatches) {
                        node = new ExpressionNode(ExpressionNodeType.NUMBER, BigInt(decimalMatches[1]));
                    }
                    else {
                        const hexNumberReg = /^(0[xX][0-9a-fA-F]+)L*$/;
                        const hexMatches = hexNumberReg.exec(t);
                        if (hexMatches) {
                            node = new ExpressionNode(ExpressionNodeType.NUMBER, BigInt(hexMatches[1]));
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
        rootNode.originalExpression = expression;

        return rootNode;
    };

    // 对于函数定义单独parse
    parseDeclareVar(line) {
        line = this.preProcess(line);

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
            addType(type);

            type = type.trim();
            name = name.trim();

            return new ExpressionNode(ExpressionNodeType.DECLARE_VAR, {
                type: type,
                name: name,
            });
        }
        else {
            return null;
        }
    }
}

exports.ExpressionNodeType = ExpressionNodeType;
exports.ExpressionNode = ExpressionNode;
exports.ExpressionNodeParser = ExpressionNodeParser;