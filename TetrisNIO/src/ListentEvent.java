import java.util.LinkedList;

public interface ListentEvent {
    public void reciverLogined();

    public void reciverLoginFalse();

    public void reciverRigister();

    public void reciverStated(String playerID, String competitor);

    public void reciverGenShap(String shap);

    public void reciverMove(LinkedList listMove);

    public void gameOver();
}
