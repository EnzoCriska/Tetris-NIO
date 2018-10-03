package example;

import example.Shapes;
import example.Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

//import example.Shapes.Tetrominoes;

public class Board extends JPanel
        implements ActionListener, Runnable{

    private final int BoardWidth = 10;
    private final int BoardHeight = 22;

    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private JLabel statusbar;
    private Shapes curPiece;
    private Shapes.Tetrominoes[] board;
    Connection connection;


    public Board(Tetris parent) {
        connection = new Connection();
        initBoard(parent);
    }

    private void initBoard(Tetris parent) {

        setFocusable(true);
        curPiece = new Shapes();
        timer = new Timer(400, this);
        timer.start();

        statusbar =  parent.getStatusBar();
        board = new Shapes.Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (isFallingFinished) {

            isFallingFinished = false;
            newPiece();
        } else {

            oneLineDown();
        }
    }

    private int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }
    private int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }
    private Shapes.Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }


    public void start() throws IOException {

        if (isPaused)
            return;

        connection.sendMes(connection.createMes(Commons.LOGIN, "start".getBytes()));
        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    private void pause()  {

        if (!isStarted)
            return;

        isPaused = !isPaused;

        if (isPaused) {

            timer.stop();
            statusbar.setText("paused");
        } else {

            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }

        repaint();
    }

    private void doDrawing(Graphics g) {

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

        for (int i = 0; i < BoardHeight; ++i) {

            for (int j = 0; j < BoardWidth; ++j) {

                Shapes.Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);

                if (shape != Shapes.Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Shapes.Tetrominoes.NoShape) {

            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(), curPiece.getShape());
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {

            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }

        pieceDropped();
    }

    private void oneLineDown()  {

        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }


    private void clearBoard() {

        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Shapes.Tetrominoes.NoShape;
    }

    private void pieceDropped() {

        for (int i = 0; i < 4; ++i) {

            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    private void newPiece()  {

        curPiece.setRandomShape();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {

            curPiece.setShape(Shapes.Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("game over");
        }
    }

    private boolean tryMove(Shapes newPiece, int newX, int newY) {

        for (int i = 0; i < 4; ++i) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;

            if (shapeAt(x, y) != Shapes.Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }

    private void removeFullLines() {

        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Shapes.Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }
        if (numFullLines > 0) {

            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Shapes.Tetrominoes.NoShape);
            repaint();
        }
    }

    private void drawSquare(Graphics g, int x, int y, Shapes.Tetrominoes shape)  {

        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);

    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Shapes.Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {

                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;

                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;

                case 'A':
                    tryMove(curPiece.rotateRight(), curX, curY);
                    try {
                        connection.sendMes(connection.createMes(Commons.LOGIN, "move right".getBytes()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;

                case 'D':
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    try {
                        connection.sendMes(connection.createMes(Commons.LOGIN, "move left".getBytes()));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;

                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;

                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;

            }
        }
    }
}