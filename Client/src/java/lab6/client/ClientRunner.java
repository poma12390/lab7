package lab6.client;

import lab6.client.commands.Utils;
import lab6.common.Transformer;
import lab6.common.Worker;
import lab6.common.dto.AddCommandDto;
import lab6.common.dto.CommandRequestDto;
import lab6.common.dto.WorkerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

public class ClientRunner {
    private static final Logger logger
            = LoggerFactory.getLogger(ClientRunner.class);
    public static void main(String[] args) {

        InputStream inputStream = System.in;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Utils.setCurrentBufferedReader(bufferedReader);


        //String input = "help";
        //Utils.runCommandFromString(input);
        try {

            while (true) {
                System.out.print("Write a command: ");
                String input = bufferedReader.readLine();
                Utils.runCommandFromString(input);
            }
        } catch (NullPointerException | NoSuchElementException e) {
            Utils.funExit();
        } catch (Exception e) {
            e.printStackTrace();
        }


//System.out.println(crd.getCommandArgs().getBum().getName() + " " + crd.getCommandName());

//        try {
//
//            serverCaller.sendToServer(transformer.Serialize(crd));}
//        catch (ServerNotFoundException e){
//            System.out.println(e.getMessage());
//        }


    }
}
