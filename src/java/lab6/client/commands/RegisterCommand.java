package lab6.client.commands;

import lab6.client.inputters.InputUtils;
import lab6.common.Transformer;
import lab6.common.dto.*;
import lab6.common.exceptions.InvalidDateFormatException;
import lab6.common.exceptions.InvalidEndDateException;
import lab6.common.exceptions.InvalidSalaryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class RegisterCommand extends BaseCommand{
    private static final Logger logger
            = LoggerFactory.getLogger(RegisterCommand.class);
    @Override
    protected void Execute(List<String> params) throws IOException, InvalidSalaryException, InvalidDateFormatException, ParseException, InvalidEndDateException {
        ParamsChecker.checkParams(0,params);
        boolean c = true;
        while (c){
        RegisterCommandDto dto = new RegisterCommandDto();
        dto.setLogin(InputUtils.inputString("login"));
        dto.setPassword(Transformer.Encrypt(InputUtils.inputString("password"),"1"));

        CommandRequestDto<RegisterCommandDto> crd = new CommandRequestDto<>(getName(), dto);
        byte[] buf = serverCaller.sendToServer(transformer.Serialize(crd));
        CommandResponseDto response = (CommandResponseDto) transformer.DeSerialize(buf);
        if ("success".equals(response.getResponse()))
            c = false;
            logger.info(response.getResponse());
        }
    }
}
