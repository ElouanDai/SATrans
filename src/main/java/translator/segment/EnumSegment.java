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

public class EnumSegment {
    private final StringBuilder enumContentChild;
    private final StringBuilder enumContentParent;
    private StringBuilder enumWaitType;
    private StringBuilder queueBuilder;
    private StringBuilder workerBuilder;

    public EnumSegment(int lockNum) {
        enumContentChild = new StringBuilder("END, ");
        enumContentParent = new StringBuilder("START, ");
        for (int i = 1; i <= lockNum; i++) {
            enumContentChild.append("ACQ_").append(i).append(", ").append("REL_").append(i).append(", ").append("WAIT_").append(i).append(", ").append("NOTIFY_").append(i).append(", ");
            enumContentParent.append("SYN_").append(i).append(", ").append("WAIT_END_").append(i).append(", ");
        }
    }

    public EnumSegment(int lockNum, int threadNum) {
        enumContentChild = new StringBuilder("END, ");
        enumContentParent = new StringBuilder("START, ");
        enumWaitType = new StringBuilder("NOT_WAIT, ");
        queueBuilder = new StringBuilder();
        workerBuilder = new StringBuilder();
        for (int i = 1; i <= lockNum; i++) {
            enumContentChild.append("ACQ_").append(i).append(", ").append("REL_").append(i).append(", ").append("WAIT_").append(i).append(", ").append("NOTIFY_").append(i).append(", ");
            enumContentParent.append("SYN_").append(i).append(", ").append("WAIT_END_").append(i).append(", ");
            enumWaitType.append("WAIT_END_").append(i).append(", ");
            queueBuilder.append("let Queue_Mutex_").append(i).append(" = new CircularQueue(10);\n");
            queueBuilder.append("let Queue_Wait_").append(i).append(" = new CircularQueue(10);\n");
        }
        for (int i = 1; i <= threadNum; i++) {
            workerBuilder.append("const worker_").append(i).append(" = new worker.ThreadWorker('entry/ets/workers/worker_").append(i).
                    append(".ts', {name:\"worker_").append(i).append("\"});\n");
        }
        workerBuilder.append("const worker_list = [ ");
        for (int i = 1; i <= threadNum; i++)
            workerBuilder.append("worker_").append(i).append(", ");
        workerBuilder.append("];\n");
    }

    public String getEnumCodeChild() {
        return "enum PostMsgType { " + enumContentChild.toString() + "};\n"
                + "enum OnMsgType { " + enumContentParent.toString() + "};\n\n";
    }

    public String getEnumCodeParent() {
        return workerBuilder.toString() + "\n"
                + "enum OnMsgType { " + enumContentChild.toString() + "};\n"
                + "enum PostMsgType { " + enumContentParent.toString() + "};\n"
                + "enum WaitType { " + enumWaitType.toString() + "};\n\n"
                + queueBuilder.toString() + "\n";
    }
}
