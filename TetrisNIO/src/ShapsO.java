import java.awt.*;

public class ShapsO extends Shaps {

    public ShapsO(int x, int y) {
        this.color = Color.CYAN;
        for (int i = 0; i < 2; i++){
                listRect[i] = new Rect(x + i * 20, y , color);
                listRect[i+2] =  new Rect(x + i* 20, y+20, color);
            }
        moving=true;
        status = 0;

    }



    @Override
    public void rotate() {

    }


}
