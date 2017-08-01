package generics;

public class GenericWildCardDemo {

    public static void main(String args[]){

        NumericFns<Integer> iOb =
                new NumericFns<Integer>(6);

        NumericFns<Double> dOb =
                new NumericFns<Double>(-6.0);

        NumericFns<Long> lOb =
                new NumericFns<Long>(5L);


        System.out.println("Testing iOb and dObj.");
        if(iOb.absValue(dOb))
            System.out.println("Absolute values are equal");
        else
            System.out.println("Absolute values are not equal");

        System.out.println();

        System.out.println("Testing iOb and lObj.");
        if(iOb.absValue(lOb))
            System.out.println("Absolute values are equal");
        else
            System.out.println("Absolute values are not equal");
    }
}
