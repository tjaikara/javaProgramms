
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WriteFileDifferenceToFile {

    public static void main(String[] args) throws IOException {

        System.out.println("Please input files to compare seperated with space");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        input = "updatedCompanies.txt AllCompanies.txt";
        String input1 [] = input.split(" ");

        List<String> fileOneData = new ArrayList<>();
        List<String> fileTwoData = new ArrayList<>();

        try(BufferedReader fin1 = new BufferedReader(new FileReader("/Users/taikara/Desktop/"+input1[0]));
            BufferedReader fin2 = new BufferedReader(new FileReader("/Users/taikara/Desktop/"+input1[1]))){

            String data;
            while ((data = fin1.readLine()) != null){
                fileOneData.add(data);
            }
            while ((data = fin2.readLine()) != null){
                fileTwoData.add(data);
            }
        }

        String Path = System.getProperty("user.dir");
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Path+"/fileDifference.txt"))){

            for(String s: fileTwoData){
                if(!fileOneData.contains(s)){
                    bufferedWriter.write(s);
                    bufferedWriter.newLine();
                }
            }
        }
    }
}
