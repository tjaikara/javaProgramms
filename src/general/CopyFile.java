package general;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by taikara on 6/19/17.
 */
public class CopyFile {


    public static void main(String[] args) throws IOException{

        String filToCopyFrom = "";
        String fileTOCopyTo = "";

        System.out.println("Please input file name to copy from...");
        Scanner scanner = new Scanner(System.in);
        filToCopyFrom = scanner.nextLine();
        System.out.println(filToCopyFrom);

        System.out.println("Please input file name to copy to...");
        scanner = new Scanner(System.in);
        fileTOCopyTo = scanner.nextLine();
        System.out.println(fileTOCopyTo);

        int i;
        try(FileInputStream fin = new FileInputStream("/Users/taikara/myProjects/javaProgramms/data/"+filToCopyFrom); FileOutputStream fout = new FileOutputStream("/Users/taikara/myProjects/javaProgramms/data/"+fileTOCopyTo)){
            do{
                i = fin.read();
                if(i != -1 ){
                        fout.write(i);
                }
            }while (i!=-1);

        }catch (IOException e){

            System.out.println("I/O Exception:"+e);

        }
    }
}
