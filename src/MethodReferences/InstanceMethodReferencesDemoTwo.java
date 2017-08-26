package MethodReferences;

public class InstanceMethodReferencesDemoTwo {

    public static void main(String args[]){

        MyIntNum myIntNum = new MyIntNum(12);
        MyIntNum myIntNum1 = new MyIntNum(16);

        //A Method reference to any object of type MyIntNum
        MyIntNumPredicate inp = MyIntNum::isFactor;

        //The following calls isFactor() on myIntNum
        boolean result = inp.test(myIntNum, 3);
        if(result) System.out.println("3 is a factor of " + myIntNum.getNum());

        //The following calls isFactor() on myIntNum2
        if(inp.test(myIntNum1, 3)) System.out.println("3 is not a factor of " + myIntNum1.getNum());

    }
}
