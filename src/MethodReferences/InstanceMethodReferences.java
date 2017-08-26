package MethodReferences;


public class InstanceMethodReferences {

    public static void main (String args[]){

        MyIntNum myIntNum = new MyIntNum(12);
        MyIntNum myIntNum1 = new MyIntNum(16);

        IntPredicate ip = myIntNum::isFactor;

        boolean result = ip.test(3);
        if(result) System.out.println("3 is a factor of " + myIntNum.getNum());

        if(!numTest(myIntNum1::isFactor, 3)) System.out.println("3 is not a factor of " + myIntNum1.getNum());

    }

    static boolean numTest(IntPredicate p, int v){
        return p.test(v);
    }
}
