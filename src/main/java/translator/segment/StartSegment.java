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
import static translator.Main.getThreadIndex;

public class StartSegment extends MoveSegment {
    public StartSegment(ActionBlock actionBlock) {
        StringBuilder sb = new StringBuilder();
        String threadIndex = getThreadIndex(actionBlock.getMsg().split(" ")[1]);
        sb.append(String.format("\nworker_%s.onmessage = onMessage\n",threadIndex));
        sb.append(String.format("worker_%s.postMessage({type: PostMsgType.START})\n",threadIndex));
        moveCode = sb.toString();
    }
}
