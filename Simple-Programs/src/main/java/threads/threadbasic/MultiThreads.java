package threads.threadbasic;

/**
 * @author taikara
 * Created on 6/26/17.
 */


public class MultiThreads {

    public static void main(String args[]){

        System.out.println("Main thread starting...");

        MyThread myThread = new MyThread("Child #1");
        MyThread myThread1 = new MyThread("Child #2");
        MyThread myThread2 = new MyThread("Child #3");

        do{
            System.out.print(".");
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                System.out.println("Main thread interrupted.");
            }
        }while (myThread.thread.isAlive() || myThread1.thread.isAlive() || myThread2.thread.isAlive());

        System.out.println("Main thread ending.");
    }

}


