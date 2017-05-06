import processing.core.*;

public class MyPApplet extends PApplet {

    private String URL = "palmTrees.jpg";
    private PImage backGroundImg;

    public void setup(){
        size(800, 600);
        backGroundImg = loadImage(URL, "jpg");
    }

    public void draw(){

        backGroundImg.resize(0, height);
        image(backGroundImg, 0, 0);
    }
}
