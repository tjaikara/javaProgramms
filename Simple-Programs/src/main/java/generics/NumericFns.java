package generics;

class NumericFns<T extends Number>{
    T num;

    NumericFns(T n){
        num = n;
    }

    double reciprocal(){
        return 1/num.doubleValue();
    }

    double fraction(){
        return num.doubleValue() - num.intValue();
    }

    boolean absValue(NumericFns<?> obj){

        if(Math.abs(num.doubleValue()) == Math.abs(obj.num.doubleValue()))
            return true;

        return false;
    }
}
