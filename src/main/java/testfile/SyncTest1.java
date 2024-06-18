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

public class SyncTest1 { //和SyncTest2的区别：thread1和thread2调用的顺序是相反的

    private static final Object lock = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("thread1");
                try {
                    lock.wait();
                } catch (Exception e) {
                    // ignore
                }
                System.out.println("thread2");
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("thread3");
                lock.notify();
                System.out.println("thread4");
            }
        });

        thread1.start();
        thread2.start();
    }
}
