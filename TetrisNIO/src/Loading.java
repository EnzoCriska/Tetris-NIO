import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Loading extends JPanel implements Commons, Runnable{

    private JLabel jLabel1;
    private JLabel jLabel2;

    Connection connection;
    String playerName;
    boolean stated;
    private JFrame parentFrame;

    public Loading(Connection connection, String name, JFrame parentFrame) {
        this.connection = connection;
        this.playerName = name;
        this.parentFrame = parentFrame;
        initComponents();
//        this.setVisible(true);
        byte[] TVLRegistePlay = connection.createMes(Register_Play, connection.createMes(PLAYER_1, playerName.getBytes()));
        try {
            connection.sendMes(TVLRegistePlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setBackground(Color.WHITE);
//        setFocusable(true);
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();

        jLabel1.setIcon(new javax.swing.ImageIcon("turningArrow.gif"));

        jLabel2.setFont(new java.awt.Font("Cantarell", 2, 18)); // NOI18N
        jLabel2.setText("Finding player 2...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jLabel1)
                .addContainerGap(221, Short.MAX_VALUE))
        );
    }




    @Override
    public void run() {
//        while (true){
////            if (!stated) {
////                stated = connection.isStart();
////                if (stated) {
//////                    Container frame = this.getParent();
//////                    Board board = new Board(connection, parentFrame);
//////
//////                    parentFrame.remove(this);
//////                    parentFrame.add(board);
//////                    new Thread(board).start();
//////                    TestMain testMain = new TestMain(connection, board);
//////                    new Thread(testMain).start();
//////                    testMain.setVisible(true);
//////                    testMain.setTitle(playerName +" competitor");
////                    parentFrame.setVisible(true);
////                    break;
////                }
////            }
//            repaint();
//        }
    }
}
