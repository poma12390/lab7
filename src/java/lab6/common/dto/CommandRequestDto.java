package lab6.common.dto;

import java.io.Serializable;

public class CommandRequestDto<T extends Serializable> implements Serializable {
    private String commandName;
    private T commandArgs;

    private String login;
    private String password;

    public CommandRequestDto(String commandName, T commandArgs) {
        this.commandName = commandName;
        this.commandArgs = commandArgs;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public T getCommandArgs() {
        return commandArgs;
    }

    public void setCommandArgs(T commandArgs) {
        this.commandArgs = commandArgs;
    }
}
