import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class RoomGame extends Server implements Commons{
    User player1, player2;
    Shaps shaps;
    boolean newShaps;
    private boolean ingame;
    LinkedList<Shaps> listShaps = new LinkedList<>();
    LinkedList<byte[]> listMessShaps = new LinkedList<>();

    public RoomGame(User user1, User user2){
        super();
        System.out.println("game start--------");
        this.player1 = user1;
        this.player2 = user2;

        broadCast();
        randomShaps();
        byte[] shapsMes = listMessShaps.poll();
        if (shapsMes!= null) broadCast(shapsMes);
    }

    public void broadCast(){

        try {
            byte[] competitorBytes1 = createMessage(COMPETITOR,player2.getName().getBytes() );
            byte[] competitorBytes2 = createMessage(COMPETITOR, player1.getName().getBytes());
            byte[] playerID1 = createMessage(PLAYER_1, "P1".getBytes());
            byte[] playerID2 = createMessage(PLAYER_2, "P2".getBytes());
            byte[] startMess1 = createMessage(START, playerID1, competitorBytes1);
            byte[] startMess2 = createMessage(START, playerID2, competitorBytes2);
            ByteBuffer byteBuffer = ByteBuffer.wrap(startMess1);
            SocketChannel channel1 = (SocketChannel) player1.getSkey().channel();
            channel1.write(byteBuffer);

            ByteBuffer byteBuffer2 = ByteBuffer.wrap(startMess2);
            SocketChannel channel2 = (SocketChannel) player2.getSkey().channel();
            channel2.write(byteBuffer2);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void broadCast(byte[] broadCastMess){


        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(broadCastMess);
            SocketChannel channel1 = (SocketChannel) player1.getSkey().channel();
            channel1.write(byteBuffer);

            ByteBuffer byteBuffer2 = ByteBuffer.wrap(broadCastMess);
            SocketChannel channel2 = (SocketChannel) player2.getSkey().channel();
            channel2.write(byteBuffer2);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }
}
