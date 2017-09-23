package singleprograms;

import java.util.ArrayList;
import java.util.List;

public class BitManipulation {

    public static void main(String[] args){

        List<Integer> setbid = getSetBits(19);
        for(Integer integer : setbid){
            System.out.println("Bit "+ integer + " is set.");
        }
        if(isSet(262144, 25))
            System.out.println("Bit 25 is set");
        else
            System.out.println("Bit 25 is not set");
    }

    public static List<Integer> getSetBits(int mask){
        List<Integer> setbits = new ArrayList<Integer>();
        int i =0;

        while (mask != 0){

            if((mask & 1) != 0)
                setbits.add(i);

            i++;
            mask >>= 1;

        }
        return setbits;
    }

    public static boolean isSet(int mask, int bit){

        mask >>= bit;
        return (mask & 1) !=0 ;

    }
}
