package applet;

import processing.core.*;

/*
<applet code="MyPApplet" width=300 height=80>
</applet>
 */

//public class MyPApplet extends PApplet {
//
//    private String URL = "/Users/taikara/myProjects/javaProgramms/data/palmTrees.jpg";
//    private PImage backGroundImg;
//
//    public void setup(){
//        size(400, 400);
//        backGroundImg = loadImage(URL, "jpg");
//    }
//
//    public void draw(){
//
//        backGroundImg.resize(0, height);
//        image(backGroundImg, 0, 0);
//
//        int [] fillColor = sunColor(second());
//        fill(fillColor[0], fillColor[1], fillColor[2]);
//
//        ellipse(width/4, height/5, width/5, height/5);
//    }
//
//    private int [] sunColor(float seccond) {
//
//        int[] sunColorVlaues = new int[3];
//
//        float diffFrom30 = Math.abs(30-seccond);
//
//        float ratio = diffFrom30/30;
//        sunColorVlaues[0]=(int)(255*ratio);
//        sunColorVlaues[1]=(int)(255*ratio);
//        sunColorVlaues[2]=0;
//
//        return sunColorVlaues;
//    }
//}
