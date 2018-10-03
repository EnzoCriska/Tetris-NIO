import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.*;

public class Server implements Commons, Runnable{

    ServerSocketChannel serverSocketChannel;
    Selector selector;
    SelectionKey selectionKey;
    SocketChannel channelClient;
    SQLiteJDBC sqLiteJDBC;
    LinkedList<User> listOnlineUser = new LinkedList<>();
    LinkedList<RoomGame> listRoom  = new LinkedList<>();
    LinkedList<User> requestPlay = new LinkedList<>();

    Shaps shaps;
    boolean newShaps;
    private boolean ingame;
    LinkedList<Shaps> listShaps = new LinkedList<>();
    LinkedList<byte[]> listMessShaps = new LinkedList<>();
    LinkedList<Message> mess = new LinkedList<>();

    public Server(){}

    public Server(InetSocketAddress address){
        try {
            sqLiteJDBC = new SQLiteJDBC();

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);

            System.out.println("Wait for connect...");
            selector = Selector.open();
            int ops = serverSocketChannel.validOps();
            selectionKey = serverSocketChannel.register(selector,ops, null);
            new Thread(this).start();
            new Thread(new ProcessMessage()).start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void accept(SelectionKey key) throws IOException{
        SocketChannel sc =((ServerSocketChannel) key.channel()).accept();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
        System.out.println("Server Connect to: " + sc.getLocalAddress());
    }

    public void readable(SelectionKey key) throws IOException{
        SocketChannel channelClient = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channelClient.read(byteBuffer);
//        System.out.println(byteBuffer.array());
        addQueue(new Message(key,byteBuffer.array()));

    }

    public void addQueue(Message message){
        try{
            synchronized (mess){
                while (mess.size() == 32000){
                    try {
                        mess.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mess.add(message);
                mess.notifyAll();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeable(byte[] bytes) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        channelClient.write(byteBuffer);
    }

    public void randomShaps() {
        Random random = new Random();
        byte[] shapsMess;
        int shapRandom = random.nextInt()%7;

        switch (shapRandom){
            case 0:
                shaps = new ShapsO(START_X, START_Y);
                shapsMess = createMessage(SHAPS,createMessage(SHAPO, "SHAPSO".getBytes()));
                break;
            case 1:
                shaps = new ShapsI(START_X, START_Y);
                shapsMess = createMessage(SHAPS,createMessage(SHAPI, "SHAPSI".getBytes()));

                break;
            case 2:
                shaps = new ShapsL(START_X, START_Y);
                shapsMess = createMessage(SHAPS,createMessage(SHAPL, "SHAPSL".getBytes()));
                break;

            case 3:
                shaps = new ShapsRvL(START_X, START_Y);
                shapsMess = createMessage(SHAPS,createMessage(SHAPRL, "SHAPSRvL".getBytes()));
                break;
            case 4:
                shaps = new ShapsZ(START_X, START_Y);
                shapsMess = createMessage(SHAPS,createMessage(SHAPZ, "SHAPSZ".getBytes()));
                break;

            case 5:
                shaps = new ShapsRvZ(START_X, START_Y);
                shapsMess = createMessage(SHAPS,createMessage(SHAPRZ, "SHAPSRvZ".getBytes()));
                break;
            default:
                shaps = new ShapsT(START_X, START_Y);
                shapsMess = createMessage(SHAPS,createMessage(SHAPT, "SHAPST".getBytes()));
                break;
        }

        listShaps.add(shaps);
        listMessShaps.add(shapsMess);
//        listMessShaps.add(shapsMess);

    }

    public byte[] createMessage(String key, byte[] ...Message){
        int length = 0;
        for ( int i = 0; i<Message.length; i++){
            length += Message[i].length;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        for (int i = 0; i < Message.length; i++){
            byteBuffer.put(Message[i]);
        }

        byte[] data = byteBuffer.array();
        short lengthData = 0;

        if (data.length > Short.MAX_VALUE){
            lengthData = Short.MAX_VALUE;
            data = Arrays.copyOf(data,lengthData);
        }else{
            lengthData = (short) data.length;
        }
        ByteBuffer dataBuffer = ByteBuffer.allocate(length+4);
        dataBuffer.put(key.getBytes()).putShort(lengthData).put(data);
        return dataBuffer.array();
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        new Thread(new Server(address)).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (selector != null){
                    selector.select();
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    Iterator<SelectionKey> sKeyIterator = selectionKeySet.iterator();
                    while (sKeyIterator.hasNext()) {
                        SelectionKey key = sKeyIterator.next();

                        if (key.isAcceptable()) {
                            accept(key);
                        }

                        if (key.isReadable()) {
                            readable(key);
                        }

                    }
                    sKeyIterator.remove();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ProcessMessage implements Runnable{
        public ProcessMessage(){

        }

        public Queue<byte[]> Process(byte[] data){
            LinkedList<byte[]> Messange = new LinkedList<>();
            while(data.length >4){
                String IdField = new String(data, 0,2);
                short LengthField = ByteBuffer.wrap(data, 2, 2).getShort();
                byte[] dataField = Arrays.copyOfRange(data, 4, 4+LengthField);
                Messange.add(dataField);

                if (4+ LengthField > data.length) break; else
                    data = Arrays.copyOfRange(data,4+LengthField, data.length);

            }
            return Messange;
        }

        public void checkLogin(String id, String pass, SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            try {
                User user = sqLiteJDBC.queryLogin(id, pass);
                if (user != null){
                    user.setSkey(key);
                    byte[] resp = createMessage(LOGIN_TRUE, "LoginTrue".getBytes());
                    byte[] Rep = createMessage(LOGIN_TRUE, resp);
                    channel.write(ByteBuffer.wrap(Rep));
                    listOnlineUser.add(user);
                    System.out.println(id +" loged!");
                } else {
                    byte[] resp = createMessage(LOGIN_FALSE, "LoginFalse".getBytes());
                    byte[] Rep = createMessage(LOGIN_FALSE, resp);
                    channel.write(ByteBuffer.wrap(Rep));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                byte[] resp = createMessage(LOGIN_FALSE, "LoginFalse".getBytes());
                byte[] Rep = createMessage(LOGIN_FALSE, resp);
                channel.write(ByteBuffer.wrap(Rep));
            }
        }

        public void register(String ID, String MK, SelectionKey key) throws IOException {
            SocketChannel chanel = (SocketChannel) key.channel();
            try {
                sqLiteJDBC.insertUser(ID, MK);
                byte[] repRegis = createMessage(REGISTER_TRUE, "RegisterTrue".getBytes());
                chanel.write(ByteBuffer.wrap(repRegis));
            } catch (SQLException e) {
                //e.printStackTrace();
                byte[] repRegis = createMessage(REGISTER_FALSE, "RegisterFalse".getBytes());
                chanel.write(ByteBuffer.wrap(repRegis));
            }
        }


        @Override
        public void run() {
            while (true) {
                synchronized (mess) {
                    while (mess.isEmpty()) {
                        try {
                            mess.wait();  //Important
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
//                    isRunning = true;
                    Message message =  mess.remove();
                    byte[] MessageByte = message.getByteMess();
                    SelectionKey key = message.getKey();
                    String Header = new String(MessageByte,0,2);
                    short mesLength = ByteBuffer.wrap(MessageByte, 2, 2).getShort();
                    byte[] data = Arrays.copyOfRange(MessageByte, 4, 4+mesLength);

                    LinkedList<byte[]> messageDecode = (LinkedList) Process(data);

                    switch (Header){
                        case LOGIN:
                            String userID = new String(messageDecode.poll());
                            String pass = new String(messageDecode.poll());
                            try {
                                checkLogin(userID, pass, key);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case REGISTER:
                            String ID = new String(messageDecode.poll());
                            String MK = new String(messageDecode.poll());
                            try {
                                register(ID, MK, key);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case Register_Play:
                            String nameplay = new String(messageDecode.poll());

                            for (int i = 0; i < listOnlineUser.size(); i++){
                                if (nameplay.equals(listOnlineUser.get(i).getName())){
                                    requestPlay.add(listOnlineUser.get(i));
                                    break;
                                }
                            }

                            if (requestPlay.size()%2 == 0 && requestPlay.size() >0){
                                RoomGame roomGame = new RoomGame(requestPlay.poll(), requestPlay.poll());
                                listRoom.add(roomGame);
                                new Thread(roomGame).start();
                            }
                            break;
                        case NewShaps:
                            randomShaps();
                            byte[] shapsMes = listMessShaps.poll();
                            System.out.println(new String(shapsMes  ));
                            listRoom.get(0).broadCast(shapsMes);
//                            try {
//                                ((SocketChannel) key.channel()).write(ByteBuffer.wrap(shapsMes));
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            break;
                        case GAME_OVER:
                            String name = new String(messageDecode.poll());
                            System.out.println(name);
                            listRoom.get(0).broadCast(createMessage(GAME_OVER, createMessage(GAME_OVER, "game over".getBytes())));
                            break;
                        case SCORE:
                            int newScore = Integer.parseInt(new String(messageDecode.poll()));
//                            System.out.println(newScore);
                            for (int i = 0; i<listOnlineUser.size(); i++){
                                User user = listOnlineUser.get(i);
                                if (user.getSkey().equals(key)){
                                    if (user.getHightScore() <= newScore){
                                        user.setHightScore(newScore);
                                        try {
                                            sqLiteJDBC.updateScore(user.getName(), newScore);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            break;
                            case MOVE_ROTATE:
                                String nameMove = new String(messageDecode.poll()).trim();
                                for (int i = 0; i < listRoom.size(); i++){
                                    if (nameMove.equals(listRoom.get(i).getPlayer1().getName())){
                                        String move = new String(messageDecode.poll());
                                        byte[] moveMess = createMessage(MOVE_ROTATE, createMessage(MOVE_ROTATE, move.getBytes()));
                                        SocketChannel channel = (SocketChannel) listRoom.get(0).player2.getSkey().channel();
                                        try {
                                            channel.write(ByteBuffer.wrap(moveMess));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }else if (nameMove.equals(listRoom.get(i).getPlayer2().getName())){
                                        String move2 = new String(messageDecode.poll());
                                        byte[] moveMess2 = createMessage(MOVE_ROTATE, createMessage(MOVE_ROTATE, move2.getBytes()));
                                        SocketChannel channel2 = (SocketChannel) listRoom.get(0).player1.getSkey().channel();
                                        try {
                                            channel2.write(ByteBuffer.wrap(moveMess2));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                    }
                    //notify to parseInt

                    //
                    mess.notifyAll();

//                    isRunning = false;
                } //synchronized ends here : NOTE
            }
        }
    }
}
