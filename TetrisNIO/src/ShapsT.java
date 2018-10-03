import java.awt.*;

public class ShapsT extends Shaps {

    public ShapsT(int x, int y){
        this.color = Color.BLUE;
        for (int i =0; i<3; i++){
            listRect[i] = new Rect(x, y+i*20, color);
        }
        listRect[3] = new Rect(x+20, listRect[1].getY(), color);

        moving = true;
        status = 400;
    }


    @Override
    public void rotate() {
        status = Math.abs((status+1)%4);
        switch (status){
            case 1:
                listRect[2].moveUp();
                listRect[2].moveLeft();
                break;
            case 2:
                listRect[3].moveLeft();
                listRect[3].moveDown();
                break;
            case 3:
                listRect[0].moveDown();
                listRect[0].moveRight();
                break;
            case 0:
                listRect[0].moveLeft();
                listRect[0].moveUp();
                listRect[2].moveRight();
                listRect[2].moveDown();
                listRect[3].moveUp();
                listRect[3].moveRight();
                break;
        }

    }

}
