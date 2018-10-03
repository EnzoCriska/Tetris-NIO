package example;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class Tetris extends JFrame {

    private JLabel statusbar;

    public Tetris() {

        try {
            initUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUI() throws IOException {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(200, 400);
        setTitle("example.Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JLabel getStatusBar() {

        return statusbar;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                Tetris game = new Tetris();
                game.setVisible(true);
            }
        });
    }
}