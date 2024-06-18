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
package testfile;

public class YieldTest {
    public static void main(String[] args) {
        PrintThread threadA = new PrintThread("A");
        PrintThread threadB = new PrintThread("B");
        PrintThread threadC = new PrintThread("C");

        threadA.start();
        threadB.start();
        threadC.start();
    }

    static class PrintThread extends Thread {
        private String message;

        public PrintThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.print(message);
                // 每次输出后调用yield()放弃CPU执行权
                Thread.yield();
            }
        }
    }
}
