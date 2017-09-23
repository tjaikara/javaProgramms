package lambda;

public class expressionLambda {

    public static void main(String [] args){
        MyValue myValue;

        myValue = ()->98.6;

        System.out.println("Value of myValue"+myValue.getValue());

        MyParamValue myParamValue = (n)->1.0/n;

        System.out.println("Recciprocal of 4 is "+ myParamValue.getValue(4));
        System.out.println("Recciprocal of 8 is "+ myParamValue.getValue(8));


        NumericTest isFactor = (n,d) -> (n%d) == 0;

        if(isFactor.test(10,2))
            System.out.println("2 is a factor of  10"+ isFactor.test(10, 2));
        if(!isFactor.test(10,3))
            System.out.println("3 is not a factor of 10 "+ isFactor.test(10, 3));


        NumericTest lessThan = (n, m)->(n<m);

        if(lessThan.test(2, 10))
            System.out.println("2 is less than 10");
        if(lessThan.test(10, 2))
            System.out.println("10 is not less than 2");

        NumericTest absEqual = (n,m)->(n < 0 ? -n :n ) == (m < 0 ? -m : m);

        if(absEqual.test(4, -1))
            System.out.println("Absolute value of 4 and -4 are equal");
        if(absEqual.test(4, -5))
            System.out.println("Absolute value of 4 and -5 are not equal");

        StringTest isIn = (a, b)-> a.indexOf(b) != -1;

        String str = " This is a AvroToJson";

        System.out.println("Testing String: "+str);

        if(isIn.test(str, "is a"))
            System.out.println("'is a' found");
        else
            System.out.println("'is a' not found");

        if(isIn.test(str, "xyz"))
            System.out.println("'xyz' found");
        else
            System.out.println("'xyz' not found");
    }
}
