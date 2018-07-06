package threads.threadComm;

/**
 * Created by taikara on 7/13/17.
 */
public class MyThread implements   Runnable {

    Thread thread;
    TickTock tickTock;

    MyThread(String name , TickTock tt){
        thread = new Thread(this, name);
        tickTock = tt;
        thread.start();
    }

    public void run(){
        if(thread.getName().compareTo("Tick")==0){
            for(int i =0; i < 5; i++){
                tickTock.tick(true);
            }
            tickTock.tick(false);
        }
        else{
            for(int i = 0; i < 5; i++){
                tickTock.tock(true);
            }
            tickTock.tock(false);
        }
    }
}
