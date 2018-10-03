package example;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Connection implements Commons, Runnable {
    InetSocketAddress inetAdress = new InetSocketAddress(host, port);
    SocketChannel channel;
    private Selector selector;
    boolean login = false;



    public Connection(){
        try {
            channel = SocketChannel.open(inetAdress);
            selector = Selector.open();
            channel.configureBlocking(false);
            System.out.println(" Connect to Server...");
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMes(byte[] TVL) throws IOException {

        channel.write(ByteBuffer.wrap(TVL));
        SelectionKey key = channel.keyFor(selector);
        key.interestOps(SelectionKey.OP_WRITE);

    }


    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keySet.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey currentKey = keyIterator.next();
                    keyIterator.remove();

                    if (!currentKey.isValid()) {
                        continue;
                    }
                    if (currentKey.isConnectable()) {
                        System.out.println("I'm connected to the server!");
                        Connectable(currentKey);
                    }

                    if (currentKey.isReadable()) {
                        readable(currentKey);
                    }

                    if (currentKey.isWritable()) {
                        write(currentKey);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();

            }

        }
    }

    public void Connectable(SelectionKey currentKey) throws IOException {
        SocketChannel channel = (SocketChannel) currentKey.channel();
        if(channel.isConnectionPending()) {
            channel.finishConnect();
        }
        channel.configureBlocking(false);
        channel.register(selector,SelectionKey.OP_READ|SelectionKey.OP_WRITE);
    }


    public void readable(SelectionKey currentKey) throws IOException{
        SocketChannel socketChannel = (SocketChannel) currentKey.channel();
        ByteBuffer bff = ByteBuffer.allocate(1024);

        socketChannel.read(bff);

        byte[] TVL = bff.array();

        String Header = new String(TVL,0,2);
        short mesLength = ByteBuffer.wrap(TVL, 2, 2).getShort();
        byte[] data = Arrays.copyOfRange(TVL, 4, 4+ mesLength);

        switch(Header){
            case LOGIN_TRUE:
                login = true;
                break;
            case LOGIN_FALSE:
                login = false;
                break;
            case REGISTER_TRUE:
                JOptionPane.showMessageDialog(null, "register successfully");
                break;
        }
    }

    public void write(SelectionKey currentKey) throws IOException {
        SocketChannel channel = (SocketChannel) currentKey.channel();
        currentKey.interestOps(SelectionKey.OP_READ);
    }

    public byte[] createMes(String key, byte[] ...Message){
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

    public boolean isLogin(){
        return login;
    }



}

