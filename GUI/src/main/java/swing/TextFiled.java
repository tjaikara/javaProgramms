package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextFiled implements ActionListener {

    JLabel  jLabel;
    JLabel jLabel1;
    JButton jButton;
    JTextField jTextField;

    TextFiled(){

        JFrame jFrame = new JFrame("Reverse a Text");
        jFrame.setSize(200, 120);
        jFrame.setLayout(new FlowLayout());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jLabel1 = new JLabel("Enter Text: ");
        jTextField = new JTextField("Text");
        jTextField.setActionCommand("Enter");
        jTextField.addActionListener(this);

        jButton = new JButton("Reverse");
        jButton.addActionListener(this);

        jLabel = new JLabel("Please Enter a Text and Press Reverse Button");

        jFrame.add(jLabel1);
        jFrame.add(jTextField);
        jFrame.add(jButton);
        jFrame.add(jLabel);

        jFrame.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae){

        if(ae.getActionCommand().equals("Enter")){
            jLabel.setText("You pressed ENTER. Text is: "+jTextField.getText());
        }
        else{
            new Reverse();
        }
    }

    class Reverse implements Runnable{

        Thread thread;

        Reverse(){
            thread = new Thread(this, "Reverse Text Thread");
            thread.start();
        }

        public void run(){

            System.out.println("Starting "+thread.getName());
            String text = jTextField.getText();
            String reverseText = "";

            for(int i = text.length()-1; i >= 0; i--){
                reverseText += text.charAt(i);
            }

            jLabel.setText("You pressed Reverse button. Reversed Text is: "+reverseText);
        }
    }

    public static void main (String [] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TextFiled();
            }
        });
    }
}
