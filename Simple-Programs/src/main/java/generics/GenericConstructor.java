package generics;

public class GenericConstructor {

    public static void main(String args[]){
        Summation ob = new Summation(4.0);

        System.out.println("Summation of 4.0 is "+
                                            ob.getSum());
    }
}

