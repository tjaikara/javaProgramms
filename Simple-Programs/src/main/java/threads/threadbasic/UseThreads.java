package threads.threadbasic;

public class UseThreads {

    public static void main(String args[]){

        System.out.println("Main thread starting...");
        MyThread myThread = new MyThread("Child #1");
        mainThreadRun();

        System.out.println("Main thread starting again...");
        MyThreadExt myThreadExt = new MyThreadExt("Child #2");
        mainThreadRun();
    }

    private static void mainThreadRun(){

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


