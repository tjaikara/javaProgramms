package generics;

class GenericClass<T, V> {

    T obj1;
    V obj2;

    GenericClass(T o1, V o2){
        obj1 = o1;
        obj2 = o2;
    }

    void showTypes(){
        System.out.println("Type of T is "+
                    obj1.getClass().getName());
        System.out.println("Type of V is" +
                obj2.getClass().getName());
    }

    T getObj1(){
        return obj1;
    }

    V getObj2(){
        return obj2;
    }
}
