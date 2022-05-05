package lab6.server.commands;

import lab6.common.Person;
import lab6.common.Worker;
import lab6.common.dto.CommandRequestDto;
import lab6.common.exceptions.EmptyCollectionException;
import lab6.common.exceptions.EndStreamException;
import lab6.common.exceptions.InvalidDataException;
import lab6.common.exceptions.MissedCommandArgumentException;
import lab6.server.database.Database;
import lab6.server.setters.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;



public class Commands {
    private static final Logger logger
            = LoggerFactory.getLogger(Commands.class);
    public static boolean blockPrompts = false;
    private static ArrayList<Integer> ids = new ArrayList<Integer>();
    private static LinkedHashSet<Worker> workers = new LinkedHashSet<>();

    public static LinkedHashSet<Worker> getWorkersSet() {
        return workers;
    }

    public static void setBlockPrompts(boolean blockPrompts) {
        Commands.blockPrompts = blockPrompts;
    }

    public static void setIsFileExecuted(boolean isFileExecuted) {
        Commands.isFileExecuted = isFileExecuted;
    }

    public static boolean isFileExecuted = false;

    public static Database getDatabase() {
        return database;
    }

    private static Database database;

    private static final List<lab6.server.commands.BaseCommand> commands = Arrays.asList(
            new ShowCommand(),
            new ExitCommand(),
            new InfoCommand(),
            new AddCommand(),
            new AddIfMinCommand(),
            new ClearCommand(),
            new FilterBySalaryCommand(),
            new PrintFieldDescendingEndDateCommand(),
            new RemoveLowerCommand(),
            new RemoveByIdCommand(),
            new RemoveAllByEndDateCommand(),
            new SaveCommand(),
            new UpdateIdCommand(),
            new AuthCommand(),
            new RegisterCommand()
    );

    public static void temporaryStart(String filename){
        database = Database.getInstance();
        String start = open(filename);
        begin(start, workers);
    }

    public static int getRandomNumber(){
        return (int) ((Math.random() * 1000));
    }

    /**
     * open command
     * @param filename file name
     */
    public static String open(String filename) {
        try (InputStream is = Files.newInputStream(Paths.get(filename))) {
            try (BufferedInputStream bis = new BufferedInputStream(is)) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result = bis.read();
                while (result != -1) {
                    buf.write((byte) result);
                    result = bis.read();
                }
                logger.info(buf.toString().replace("\"", ""));
                return buf.toString().replace("\"", "");
            } catch (Exception e) {

                if (e.getMessage().isEmpty()){
                    logger.warn(e.getCause().getMessage());
                }
                else {
                    logger.warn(e.getMessage());
                }

                return "";
            }
        } catch (Exception e) {
            if (e.getMessage().isEmpty()){
                logger.warn(e.getCause().getMessage());
            }
            else {
                logger.warn(e.getMessage());
            }
            return ""; // ?????????
        }
    }

    /**
     * upload command
     * @param sts stats to upload bum
     * @return Worker
     */

    private static Worker upload(String[] sts) throws InvalidDataException, ParseException {
        Worker bum = new Worker();
            String name = sts[0].trim();
            SetName.setname(name, bum);
            String x = sts[1].trim();
            String y = sts[2].trim();
            SetCordinates.setcordinates(x, y, bum);
            String salary = sts[3].trim();
            SetSalary.setSalary(salary, bum);
            String startDate = sts[4].trim();
            SetData.setStartData(startDate, bum);
            String endDate = sts[5].trim();
            SetData.setEndData(endDate, bum);
            String birthday = sts[6].trim();
            Person person = new Person();
            SetPersParams.setBirthday(birthday, person);
            String height = sts[7].trim();
            SetPersParams.setHeight(height, person);
            String weight = sts[8].trim();
            SetPersParams.setWeight(weight, person);
            bum.setPerson(person);
            String pos = sts[9].trim();
            SetPosition.setPosition(pos, bum);
            String id = sts[10].trim();
            SetId.setId(id,bum);
            ids.add(Integer.parseInt(id));
            String crdate = sts[11].trim();
            SetData.setCreationData(crdate,bum);
            return bum;
        }

    public static Worker getWorkerById(int id) {
        for (Worker bum : workers) {
            Integer s = bum.getId();
            if (s == id) {
                return bum;
            }

        }
        return null;
    }



    /**
     * begin command
     * create new Worker from file
     */

    public static void begin(String start, LinkedHashSet<Worker> set) {
        Worker worker = new Worker();
        String[] str = start.split("\r\n");
        for (int i = 1; i < str.length; i++) {
            String[] stats = str[i].split(";");
            try {
                worker = upload(stats);
                if (worker.getPosition() == null){
                    throw new EmptyCollectionException();
                }
                set.add(worker);
            } catch (InvalidDataException | ParseException e) {
                logger.warn(e.getMessage());
            }catch (EmptyCollectionException e){
                logger.warn("не вайлидый файл");
            }catch (Exception e){
                    logger.warn("не корректный csv файл, один из элементов не установлен");
            }
        }

    }

    /**
     * id getter
     */

    public static ArrayList<Integer> getIds() {
        return ids;
    }





    public static Worker makeId(Worker bum) {
        Collections.sort(ids);
        for (int i =1; i<ids.size()+2; i++){
            if (!ids.contains(i)){
                bum.setId(i);
                ids.add(i);
                break;
            }
        }
        return bum;
    }

    static boolean test = true;
    public static void runCommandFromString(LinkedHashSet<Worker> workers, String input, CommandRequestDto<? extends Serializable> params) {
        try {
            test = false;
            String[] items = input.split(" ");
            String cmd = items[0].toLowerCase();

            //runCommand(workers, cmd, params);
            runCommand2(workers, cmd, params);
            if (!test){
                logger.warn("no such method");
            }
        } catch (NullPointerException | NoSuchElementException e) {
            funExit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void runCommand2(LinkedHashSet<Worker> workers, String commandName, CommandRequestDto<? extends Serializable> commandParams) {
        for (BaseCommand command: commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                try {
                    command.ExecuteCommand(commandParams, workers);
                    test = true;
                }catch (MissedCommandArgumentException e) {
                    logger.error(e.getMessage());

                }catch (EndStreamException ignored){

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void funExit(){
        runCommandFromString(workers, "save", null);
        logger.info("для выхода я написал комманду exit");
        logger.info(" +\"\"\"\"\"+ ");
        logger.info("[| o o |]");
        logger.info(" |  ^  | ");
        logger.info(" | '-' | ");
        logger.info(" +-----+ ");
        System.exit(1);
    }

}
