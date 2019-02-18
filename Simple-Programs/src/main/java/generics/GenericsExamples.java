package generics;

public class GenericsExamples {

    public static void main(String args[]){


        /**
         * Use of generic constructor
         */
        Summation ob = new Summation(4.0);
        System.out.println("Summation of 4.0 is "+ ob.getSum());

        /**
         * Use of generic interface
         */
        Integer x[] ={ 1, 2,3};

        MyClass<Integer> obj = new MyClass<Integer>(x);

        if(obj.contains(2))
            System.out.println("2 is in the obj");
        else
            System.out.println("2 is not in the obj");

        if(obj.contains(5))
            System.out.println("5 is in the obj");
        else
            System.out.println("5 is not in the obj");

        /**
         * Use of generic bound types
         */
        NumericFns<Integer> iOb= new NumericFns<Integer>(5);

        System.out.println("Reciprocal of 5: "+ iOb.reciprocal());

        System.out.println("Fractional component of iOb is "+
                iOb.fraction());

        System.out.println();

        NumericFns<Double> dObj = new NumericFns<Double>(5.25);

        System.out.println("Reciprocal of 5: "+ dObj.reciprocal());

        System.out.println("Fractional component of iOb is "+
                dObj.fraction());

        /**
         * generic methods
         */
        Integer nums [] = {1, 2, 3, 4, 5};
        Integer nums1 [] = {1, 2, 3, 4, 5};
        Integer nums2 [] = {1, 2, 3, 4, 5};
        Integer nums3 [] = {1, 2, 3, 4, 5};
        Integer nums4 [] = {1, 2, 3, 4, 5, 6};

        if(arraysEqual(nums, nums)){
            System.out.println("nums equals nums");
        }

        if(arraysEqual(nums, nums2)){
            System.out.println("nums equals nums2");
        }

        if(arraysEqual(nums, nums2)){
            System.out.println("nums equals nums3");
        }

        if(arraysEqual(nums, nums3)){
            System.out.println("nums ae equals");
        }

        /**
         * Use of generic wild cards
         */
        iOb = new NumericFns<Integer>(6);

        NumericFns<Double> dOb = new NumericFns<Double>(-6.0);

        NumericFns<Long> lOb = new NumericFns<Long>(5L);

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


        /**
         * Use of generic class
         */
        GenericClass<Integer, String> tgObj = new GenericClass<Integer, String>(88, "Generics");
        tgObj.showTypes();

        int v = tgObj.getObj1();
        System.out.println("Value: "+v);

        String str= tgObj.getObj2();
        System.out.println("Value: "+ str);
    }

    static < T extends  Comparable<T>, V extends T> boolean arraysEqual(T[] x, V[] y){
        if(x.length != y.length){
            return false;
        }

        for(int i =0; i < x.length; i++){
            if(!x[i].equals(y[i])){
                return  false;
            }
        }
        return true;
    }
}

