import sun.awt.windows.ThemeReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Board extends JPanel implements Commons, Runnable {
    Shaps shaps, shaps2;
    Thread thread;
    boolean newShaps = true, newshaps2 = true, ingame, win = true;
    Connection connection;
    ArrayList<Rect> listRectBoard, listRectBoard2;
    LinkedList<Shaps> subShaps = new LinkedList<>();
    LinkedList<Shaps> subShaps2 = new LinkedList<>();

    LinkedList<byte[]> movePress = new LinkedList<>();
    int Score, Score2;
    String IDPlayer, competitor, namePlayer;
    AdapterKey keyAdapter;
    int[][] matrix = new int[20][15];
    int[][] matrix2 = new int[20][15];
    JFrame parentFrame;



    public Board(Connection connection, JFrame parentFrame, String IDPlayer, String name, String competitorName){
        Score = 0;
        this.connection = connection;
        this.IDPlayer = IDPlayer;
        this.namePlayer = name;
        this.competitor = competitorName;
        this.parentFrame = parentFrame;
        parentFrame.addKeyListener(new AdapterKey());

        init();
//        this.setVisible(true);
        this.setFocusable(true);
    }

    public void addNotify(){
        super.addNotify();
        init();
    }

    public void init(){
        parentFrame.setSize(FRAME_WIDTH*2, FRAME_HEIGHT);
        for (int i = 0; i<20; i++)
            for (int j = 0; j <15; j++){
            matrix[i][j] = 0;
            matrix2[i][j]=0;
            }
        newShaps = true;
        newshaps2 = true;
        ingame = true;
        listRectBoard = new ArrayList<>();
        listRectBoard2 = new ArrayList<>();

    }

    public void paint(Graphics g){
        super.paint(g);
        if (shaps != null) shaps.draw(g);
        if (shaps2 != null) shaps2.draw(g);

        g.setColor(Color.MAGENTA);
        g.drawRect(10, 10, 300, 400);
        g.drawRect(320, 10, 300, 400);

        int i = 0;
        while(i<listRectBoard.size()){

                listRectBoard.get(i).draw(g);
                i++;
        }
        int j = 0;
        while(j<listRectBoard2.size()){

            listRectBoard2.get(j).draw(g);
            j++;
        }
        g.setColor(Color.GREEN);
        g.drawString("Player 1:", 100, 430);
        g.drawString(namePlayer + " score: " +Score, 30, 500);
        g.setColor(Color.RED);
        g.drawString("Player 2:", 420, 430);
        g.drawString(competitor + " score: " +Score2, 350, 500);
    }

    public void reciverShaps(String shaps){
        String shapsGet = shaps;

        if (shapsGet != null){
            Shaps subshaps = null, subshaps2 = null;
            switch (shapsGet){
                case "SHAPSO":
                    subshaps = new ShapsO(START_X, START_Y);
                    subshaps2 = new ShapsO(START_X+310, START_Y);
                    break;
                case "SHAPSI":
                    subshaps = new ShapsI(START_X, START_Y);
                    subshaps2 = new ShapsI(START_X+310, START_Y);
                    break;
                case "SHAPSL":
                    subshaps = new ShapsL(START_X, START_Y);
                    subshaps2 = new ShapsL(START_X+310, START_Y);
                    break;
                case "SHAPSRvL":
                    subshaps = new ShapsRvL(START_X, START_Y);
                    subshaps2 = new ShapsRvL(START_X+310, START_Y);
                    break;
                case "SHAPSZ":
                    subshaps = new ShapsZ(START_X, START_Y);
                    subshaps2 = new ShapsZ(START_X+310, START_Y);
                    break;
                case "SHAPSRvZ":
                    subshaps = new ShapsRvZ(START_X, START_Y);
                    subshaps2 = new ShapsRvZ(START_X+310, START_Y);
                    break;
                case "SHAPST":
                    subshaps = new ShapsT(START_X, START_Y);
                    subshaps2 = new ShapsT(START_X+310, START_Y);
                    break;
            }
            subShaps.add(subshaps);
            subShaps2.add(subshaps2);
        }
//        connection.genShaps = null;
    }

    public void randomShaps() throws IOException{
        byte[] newShapReqs = connection.createMes(NewShaps, connection.createMes(NewShaps, "newshaps".getBytes()));
        connection.sendMes(newShapReqs);
        if (subShaps.size()>0){
            shaps = subShaps.poll();
            new Thread(shaps).start();
            newShaps = false;
        }
    }

    public void randomShaps2(){
        if (subShaps2.size()>0){
            shaps2 = subShaps2.poll();
            new Thread(shaps2).start();
            newshaps2 = false;
        }
    }

    public void game() throws IOException {
        if (newShaps) randomShaps();
        if (newshaps2) randomShaps2();

        if (shaps != null){
            for (Rect rectShap : shaps.getListRect()){
                if (shaps.getShapYmax() >= 450 - 50){
                    addBoardRect(shaps.getListRect());
                    shaps.setMoving(false);
                    newShaps = true;
                    break;
                }else{
                    int i;
                    for (i = 0; i <listRectBoard.size(); i++){
                        Rect rectBoard = listRectBoard.get(i);
                        if (rectShap.getX() == rectBoard.getX()){
                            if (rectShap.getY()+20 == rectBoard.getY()){
                                addBoardRect(shaps.getListRect());
                                shaps.setMoving(false);
                                newShaps = true;
                                break;
                            }
                        }
                    }
                    if (i < listRectBoard.size()-1){
                        break;
                    }
                }
            }
        }

        if (shaps2 != null){
            for (Rect rectShap : shaps2.getListRect()){
                if (shaps2.getShapYmax() >= 450 - 50){
                    addBoardRect2(shaps2.getListRect());

                    shaps2.setMoving(false);
                    newshaps2 = true;
                    break;
                }else{
                    int i;
                    for (i = 0; i <listRectBoard2.size(); i++){
                        Rect rectBoard = listRectBoard2.get(i);
                        if (rectShap.getX() == rectBoard.getX()){
                            if (rectShap.getY()+20 == rectBoard.getY()){
                                addBoardRect2(shaps2.getListRect());
                                shaps2.setMoving(false);
                                newshaps2 = true;
                                break;
                            }
                        }
                    }
                    if (i < listRectBoard2.size()-1){
                        break;
                    }
                }
            }
        }

        for (int i = 0; i <15; i++){
            if (matrix[1][i] == 1){
                System.out.println("Game over");
                win = false;
                byte[] gameOver = connection.createMes(GAME_OVER, connection.createMes(GAME_OVER, namePlayer.getBytes()));
                connection.sendMes(gameOver);
                connection.sendMes(connection.createMes(SCORE, connection.createMes(SCORE, String.valueOf(Score).getBytes())));
                ingame = false;
            }
        }
    }

    public void addBoardRect(ArrayList<Rect> list){
        for (int i = 0; i < 4; i++){
            listRectBoard.add(list.get(i));
            matrix[list.get(i).getY()/20-1][list.get(i).getX()/20-1] = 1;
        }
//        System.out.println(listRectBoard.size());
        removeFullLine(checkFullLine(matrix), listRectBoard, matrix, 1);
//        drawMatrix();
    }

    public void addBoardRect2(ArrayList<Rect> list){
        for (int i = 0; i < 4; i++){
            listRectBoard2.add(list.get(i));
//            System.out.println(list.get(i).getY()/20 +":"+ list.get(i).getX()/20);
            matrix2[list.get(i).getY()/20-1][list.get(i).getX()/20-16] = 1;
        }
//        System.out.println(listRectBoard2.size());
        removeFullLine(checkFullLine(matrix2), listRectBoard2, matrix2, 2);
//        drawMatrix();
    }

    public int checkFullLine(int[][] matrixx){
        int full = -1;
        for (int i = 19; i>=0; i--){
            int Sum = 0 ;
            for (int j = 0; j < 15; j++){
                Sum += matrixx[i][j];
            }
            if (Sum == 15){
                full = i;
                break;
            }
        }
        return full;
    }

    public void drawMatrix(){
        System.out.println("-----");
        for (int i = 0; i <20; i++) {
            for (int j = 0; j < 15; j++){
                System.out.print(matrix[i][j] +"\t");
            }
            System.out.println();
        }
    }


    public synchronized void removeFullLine(int full, ArrayList<Rect> listRectB, int[][] matrixx , int scoses){
        int xRemove = 0, yRemove;
        LinkedList<Rect> listRemove = new LinkedList<>();
        LinkedList<Integer> listMoveDown = new LinkedList<>();

        yRemove = full;
        if (full != -1){

            for (int i = 0; i< listRectB.size(); i++){
                int yRectList = listRectB.get(i).getY()/20-1;
                if (full == yRectList){
                    xRemove = listRectB.get(i).getX()/20-1;
                    if (xRemove >= 15) {
                        xRemove -= 15;
                    }

                    listRemove.add(listRectB.get(i));
                    for (int k = yRemove; k>0; k--){
//                        System.out.println(k +","+xRemove);
                        matrixx[k][xRemove] = matrixx[k-1][xRemove];
                    }
//                    scoses++;
                }
            }

            while (listRemove.size()>0){
                listRectB.remove(listRemove.pollLast());
            }
            scoses++;

            for (int i = 0; i< listRectB.size(); i++){
                int yRectList = listRectB.get(i).getY()/20-1;
                if (full-1 >= yRectList){
                    listMoveDown.add(i);
                }
            }
            while (listMoveDown.size() > 0){
                listRectB.get(listMoveDown.poll()).moveDown();
            }

            if (scoses == 1){
                Score++;
            }else Score2++;
        }
    }

        public void listend(){
        if (connection.listMove.size()>0){
            String move = connection.listMove.poll();
//            System.out.println(">>>>>>>>" + move);
            boolean access = true;
            switch (move) {
                case "Rotate":
                    shaps2.rotate();
                    connection.move = null;
                    break;
                case "MoveLeft":
                    for (Rect rect : listRectBoard2) {
                        for (Rect rectShaps : shaps2.getListRect()) {
                            if (rectShaps.getX() - 40 < rect.getX() &&
                                    ((rectShaps.getY() + 20 >= rect.getY() && rectShaps.getY() + 20 <= rect.getY() + 20) ||
                                            (rectShaps.getY() >= rect.getY() && rectShaps.getY() <= rect.getY() + 20))) {
                                access = false;
                                break;
                            }
                        }
                    }
                    if (access) {
                        shaps2.moveLeft();
                        connection.move = null;
                        break;
                    }
                    break;
                case "MoveRight":
                    for (Rect rect : listRectBoard2) {
                        for (Rect rectShaps : shaps2.getListRect()) {
                            if ((rectShaps.getX() + 40 > rect.getX()) && ((rectShaps.getY() + 20 >= rect.getY() && rectShaps.getY() + 20 <= rect.getY() + 20) ||
                                    (rectShaps.getY() >= rect.getY() && rectShaps.getY() <= rect.getY() + 20))) {
                                access = false;
                                break;
                            }
                        }
                    }
                    if (access) {
                        shaps2.moveRight();
                        connection.move = null;
                        break;
                    }
                    break;
            }
        }

    }

    public void gameOver(){
        Graphics graphics = this.getGraphics();
//        super.paint(graphics);

        graphics.setColor(Color.getHSBColor(12,12, 12));
        graphics.fillRect(250, 110, 330, 50);
        graphics.setColor(Color.RED);
        graphics.drawRect(250, 110, 330, 50);
        graphics.setColor(Color.GREEN);
        graphics.drawString("Game Over", 300, 130);
        if (win) {
            graphics.drawString("You Win \n Your Score: " + Score +"\n Competitor's Score: "+ Score2,260, 150 );
        }else{
            graphics.drawString("You Lose\n Your Score: " + Score +"\n Competitor's Score: "+ Score2, 260, 150);
        }

    }

    @Override
    public void run() {
        System.out.println("Game Start ---");
        while (ingame){
            try {
                while (movePress.size()>0){
                    connection.sendMes(movePress.poll());
                }
//                reciverShaps();
                game();
                repaint();
//                listend();

                Thread.sleep(100);
            }catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public class AdapterKey extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            byte[] message = new byte[0];
            boolean access = true;
            switch (keyEvent.getKeyCode()){
                case KeyEvent.VK_SPACE:
                    shaps.rotate();
//                    message = connection.createMes(namePlayer, connection.createMes(ROTATE, "Rotate".getBytes()));
                    byte[] nameMove = connection.createMes(PLAYER_1, namePlayer.getBytes());
                    byte[] moveBytes = connection.createMes(ROTATE, "Rotate".getBytes());
                    movePress.add(connection.createMes(MOVE_ROTATE, nameMove, moveBytes));
                    break;
                case KeyEvent.VK_LEFT:
                    for (Rect rect : listRectBoard){
                        for (Rect rectShaps : shaps.getListRect()){
                            if (rectShaps.getX()-40 < rect.getX() &&
                                    ( (rectShaps.getY()+20 >= rect.getY() && rectShaps.getY()+20 <= rect.getY()+20) ||
                                                    (rectShaps.getY() >= rect.getY() && rectShaps.getY() <= rect.getY()+20))){
                                access = false;
                                break;
                            }
                        }
                    }
                    if (access){
                        shaps.moveLeft();
//                        message = connection.createMes(namePlayer, connection.createMes(MOVE_LEFT, "MoveLeft".getBytes()));
                        byte[] nameMoveLeft = connection.createMes(PLAYER_1, namePlayer.getBytes());
                        byte[] moveLBytes = connection.createMes(MOVE_LEFT, "MoveLeft".getBytes());
                         movePress.add(connection.createMes(MOVE_ROTATE, nameMoveLeft, moveLBytes));
                        break;
                    }


                case KeyEvent.VK_RIGHT:
                    for (Rect rect : listRectBoard){
                        for (Rect rectShaps : shaps.getListRect()){
                            if ((rectShaps.getX()+40 > rect.getX()) && ((rectShaps.getY()+20 >= rect.getY() && rectShaps.getY()+20 <= rect.getY()+20) ||
                                    (rectShaps.getY() >= rect.getY() && rectShaps.getY() <= rect.getY()+20))){
                                access = false;
                                break;
                            }
                        }
                    }
                    if (access){
                        shaps.moveRight();
//                        message = connection.createMes(namePlayer, connection.createMes(MOVE_RIGHT, "MoveRight".getBytes()));
                        byte[] nameMoveRight = connection.createMes(PLAYER_1, namePlayer.getBytes());
                        byte[] moveRBytes = connection.createMes(MOVE_RIGHT, "MoveRight".getBytes());
                        movePress.add(connection.createMes(MOVE_ROTATE, nameMoveRight, moveRBytes));
                        break;
                    }


                case KeyEvent.VK_DOWN:
                    shaps.moveDown();
                    break;


            }

//            try {
//                connection.sendMes(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            repaint();
//            System.out.println(shaps.listRect[0].getX() +":"+shaps.listRect[0].getY());

        }
    }
}
