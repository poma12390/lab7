package lab6.client.commands;

import lab6.client.inputters.InputUtils;
import lab6.client.memory.LoginPassword;
import lab6.common.Transformer;
import lab6.common.dto.AuthCommandDto;
import lab6.common.dto.CommandRequestDto;
import lab6.common.dto.RegisterCommandDto;
import lab6.common.exceptions.InvalidDateFormatException;
import lab6.common.exceptions.InvalidEndDateException;
import lab6.common.exceptions.InvalidSalaryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class AuthCommand extends BaseCommand{
    private static final Logger logger
            = LoggerFactory.getLogger(AuthCommand.class);



    @Override
    protected void Execute(List<String> params) throws IOException, InvalidSalaryException, InvalidDateFormatException, ParseException, InvalidEndDateException {
        ParamsChecker.checkParams(0,params);
        LoginPassword.setPassword(null);
        LoginPassword.setLogin(null);

        AuthCommandDto dto = new AuthCommandDto();
        dto.setLogin(InputUtils.inputString("login"));
        dto.setPassword(Transformer.Encrypt(InputUtils.inputString("password"),"1"));

        CommandRequestDto<AuthCommandDto> crd = new CommandRequestDto<>(getName(), dto);
        System.out.println(crd.getCommandArgs().getPassword());

    }
}
