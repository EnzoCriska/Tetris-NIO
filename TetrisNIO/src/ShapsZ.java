import java.awt.*;

public class ShapsZ extends Shaps {

    public ShapsZ(int x, int y){
        this.color = Color.DARK_GRAY;
        for (int i = 0; i < 4; i++){
            if (i<2){
                listRect[i] = new Rect(x, y+i*20, color);
            }else{
                listRect[i] = new Rect(x-20, y+(i-1)*20, color);
            }
        }
        moving = true;
        status = 0;
    }
    @Override
    public void rotate() {
        status = Math.abs((status+1)%2);
        if (status == 1){
           listRect[0].moveDown();
           listRect[0].moveRight();
           listRect[3].moveUp();
           listRect[3].moveUp();
           listRect[2].moveRight();
           listRect[2].moveUp();

        } else {
           listRect[3].moveDown();
           listRect[3].moveDown();
           listRect[2].moveDown();
           listRect[2].moveLeft();
           listRect[0].moveLeft();
           listRect[0].moveUp();
        }
    }


}
