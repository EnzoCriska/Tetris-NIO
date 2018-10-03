import java.awt.*;

public abstract class Map {
    int x, y;
    Color color;
    public Map(){

    }

    public Map(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract void moveLeft();
    public abstract void moveRight();
    public abstract void moveDown();
    public abstract void moveUp();

    public abstract void rotate();

    public abstract void draw(Graphics graphics);

}
