package lab6.server.commands;

import lab6.common.Transformer;
import lab6.common.Worker;
import lab6.common.dto.CommandRequestDto;
import lab6.server.ClientCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class SaveCommand extends BaseCommand {
    private static final Logger logger
            = LoggerFactory.getLogger(SaveCommand.class);

    /**
     * save command
     * save collection in csv file
     */

    @Override
    protected void Execute(CommandRequestDto<? extends Serializable> params, LinkedHashSet<Worker> set, Transformer transformer, ClientCaller clientCaller) throws IOException {
        OutputStream outputStream = new FileOutputStream("save.csv");
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        outputStreamWriter.write("name;x;y;salary;startDate;endDate;birthday;height;weight;position;id;creationDate\r\n");
        Iterator<Worker> it1 = set.iterator();
        while (it1.hasNext()) {
            Worker bum = it1.next();
            logger.info(bum.toString());
            outputStreamWriter.write(bum.toString());
            //outputStreamWriter.write((bum.getName()+";"+Long.toString(bum.getCoordinates().getX())+";"+Integer.toString(bum.getCoordinates().getY())+";"+Float.toString(bum.getSalary())+";"+df.format(bum.getStartDate())+";"+df.format(bum.getEndDate())+";"+bum.getPerson().getBirthday().format(formatter)+";"+Float.toString(bum.getPerson().getHeight())+";"+Float.toString(bum.getPerson().getWeight())+";"+bum.getPosition().toString())+"\r\n");
        }
        outputStreamWriter.close();
    }
}
