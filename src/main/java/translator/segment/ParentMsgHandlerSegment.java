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

import Block.MyBlock;

import javax.crypto.interfaces.PBEKey;
import java.util.List;

public class ParentMsgHandlerSegment {
    private final String msgHandlerCode;

    public ParentMsgHandlerSegment(int lockNum) {
        msgHandlerCode = """
                        let onMessage = function (e: MessageEvents) {
                          switch (e.data.type) {
                        """ + getMsgCaseCode(lockNum) + """
                          }
                        }
                        """ + """
                                                
                        function runBasedIsWait(queue, msgWhileNotWait) {
                          let thread = queue.front();
                          switch (thread.waitType) {
                        """ + getIsWaitCode(lockNum) + """
                          }
                        }
                        """
        ;
    }

    public String getMsgHandlerCode() {
        return msgHandlerCode;
    }

    public static String getMsgCaseCode(int lockNum) {
        StringBuilder casesCode = new StringBuilder();
        for (int i = 1; i <= lockNum; i++) {
            casesCode.append(String.format("\n    case OnMsgType.ACQ_%s:\n", i));
            casesCode.append(String.format("      if (Queue_Mutex_%s.isEmpty()) {\n", i));
            casesCode.append(String.format("        worker_list[e.data.worker_id-1].postMessage({type: PostMsgType.SYN_%s});\n", i));
            casesCode.append("      }\n");
            casesCode.append(String.format("      Queue_Mutex_%s.push({worker: worker_list[e.data.worker_id-1], waitType: WaitType.NOT_WAIT});\n", i));
            casesCode.append("      break;\n");

            casesCode.append(String.format("\n    case OnMsgType.REL_%s:\n", i));
            casesCode.append(String.format("      Queue_Mutex_%s.pop();\n", i));
            casesCode.append(String.format("      if (!Queue_Mutex_%s.isEmpty()) {\n", i));
            casesCode.append(String.format("        runBasedIsWait(Queue_Mutex_%s, PostMsgType.SYN_%s);\n", i, i));
            casesCode.append("      }\n");
            casesCode.append("      break;\n");

            casesCode.append(String.format("\n    case OnMsgType.WAIT_%s:\n", i));
            casesCode.append(String.format("      Queue_Wait_%s.push(Queue_Mutex_%s.pop())\n", i, i));
            casesCode.append(String.format("      if (!Queue_Mutex_%s.isEmpty()) {\n", i));
            casesCode.append(String.format("        runBasedIsWait(Queue_Mutex_%s, PostMsgType.SYN_%s);\n", i, i));
            casesCode.append("      }\n");
            casesCode.append("      break;\n");

            casesCode.append(String.format("\n    case OnMsgType.NOTIFY_%s:\n", i));
            casesCode.append(String.format("      if (!Queue_Wait_%s.isEmpty()) {\n", i));
            casesCode.append(String.format("        let thread = Queue_Wait_%s.pop()\n", i));
            casesCode.append(String.format("        thread.waitType = WaitType.WAIT_END_%s\n", i));
            casesCode.append(String.format("        Queue_Mutex_%s.push(thread);\n", i));
            casesCode.append("      }\n");
            casesCode.append("      break;\n");
        }
        casesCode.append("\n").append("""
                    case OnMsgType.END:
                      break;
                """);
        return casesCode.toString();
    }

    public static String getIsWaitCode(int lockNum) {
        StringBuilder casesCode = new StringBuilder();
        casesCode.append("""
                   case WaitType.NOT_WAIT:
                      thread.worker.postMessage({type: msgWhileNotWait});
                      break;
                """);
        for (int i = 1; i <= lockNum; i++) {
            casesCode.append(String.format("\n    case WaitType.WAIT_END_%s:\n", i));
            casesCode.append(String.format("      thread.worker.postMessage({type: PostMsgType.WAIT_END_%s});\n", i));
            casesCode.append("      break;\n");
        }
        return casesCode.toString();
    }
}
