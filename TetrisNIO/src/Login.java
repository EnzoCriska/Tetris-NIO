import example.Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class Login extends JPanel implements Commons{

    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JButton login, registerbtn;
    private JPasswordField pass;
    private JLabel register;
    private JTextField user;
    private String userN;

    Connection connection;
    private boolean loged;
    private JFrame parentFrame;

    public Login(Connection connection, JFrame parentFrame){
        this.connection = connection;
        this.parentFrame = parentFrame;
        initComponents();

    }

    private void initComponents() {
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setFocusable(true);
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        user = new JTextField();
        login = new JButton();
        registerbtn = new JButton();
        pass = new JPasswordField();
        register = new JLabel();

        jLabel1.setFont(new java.awt.Font("Cantarell", 2, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Login");
        jLabel1.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel2.setText("User Name");

        jLabel3.setText("Pass Word");

        login.setText("Login");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        registerbtn.setText("Register");
        registerbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                registerbtnActionPerformed(evt);
            }
        });
        pass.setText("");

        register.setFont(new java.awt.Font("Cantarell", 2, 12));
        register.setForeground(new java.awt.Color(0, 33, 255));
        register.setText("register if you haven't account");


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(100, 100, 100)
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel3))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(register, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(user)
                                                                .addComponent(login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(registerbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(pass, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)))))
                                .addContainerGap(80, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addComponent(register)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(login)
                                .addContainerGap(320, Short.MAX_VALUE)
                                .addComponent(registerbtn)
                                .addContainerGap(550, Short.MAX_VALUE))
        );
    }

    private void registerbtnActionPerformed(ActionEvent evt){
        try {
            userN = user.getText();
            String passW = pass.getText();
            byte[] registerMessage = connection.createMes(REGISTER, connection.createMes(USER_NAME, userN.getBytes()), connection.createMes(PASS_WORD, passW.getBytes()));

            ByteBuffer byteBuffer = ByteBuffer.wrap(registerMessage);
            connection.sendMes(registerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loginActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            userN = user.getText();
            String passW = pass.getText();

            byte[] loginMessage = connection.createMes(LOGIN, connection.createMes(USER_NAME, userN.getBytes()), connection.createMes(PASS_WORD, passW.getBytes()));

            ByteBuffer byteBuffer = ByteBuffer.wrap(loginMessage);

            connection.sendMes(loginMessage);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserN() {
        return userN;
    }
}
