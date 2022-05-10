package lab6.server.commands;

import lab6.common.Transformer;
import lab6.common.Worker;
import lab6.common.dto.CommandRequestDto;
import lab6.common.dto.CommandResponseDto;
import lab6.common.dto.RemoveLowerCommandDto;
import lab6.common.exceptions.EmptyCollectionException;
import lab6.server.ClientCaller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


public class RemoveLowerCommand extends BaseCommand {
    @Override
    public String getName() {
        return "remove_lower";
    }

    /**
     * removeLower command
     * remove lower element from collection
     */

    @Override
    protected void Execute(CommandRequestDto<? extends Serializable> params, LinkedHashSet<Worker> set, Transformer transformer, ClientCaller clientCaller) {
        boolean auth = Commands.checkAuth(params);
        CommandResponseDto<RemoveLowerCommandDto> dto = new CommandResponseDto<>((RemoveLowerCommandDto) params.getCommandArgs());
        if (!auth) {
            dto.setResponse("you should be authorized");
        } else {
            if (set.size() == 0) {
                dto.setResponse("Collection is empty");
                clientCaller.sendToClient(transformer.serialize(dto));
                throw new EmptyCollectionException();
            }
            List<Worker> set1 = set.stream().filter((p) -> p.getUser().equals(params.getLogin())).collect(Collectors.toList());
            Worker min = Collections.min(set1);
            try {
                int count = Commands.getDatabase().executeUpdate("delete from workers where username = ? and name = ? and salary = ? and creationdate = ?", params.getLogin(), min.getName(), min.getSalary(), min.getCreationDate());
                dto.setResponse("success");
                set.remove(min);
            } catch (SQLException ignored) {

            }

        }
        clientCaller.sendToClient(transformer.serialize(dto));
    }
}
