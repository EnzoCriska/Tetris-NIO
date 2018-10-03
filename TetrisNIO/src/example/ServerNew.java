package example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class ServerNew extends Header implements Runnable{
    private Selector selector;
    private InetSocketAddress address;
    private List<UserChat> userLog = new ArrayList();
    LinkedList<byte[]> Message;
    
    public ServerNew(String add, int port) throws IOException{
        address = new InetSocketAddress(add, port);
        this.selector = Selector.open();
        ServerSocketChannel skc = ServerSocketChannel.open();
        
        skc.configureBlocking(false);
        skc.bind(address);
        skc.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server waitting for connect...");
        userLog.add( new UserChat("dungtt", "123"));
        userLog.add( new UserChat("hz", "123"));
        userLog.add( new UserChat("En", "123"));
    }
    
    
    @Override
    public void run() {
         while (true){
            try {   
                selector.select();
                Set<SelectionKey> skey  = selector.selectedKeys();
                Iterator<SelectionKey> ikey = skey.iterator();
                while(ikey.hasNext()){
                    SelectionKey key = (SelectionKey) ikey.next();
                    if(key.isAcceptable()){
                        this.accept(key);
                    } else if(key.isReadable()){
                        this.read(key);
                    }
                }
                ikey.remove();
            } catch (IOException ex) {
                System.out.println("IOException, server of port " +this.address+ " terminating. Stack trace:");
		ex.printStackTrace();
            }
        }
    }
    private void accept(SelectionKey key) throws IOException{
        SocketChannel sc =((ServerSocketChannel) key.channel()).accept();
        sc.configureBlocking(false);                   
        sc.register(selector, SelectionKey.OP_READ);
        System.out.println("Connect to: " + sc.getLocalAddress());
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer bff = ByteBuffer.allocate(1024);
        sc.read(bff);
        byte[] TVL = bff.array();
        
        for(byte k : TVL) System.out.print(k+ "\t");
        String Header = new String(TVL,0,2);
        short mesLength = ByteBuffer.wrap(TVL, 2, 2).getShort();
        byte[] data = Arrays.copyOfRange(TVL, 4, 4+mesLength);
        
        String fileReciver, fileName;
        int SizeFile;
        
        Queue<byte[]> multiFile = new LinkedList<>();
        Queue<byte[]> dataTVL = Process(data);
        switch(Header){
            case LogIn: 
                if(login(new String(dataTVL.poll()).trim(), new String(dataTVL.poll()).trim(), key)){
                    byte[] resp = createMes(LoginTrue, "LoginTrue".getBytes());
                    byte[] Rep = createMes(LoginTrue, resp);
                    sc.write(ByteBuffer.wrap(Rep));
                }else{
                    byte[] resp = createMes(LoginFalse, "LoginFalse".getBytes());
                    byte[] Rep = createMes(LoginFalse, resp);
                    sc.write(ByteBuffer.wrap(Rep));
                }                
                break;
            case SendTo:
                System.out.println("Send to");
                String toName = new String(dataTVL.poll()).trim();
                byte[] from = dataTVL.poll();
                byte[] chat = dataTVL.poll();
                sendTo(toName, from, chat);
                break;
            case SigBlock:
                System.out.println("Send file Singal");
                String toNameFile = new String(dataTVL.poll()).trim();
                byte[] dataName = dataTVL.poll();
                byte[] dataFile = dataTVL.poll();
                byte[] name = createMes(FileName, dataName);
                byte[] fileDa = createMes(FileData, dataFile);
                byte[] sendToMes = createMes(SigBlock, name, fileDa);
                send(toNameFile, sendToMes);
                break;
            case FileInfo:
                multiFile.clear();
                System.out.println("Info");
                fileReciver = new String(dataTVL.poll());
                byte[] dataMName = dataTVL.poll();
                byte[] dataSize = dataTVL.poll();
                fileName = new String(dataMName).trim();
                String sizeString = new String(dataSize).trim();
                SizeFile = Integer.parseInt(sizeString);
                byte[] TVLdataInfo = createMes(FileInfo, createMes(FileName, dataMName), createMes(FileSize, dataSize));
                send(fileReciver, TVLdataInfo);
                System.out.println("Recived: " + fileReciver + " " + fileName + " " + SizeFile);
                break;
            case MultiBlock:
                System.out.println("Server process MultiBlock");
                while(!dataTVL.isEmpty()){
                    multiFile.add(dataTVL.poll());
                }
                break;
            case Broadcast:
                byte[] fromc = dataTVL.poll();
                byte[] chatc = dataTVL.poll();
                
                broadcast(fromc, chatc);
                break;
            case RequestLogin:
                byte[] notic = createMes(Broadcast, dataTVL.poll());
                byte[] noticTVL = createMes(Broadcast, notic);
                for (UserChat use : userLog){
                    if (use.getStatus()){
                        if(use.getKey().isValid() && use.getKey().channel() instanceof SocketChannel) {
                            SocketChannel sch=(SocketChannel) use.getKey().channel();
                            sch.write(ByteBuffer.wrap(noticTVL));
                            
                        }
                    }
                }
                break;
        }
        bff.clear();
    }
    
    private Queue<byte[]> Process(byte[] data){
        LinkedList<byte[]> Mes = new LinkedList<>();
        while(data.length >4){
            String IdField = new String(data, 0,2);
            short LengthField = ByteBuffer.wrap(data, 2, 2).getShort();
            byte[] dataField = Arrays.copyOfRange(data, 4, 4+LengthField);
            Mes.addLast(dataField);
            if (4+ LengthField > data.length) break; else
            data = Arrays.copyOfRange(data,4+ LengthField, data.length);
        }
        return Mes;
    }
    
    private boolean login(String id, String pass, SelectionKey key) throws IOException{
        for(UserChat user : userLog){
            if(id.equals(user.getID()) && (pass.equals(user.getPass())) )
            {
                user.login();
                user.setKey(key);
                return true;
            }
        }
        return false;
    }
    
    private byte[] createMes(String key, byte[] ...Message){
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

    private void sendTo (String nameto,byte[] nameFrom, byte[] str) throws IOException{
        System.out.println("in send to");
        byte[] from = createMes(FromName, nameFrom);
        byte[] chat = createMes(Chat, str);
        byte[] sendToMes = createMes(SendTo, from, chat);
        ByteBuffer msgBuf=ByteBuffer.wrap(sendToMes);
        for(UserChat user : userLog){
            if(nameto.equals(user.getID())){
                if(user.getKey().isValid() && user.getKey().channel() instanceof SocketChannel) {
                    SocketChannel sch=(SocketChannel) user.getKey().channel();
                    sch.write(msgBuf);
                    msgBuf.rewind();
                    msgBuf.clear();
                    System.out.println("Send to");
                    break;
                } else System.out.println("Not valid");
            }else System.out.println("not equals");
        }
        //System.out.println("Not found");
    }

    private void send(String toname, byte[] arr) throws IOException{
        ByteBuffer msgBuf=ByteBuffer.wrap(arr);
        for(UserChat user : userLog){
            if(toname.equals(user.getID())){
                if(user.getKey().isValid() && user.getKey().channel() instanceof SocketChannel) {
                    SocketChannel sch=(SocketChannel) user.getKey().channel();
                    sch.write(msgBuf);
                    msgBuf.rewind();
                    msgBuf.clear();
                    System.out.println("Send to");
                    break;
                } else System.out.println("Not valid");
            }else System.out.println("not equals");
        }
    }
    
    private void broadcast(byte[] from, byte[] str) throws IOException {
        String mes = new String(from).trim() + " talk " + new String(str).trim();
        System.out.println(mes);
        byte[] TVLx = createMes(Broadcast, mes.getBytes());
        byte[] TVL = createMes(Broadcast, TVLx);
	ByteBuffer msgBuf=ByteBuffer.wrap(TVL);
	for(SelectionKey key : selector.keys()) {
	if(key.isValid() && key.channel() instanceof SocketChannel) {
            SocketChannel sch=(SocketChannel) key.channel();
            sch.write(msgBuf);
            msgBuf.rewind();
            msgBuf.clear();
            }
	}
    }
    
    public static void main(String[] args) throws IOException{
        ServerNew server = new ServerNew("localhost", 1125);
        new Thread(server).start();
    }
    
}
