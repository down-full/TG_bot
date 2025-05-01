package org.example.bot;
import java.util.Map;

public class HelpCommand extends Executer implements Command{
    private Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Расскажет о доступных командах";
    }

    public void execute(Long chatId, String messageText) {
        if(messageText.equals("/help")){
            StringBuilder helpMessage = new StringBuilder("Список достпуных команд\n\n");
            for(Command cmd : commands.values()){
                helpMessage.append(cmd.getName()).append("-").append(cmd.getDescription()).append("\n");
            }
            sendMessage(chatId, helpMessage.toString());
        }
        else{
            String cmdName = messageText.substring(6).trim();
            Command cmd = commands.get(cmdName);
            if(cmd!=null){
                sendMessage(chatId, cmd.getDescription());
            }
            else{
                sendMessage(chatId, "Такой команды нет, воспользуйтесь /help");
            }
        }

    }
    @Override
    public void register(Map<String, Command> registry) {

        registry.put(getName(), this);
    }
}
