

public class Enumeration {

    public static void main (String [] args){

        Transport transport;

        System.out.println("\nTypical speed for an airplane is " +
                Transport.AIRPLANE.getSpeed() + " miles per hour.\n");

        System.out.println("All general.Transport speeds: ");
        for(Transport t : Transport.values()){
            System.out.println(t + " typical speed is "+
                                    t.getSpeed() + " miles per hour.");
        }

    }


    enum Transport{
        CAR(65), AIRPLANE(600), TRUCK(55), TRAIN(70), BOAT(22);

        private int speed;

        Transport(int s){
            speed = s;
        }

        int getSpeed(){
            return speed;
        }
    }
}
