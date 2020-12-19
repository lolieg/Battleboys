package me.lolieg.battleboys.managers;

import me.lolieg.battleboys.Battleboys;
import me.lolieg.battleboys.utils.Status;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import static me.lolieg.battleboys.utils.Utility.formatString;

public class BossBarManager {
    private final Battleboys plugin;
    private BossBar bossBar;
    private String titleOverride;

    public BossBarManager(Battleboys battleboys) {
        this.plugin = battleboys;
        this.bossBar = Bukkit.createBossBar(formatString("&b<----&a&lBattleBoys&b&l--->"), BarColor.WHITE, BarStyle.SOLID);
    }

    public void updateBossBar(){
        plugin.getServer().getOnlinePlayers().forEach((player -> {
            if(!bossBar.getPlayers().contains(player))
                bossBar.addPlayer(player);
        }));
        if(titleOverride != null)
            setTitle(titleOverride);
        else if(plugin.status == Status.NOT_STARTED){
            setTitle("&b<----&a&lBattleBoys&b&l--->");
            if(plugin.currentTicker != null)
                plugin.currentTicker.cancel();
        }
    }
    public void setTitle(String title){
        bossBar.setTitle(formatString(title));
    }
    public void setTitleOverride(String titleOverride){
        this.titleOverride = titleOverride;
        this.updateBossBar();
    }
}
