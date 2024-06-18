/*
    Copyright 2024 CodeTranscenders
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package translator.segment;


import org.eclipse.jdt.core.dom.Statement;
import Block.MyBlock;
import Block.StmtBlock;

import java.util.List;

public class FunctionGroupSegment {
    private final String functionGroupCode;

    public FunctionGroupSegment(List<MyBlock> blockList) {
        functionGroupCode = getFunctionsCode(blockList);
    }

    public String getFunctionGroupCode() {
        return functionGroupCode;
    }

    public static String getFunctionsCode(List<MyBlock> blockList) {
        // 初始化
        int blockCnt = 1;
        StringBuilder functionsCode = new StringBuilder();
        // 遍历翻译
        for (MyBlock block : blockList) {
            if (block instanceof StmtBlock stmtBlock) {
                functionsCode.append(String.format("function block_%s() {\n", blockCnt++));
                for (Statement statement : stmtBlock.getStatements()) {
                    functionsCode.append("  ");
                    String str = statement.toString().replace("System.out.println", "console.log");
                    functionsCode.append(str).append("\n");
                }
                functionsCode.append("}\n\n");
            }
        }
        return functionsCode.toString();
    }
}
