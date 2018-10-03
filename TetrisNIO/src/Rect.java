import java.awt.*;
import java.sql.SQLOutput;

public class Rect extends Map{
    Color color;
    public final int weith = 20;

    public Rect(int x, int y, Color color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public void moveLeft() {
        this.setX(this.getX()-weith);
//        System.out.println("------Rect >>>");
    }

    @Override
    public void moveRight() {
        this.setX(this.getX()+weith);
//        System.out.println("------Rect <<<");
    }

    @Override
    public void moveDown() {
        this.setY(this.getY()+weith);
    }

    @Override
    public void moveUp() {
        this.setY(this.getY()-weith);
    }

    @Override
    public void rotate() {

    }


    @Override
    public void draw(Graphics graphics) {
        if (this.getY() > 0) {
            graphics.setColor(this.getColor());
//            graphics.drawRect(10, 10, 50, 50);
            graphics.fillRect(this.getX() - weith / 2, this.getY() - weith/ 2, weith, weith);
            graphics.setColor(Color.BLACK);
            graphics.drawLine(this.getX()-weith/2, this.getY()-weith/2, this.getX()-weith/2,this.getY()+weith/2);
            graphics.drawLine(this.getX()-weith/2,this.getY()+weith/2, this.getX() + weith / 2, this.getY() + weith / 2);
            graphics.drawRect(this.getX() - weith / 2, this.getY() - weith/ 2, weith, weith);

        }
    }

    public void draw(Graphics g, Color color){
        if (this.getY() > 0) {
            g.setColor(color);
//            graphics.drawRect(10, 10, 50, 50);
            g.fillRect(this.getX() - weith / 2, this.getY() - weith/ 2, weith, weith);
            g.setColor(Color.BLACK);
            g.drawLine(this.getX()-weith/2, this.getY()-weith/2, this.getX()-weith/2,this.getY()+weith/2);
            g.drawLine(this.getX()-weith/2,this.getY()+weith/2, this.getX() + weith / 2, this.getY() + weith / 2);
            g.drawRect(this.getX() - weith / 2, this.getY() - weith/ 2, weith, weith);

        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}