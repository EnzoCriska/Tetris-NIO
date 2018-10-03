import javax.swing.*;
import java.io.IOException;
import java.util.LinkedList;


public class Client extends JFrame implements Commons, Runnable, ListentEvent{
    Connection connection;
    Login login;
    Loading loading;
    Board board;
    boolean loged, stated;
    Connection.ClientProcessData clientProcessData;

    public Client(){
        connection = new Connection();
        clientProcessData = connection.getClientProcessData();
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        new Thread(connection).start();

        new Thread(this).start();
    }

    public static void main(String[] args) {
        new Client();
    }

    @Override
    public void run() {
        login = new Login(connection, this);
        this.add(login);
        this.setVisible(true);
        clientProcessData.setOnEvent(this);

    }

    @Override
    public void reciverLogined() {
        JOptionPane.showMessageDialog(this, "Login successfully");
        loading = new Loading(connection,login.getUserN(), this);
        new Thread(loading).start();
        this.remove(login);
        this.add(loading);
        this.setVisible(true);
    }

    @Override
    public void reciverLoginFalse() {
        JOptionPane.showMessageDialog(null, "Tên đăng nhập hoặc mật khẩu chưa chính xác!");
    }

    @Override
    public void reciverRigister() {
        JOptionPane.showMessageDialog(null, "register successfully");
    }

    @Override
    public void reciverStated(String playerID, String competitor) {
        board = new Board(connection, this, playerID, login.getUserN(),competitor);
        new Thread(board).start();
        this.remove(loading);
        this.add(board);
//                this.setFocusable(true);
        this.setVisible(true);
    }

    @Override
    public void reciverGenShap(String shap) {
        board.reciverShaps(shap);
    }

    @Override
    public void reciverMove(LinkedList listMove) {
        board.listend();
    }

    @Override
    public void gameOver() {
        try {
            connection.sendMes(connection.createMes(SCORE, connection.createMes(SCORE,String.valueOf(board.Score).getBytes())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        board.gameOver();
        board.ingame = false;

    }


}
