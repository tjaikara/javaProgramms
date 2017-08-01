package generics;


public class GenericInterface{
    public static void main(String [] args){

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

    }

}