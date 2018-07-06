package threads.threadSuspend;

/**
 * Created by taikara on 7/15/17.
 */
public class Suspend {

    public static void main(String [] args){
        MyThread myThread = new MyThread("My Thread");

        try{
            Thread.sleep(10000);

            myThread.mySuspend();
            System.out.println("Suspending thread.");
            Thread.sleep(1000);

            myThread.myresume();
            System.out.println("Resuming thread");
            Thread.sleep(1000);

            myThread.mySuspend();
            System.out.println("Suspending thread.");
            Thread.sleep(1000);

            myThread.myresume();
            System.out.println("Resuming thread");
            Thread.sleep(1000);

            myThread.mySuspend();
            System.out.println("Stopping thread.");
            myThread.myStop();

        }catch (InterruptedException e){
            System.out.println("Main thread Interrupted");
        }

        try{
            myThread.thread.join();
        }catch (InterruptedException e){
            System.out.println("Main thread interrypted");
        }
        System.out.println("Main Thread exiting.");
    }
}
