package singleprograms;

public class enumeratiion {

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
}
