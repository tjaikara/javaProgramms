package applet;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*
<applet code="MouseDrawLine" width=300 height=80>
</applet>
 */

public class MouseDrawLine extends Applet implements MouseListener, MouseMotionListener {

    private int mouseX = 0, mouseY = 0,  x =0, y =0;
    private boolean draw;

    public void init(){
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void mouseClicked(MouseEvent e){
    }

    public void mousePressed(MouseEvent e){
        x = e.getX();
        y = e.getY();
        draw = true;
    }

    public void mouseReleased(MouseEvent e){
        draw = false;
    }

    public void mouseEntered(MouseEvent e){

    }

    public void mouseExited(MouseEvent e){

    }

    public void mouseDragged(MouseEvent e){
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    public void mouseMoved(MouseEvent e){
        showStatus("Moving mouse at "+e.getX()+", "+ e.getY());
    }

    public void paint(Graphics g){

        if(draw){
            g.setColor(Color.red);
            g.drawLine(x, y, mouseX,mouseY);
        }
    }
}

