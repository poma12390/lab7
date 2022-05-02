package lab6.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientCaller {
    private static final Logger logger
            = LoggerFactory.getLogger(ClientCaller.class);
    InetAddress host;
    int port;
    byte arr[];
    int len;
    public void sendToClient(byte[] test) {
        DatagramSocket ds = ClientReceiver.ds;
        DatagramPacket dp = ClientReceiver.dp;
        port = dp.getPort();
        host = dp.getAddress();
        arr = test;
        len = arr.length;
        dp = new DatagramPacket(arr,len,host,port);
        try {
            logger.info("send " + len + " bytes to" + host + " " + port);

            ds.send(dp);
        } catch (IOException ignored) {}


    }
}
