const fs = require('fs');
const ExpressionNodeParser = require('./CppDefuse/ExpressionNodeParser');
const BranchNodeParser = require('./CppDefuse/BranchNodeParser');

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
    console.log(rootNode.toString());

    // const parsedConditions = collectParsedConditions(rootNode);
    //
    // console.log(parsedConditions);
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