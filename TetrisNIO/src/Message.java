import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Message {
    SelectionKey key;
    byte[] byteMess, byteContent;
    String header, content, subheader;

    public Message(SelectionKey key, byte[] byteMess) {
        this.key = key;
        this.byteMess = byteMess;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public byte[] getByteMess() {
        return byteMess;
    }

    public void setByteMess(byte[] byteMess) {
        this.byteMess = byteMess;
    }
}
