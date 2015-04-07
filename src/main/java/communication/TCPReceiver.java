package communication;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

/**
 * Created by cjf on 2014/9/15.
 */
public class TCPReceiver extends Thread {
    private static Logger logger = Logger.getLogger(TCPReceiver.class);
    private static final int BUFSIZE = 32;
    private static final int PORT = 20001;
    public Socket socket = null;
    public Queue<String> inQueue;
    ServerSocket serverSocket;

    public TCPReceiver(Queue inQueue) {
        //this.socket = socket;
        this.inQueue = inQueue;
        try {
            serverSocket= new ServerSocket(PORT);
        } catch (IOException e) {
            logger.error(PORT+" 端口已被占用");
            e.printStackTrace();
        }
    }

    /**
     * Package receiving method used for processing multi requests
     *
     * @param inQueue message in queue
     */
    public void multiReceivePackage(Queue<String> inQueue) {
        InputStream in = null;

        try {
            byte[] receiveBuf = new byte[BUFSIZE];
            //socket = server.accept();
            //System.out.println("Receive: Connected to socket:" + socket.getRemoteSocketAddress() + "...sending echo string");
            //System.out.println(socket.getRemoteSocketAddress());
            logger.info("Receive: Connected to socket:" + socket.getRemoteSocketAddress().toString() + "...sending echo string");
            in = socket.getInputStream();
            String temp = "";
            while (in.read(receiveBuf) != -1) {
                temp += new String(receiveBuf);
                receiveBuf = new byte[BUFSIZE];
                if (temp.indexOf("#E") >= 0) {
                    break;
                }
            }
            logger.info("Message received: " + temp.trim());

            inQueue.offer(temp.trim() + "|" + socket.getRemoteSocketAddress().toString());
            //System.out.println("Message and IP received: " + temp.trim() + "|" + socket.getRemoteSocketAddress().toString());
            logger.debug("Message and IP received: " + temp.trim() + "|" + socket.getRemoteSocketAddress().toString());
            socket.close();
            //Thread.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        //System.out.println("TCPReceiver is running");
        logger.debug("TCPReceiver is running");
        //receivePackage(DataHandler.inQueue);
        while (true){
            try {
                socket = serverSocket.accept();
                multiReceivePackage(this.inQueue);
            } catch (Exception e) {
                //System.out.println("[" + Thread.currentThread().getName() + "] Receiver 线程内出错。");
                logger.error("[" + Thread.currentThread().getName() + "] Receiver 线程内出错。");
                e.printStackTrace();
            }
        }


    }

    public void receiveData(){
        //System.out.println("TCPReceiver is running");
        logger.debug("TCPReceiver is running");
        //receivePackage(DataHandler.inQueue);


        try {
            socket = serverSocket.accept();
            multiReceivePackage(this.inQueue);
        } catch (Exception e) {
            //System.out.println("[" + Thread.currentThread().getName() + "] Receiver 线程内出错。");
            logger.error("[" + Thread.currentThread().getName() + "] Receiver 线程内出错。");
            e.printStackTrace();
        }
    }
}
