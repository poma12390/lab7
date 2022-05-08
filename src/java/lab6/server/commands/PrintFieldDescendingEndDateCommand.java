package lab6.server.commands;

import lab6.common.Transformer;
import lab6.common.Worker;
import lab6.common.dto.CommandRequestDto;
import lab6.common.dto.CommandResponseDto;
import lab6.common.dto.PrintFieldDescendingEndDateCommandDto;
import lab6.server.ClientCaller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrintFieldDescendingEndDateCommand extends BaseCommand {
    @Override
    public String getName() {
            return "print_field_descending_end_date";
    }

    /**
     * printFieldDescendingDate command
     * show sorted endDate
     */

    @Override
    protected void Execute(CommandRequestDto<? extends Serializable> params, LinkedHashSet<Worker> set, Transformer transformer, ClientCaller clientCaller) {
        //TODO fork join pool sort
        boolean auth = Commands.checkAuth(params);
        PrintFieldDescendingEndDateCommandDto dts = new PrintFieldDescendingEndDateCommandDto();
        CommandResponseDto<PrintFieldDescendingEndDateCommandDto> dto = new CommandResponseDto<>(dts);
        if (!auth) {
            dto.setResponse("you should be authorized");
        } else {
            List<Date> dates = Arrays.stream(set.stream().flatMap((p) -> Stream.of(p.getEndDate())).toArray(Date[]::new)).sorted().collect(Collectors.toList());
            // На часах 5:20 Жестко переписал с Stream Api

            dts.setDates(dates);
        }
        clientCaller.sendToClient(transformer.serialize(dto));
    }
}
