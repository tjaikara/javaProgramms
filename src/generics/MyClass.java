package generics;

class MyClass<T> implements  Containment <T>{ //Any class that implements a generic interface must itself be generic

    T[] array;

    MyClass(T[] o){
        array = o;
    }

    public boolean contains(T o){
        for ( T x : array){
            if(x.equals(o))
                return  true;
        }
        return  false;
    }
}
