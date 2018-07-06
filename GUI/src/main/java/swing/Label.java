package swing;

import javax.swing.*;

public class Label {

    Label(){

        //Create a new JFrame container
        JFrame jFrame = new JFrame("A Simple Swing Application");

        //Give the frame an initial size
        jFrame.setSize(275, 100);

        //Terminate the program when the user closes the application
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create a text-based label
        JLabel jLabel = new JLabel(" Swing defines the modern Java GUI");

        //Add the label to the content pane
        jFrame.add(jLabel);

        //Display the frame
        jFrame.setVisible(true);
    }

    public static void main(String args []){

        //Create the frame on the event dispatching thread
        SwingUtilities.invokeLater(new Runnable(){
           public void run(){
               new Label();
           }
        });
    }
}
