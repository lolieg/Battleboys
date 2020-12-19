package me.lolieg.battleboys.commands;

import me.lolieg.battleboys.Battleboys;
import me.lolieg.battleboys.utils.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RunCommand extends BattleboysCommand implements CommandExecutor {
    public RunCommand(Battleboys battleboys) {
        super(battleboys);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.status = Status.RUNNING;
        return true;
    }
}
