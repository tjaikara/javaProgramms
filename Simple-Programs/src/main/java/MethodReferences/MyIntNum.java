package MethodReferences;

class MyIntNum{
    private int v;

    MyIntNum(int x){ v =x;}

    int getNum(){return  v;}

    boolean isFactor(int n){
        return (v%n) == 0;
    }
}
