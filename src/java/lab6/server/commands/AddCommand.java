package lab6.server.commands;

import lab6.common.Transformer;
import lab6.common.Worker;
import lab6.common.dto.AddCommandDto;
import lab6.common.dto.CommandRequestDto;
import lab6.common.dto.CommandResponseDto;
import lab6.common.dto.WorkerDto;
import lab6.server.ClientCaller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class AddCommand extends BaseCommand {
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
            lab6.server.commands.Commands.makeId(bum);
            try {
                int req = Commands.getDatabase().executeUpdate("INSERT INTO workers VALUES (nextval('WorkerIdSetter'), 'poma',1,2, '2022-02-02' , 25 , '2010-04-04' , '2020-05-05' , 'DIRECTOR' , ?, 12.1 , 13.0)", bum.getCreationDate());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            dto.setResponse("success");
        }
        clientCaller.sendToClient(transformer.serialize(dto));
    }
}