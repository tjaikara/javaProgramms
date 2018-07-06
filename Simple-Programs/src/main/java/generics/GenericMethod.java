package generics;

public class GenericMethod {

    public static void main(String [] args){

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
