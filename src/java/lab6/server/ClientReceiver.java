package lab6.server;


import lab6.common.Transformer;
import lab6.common.dto.CommandRequestDto;
import lab6.server.commands.Commands;
import lab6.server.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientReceiver {
    private static final Logger logger
            = LoggerFactory.getLogger(ClientReceiver.class);
    public static byte arr[] = new byte[65536];
    public static int len = arr.length;
    public static DatagramSocket ds;
    public static DatagramPacket dp;
    int port = 12390;
    boolean d;

    private boolean checkAuth(String login, String password) {
        boolean c = false;
        Database database = Commands.getDatabase();
        try {
            ResultSet set = database.executeQuery("select * from users where username = ?", login);
            while (set.next()) {
                String password1 = set.getString("password");
                String password2 = Transformer.Encrypt(password, set.getString("salt"));
                if (password1.equals(password2)) {
                    c = true;
                }
            }
            database.closeQuery();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return c;
    }


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
                if (arr[0] == 0) {
                    continue;
                }
                CommandRequestDto dto = (CommandRequestDto) transformer.DeSerialize(arr);
                command = dto.getCommandName();
                logger.info("Receive command " + command);
                Commands.runCommandFromString(Commands.getWorkersSet(), command, dto);
//                if (dto.getLogin()== null && dto.getPassword() == null){
//                    d = false;
//                }
//                else if (!command.equals("Auth") && !command.equals("Register")) {
//                    d = checkAuth(dto.getLogin(), dto.getPassword());
//                }
//                if (d || command.equals("Auth") || command.equals("Register")) {
//                    Commands.runCommandFromString(Commands.getWorkersSet(), command, dto);
//                }
//                else {
//                    CommandResponseDto dto1 = new CommandResponseDto(dto.getCommandArgs()); // if client set wrong login and password
//                    dto1.setResponse("you should be authorized");
//                    ClientCaller clientCaller = new ClientCaller();
//                    clientCaller.sendToClient(transformer.serialize(dto1));
//                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
