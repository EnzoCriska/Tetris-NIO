import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public abstract class Shaps extends Map implements Runnable{
    Rect[] listRect  =new Rect[4];
    String type;
    boolean moving;
    int status;
//
//    public Shaps(){
//
//    };

    @Override
    public void moveLeft() {
        boolean access = true;
        for (int i = 0; i <4; i++){
            if (listRect[i].getX()-20 < 20){
                access = false;
                break;
            }
        }
        if (access){
            for (int i = 0; i<4; i++){
                listRect[i].moveLeft();
            }
        }
    }

    @Override
    public void moveRight() {
        boolean access = true;
        for (int i = 0; i <4; i++){
            if (((listRect[i].getX()+ 20 > 300) && (listRect[i].getX() < 320)) || listRect[i].getX()+20 >630){
                access = false;
                break;
            }
        }
        if (access){
            for (int i = 0; i<4; i++){
                listRect[i].moveRight();
            }
        }

    }

    @Override
    public void moveDown() {
        boolean access = true;
        for (int i = 0; i <4; i++){
            if (listRect[i].getY()+ 20 > 400){
                access = false;
                break;
            }
        }
        if (access){
            for (int i = 0; i<4; i++){
                listRect[i].moveDown();
            }
        }
    }

    @Override
    public void moveUp(){
        for (int i = 0; i<4; i++){
            listRect[i].moveUp();
        }
    }


    @Override
    public void draw(Graphics graphics) {
        for (int i = 0; i<4; i++){
            listRect[i].draw(graphics);
        }
    }

    public ArrayList<Rect> getListRect() {
        ArrayList<Rect> list = new ArrayList<>();
        Collections.addAll(list, listRect);
        return list;
    }

    public void setListRect(Rect[] listRect) {
        this.listRect = listRect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getShapYmax(){
        int max = listRect[0].getY();
        for (int i = 0; i<4; i++){
            if (listRect[i].getY() > max){
                max = listRect[i].getY();
            }
        }
        return max;
    }

    public int getShapYmin(){
        int min = listRect[0].getY();
        for (int i = 0; i<4; i++){
            if (listRect[i].getY() < min){
                min = listRect[i].getY();
            }
        }
        return min;
    }

    @Override
    public void run(){
        while (moving){
            this.moveDown();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}