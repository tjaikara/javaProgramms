package threads.trafficlight;

/**
 * Created by taikara on 7/18/17.
 */


public class TrafficLight {

    public static void main (String [] args){
        TrafficLightSimulator ts = new TrafficLightSimulator(TrafficLightColor.GREEN);



        for(int i =0; i < 9;i++){
            System.out.println(ts.getColor());
            ts.waitForChange();
        }
        ts.cancel();
    }
}
