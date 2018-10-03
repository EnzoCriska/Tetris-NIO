import java.awt.*;

public class ShapsI extends Shaps{


    public ShapsI(int x, int y){
        this.color = Color.YELLOW;
        for (int i = 0; i<4; i++){
            this.listRect[i] = new Rect(x, y+i*20, color);
        }

        moving = true;
        status = 0;
    }


    @Override
    public void rotate() {
        status = Math.abs((status+1)%2);
        if (status == 1){
            listRect[0].setX(listRect[1].getX() - listRect[1].weith);
            listRect[0].setY(listRect[1].getY());

            listRect[2].setX(listRect[1].getX() + listRect[1].weith);
            listRect[2].setY(listRect[1].getY());

            listRect[3].setX(listRect[2].getX() + listRect[2].weith);
            listRect[3].setY(listRect[2].getY());

        } else {
            listRect[0].setX(listRect[1].getX());
            listRect[0].setY(listRect[1].getY() - listRect[1].weith);

            listRect[2].setX(listRect[1].getX());
            listRect[2].setY(listRect[1].getY() + listRect[1].weith);

            listRect[3].setX(listRect[2].getX());
            listRect[3].setY(listRect[2].getY() + listRect[1].weith);
        }
    }


}
