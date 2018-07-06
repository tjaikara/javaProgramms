package swing;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Button {

    JLabel jLabel;

    Button(){

        JFrame jFrame = new JFrame("Button Demo");
        jFrame.setSize(220, 90);
        jFrame.setLayout(new FlowLayout());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton jButton = new JButton("Up");
        JButton jButton1 = new JButton("Down");

        jButton.addActionListener(new Listner());
        jButton1.addActionListener(new Listner());

        jLabel = new JLabel("Press Button");

        jFrame.add(jButton);
        jFrame.add(jButton1);
        jFrame.add(jLabel);

        jFrame.setVisible(true);
    }

    class Listner implements ActionListener{

        public void actionPerformed(ActionEvent ae){

            if(ae.getActionCommand().equals("Up")){
                jLabel.setText("Buttom Up Pressed");
            }
            else if(ae.getActionCommand().equals("Down")){
                jLabel.setText("Buttom Down Pressed");
            }
        }
    }

    public static void main(String [] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Button();
            }
        });
    }
}
