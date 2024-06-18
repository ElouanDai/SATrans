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
package Block;

public class ActionBlock extends MyBlock {
    /*
            0: START thread
            1: ACQ lock
            2: REL lock
            3: WAIT
            4: NOTIFY
         */
    private final int type;
    private final String msg;

    public ActionBlock(String msg) {
        this.msg = msg;
        String s = msg.split(" ")[0];
        switch (s) {
            case "START" -> type = 0;
            case "ACQ" -> type = 1;
            case "REL" -> type = 2;
            case "WAIT" -> type = 3;
            case "NOTIFY" -> type = 4;
            default -> type = -1;
        }
    }

    public String getMsg() { return msg; }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MoveBlock: " + msg;
    }
}
