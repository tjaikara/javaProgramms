package threads.threadsynchronize;

/**
 * Created by taikara on 7/12/17.
 */
public class MyThread implements Runnable {

    Thread thread;
    static SumArray sumArray = new SumArray();
    int a[];
    int answer;

    MyThread(String name, int num[]){
        thread = new Thread(this, name);
        a = num;
        thread.start();
    }

    public void run(){

        System.out.println(thread.getName() + " starting.");

        answer = sumArray.sumArray(a);
        System.out.println("Sum for " + thread.getName() + " is "+answer);

        System.out.println(thread.getName() + " terminating.");
    }
}
