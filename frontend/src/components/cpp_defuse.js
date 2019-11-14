const fs = require('fs');
const ExpressionNodeParser = require('./CppDefuse/ExpressionNodeParser').ExpressionNodeParser;
const ExpressionNodeType = require('./CppDefuse/ExpressionNodeParser').ExpressionNodeType;
const BranchNodeParser = require('./CppDefuse/BranchNodeParser').BranchNodeParser;
const BranchNodeType = require('./CppDefuse/BranchNodeParser').BranchNodeType;

const content = fs.readFileSync("./didAddDyldImage_104B8768C.cpp", {encoding: 'utf8'});

function testExpressionParser() {
    const test = (expresion) => {
        const parser = new ExpressionNodeParser();
        const ret = parser.parse(expresion);
        console.log(ret.toString());
    };

    // test('(void *)(a + b)');
    // test('sub_10100F444(v27, (__int64)&v19, (__int128 *)&v19);');
    // test('(unsigned int*)a');
    // test('*((_BYTE *)v14 + v30) = 0;')
    // test('std::__1::mutex::unlock((std::__1::mutex *)std::mutexP::lock_qword_1075484D0);')
    // test('v13 = (void *)operator new(v12);');
    test('12341L');
}

function process(content) {
    const parser = new BranchNodeParser();
    const rootNode = parser.parse(content);
    // rootNode.print();
    // console.log(rootNode.toString());

    const graph = parser.buildBranchGraph(rootNode);
    console.log()

    // const parsedConditions = collectParsedConditions(rootNode);
    // console.log(parsedConditions);

    // evaluate(rootNode);
}

function evaluate(rootNode) {
    const varDeps = {};

    const stack = [];

    const isNode = (e) => {
        const className = e.constructor.name;
        return className !== 'ExpressionNode' && className !== 'BranchNode';
    }

    const getBranchNodeHandlers = () => {
        const ret = {};

        ret[BranchNodeType.FUNCTION] = (node) => {
            evalNodeChildren(node);
        };

        ret[BranchNodeType.DO] = (node) => {
            while (!node._executed || evalNode(node.parsedCondition)) {
                evalNodeChildren(node);
                node._executed = true;
            }
        };

        ret[BranchNodeType.WHILE] = (node) => {
            if (!evalNode(node.parsedCondition)) {
                return;
            }

            while(evalNode(node.parsedCondition)) {
                evalNodeChildren(node);
            }
        };

        ret[BranchNodeType.IF] = (node) => {
            const ifChainNodes = [node, ...node.getNextSiblingNodes((n) => {
                return n.type === BranchNodeType.ELSEIF || n.type === BranchNodeType.ELSE;
            })];

            ifChainNodes.some((n) => {
                if (n.type === n.type === BranchNodeType.ELSE) {
                    evalNodeChildren(n);
                    return true;
                }
                else {
                    if (evalNode(n.parsedCondition)) {
                        evalNodeChildren(n);
                        return true;
                    }
                }

                return false;
            });
        };

        ret[BranchNodeType.SWITCH] = (node) => {
            console.assert(node.parsedCondition.type === ExpressionNodeType.VAR);

            const varName = node.parsedCondition.val;
            const varVal = varDeps[varName];

            node.children.some((caseNode) => {
                console.assert(caseNode.type === BranchNodeType.CASE);

                const caseVal = evalNode(node.parsedCondition);

                if (varVal == caseVal) {
                    evalNodeChildren(caseNode);

                    if (caseNode._break) {
                        return true;
                    }
                    else {
                        // case 后面没哟跟 break，继续执行下一个 case
                        return false;
                    }
                }

                return false;
            })
        };

        ret[BranchNodeType.BREAK] = (breakNode) => {
            // break 出现在几种场合：do...while, while, switch

            const parent = breakNode.parent;

            // switch
            if (parent.type === BranchNodeType.CASE) {
                parent._break = true;
            }
            // do...while, while
            else {
                let whileNode = parent;
                while (whileNode && whileNode.type !== BranchNodeType.WHILE && whileNode.type !== BranchNodeType.DO) {
                    whileNode = whileNode.parent;
                }

                console.assert(!!whileNode);

                // TODO: stop executing current loop
            }
        };

        ret[BranchNodeType.GOTO] = (gotoNode) => {
            const labelNode = gotoNode.children[0];

            console.assert(!!labelNode);

            const nextNode = labelNode.getNextSiblingNode();
            evalNode(nextNode);

            // TODO:
        };

        ret[BranchNodeType.TEXT] = (textNode) => {
            textNode.parsedLines.forEach((pLine) => {
                evalNode(pLine);
            });
        };

        ret[BranchNodeType.LABEL] = (node) => {
            // Do nothing
        };

        return ret;
    };
    const branchNodeHandlers = getBranchNodeHandlers();

    const getExpressionNodeHandlers = () => {
        const ret = {};

        ret[ExpressionNodeType.VAR] = (node) => {
            const varName = node.val;
            let ret = varDeps[varName];

            if (ret === undefined) {
                // 如果当前没有对应变量的值，那么可能是全局变量，直接返回 node 作为依赖
                ret = node;
            }

            return ret;
        };

        ret[ExpressionNodeType.NUMBER] = (numNode) => {
            return Number(numNode.val);
        };

        ret[ExpressionNodeType.STRING] = (stringNode) => {
            return stringNode.val;
        };

        ret[ExpressionNodeType.BINARY_OPERATOR] = (node) => {
            const leftNode = node.children[0];
            const rightNode = node.children[1];

            const leftEvaluated = evalNode(leftNode);
            const rightEvaluated = evalNode(rightNode);

            if (node.val === '=') {
                if (leftNode.type === ExpressionNodeType.VAR) {
                    varDeps[leftNode.val] = rightEvaluated;
                }
                else {
                    varDeps[leftNode] = rightEvaluated;
                }

                return rightEvaluated;
            }
            else if (!isNode(leftEvaluated) && !isNode(rightEvaluated)) {
                switch (node.val) {
                    case '+':
                        return leftEvaluated + rightEvaluated;
                    case '-':
                        return leftEvaluated - rightEvaluated;
                    case "*":
                        return leftEvaluated * rightEvaluated;
                    case '/':
                        return leftEvaluated / rightEvaluated;
                    case '%':
                        return leftEvaluated % rightEvaluated;
                    case '>':
                        return leftEvaluated > rightEvaluated;
                    case '>=':
                        return leftEvaluated >= rightEvaluated;
                    case '<':
                        return leftEvaluated < rightEvaluated;
                    case '<=':
                        return leftEvaluated <= rightEvaluated;
                    case '==':
                        return leftEvaluated == rightEvaluated;
                    case '!=':
                        return leftEvaluated != rightEvaluated;
                }
            }

            return node;
        };

        ret[ExpressionNodeType.UNARY_OPERATOR] = (node) => {
            const child = node.children[0];
            const childEvaluated = evalNode(child);

            if (!isNode(childEvaluated)) {
                switch (node.val) {
                    case '!':
                        return !childEvaluated;

                    case '~':
                        return ~childEvaluated;

                    case '++':
                        var varName = child.val;
                        varDeps[varName] += 1;

                        return childEvaluated + 1;

                    case '--':
                        var varName = child.val;
                        varDeps[varName] -= 1;

                        return childEvaluated - 1;
                }
            }


            return node;
        };

        ret[ExpressionNodeType.FUNCTION_CALL] = (node) => {
            return node;
        };

        ret[ExpressionNodeType.DECLARE_VAR] = (declareVarNode) => {
            const varName = declareVarNode.val.name;
            varDeps[varName] = undefined;
        };

        return ret;
    };
    const expressionNodeHandlers = getExpressionNodeHandlers();

    const evalNodeChildren = (node) => {
        node.children.forEach(childNode => {
            evalNode(childNode);
        })
    };

    const evalNode = (node) => {
        let handler = null;

        console.debug(`execute node: ${node.type} ${node.val}`);

        const className = node.constructor.name;
        if (className === 'BranchNode') {
            handler = branchNodeHandlers[node.type];
        }
        else if (className === 'ExpressionNode') {
            handler = expressionNodeHandlers[node.type];
        }

        if (!handler) {
            console.warn(`can not find handler for node: ${node.type} ${node.val}`);
        }
        else {
            return handler(node);
        }
    };

    evalNode(rootNode);
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

process(content);
// testExpressionParser();

