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
import Block.MyBlock;
import Block.StmtBlock;
import Block.ThreadBlock;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translator {
    private final List<MyBlock> myBlockList;
    private final Map<String, String> threadResultMap = new HashMap<>();
    final IndexFrame indexFrame = new IndexFrame();


    public Translator(List<MyBlock> blocks) {
        this.myBlockList = blocks;
    }

    public void interpret() {
        for (MyBlock block : myBlockList) {
            if (block instanceof StmtBlock stmtBlock) {
                //do nothing?
                interpretStmtBlock(stmtBlock);
            } else if (block instanceof ActionBlock actionBlock) {
                interpretMoveBlock(actionBlock);
            } else if (block instanceof ThreadBlock threadBlock) {
                interpretThreadBlock(threadBlock);
            }
        }
        generatrArkTs();
    }

    private void interpretStmtBlock(StmtBlock stmtBlock) {

    }

    private void interpretMoveBlock(ActionBlock actionBlock) {
        indexFrame.addMove(actionBlock);
    }

    private void interpretThreadBlock(ThreadBlock threadBlock) {
        String threadId = threadBlock.getThreadId();
        List<MyBlock> blockList = threadBlock.getBlocks();
        // 由 ThreadFrame 类完成框架构建及翻译
        ThreadFrame threadFrame = new ThreadFrame(threadId, blockList);
        threadResultMap.put(threadId, threadFrame.getThreadResult());
    }

    private void generateIndex() {
        File file = new File("src/main/java/output/pages");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                System.out.println("Failed to create directory!");
            }
        }
        String fileName = "src/main/java/output/pages/Index.ets";
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(indexFrame.getIndexResult());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateThread() {
        for (String key : threadResultMap.keySet()) {
            File file = new File("src/main/java/output/workers");
            if (!file.exists()) {
                if (!file.mkdirs()){
                    System.out.println("Failed to create directory!");
                }
            }
            String fileName = "src/main/java/output/workers/worker_" + key + ".ts";
            try {
                FileWriter fileWriter = new FileWriter(fileName);
                fileWriter.write(threadResultMap.get(key));
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void generateQueue() {
        File file = new File("src/main/java/output/pages");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                System.out.println("Failed to create directory!");
            }
        }
        String fileName = "src/main/java/output/pages/Queue.ets";
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write("export class CircularQueue {\n" +
                    "  items: any[];\n" +
                    "  n: number;\n" +
                    "  head: number;\n" +
                    "  tail: number;\n" +
                    "\n" +
                    "  constructor(capacity) {\n" +
                    "    this.items = [];\n" +
                    "    this.n = capacity;\n" +
                    "    this.head = 0;\n" +
                    "    this.tail = 0;\n" +
                    "  }\n" +
                    "\n" +
                    "  push(item) {\n" +
                    "    const { head, tail, n } = this;\n" +
                    "    if ((tail + 1) % n == head) return false;\n" +
                    "    this.items[tail] = item;\n" +
                    "    this.tail = (tail + 1) % n;\n" +
                    "    return true;\n" +
                    "  }\n" +
                    "\n" +
                    "  pop() {\n" +
                    "    const { head, tail, n, items } = this;\n" +
                    "    if (head == tail) return null;\n" +
                    "    const result = items[head];\n" +
                    "    this.head = (head + 1) % n;\n" +
                    "    return result;\n" +
                    "  }\n" +
                    "\n" +
                    "  isEmpty() {\n" +
                    "    const { head, tail } = this;\n" +
                    "    return (head == tail);\n" +
                    "  }\n" +
                    "\n" +
                    "  front() {\n" +
                    "    const { head, tail, items } = this;\n" +
                    "    if (head == tail) return null;\n" +
                    "    return items[head];\n" +
                    "  }\n" +
                    "}");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatrArkTs() {
        generateIndex();
        generateQueue();
        generateThread();
    }
}
