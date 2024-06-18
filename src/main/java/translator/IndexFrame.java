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

import Block.ActionBlock;
import translator.segment.*;

import java.util.ArrayList;

import static translator.Main.getLockNum;
import static translator.Main.getThreadNum;


public class IndexFrame {
    private final InitSegment initSegment;
    private final EnumSegment enumSegment;
    private final ParentMsgHandlerSegment parentMsgHandlerSegment;
    private final ArrayList<MoveSegment> moveSegments;
    private final OtherSegment otherSegment;

    protected IndexFrame() {
        this.initSegment = new InitSegment(1);
        this.enumSegment = new EnumSegment(getLockNum(), getThreadNum());
        this.parentMsgHandlerSegment = new ParentMsgHandlerSegment(getLockNum());
        this.moveSegments = new ArrayList<>();
        this.otherSegment = new OtherSegment();
    }

    public void addMove(ActionBlock actionBlock){
        int type = actionBlock.getType();
        switch (type){
            case 0 -> {
                this.moveSegments.add(new StartSegment(actionBlock));
                break;
            }
        }
    }

    public String getIndexResult() {
        StringBuilder sb = new StringBuilder();
        sb.append(initSegment.getInitCode()).append(enumSegment.getEnumCodeParent()).append(parentMsgHandlerSegment.getMsgHandlerCode());
        for (MoveSegment moveSegment : moveSegments) {
            sb.append(moveSegment.getMoveCode());
        }
        sb.append(otherSegment.getOtherCode());
        return sb.toString();
    }
}
