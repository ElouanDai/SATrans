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

public class NotifyTest {
    public static void main(String[] args) {
        Object object_1 = new Object();
        Object object_2 = new Object();
        Thread thread_1 = new Thread(()-> {
            System.out.println("thread_1开始");
            synchronized (object_1) {
                System.out.println("thread_1操作object_1");
                System.out.println("thread_1等待thread_2对object_1的操作");
                try {
                    object_1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("thread_1继续操作thread_2操作完毕的object_1");
            }
            System.out.println("thread_1操作完毕object_1");
            synchronized (object_2) {
                System.out.println("thread_1操作object_2");
            }
            System.out.println("thread_1操作完毕object_2");
            System.out.println("thread_1结束");
        });
        Thread thread_2 = new Thread(()-> {
            System.out.println("thread_2开始");
            synchronized (object_1) {
                System.out.println("thread_2操作object_1");
                System.out.println("thread_2通知thread1操作完毕object_1");
                object_1.notify();
            }
            System.out.println("thread_2结束");
        });
        Thread thread_3 = new Thread(()-> {
            System.out.println("thread_3开始");
            synchronized (object_2) {
                System.out.println("thread_3操作object_2");
            }
            System.out.println("thread_3结束");
        });
        thread_1.start();
        thread_2.start();
        thread_3.start();
    }
}
