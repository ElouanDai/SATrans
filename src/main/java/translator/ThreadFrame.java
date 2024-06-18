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
package translator;


import Block.MyBlock;
import translator.segment.EnumSegment;
import translator.segment.FunctionGroupSegment;
import translator.segment.InitSegment;
import translator.segment.ChildMsgHandlerSegment;

import java.util.List;

import static translator.Main.getLockNum;

public class ThreadFrame {
    private final InitSegment initSegment;
    private final EnumSegment enumSegment;
    private final ChildMsgHandlerSegment childMsgHandlerSegment;
    private final FunctionGroupSegment functionGroupSegment;


    public ThreadFrame(String threadId, List<MyBlock> blockList) {
        this.initSegment = new InitSegment(2);
        this.enumSegment = new EnumSegment(getLockNum());
        this.childMsgHandlerSegment = new ChildMsgHandlerSegment(threadId, blockList);
        this.functionGroupSegment = new FunctionGroupSegment(blockList);
    }

    public String getThreadResult() {
        return initSegment.getInitCode() + enumSegment.getEnumCodeChild() +
                childMsgHandlerSegment.getMsgHandlerCode() + functionGroupSegment.getFunctionGroupCode();
    }
}
