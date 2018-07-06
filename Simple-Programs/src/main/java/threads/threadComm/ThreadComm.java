package threads.threadComm;

/**
 * Created by taikara on 7/13/17.
 */
public class ThreadComm {

    public static void main(String args[]){
        TickTock tickTock = new TickTock();
        MyThread myThread = new MyThread("Tick", tickTock);
        MyThread myThread1 = new MyThread("Tock", tickTock);

        try{
            myThread.thread.join();
            myThread1.thread.join();
        }
        catch (InterruptedException e ){
            System.out.println("Main Thread Interrupted");
        }
    }
}
