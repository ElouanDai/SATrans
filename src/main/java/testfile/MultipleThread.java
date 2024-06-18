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

public class MultipleThread {
    private static final LinkedList<String> urls = new LinkedList<>();
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            synchronized(urls) {
                System.out.println("add1");
            }
        });
        Thread thread2 = new Thread(() -> {
            synchronized(urls) {
                System.out.println("add2");
            }
        });
        Thread thread3 = new Thread(() -> {
            synchronized(urls) {
                System.out.println("add3");
            }
        });
        Thread thread4 = new Thread(() -> {
            synchronized(urls) {
                System.out.println("remove");
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
