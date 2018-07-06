package applet;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

/*
<applet code="MouseTrack" width=300 height=80>
</applet>
 */

@SuppressWarnings("unchecked")
public class MouseTrack extends Applet implements MouseListener, MouseMotionListener {

    private boolean draw;
    private static int length =0;
    private static List drawBreak = new ArrayList<Integer>();
    private static List xCord = new ArrayList<Integer>();
    private static List yCord = new ArrayList<Integer>();

    public void init(){
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void mouseClicked(MouseEvent e){
    }

    public void mousePressed(MouseEvent e){
        xCord.add(e.getX());
        yCord.add(e.getY());
        draw = true;
    }

    public void mouseReleased(MouseEvent e){
        draw = false;
        length++;
        drawBreak.add(length);
    }

    public void mouseEntered(MouseEvent e){

    }

    public void mouseExited(MouseEvent e){

    }

    public void mouseDragged(MouseEvent e){
        length++;
        xCord.add(e.getX());
        yCord.add(e.getY());
        showStatus("Drawing at "+e.getX()+", "+ e.getY());
        repaint();
    }

    public void mouseMoved(MouseEvent e){
        showStatus("Moving mouse at "+e.getX()+", "+ e.getY());
    }

    public void paint(Graphics g){
        if(draw){
            for(int i = 0 ; i < xCord.size()-1; i++){
                g.setColor(Color.red);
                if(drawBreak.contains(i+1)){
                    i+=1;
                    g.drawLine((int)xCord.get(i), (int)yCord.get(i), (int)xCord.get(i+1), (int)yCord.get(i+1));
                }else{
                    g.drawLine((int)xCord.get(i), (int)yCord.get(i), (int)xCord.get(i+1), (int)yCord.get(i+1));
                }
            }
        }
    }
}

