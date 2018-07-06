package applet;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.applet.*;

/*
<applet code="Banner" width=300 height=80>
<param name=message value="Java Rules the web">
<param name=timeDelay value=250>
</applet>
 */

public class Banner  extends  Applet implements Runnable{

    String message;
    long timeDelay;
    Thread t;
    boolean stopFlag;

    public void init(){
        t = null;
    }

    public void start(){

        message = getParameter("message");
        if(message == null) message = " not found";

        String temp = getParameter("timeDelay");
        if(temp != null){
            timeDelay = NumberUtils.toLong(temp);
        }
        else{
            timeDelay = 0;
        }

        t = new Thread(this);
        stopFlag = false;
        t.start();
    }

    public void run(){
        for( ; ;){
            try{
                repaint();
                Thread.sleep(250);
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

    public void paint( Graphics g){
        char ch;

        ch = message.charAt(0);
        message = message.substring(1, message.length());
        message += ch;
        g.drawString(message, 50, 30);
        showStatus("Displaying Scrolling Banner.");
    }

}
