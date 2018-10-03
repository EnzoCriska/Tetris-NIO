/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ThreadWithQueue extends Thread {

    private Queue<String> queueItems;
    private Queue<String> queueUsers;
    private int queueSize = 32000;
    public boolean isRunning = false;

    public ThreadWithQueue(String ThreadName) {
        super(ThreadName);
        this.queueItems = new LinkedList<>();
        this.queueUsers = new LinkedList<>();
    }

    public void addQueue(String user, String sCards) {
        try {
            synchronized (queueItems) {
                while (queueItems.size() == queueSize) {
                    try {
                        queueItems.wait();   //Important
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
//                System.out.println("addQueue: " + chatInfo.msg);
                queueItems.add(sCards);
                queueUsers.add(user);
                queueItems.notifyAll();  //Important
            } //synchronized ends here : NOTE
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queueItems) {
                while (queueItems.isEmpty()) {
//                    LOGGER.info(Thread.currentThread().getName() + " Empty        : waiting...\n");
                    try {
                        queueItems.wait();  //Important
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                isRunning = true;
                String sItems = queueItems.remove();
                String user = queueUsers.remove();
                //notify to parseInt

                //
                queueItems.notifyAll();

                isRunning = false;
            } //synchronized ends here : NOTE
        }
    }
    
}
