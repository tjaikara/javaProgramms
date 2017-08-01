package threads.threadbasic;

/**
 * Created by taikara on 6/26/17.
 */

class MyThreadExt extends Thread{



    MyThreadExt(String name){
        super(name);
        super.start();
    }

    public void run() {
        System.out.println(getName() + " starting..");

        try{
            for(int count =0; count < 10; count++){
                Thread.sleep(400);
                System.out.println("In "+ getName() + " , count is " + count);
            }
        }
        catch (InterruptedException e){
            System.out.println(getName() + " interrupted.");
        }
        System.out.println(getName() + " terminating.");
    }
}
