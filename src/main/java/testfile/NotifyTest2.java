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


class MRunnable1 implements Runnable {
    private final Object object;

    public MRunnable1(Object object) {
        this.object = object;
    }

    public void run() {
        System.out.println("thread1开始");
        synchronized (object) {
            System.out.println("thread1操作共享资源");
            System.out.println("thread1等待thread2对共享资源的操作");
            try {
                object.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread1继续操作thread2操作完毕的共享资源");
        }
        System.out.println("thread1结束");
    }
}

class MRunnable2 implements Runnable {
    private final Object object;

    public MRunnable2(Object object) {
        this.object = object;
    }


    public void run() {
        System.out.println("thread2开始");
        synchronized (object) {
            System.out.println("thread2操作共享资源");
            System.out.println("thread2通知thread1操作完毕共享资源");
            object.notify();
        }
        System.out.println("thread2结束");
    }
}

public class NotifyTest2 {
    public static void main(String[] args) {
        Object object = new Object();
        Thread thread1 = new Thread(new MRunnable1(object));
        Thread thread2 = new Thread(new MRunnable2(object));
        thread1.start();
        thread2.start();
    }
}
