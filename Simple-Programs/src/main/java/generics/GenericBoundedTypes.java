package generics;

public class GenericBoundedTypes {

    public static void main(String [] args){

        NumericFns<Integer> iOb= new NumericFns<Integer>(5);

        System.out.println("Reciprocal of 5: "+ iOb.reciprocal());

        System.out.println("Fractional component of iOb is "+
                                iOb.fraction());

        System.out.println();

        NumericFns<Double> dObj = new NumericFns<Double>(5.25);

        System.out.println("Reciprocal of 5: "+ dObj.reciprocal());

        System.out.println("Fractional component of iOb is "+
                dObj.fraction());
    }
}
