package applet;
import java.awt.*;
import java.applet.*;


public class Banner  extends  Applet implements Runnable{

    String msg = " Java Rules the web";
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

        ch = msg.charAt(0);
        msg = msg.substring(1, msg.length());
        msg += ch;
        g.drawString(msg, 50, 30);
        showStatus("Displaying Scrolling Banner.");
    }

}
