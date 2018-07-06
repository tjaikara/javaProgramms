package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

public class CompareFiles {

    JLabel jLabel;
    JLabel jLabelOne;
    JButton jButton;
    JTextField jTextField;
    JTextField jTextFieldOne;
    String firstFileName = "";
    String secondFileName = "";


    CompareFiles (){

        JFrame jFrame = new JFrame("Compare Files");
        jFrame.setSize(200, 120);
        jFrame.setLayout(new FlowLayout());
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jLabel = new JLabel("First File Name");
        jTextField = new JTextField("enter file name");
        jLabelOne = new JLabel("Second File Name");
        jTextFieldOne = new JTextField("enter file name");

        jButton = new JButton("Compare");
        jButton.addActionListener(new ButtonListner());

        jFrame.add(jLabel);
        jFrame.add(jTextField);
        jFrame.add(jLabelOne);
        jFrame.add(jTextFieldOne);
        jFrame.add(jButton);

        jFrame.setVisible(true);

    }


    class ButtonListner implements ActionListener{

        public void actionPerformed(ActionEvent ae){

            firstFileName = jTextField.getText();
            secondFileName = jTextFieldOne.getText();
            new Compare();
        }
    }

    class Compare implements Runnable{

        Thread thread;

        Compare(){
            thread = new Thread(this, "Compare Files");
            thread.start();
        }

        public void run(){

            int i, j;

            try (FileInputStream fin = new FileInputStream("/Users/taikara/myProjects/javaProgramms/data/"+firstFileName);
                 FileInputStream fin2 = new FileInputStream("/Users/taikara/myProjects/javaProgramms/data/"+secondFileName)){

                do{
                    i = fin.read();
                    j = fin2.read();

                    if(i != j){
                        break;
                    }
                }while (i!=-1 && j!= -1);

                if(i == j){
                    jButton.setText("Files are identical");
                    System.out.println("Files are identical");
                }
                else{
                    jButton.setText("Files are different");
                    System.out.println("Files are different");
                }
            }
            catch (IOException e){
                System.out.println("I/O Exception "+e);
            }
        }
    }
    public static void main(String [] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CompareFiles();
            }
        });
    }
}
