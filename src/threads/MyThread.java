package threads;

/**
 * @author taikara
 * Created on 6/26/17.
 */

class MyThread implements Runnable{

    Thread thread;

    MyThread(String name){
        thread = new Thread(this, name);
        thread.start();
    }

    public void run() {
        System.out.println(thread.getName() + " starting..");

        try{
            for(int count =0; count < 10; count++){
                Thread.sleep(400);
                System.out.println("In "+ thread.getName() + " , count is " + count);
            }
        }
        catch (InterruptedException e){
            System.out.println(thread.getName() + " interrupted.");
        }
        System.out.println(thread.getName() + " terminating.");
    }
}
