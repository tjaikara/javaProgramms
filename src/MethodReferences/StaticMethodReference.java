package MethodReferences;

public class StaticMethodReference {

    public static void main(String args[]){
        boolean result;

        result = numTest(MyIntPredicates::isPrime, 17);
        if(result) System.out.println("17 is prime");

        result = numTest(MyIntPredicates::isEven, 12);
        if(result) System.out.println("12 is even");

        result = numTest(MyIntPredicates::isPositive, 11);
        if(result) System.out.println("11 is positive");
    }


    static boolean numTest(IntPredicate p, int v){
        return p.test(v);
    }
}
