package communication;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by cjf on 2014/9/15.
 */
public class TCPReceiver extends Thread {
    private static Logger logger = Logger.getLogger(TCPReceiver.class);
    private static final int BUFSIZE = 32;
    private static final int PORT = 20001;
    public Socket serverSocket = null;
    public Queue<String> inQueue;


    public TCPReceiver(Socket socket,Queue inQueue) {
        this.serverSocket = socket;
        this.inQueue = inQueue;
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
            //serverSocket = server.accept();
            //System.out.println("Receive: Connected to serverSocket:" + serverSocket.getRemoteSocketAddress() + "...sending echo string");
            //System.out.println(serverSocket.getRemoteSocketAddress());
            logger.info("Receive: Connected to serverSocket:" + serverSocket.getRemoteSocketAddress().toString() + "...sending echo string");
            in = serverSocket.getInputStream();
            String temp = "";
            while (in.read(receiveBuf) != -1) {
                temp += new String(receiveBuf);
                receiveBuf = new byte[BUFSIZE];
                if (temp.indexOf("#E") >= 0) {
                    break;
                }
            }
            logger.info("Message received: " + temp.trim());

            inQueue.offer(temp.trim() + "|" + serverSocket.getRemoteSocketAddress().toString());
            //System.out.println("Message and IP received: " + temp.trim() + "|" + serverSocket.getRemoteSocketAddress().toString());
            logger.debug("Message and IP received: " + temp.trim() + "|" + serverSocket.getRemoteSocketAddress().toString());
            serverSocket.close();
            //Thread.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * @param inQueue
     */
    public void receivePackage(Queue<String> inQueue) {

        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Connected to serverSocket"+server+":"+serverPort+"...sending echo string");

        boolean f = true;
        int recvMsgSize;
        byte[] receiveBuf = new byte[BUFSIZE];
        while (f) {
            try {
                serverSocket = server.accept();
                System.out.println("Receive: Connected to serverSocket:" + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort() + "...sending echo string");
                InputStream in = serverSocket.getInputStream();
                String temp = "";
                while (in.read(receiveBuf) != -1) {
                    temp += new String(receiveBuf);
                    receiveBuf = new byte[BUFSIZE];
                    if (temp.indexOf("#E") >= 0) {
                        break;
                    }
                }
                logger.info("Message received: " + temp.trim());
                System.out.println("Message received: " + temp.trim());
                inQueue.offer(temp.trim());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        try {
            multiReceivePackage(this.inQueue);
        } catch (Exception e) {
            //System.out.println("[" + Thread.currentThread().getName() + "] Receiver 线程内出错。");
            logger.error("[" + Thread.currentThread().getName() + "] Receiver 线程内出错。");
            e.printStackTrace();
        }

    }
}
