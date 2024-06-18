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

import java.util.LinkedList;

public class MultipleThread2 {

    private static final LinkedList<String> urls = new LinkedList<>();

    public static void main(String[] args) {

        Thread thread1 = new Thread(() -> {
            System.out.println("thread1开始运行");
            synchronized(urls) {
                System.out.println("thread1获取到锁");
                System.out.println("thread1操作共享资源");
            }
            System.out.println("thread1释放锁");
        });
        Thread thread2 = new Thread(() -> {
            System.out.println("thread2开始运行");
            synchronized(urls) {
                System.out.println("thread2获取到锁");
                System.out.println("thread2操作共享资源");
            }
            System.out.println("thread2释放锁");
        });
        Thread thread3 = new Thread(() -> {
            System.out.println("thread3开始运行");
            synchronized(urls) {
                System.out.println("thread3获取到锁");
                System.out.println("thread3操作共享资源");
            }
            System.out.println("thread3释放锁");
        });
        Thread thread4 = new Thread(() -> {
            System.out.println("thread4开始运行");
            synchronized(urls) {
                System.out.println("thread4获取到锁");
                System.out.println("thread4操作共享资源");
            }
            System.out.println("thread4释放锁");
        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
