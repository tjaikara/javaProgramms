import processing.core.*;

public class MyPApplet extends PApplet {

    private String URL = "/home/taikara/myProjects/javaProgramms/data/palmTrees.jpg";
    private PImage backGroundImg;

    public void setup(){
        size(400, 400);
        backGroundImg = loadImage(URL, "jpg");
    }

    public void draw(){

        backGroundImg.resize(0, height);
        image(backGroundImg, 0, 0);
        fill(255, 209, 0);
        ellipse(width/4, height/5, width/5, height/5);
    }
}
