package me.lolieg.battleboys.managers;

import me.lolieg.battleboys.Battleboys;
import org.bukkit.*;


public class WorldManager {
    private final Battleboys plugin;
    public World pvpWorld;
    public World playWorld;
    public int size = 100;

    public WorldManager(Battleboys battleboys) {
        this.plugin = battleboys;
        this.pvpWorld = createOrGetPvpWorld();
        this.playWorld = createOrGetPlayWorld();
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if(plugin.worldManager.playWorld != null){
                size = plugin.playingPlayers.size() > 1 ? plugin.playingPlayers.size()*100 : 100;
                playWorld.getWorldBorder().setSize(size);
            }
        }, 0, 1);
    }

    public World createOrGetPvpWorld(){
        World world = Bukkit.getWorld("pvp_world");
        if(world == null){
            world = Bukkit.createWorld(new WorldCreator("pvp_world").environment(World.Environment.NORMAL).type(WorldType.FLAT).generateStructures(false));
        }
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setSpawnLocation(0, world.getHighestBlockAt(0,0).getY(), 0);
        world.setPVP(true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(0,0);
        worldBorder.setSize(20);
        worldBorder.setWarningDistance(10);
        return world;
    }

    public World createOrGetPlayWorld() {
        World world = Bukkit.getWorld("play_world");
        if(world == null){
            world = Bukkit.createWorld(new WorldCreator("play_world").environment(World.Environment.NORMAL).type(WorldType.NORMAL).generateStructures(true).seed(plugin.random.nextLong()));
        }

        world.setSpawnLocation(0, world.getHighestBlockAt(0,0).getY(), 0);
        world.setPVP(false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(0,0);
        worldBorder.setSize(size);
        worldBorder.setWarningDistance(10);
        return world;
    }

    public void cleanUpPvpWorld(){
        for(int x = (int) (-pvpWorld.getWorldBorder().getSize()/2); x < pvpWorld.getWorldBorder().getSize(); x++)
            for(int z = (int) (-pvpWorld.getWorldBorder().getSize()/2); z < pvpWorld.getWorldBorder().getSize(); z++)
                for(int y = 4; y < pvpWorld.getMaxHeight(); y++){
                    if(!pvpWorld.getBlockAt(x, y ,z).isEmpty())
                        pvpWorld.getBlockAt(x, y, z).setType(Material.AIR);
                }
    }

}
