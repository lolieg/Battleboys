package me.lolieg.battleboys.commands;

import me.lolieg.battleboys.Battleboys;
import me.lolieg.battleboys.utils.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.lolieg.battleboys.utils.Utility.formatString;
import static me.lolieg.battleboys.utils.Utility.sendMsg;

public class StartCommand extends BattleboysCommand implements CommandExecutor {

    public StartCommand(Battleboys battleboys){
        super(battleboys);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if (plugin.status == Status.NOT_STARTED) {
                plugin.status = Status.STARTED;
                sendMsg(formatString("&3Starting.."), plugin.getServer());
            } else if (plugin.status == Status.STARTED) {
                sendMsg(formatString("&4Already Started!"), plugin.getServer());
            } else {
                sendMsg(formatString("&4The Game is running!"), plugin.getServer());
            }
        }
        return true;
    }
}
