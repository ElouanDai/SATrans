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

import static java.lang.Thread.currentThread;

class MyRunnable implements Runnable {
//    public void run() {
//        for (int i = 1; i <= 5; i++) {
//            System.out.println(currentThread().getName() + " - Count: " + i);
////            System.out.println(Thread.currentThread());
//            Thread.yield(); // 线程让步
//        }
//    }
    private int nu;

    public void run() {
        System.out.println(currentThread().getName() + " 1");
        Thread.yield();
        System.out.println(currentThread().getName() + " 2");
    }
}

public class test {
    public static void main(String[] args)
    //Block
    {
        //Statement, Variable
        Thread t1 = new Thread(new MyRunnable(), "Thread 1");
        Thread t2 = new Thread(new MyRunnable(), "Thread 2");
        t1.start();
        t2.start();
    }
}
