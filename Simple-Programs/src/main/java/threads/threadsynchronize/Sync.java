package threads.threadsynchronize;

/**
 * Created by taikara on 7/12/17.
 */
public class Sync {

    public static void main(String [] args){
        int a[] = {1, 2, 3,4, 5};

        MyThread myThread = new MyThread("Child #1", a);
        MyThread myThread1 = new MyThread("Child #2", a);

        try{
            myThread.thread.join();
            myThread1.thread.join();
        }
        catch (InterruptedException e){
            System.out.println("Mian Thread interrupted.");
        }
    }

}
