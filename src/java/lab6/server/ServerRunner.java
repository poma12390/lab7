package lab6.server;


import lab6.common.dto.ClearCommandDto;
import lab6.common.dto.CommandRequestDto;
import lab6.server.commands.Commands;
import lab6.server.setters.DiagnosticSignalHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerRunner implements SignalHandler{
    private static final Logger logger
            = LoggerFactory.getLogger(ServerRunner.class);

    public static void main(String[] args) {

        Commands.temporaryStart();
        Commands.dataBaseToCollection();

        new Thread(() -> {
            ClientReceiver receiver = new ClientReceiver();
                receiver.run();
            // выполнение в отдельном потоке

        }).start();
        logger.info("Save для закрылия");

        // TODO: после Ctrl+C вызвать сохранение коллекции



        new Thread(() -> {
            InputStream inputStream = System.in;
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String input ="";
            while (true) {
                try {
                    input = bufferedReader.readLine();
                    if (input.equals("save")){
                        Commands.runCommandFromString(Commands.getWorkersSet(), "save",new CommandRequestDto<>("save", new ClearCommandDto()));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);

                }catch (NullPointerException ignored){
                }
            }

            // выполнение в отдельном потоке

        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    logger.info("Shutting down ...");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        });

        SignalHandler signalHandler = new SignalHandler() {

            @Override
            public void handle(Signal signal) {
                logger.info("saving collection");
                Commands.runCommandFromString(Commands.getWorkersSet(), "save",new CommandRequestDto<>("save", new ClearCommandDto()));
            }
        };
        DiagnosticSignalHandler.install("TERM", signalHandler);
        DiagnosticSignalHandler.install("INT", signalHandler);
        DiagnosticSignalHandler.install("ABRT", signalHandler);


    }

    @Override
    public void handle(Signal signal) {

    }
}
