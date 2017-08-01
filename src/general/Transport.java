package general;

/**
 * Created by taikara on 7/18/17.
 */


enum Transport{
    CAR(65), AIRPLANE(600), TRUCK(55), TRAIN(70), BOAT(22);

    private int speed;

    Transport(int s){
        speed = s;
    }

    int getSpeed(){
        return speed;
    }
}
