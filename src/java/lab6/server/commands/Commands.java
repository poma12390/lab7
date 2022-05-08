package lab6.server.commands;

import lab6.common.*;
import lab6.common.dto.CommandRequestDto;
import lab6.common.exceptions.EndStreamException;
import lab6.common.exceptions.MissedCommandArgumentException;
import lab6.server.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public static void temporaryStart(String filename) {
        database = Database.getInstance();
        open(filename);
        // begin(start, workers);
    }

    public static int getRandomNumber() {
        return (int) ((Math.random() * 1000));
    }

    /**
     * open command
     *
     * @param filename file name
     */
    public static void open(String filename) {
        try (InputStream is = Files.newInputStream(Paths.get(filename))) {
            try (BufferedInputStream bis = new BufferedInputStream(is)) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
            } catch (Exception e) {
                if (e.getMessage().isEmpty()) {
                    logger.warn(e.getCause().getMessage());
                } else {
                    logger.warn(e.getMessage());
                }
            }
        } catch (Exception e) {
            if (e.getMessage().isEmpty()) {
                logger.warn(e.getCause().getMessage());
            } else {
                logger.warn(e.getMessage());
            }
        }
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

    public static void addWorkerToDataBase(Worker bum) {
        String name = bum.getName();
        long x = bum.getCoordinates().getX();
        int y = bum.getCoordinates().getY();
        String creationDate = String.valueOf(bum.getCreationDate());
        float salary = bum.getSalary();
        String startDate = String.valueOf(bum.getStartDate());
        String endDate = String.valueOf(bum.getEndDate());
        Position pos = bum.getPosition();
        String birthday = String.valueOf(bum.getPerson().getBirthday());
        float height = bum.getPerson().getHeight();
        float weight = bum.getPerson().getWeight();
        try {
            int req = Commands.getDatabase().executeUpdate("INSERT INTO workers VALUES (nextval('WorkerIdSetter'), 'poma',1,2, '2022-02-02' , 25 , '2010-04-04' , '2020-05-05' , 'DIRECTOR' , ?, 12.1 , 13.0)", bum.getCreationDate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * begin command
     * create new Worker from file
     */

    private static Worker dataBaseToWorker(ResultSet set) throws SQLException {
        Worker bum = null;
        try {
            bum = new Worker(set.getString("username"));
            bum.setId(set.getInt("id"));
            bum.setName(set.getString("name"));
            Coordinates cord = new Coordinates();
            cord.setXY(set.getInt("x"), set.getInt("y"));
            bum.setCoordinates(cord);
            bum.setCreationDate(set.getDate("creationdate"));
            bum.setSalary(set.getFloat("salary"));
            bum.setStartDate(set.getDate("startdate"));
            bum.setEndDate(set.getDate("enddate"));
            bum.setPosition(Position.valueOf(set.getString("pos")));
            java.sql.Date bith = java.sql.Date.valueOf(String.valueOf(set.getDate("birthday")));
            java.util.Date date1 = new java.util.Date(bith.getTime());
            ZonedDateTime d = ZonedDateTime.ofInstant(date1.toInstant(),
                    ZoneId.systemDefault());
            Person pers = new Person();
            pers.setBirthday(d);
            pers.setHeight(set.getFloat("height"));
            pers.setWeight(set.getFloat("weight"));
            bum.setPerson(pers);
            System.out.println(bum.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bum;
    }

    public static void dataBaseToCollection() {
        try {
            ResultSet set = database.executeQuery("select * from workers");
            while (set.next()) {
                workers.add(dataBaseToWorker(set));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        for (int i = 1; i < ids.size() + 2; i++) {
            if (!ids.contains(i)) {
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
            if (!test) {
                logger.warn("no such method");
            }
        } catch (NullPointerException | NoSuchElementException e) {
            funExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runCommand2(LinkedHashSet<Worker> workers, String commandName, CommandRequestDto<? extends Serializable> commandParams) {
        for (BaseCommand command : commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                try {
                    command.ExecuteCommand(commandParams, workers);
                    test = true;
                } catch (MissedCommandArgumentException e) {
                    logger.error(e.getMessage());

                } catch (EndStreamException ignored) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean checkAuth(CommandRequestDto<? extends Serializable> commandResponseDto) {
        String login = commandResponseDto.getLogin();
        String password = commandResponseDto.getPassword();
        boolean c = false;
        Database database = Commands.getDatabase();
        if (login == null || password == null) {
            return c;
        }
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

    public static void funExit() {
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
