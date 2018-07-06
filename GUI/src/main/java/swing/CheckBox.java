package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CheckBox implements ItemListener {

    private JLabel labelOne;
    private JLabel labelTwo;
    JCheckBox checkBoxAlpha;
    JCheckBox checkBoxGamma;
    JCheckBox checkBoxBetta;
    private int mask = 0;


    CheckBox(){

        JFrame jFrame = new JFrame("Check Box Demo");
        jFrame.setLayout(new FlowLayout());
        jFrame.setSize(300, 150);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        checkBoxAlpha = new JCheckBox("Alpha");
        checkBoxAlpha.addItemListener(this);

        checkBoxGamma = new JCheckBox("Beta");
        checkBoxGamma.addItemListener(this);

        checkBoxBetta = new JCheckBox("Gamma");
        checkBoxBetta.addItemListener(this);

        labelOne = new JLabel("Please Select One");
        labelTwo = new JLabel("");

        jFrame.add(checkBoxAlpha);
        jFrame.add(checkBoxGamma);
        jFrame.add(checkBoxBetta);
        jFrame.add(labelOne);
        jFrame.add(labelTwo);

        jFrame.setVisible(true);
    }

    public void itemStateChanged(ItemEvent ie){

        if(ie.getItem() instanceof JCheckBox){
           new CheckBoxThread((JCheckBox) ie.getItem());
        }
    }

    class CheckBoxThread implements Runnable{

        Thread thread;
        JCheckBox jCheckBox3;

        CheckBoxThread(JCheckBox jCheckBox4){
            jCheckBox3 = jCheckBox4;
            thread = new Thread(this,"Check box action thread");
            thread.start();
        }

        public void run(){

            String toDisplay = "";
            System.out.println(thread.getName() + " starting..");

            if(jCheckBox3.isSelected()){
                labelOne.setText(jCheckBox3.getText() + " just selected.");
            }
            else{
                labelOne.setText(jCheckBox3.getText() + " just unchecked.");
            }


            if(checkBoxAlpha.isSelected()){
                toDisplay += checkBoxAlpha.getText()+" ";
            }
            if(checkBoxBetta.isSelected()){
                toDisplay += checkBoxBetta.getText()+" ";
            }
            if(checkBoxGamma.isSelected()){
                toDisplay += checkBoxGamma.getText()+" ";
            }

            labelTwo.setText("Selected Check Boxes: "+toDisplay);
        }
    }

    public static void main (String [] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CheckBox();
            }
        });
    }
}
