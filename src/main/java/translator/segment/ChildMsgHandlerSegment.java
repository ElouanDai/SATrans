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


import Block.ActionBlock;
import Block.MyBlock;
import Block.StmtBlock;
import translator.Main;

import java.util.List;

public class ChildMsgHandlerSegment {
    private final String msgHandlerCode;

    public ChildMsgHandlerSegment(String threadId, List<MyBlock> blockList) {
        msgHandlerCode = """
                workerPort.onmessage = function (e: MessageEvents) {
                  switch (e.data.type) {
                """ + getMsgCaseCode(threadId, blockList) + """
                  }
                }
                
                """;
    }

    public String getMsgHandlerCode() {
        return msgHandlerCode;
    }

    public static String getMsgCaseCode(String threadId, List<MyBlock> blockList) {
        // 初始化
        int blockCnt = 1;
        StringBuilder casesCode = new StringBuilder("    case OnMsgType.START:\n");

        // 遍历翻译
        for (MyBlock block : blockList) {
            // 语句块翻译
            if (block instanceof StmtBlock) {
                casesCode.append(String.format("      block_%s();\n", blockCnt++));
            }
            // 动作翻译
            else if (block instanceof ActionBlock actionBlock) {
                String lockId;
                switch (actionBlock.getType()) {
                    // ACQ
                    case 1 -> {
                        lockId = Main.getLockIndex(actionBlock.getMsg().split(" ")[1]);
                        casesCode.append(
                                String.format("      workerPort.postMessage({type:PostMsgType.ACQ_%s, worker_id:%s});\n", lockId, threadId)
                        ).append(
                                "      break;\n\n"
                        ).append(
                                String.format("    case OnMsgType.SYN_%s:\n", lockId)
                        );
                    }

                    // REL
                    case 2 -> {
                        lockId = Main.getLockIndex(actionBlock.getMsg().split(" ")[1]);
                        casesCode.append(String.format("      workerPort.postMessage({type:PostMsgType.REL_%s});\n", lockId));
                    }

                    // WAIT
                    case 3 -> {
                        lockId = Main.getLockIndex(actionBlock.getMsg().split(" ")[1]);
                        casesCode.append(
                                String.format("      workerPort.postMessage({type: PostMsgType.WAIT_%s});\n", lockId)
                        ).append(
                                "      break;\n\n"
                        ).append(
                                String.format("    case OnMsgType.WAIT_END_%s:\n", lockId)
                        );
                    }

                    // NOTIFY
                    case 4 -> {
                        lockId = Main.getLockIndex(actionBlock.getMsg().split(" ")[1]);
                        casesCode.append(String.format("      workerPort.postMessage({type: PostMsgType.NOTIFY_%s});\n", lockId));
                    }
                }
            }
        }
        // END
        casesCode.append(String.format("      workerPort.postMessage({type:PostMsgType.END, worker_id:%s});\n", threadId));
        casesCode.append("      break;\n");

        return casesCode.toString();
    }
}
