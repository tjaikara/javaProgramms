package lambda;

public class blockLambda {

    public static void main(String[] args){

        NumericFunc smallestFactor = (n) -> {

            int result = 1;

            n = n < 0 ?-n : n;

            for(int i =2; i <= n/i; i ++){

                if((n%i) == 0){

                    result = i;
                    break;
                }
            }
            return result;
        };

        System.out.println("Smallest factor of 12 is "+ smallestFactor.func(12));
        System.out.println("Smallest factor of 11 is "+ smallestFactor.func(11));
    }
}
