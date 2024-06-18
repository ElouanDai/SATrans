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

import java.util.List;

public class ThreadBlock extends MyBlock {

    private String threadId;
    private final String threadName;
    private List<MyBlock> blocks;


    public ThreadBlock(String threadName) {
        this.threadName = threadName;
    }

    public void setBlocks(List<MyBlock> blocks) {
        this.blocks = blocks;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public List<MyBlock> getBlocks() {
        return blocks;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getThreadId() {
        return threadId;
    }
}
