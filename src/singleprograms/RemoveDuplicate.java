package singleprograms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taikara on 4/6/17.
 */
public class RemoveDuplicate
{

    public static void main(String[] args){

        double [] doubleList = {2.0, 2.0,3.0,12.5, 13.5, 12.5, 20.0, 25.0, 25.0, 2.0, 2.0,3.0,12.5, 13.5, 12.5, 20.0, 25.0, 25.0, 2.0, 2.0,3.0,12.5, 2.5, 2.5, 0.0, 5.0, 5.0};

        double [] newDoubleList = removeDuplicateMethod1(doubleList);

        for(int i=0; i < newDoubleList.length; i++){
            System.out.println(newDoubleList[i]);
        }
    }


    private static double[] removeDuplicateMethod1(double []  list){

        List<Double> doubleList = new ArrayList<>();
        for(int i=0; i < list.length;i++ ){
            if(!doubleList.contains(list[i])){
                doubleList.add(list[i]);
            }
        }

        double [] returnArray = new double[doubleList.size()];
        for(int j=0; j < doubleList.size(); j++){
            returnArray[j] = doubleList.get(j);
        }
        return returnArray;
    }

    private  static double [] removeDuplicateMethod2(double [] list){

        double [] values = new double[list.length];
        boolean contains = false;
        int k = 0;

        for(int i =0 ; i < list.length; i++){
            for (int j = 0; j < k+1 ; j++){
                if(values[j] == list[i]){
                    contains = true;
                }
            }
            if(!contains){
                values[k] = list[i];
                k++;
            }
            contains = false;
        }

        double [] returnValues = new double[k];
        for(int n = 0; n < k; n++){
            returnValues[n] = values[n];
        }

        return  returnValues;
    }
}
