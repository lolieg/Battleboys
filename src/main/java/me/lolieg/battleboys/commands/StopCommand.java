package me.lolieg.battleboys.commands;

import me.lolieg.battleboys.Battleboys;
import me.lolieg.battleboys.utils.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.lolieg.battleboys.utils.Utility.formatString;
import static me.lolieg.battleboys.utils.Utility.sendMsg;

public class StopCommand extends BattleboysCommand implements CommandExecutor {

    public StopCommand(Battleboys battleboys) {
        super(battleboys);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if (plugin.status == Status.STARTED) {
                plugin.status = Status.NOT_STARTED;
                plugin.countdown = plugin.DEFAULT_COUNTDOWN;
                sendMsg(formatString("&3Stopping.."), plugin.getServer());
            } else if (plugin.status == Status.NOT_STARTED) {
                sendMsg(formatString("&4Already Stopped!"), plugin.getServer());
            } else {
                //TODO Maybe add /forcestop or make the game stop with this command.
                plugin.stop(false);
            }
            return true;
        }
        plugin.getServer().shutdown();
        return true;
    }
}
