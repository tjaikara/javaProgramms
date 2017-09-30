package applet;

import java.applet.Applet;
import java.awt.*;
import java.util.Calendar;

/*
<applet code="DisplayTime" width=300 height=80>
</applet>
 */

public class DisplayTime extends Applet implements Runnable{

    String message= "";
    Thread t;
    boolean stopFlag;

    public void init(){
        t = null;
    }

    public void start(){

        t = new Thread(this);
        stopFlag = false;
        t.start();
    }

    public void run(){


        for(;;){
            try{
                Calendar calendar = Calendar.getInstance();
                message = calendar.getTime().toString();
                repaint();
                Thread.sleep(1000);
                if(stopFlag)
                    break;
            }
            catch (InterruptedException e){

            }
        }
    }
    public void stop(){

        stopFlag = true;
        t = null;
    }

    public void paint(Graphics g){

        g.drawString(message, 50, 30);
        showStatus("Displaying Current Local Time");
    }


}
