package swing;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class SwingList implements ListSelectionListener {

    JLabel jLabel;
    JList<String> jList;
    JScrollPane jScrollPane;

    String [] carNames = {"Audi", "BMW", "Benz", "Jaguar", "Toyota", "Honda", "Acura", "Infinity", "Volvo", "Charger", "Ford", "GMC"};

    SwingList(){

        JFrame jFrame = new JFrame("List Demo");
        jFrame.setLayout(new FlowLayout());
        jFrame.setSize(400, 260);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jList = new JList<String>(carNames);
        jList.addListSelectionListener(this);
        jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jScrollPane = new JScrollPane(jList);
        jScrollPane.setPreferredSize(new Dimension(320, 150));

        jLabel = new JLabel("Please choose a name");

        jFrame.add(jScrollPane);
        jFrame.add(jLabel);

        jFrame.setVisible(true);
    }

    public void valueChanged(ListSelectionEvent le){

        int [] selectedIndexes = jList.getSelectedIndices();
        String label = "";

        for (int i : selectedIndexes) {
            if(i != -1){
                label += carNames[i]+" ";
            }
        }

        if(!label.equals("")){
            jLabel.setText("Current selection/s: "+label);
        }
        else{
            jLabel.setText("Please choose a name");
        }

    }

    public static void main(String [] args){


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SwingList();
            }
        });
    }
}
