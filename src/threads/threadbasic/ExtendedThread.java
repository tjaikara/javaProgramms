package threads.threadbasic;

public class ExtendedThread {

    public static void main(String args[]){
        System.out.println("Main thread starting...");

        MyThreadExt myThread = new MyThreadExt("Child #1");

        for(int i =0; i < 50; i++){
            System.out.print(".");
            try{
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                System.out.println("Main thread interrupted.");
            }

            System.out.println("Main thread ending.");
        }
    }
}
