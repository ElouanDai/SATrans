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

public class InitSegment {
    private String initCode;

    /**
     * 构造一个新的InitStmt对象。
     *
     * @param type 1：主线程的初始化语句；2：子线程的初始化语句
     */
    public InitSegment(int type) {
        switch (type) {
            // TODO: 主线程的初始化语句
            case 1 -> initCode = """
                    import worker, { MessageEvents } from '@ohos.worker';
                    import { CircularQueue } from "./Queue"
                    
                    """;
            case 2 -> initCode = """
                    import worker, { ThreadWorkerGlobalScope, MessageEvents, ErrorEvent } from '@ohos.worker';

                    const workerPort: ThreadWorkerGlobalScope = worker.workerPort;
                        
                    """;
        }
    }

    public String getInitCode() {
        return initCode;
    }
}
