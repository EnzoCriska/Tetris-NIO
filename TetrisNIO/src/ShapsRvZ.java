import java.awt.*;

public class ShapsRvZ extends Shaps {

    public ShapsRvZ(int x, int y){
        this.color = Color.ORANGE;
        for (int i = 0; i < 4; i++){
            if (i<2){
                listRect[i] = new Rect(x, y+i*20, color);
            }else{
                listRect[i] = new Rect(x+20, y+(i-1)*20, color);
            }
        }
        moving = true;
        status = 0;
    }

    @Override
    public void rotate() {
        status = Math.abs((status+1)%2);
        if (status == 1){
            listRect[0].moveRight();
            listRect[0].moveRight();
            listRect[1].moveRight();
            listRect[1].moveUp();
            listRect[3].moveUp();
            listRect[3].moveLeft();
        } else {
            listRect[0].moveLeft();
            listRect[0].moveLeft();
            listRect[1].moveDown();
            listRect[1].moveLeft();
            listRect[3].moveRight();
            listRect[3].moveDown();
        }
    }


}
