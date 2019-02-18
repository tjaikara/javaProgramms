package threads.trafficlight;

/**
 * Created by taikara on 7/18/17.
 */
public class TrafficLightSimulator implements Runnable {

    private Thread thread;
    private TrafficLightColor trafficLightColor;
    boolean stop = false;
    boolean changed = false;

    TrafficLightSimulator(TrafficLightColor init){
        trafficLightColor = init;

        thread = new Thread(this);
        thread.start();
    }

    TrafficLightSimulator(){
        trafficLightColor = TrafficLightColor.RED;

        thread = new Thread(this);
        thread.start();
    }


    public void run(){
        while (!stop){
            try{
                switch (trafficLightColor){
                    case GREEN:
                        Thread.sleep(10000);
                        break;
                    case YELLOW:
                        Thread.sleep(10000);
                        break;
                    case RED:
                        Thread.sleep(10000);
                        break;
                }
            }
            catch (InterruptedException e){
                System.out.println(e);
            }
            changecolor();
        }
    }

    synchronized void changecolor(){

        switch (trafficLightColor){
            case RED:
                trafficLightColor = TrafficLightColor.GREEN;
                break;
            case YELLOW:
                trafficLightColor = TrafficLightColor.RED;
                break;
            case GREEN:
                trafficLightColor = TrafficLightColor.YELLOW;
                break;
        }
        changed = true;
        notify();

    }

    synchronized void waitForChange(){
        try{
            while (!changed)
                wait();
            changed = false;
        }
        catch (InterruptedException e){
            System.out.println(e);
        }
    }

    synchronized TrafficLightColor getColor(){
        return  trafficLightColor;
    }

    synchronized void cancel(){
        stop = true;
    }
}
