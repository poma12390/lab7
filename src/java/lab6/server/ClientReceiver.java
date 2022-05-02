package lab6.server;


import lab6.common.Transformer;
import lab6.common.dto.CommandRequestDto;
import lab6.server.commands.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientReceiver {
    private static final Logger logger
            = LoggerFactory.getLogger(ClientReceiver.class);
    public static byte arr[] = new byte[65536];
    public static int len = arr.length;
    public static DatagramSocket ds;
    public static DatagramPacket dp;
    int port = 12390;

    void run() {
        try {
            ds = new DatagramSocket(port);
            dp = new DatagramPacket(arr, len);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        requestFromClient();
    }
    public void requestFromClient() {
        Transformer transformer = new Transformer();
        try {
            boolean c = true;
            while (c) {
                String command = "";
                ds.receive(dp);
                if (arr[0]==0){
                    continue;
                }
                CommandRequestDto dto = (CommandRequestDto) transformer.DeSerialize(arr);
                command = dto.getCommandName();
                logger.info("Receive command " + command);
                Commands.runCommandFromString(Commands.getWorkersSet(), command,dto);




            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
