package lab6.server.commands;

import lab6.common.Transformer;
import lab6.common.Worker;
import lab6.common.dto.AddCommandDto;
import lab6.common.dto.CommandRequestDto;
import lab6.common.dto.CommandResponseDto;
import lab6.common.dto.WorkerDto;
import lab6.server.ClientCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedHashSet;

public class AddCommand extends BaseCommand {
    private static final Logger logger
            = LoggerFactory.getLogger(AddCommand.class);
    /**
     * add command
     * add new Worker and set stats
     */

    @Override
    protected void Execute(CommandRequestDto<? extends Serializable> params, LinkedHashSet<Worker> set, Transformer transformer, ClientCaller clientCaller) {
        AddCommandDto addCommandDto = (AddCommandDto) params.getCommandArgs();
        CommandResponseDto<AddCommandDto> dto = new CommandResponseDto<>(addCommandDto);
        boolean auth = Commands.checkAuth(params);
        if (!auth) {
            dto.setResponse("you should be authorized");
        } else {
            WorkerDto workerDto = addCommandDto.getBum();
            Worker bum = Transformer.WorkerDtoToWorker(workerDto);
            System.out.println(bum);
            try {
                bum.setId(Commands.addWorkerToDataBase(bum));
                set.add(bum);
            }catch (RuntimeException e){
                logger.warn(e.getMessage());
            }


            dto.setResponse("success");
        }
        clientCaller.sendToClient(transformer.serialize(dto));
    }
}
