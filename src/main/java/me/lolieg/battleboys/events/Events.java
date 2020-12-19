package me.lolieg.battleboys.events;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import me.lolieg.battleboys.Battleboys;
import me.lolieg.battleboys.mobs.Boss;
import me.lolieg.battleboys.utils.Status;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

import static me.lolieg.battleboys.utils.Utility.*;

public class Events implements Listener {
    private final Battleboys plugin;

    public Events(Battleboys battleboys) {
        this.plugin = battleboys;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        /*Boss boss = new Boss(((CraftServer) plugin.getServer()).getServer(), ((CraftWorld)plugin.getServer().getWorld("world")).getHandle(), "&4BOSS");
        boss.setPosition(20, 60, 20);
        boss.setSkin(UUID.fromString("e81fac2f-58c0-46bd-b2e8-e5111107f46c"));
        ((CraftWorld) plugin.getServer().getWorld("world")).addEntity(boss, CreatureSpawnEvent.SpawnReason.CUSTOM);
        boss.teleportTo(((CraftWorld) plugin.getServer().getWorld("world")).getHandle(), new BlockPosition(event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY(), event.getPlayer().getLocation().getZ()));
        PlayerConnection connection = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, boss));*/

        BPlayerBoard board = Netherboard.instance().createBoard(event.getPlayer(), formatString("&b<----&a&lBattleBoys&b&l--->"));
        board.set(" ", 1);
        if(plugin.status == Status.NOT_STARTED ||plugin.status == Status.STARTED){
            event.getPlayer().teleport(new Location(plugin.getServer().getWorld("world"), 0, plugin.getServer().getWorld("world").getHighestBlockYAt(0,0), 0));
            event.getPlayer().getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Netherboard.instance().removeBoard(event.getPlayer());
        Netherboard.instance().deleteBoard(event.getPlayer());
        plugin.playingPlayers.remove(event.getPlayer());
        plugin.spectatingPlayers.add(event.getPlayer());
        if(event.getPlayer().getWorld() == plugin.worldManager.pvpWorld){
            event.getPlayer().spigot().respawn();
            if(plugin.alivePlayers.size() > 0 && plugin.alivePlayers.contains(event.getPlayer())){
                plugin.alivePlayers.remove(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(event.getEntity().getWorld() == plugin.worldManager.pvpWorld){
            event.getEntity().spigot().respawn();
            if(plugin.alivePlayers.size() > 0 && plugin.alivePlayers.contains(event.getEntity())){
                plugin.alivePlayers.remove(event.getEntity());
            }
            event.getEntity().setHealth(20);
        }
    }
    @EventHandler
    public void damage(EntityDamageEvent ev)
    {
        if(ev.getEntity() instanceof Player && plugin.status == Status.RUNNING && ev.getEntity().getWorld() != plugin.worldManager.pvpWorld){
            Player player = (Player) ev.getEntity();
            if ((player.getHealth() - ev.getFinalDamage()) <= 0){
                int x = plugin.random.nextInt((int) plugin.worldManager.playWorld.getWorldBorder().getSize()/2);
                int z = plugin.random.nextInt((int) plugin.worldManager.playWorld.getWorldBorder().getSize()/2);
                int y = plugin.worldManager.playWorld.getHighestBlockYAt(x, z)+1;
                Location location = new Location(plugin.worldManager.playWorld, x, y, z);
                if(checkIfWaterBelow(location)){
                    location.getWorld().getBlockAt(location).setType(Material.DIRT);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY()-1, location.getBlockZ()).setType(Material.WATER), 200);
                }
                location.setY(location.getBlockY()+1);
                ev.getEntity().teleport(location);
                player.setHealth(20);
                if(!player.getInventory().isEmpty()) {
                    ArrayList<ItemStack> contents = removeNullFromInventoryContents(((Player) ev.getEntity()).getInventory().getContents(), false);
                    ItemStack itemStack;
                    if(contents.size() > 1){
                        itemStack = contents.get(plugin.random.nextInt(contents.size()-1));
                    }else{
                        itemStack = contents.get(0);
                    }

                    player.getInventory().remove(itemStack);
                    sendMsgtoPlayer(formatString("&4Removed random Item from your inventory, because of your death. &dItem: " + itemStack.getType().name().replace('_', ' ').toLowerCase()), player, player.getServer());
                }
                ev.setCancelled(true);

            }
        }

    }

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getPlayer().getWorld().equals(plugin.worldManager.pvpWorld))
            event.setCancelled(true);
    }*/
}
