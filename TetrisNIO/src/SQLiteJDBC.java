import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteJDBC {
    Connection conn;
    Statement stat;

    public SQLiteJDBC() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:test.db");

        stat =conn.createStatement();
        createTable();
    }

    public void createTable() throws SQLException {
        String sql ="CREATE TABLE IF NOT EXISTS UserPlayer" +
                "(NAMEPlay VARCHAR(50) NOT NULL PRIMARY KEY, " +
                " PASSWORD VARCHAR(20) NOT NULL," +
                "SCORE INT )" ;
        stat.executeUpdate(sql);
    }

    public void insertUser(String name, String pass) throws SQLException {
        String sql = "INSERT INTO UserPlayer(NAMEPlay, PASSWORD) VALUES (\'" + name + "\',\'"+ pass +"\');";
        stat.executeUpdate(sql);

    }

    public void updateScore(String name, int score) throws SQLException {
        String sql = "UPDATE UserPlayer SET SCORE = " + score +" WHERE NAMEPlay = \'" + name +"\';" ;
          stat.executeUpdate(sql);
    }

    public User queryLogin(String username, String passWord) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM UserPlayer WHERE NAMEPlay = \'"+ username + "\' AND PASSWORD = \'" + passWord +"\';";
        ResultSet set = stat.executeQuery(sql);
        while (set.next()){
            user = new User(set.getString(1), set.getString(2), set.getInt(3));
        }
       return user;
    }



    public void query(String sql) throws SQLException {
        stat.executeQuery(sql);

    }

    public void close(){

        try {
            stat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
