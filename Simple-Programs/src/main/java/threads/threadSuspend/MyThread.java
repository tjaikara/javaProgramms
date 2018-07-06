package threads.threadSuspend;

/**
 * Created by taikara on 7/15/17.
 */
public class MyThread implements Runnable {

    Thread thread;

    boolean suspended;
    boolean stopped;

    MyThread(String name){
        thread = new Thread(this, name);
        suspended = false;
        stopped = false;
        thread.start();
    }

    public void run(){
        System.out.println(thread.getName() + " starting...");
        try {
            for(int i = 1; i < 1000; i++){
                System.out.println(i + " ");
                if(i%10 == 0){
                    System.out.println();
                    Thread.sleep(250);
                }

                synchronized (this){
                    while (suspended){
                        wait();
                    }
                    if(stopped) break;
                }
            }
        }catch (InterruptedException e){
            System.out.println(thread.getName() + " interrupted.");
        }
        System.out.println(thread.getName() + " exiting..");
    }

    synchronized void myStop(){
        stopped = true;
        suspended = false;
        notify();
    }

    synchronized void mySuspend(){
        suspended = true;
    }

    synchronized void myresume(){
        suspended = false;
        notify();
    }
}
