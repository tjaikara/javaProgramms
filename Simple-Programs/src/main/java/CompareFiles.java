

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by taikara on 6/19/17.
 */
public class CompareFiles {
    public static void main(String[] args){

        int i, j;
        System.out.println("Please input files to compare seperated with space");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String input1 [] = input.split(" ");

        try (FileInputStream fin = new FileInputStream("data/"+input1[0]);
             FileInputStream fin2 = new FileInputStream("data/"+input1[1])){

            do{
                i = fin.read();
                j = fin2.read();

                if(i != j){
                   break;
                }
            }while (i!=-1 && j!= -1);

            if(i == j){
                System.out.println("Files are identical");
            }
            else{
                System.out.println("Files are different");
            }
        }
        catch (IOException e){
                 System.out.println("I/O Exception "+e);
        }

    }
}
