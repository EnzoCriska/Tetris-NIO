import java.nio.channels.SelectionKey;

public class User {


    private String name;
    private String pass;
    private int hightScore;
    private SelectionKey skey;

    public User(String name) {
        this.name = name;
    }

    public User(String name, String pass, int hightScore) {

        this.name = name;
        this.pass = pass;
        this.hightScore = hightScore;
    }

    public SelectionKey getSkey() {
        return skey;
    }

    public void setSkey(SelectionKey skey) {
        this.skey = skey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getHightScore() {
        return hightScore;
    }

    public void setHightScore(int hightScore) {
        this.hightScore = hightScore;
    }
}
