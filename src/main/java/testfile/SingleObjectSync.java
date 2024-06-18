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

public class SingleObjectSync {

    public static void main(String[] args) {
        SyncThread syncThread = new SyncThread();
        Thread thread1 = new Thread(syncThread, "SyncThread1");
        Thread thread2 = new Thread(syncThread, "SyncThread2");
        thread1.start();
        thread2.start();
    }
}

class SyncThread implements Runnable {
    private static int count;
 
    public SyncThread() {
       count = 0;
    }
 
    public void run() {
       synchronized(this) {
          for (int i = 0; i < 5; i++) {
             try {
                System.out.println(Thread.currentThread().getName() + ":" + (count++));
                Thread.sleep(100);
             } catch (InterruptedException e) {
                e.printStackTrace();
             }
          }
       }
    }
 
    public int getCount() {
       return count;
    }
 }