package me.lolieg.battleboys.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

public class Utility {
    public static String formatString(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static void sendMsg(String s, Server server){
        server.broadcastMessage(formatString("&6[&aBattleBoys&6] &r" + s));
    }
    public static void sendMsgtoPlayer(String s, Player player, Server server){
        player.sendMessage(formatString("&6[&aBattleBoys&6] &r" + s));
    }
    public static ArrayList<ItemStack> removeNullFromInventoryContents(ItemStack[] inv, boolean replaceWithAir){
        ArrayList<ItemStack> contents = new ArrayList<>();
        for(ItemStack itemStack : inv){
            if(itemStack != null){
                contents.add(itemStack);
            } else if(replaceWithAir){
                contents.add(new ItemStack(Material.AIR));
            }
        }
        return contents;
    }
    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }
    static public boolean checkIfWaterBelow(Location location){
        location.setY(location.getBlockY()-1);
        return location.getBlock().getType() == Material.WATER;
    }



}
