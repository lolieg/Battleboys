package me.lolieg.battleboys.managers;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import me.lolieg.battleboys.Battleboys;
import me.lolieg.battleboys.utils.Status;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static me.lolieg.battleboys.utils.Utility.formatString;
import static me.lolieg.battleboys.utils.Utility.sendMsg;

public class ScoreboardManager {
    private final Battleboys plugin;
    private String scoreboardOverride;

    public ScoreboardManager(Battleboys battleboys){
        this.plugin = battleboys;
    }

    public void updateScoreboard(){
        if(Netherboard.instance().getBoards().isEmpty())
            return;

        for(Player player : Bukkit.getOnlinePlayers()){
            if(Netherboard.instance().getBoard(player) == null){
                BPlayerBoard board = Netherboard.instance().createBoard(player, formatString("&b<----&a&lBattleBoys&b&l--->"));
                board.set(" ", 1);
            }
        }

        if(scoreboardOverride != null){
            Netherboard.instance().getBoards().forEach((player, playerBoard) -> {
                playerBoard.clear();
                playerBoard.set(scoreboardOverride, 1);
            });
        }

        else if(plugin.status == Status.NOT_STARTED){
            plugin.getServer().getOnlinePlayers().forEach((player) -> {
                player.setGameMode(GameMode.CREATIVE);
            });
            plugin.countdown = plugin.DEFAULT_COUNTDOWN;
            BPlayerBoard board = (BPlayerBoard) Netherboard.instance().getBoards().values().toArray()[0];
            String newString = !board.get(1).contains("Waiting") ? formatString("&4&lWaiting") : board.get(1);

            int matches = StringUtils.countMatches(board.get(1), ".");
            if(matches >= 3){
                newString = newString.substring(0, newString.length()-3);
            }else {
                newString += ".";
            }
            String finalNewString = newString;
            Netherboard.instance().getBoards().forEach((player, playerBoard) -> {
                playerBoard.clear();
                playerBoard.set(finalNewString, 1);
            });
        }else if(plugin.status == Status.STARTED){
            Netherboard.instance().getBoards().forEach((player, board) -> {
                board.clear();
                board.set(formatString("&9Players: ") + plugin.getServer().getOnlinePlayers().size() + formatString("&9/") + plugin.getServer().getMaxPlayers() + formatString(" &7(2 needed)"), 2);
                board.set(formatString("&2Starting in &5&l" + String.valueOf(plugin.countdown) + " &2Seconds"), 1);
            });
            if(plugin.countdown < 1 && plugin.getServer().getOnlinePlayers().size() >= 2){
                plugin.status = Status.RUNNING;
                plugin.start();
            }else if(plugin.countdown < 1){
                plugin.countdown = plugin.DEFAULT_COUNTDOWN+1;
                sendMsg(formatString("&4Couldn't start! Not enough players on the server!"), plugin.getServer());
            }
            plugin.countdown--;
        }else if(plugin.status == Status.RUNNING){
            if(plugin.scores.isEmpty()){
                plugin.playingPlayers.forEach((player -> {
                    plugin.scores.put(player, 0);
                }));
            }

            ArrayList<Map.Entry<Player, Integer>> scoresSorted =  plugin.scores.entrySet().stream().sorted((Map.Entry.comparingByValue())).collect(Collectors.toCollection(ArrayList::new));
            Collections.reverse(scoresSorted);

            Netherboard.instance().getBoards().forEach((player, board) -> {
                ArrayList<String> scoreStrings = new ArrayList<>();
                scoreStrings.add(formatString("&c&nLeaderboard"));
                scoresSorted.forEach((entry) -> {
                    scoreStrings.add(formatString("&7"+ (entry.getKey().equals(player) ? "&l" : "") + (!plugin.playingPlayers.contains(entry.getKey()) ? "&m" : "") + entry.getKey().getDisplayName() + ": &d" + entry.getValue()));
                });
                board.setAll(scoreStrings.toArray(String[]::new));
                if(plugin.scores.get(player) != null && plugin.playingPlayers.contains(player)){
                    board.set(formatString("&l&6Your Score: &d" + plugin.scores.get(player)), board.getLines().size() + 1);
                }

            });
        }
    }

    public void setScoreboardOverride(String scoreboardOverride) {
        this.scoreboardOverride = scoreboardOverride;
        updateScoreboard();
    }

}
