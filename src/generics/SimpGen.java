package generics;

public class SimpGen{

    public static void main(String args[]){
        GenericClass<Integer, String> tgObj =
                new GenericClass<Integer, String>(88, "Generics");

        tgObj.showTypes();

        int v = tgObj.getObj1();
        System.out.println("Value: "+v);

        String str= tgObj.getObj2();
        System.out.println("Value: "+ str);
    }
}
