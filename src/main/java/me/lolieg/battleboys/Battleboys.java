package me.lolieg.battleboys;


import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import me.lolieg.battleboys.commands.RunCommand;
import me.lolieg.battleboys.commands.StartCommand;
import me.lolieg.battleboys.commands.StopCommand;
import me.lolieg.battleboys.events.Events;
import me.lolieg.battleboys.managers.BossBarManager;
import me.lolieg.battleboys.managers.ScoreboardManager;
import me.lolieg.battleboys.managers.WorldManager;
import me.lolieg.battleboys.mobs.Boss;
import me.lolieg.battleboys.utils.Status;
import me.lolieg.battleboys.utils.Task;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import static me.lolieg.battleboys.utils.Utility.*;

public final class Battleboys extends JavaPlugin {

    public static Logger LOGGER = Bukkit.getLogger();
    public FileConfiguration config = getConfig();

    public int DEFAULT_COUNTDOWN;
    public int DEFAULT_TIMELEFT;
    public int DEFAULT_IDLETIME;
    public int SCORE_TO_WIN;

    public Status status = Status.NOT_STARTED;
    public int countdown = DEFAULT_COUNTDOWN;
    public HashMap<Player, Integer> scores = new HashMap<>();
    public ScoreboardManager scoreboardManager;
    public BossBarManager bossBarManager;
    public WorldManager worldManager;
    public Task task;
    public Random random = new Random();
    public int timeLeft = DEFAULT_TIMELEFT;
    public BukkitTask currentTicker;
    public Material toGather;
    public int idleTime = DEFAULT_IDLETIME;
    public ArrayList<Player> playingPlayers = new ArrayList<>();
    public ArrayList<Player> spectatingPlayers = new ArrayList<>();
    public ArrayList<Player> alivePlayers = new ArrayList<>();
    public HashMap<Player, ItemStack[]> savedInventories = new HashMap<>();
    public HashMap<Player, Location> savedPosition = new HashMap<>();

    public Gui gui;

    @Override
    public void onEnable() {
        init();
    }

    private void init(){
        config.addDefault("default_countdown", 60);
        config.addDefault("default_timeleft", 60);
        config.addDefault("default_idletime", 10);
        config.addDefault("score_to_win", 10);
        config.options().copyDefaults(true);
        saveConfig();
        DEFAULT_COUNTDOWN = config.getInt("default_countdown");
        DEFAULT_TIMELEFT = config.getInt("default_timeleft");
        DEFAULT_IDLETIME = config.getInt("default_idletime");
        SCORE_TO_WIN = config.getInt("score_to_win");

        scoreboardManager = new ScoreboardManager(this);
        bossBarManager = new BossBarManager(this);
        worldManager = new WorldManager(this);
        getServer().getScheduler().runTaskTimer(this, () -> scoreboardManager.updateScoreboard() , 0, 20);
        getServer().getScheduler().runTaskTimer(this, () -> bossBarManager.updateBossBar() , 0, 20);

        getServer().getPluginManager().registerEvents(new Events(this), this);

        this.getCommand("start").setExecutor(new StartCommand(this));
        this.getCommand("stop").setExecutor(new StopCommand(this));
        this.getCommand("run").setExecutor(new RunCommand(this));

        setTaskRandom(Task.FIGHT);
        gui = new Gui(3, "test");
        GuiItem guiItem = new GuiItem(Material.DIAMOND, (event -> {
            event.getWhoClicked().sendMessage(formatString("&aCongrats!"));
        }));
        gui.setItem(2, 3, guiItem);
        gui.getFiller().fill(new GuiItem(Material.BLUE_STAINED_GLASS_PANE));
        gui.setDefaultClickAction((event) -> event.setCancelled(true));
        }

    //TODO make start
    public void start() {
        playingPlayers.addAll(getServer().getOnlinePlayers());
        worldManager.playWorld.getWorldBorder().setSize(100*playingPlayers.size());
        worldManager.playWorld.setTime(1000);
        playingPlayers.forEach((player -> {
            int x = random.nextInt((int) worldManager.playWorld.getWorldBorder().getSize()/2);
            int z = random.nextInt((int) worldManager.playWorld.getWorldBorder().getSize()/2);
            int y = worldManager.playWorld.getHighestBlockYAt(x, z)+1;
            Location location = new Location(worldManager.playWorld, x, y, z);
            if(checkIfWaterBelow(location)){
                location.getWorld().getBlockAt(location).setType(Material.DIRT);
                getServer().getScheduler().scheduleSyncDelayedTask(this, () -> location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY()-1, location.getBlockZ()).setType(Material.WATER), 200);
            }
            location.setY(location.getBlockY()+1);
            player.teleport(location);
            player.getInventory().clear();
        }));
        currentTicker = getServer().getScheduler().runTaskTimer(this, this::tick, 0, 20);
    }

    public void checkToGather(){
        if(toGather == null)
            toGather = Material.values()[random.nextInt(Material.values().length)];
        /*for(int X = -worldManager.size/2; X <= worldManager.size; X += 16)
            for(int Z = -worldManager.size/2; Z <= worldManager.size; Z += 16){
                System.out.println(X + "   " + Z);
                if(toGather.isBlock() && !worldManager.playWorld.getChunkAt(X, Z).contains(toGather.createBlockData())){
                    toGather = null;
                    checkToGather();
                    break;
                }

            }*/
        if(toGather.name().contains("SPAWN") || toGather.name().contains("LEGACY") || toGather.name().contains("NETHER") || toGather.name().contains("END") || toGather.name().contains("HEAD")
                || toGather.name().contains("CRIMSON") || toGather.name().contains("WALL") || toGather.name().contains("SHULKER") || toGather.name().contains("WARPED") || toGather.name().contains("POTTED")
                || toGather.name().contains("BLACKSTONE") || toGather.name().contains("SPONGE") || toGather.name().contains("COMMAND") || toGather.name().contains("CORAL") || toGather.name().contains("TERRACOTTA")
                || toGather.name().contains("JIGSAW") || toGather.name().contains("INFESTED")){
            toGather = null;
            checkToGather();
        }

    }
    public void setTaskRandom(Task taskBefore){
        task = Task.IDLE.getRandom();
        if(task == taskBefore){
            setTaskRandom(taskBefore);
        }
        if(task == Task.FIGHT)
            timeLeft = 120;
    }
    public void tick() {
        scores.forEach((player, score) -> {
            if(score >= SCORE_TO_WIN){
                sendMsg("&9" + player.getDisplayName() + " WON THE GAME!", getServer());
                stop(false);
            }
        });
        if(playingPlayers.size() <= 1){
            if(playingPlayers.size() > 0)
                sendMsg("&9" + playingPlayers.get(0).getDisplayName() + " WON THE GAME!", getServer());
            stop(false);
            return;
        }

        getServer().getOnlinePlayers().forEach((player -> {
            if(playingPlayers.contains(player)){
                player.setGameMode(GameMode.SURVIVAL);
            }else{
                player.setGameMode(GameMode.SPECTATOR);
            }

        }));

        if(task == Task.GATHER){
            checkToGather();
            bossBarManager.setTitle("&b<-- &aGet &c&l" + toGather.name().replace('_', ' ').toLowerCase() + " &b|&d " + timeLeft + " &aseconds left &b-->");
            timeLeft--;
            playingPlayers.forEach((player) -> {
                if(toGather != null && player.getInventory().contains(toGather)){
                    scores.put(player, scores.get(player)+1);
                    task = Task.IDLE;
                    toGather = null;
                    timeLeft = DEFAULT_TIMELEFT;
                }
            });
        }else if(task == Task.FIGHT){
            bossBarManager.setTitle("&b<-- &aFight &b|&d " + timeLeft + " &aseconds left &b-->");
            timeLeft--;
            if(alivePlayers.isEmpty()){
                alivePlayers.addAll(playingPlayers);
                playingPlayers.forEach((player -> {
                    savedPosition.put(player, player.getLocation());
                    int x = random.nextInt((int) worldManager.pvpWorld.getWorldBorder().getSize()/2);
                    int z = random.nextInt((int) worldManager.pvpWorld.getWorldBorder().getSize()/2);
                    int y = worldManager.pvpWorld.getHighestBlockYAt(x, z)+1;
                    Location location = new Location(worldManager.pvpWorld, x, y, z);
                    player.teleport(location);
                    player.setHealth(20);
                    player.setSaturation(20);


                    if(!player.getInventory().isEmpty()) {
                        savedInventories.put(player, player.getInventory().getContents());
                        player.getInventory().clear();
                    }
                    ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
                    goldenApple.setAmount(5);
                    ItemStack cobble = new ItemStack(Material.COBBLESTONE);
                    cobble.setAmount(64);
                    ItemStack steak = new ItemStack(Material.COOKED_BEEF);
                    steak.setAmount(8);
                    player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)});
                    player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
                    player.getInventory().addItem(new ItemStack(Material.IRON_SWORD), new ItemStack(Material.IRON_PICKAXE), cobble);
                    player.getInventory().setItem(44, steak);
                    player.getInventory().setItem(43, goldenApple);
                }));
            }

            if(alivePlayers.size() == 1){
                Player player = alivePlayers.get(0);
                scores.put(player, scores.get(player)+1);
                task = Task.IDLE;
                alivePlayers.clear();
                timeLeft = DEFAULT_TIMELEFT;

                int y1 = worldManager.playWorld.getHighestBlockYAt(0, 0)+1;
                Location location = new Location(worldManager.playWorld, 0, y1, 0);
                getServer().getOnlinePlayers().forEach((player1 -> {
                    player1.setHealth(20);
                    if (savedPosition.containsKey(player1)){
                        player1.teleport(savedPosition.get(player1));
                    }else{
                        player1.teleport(location);
                    }
                }));
                savedPosition.clear();
                getServer().getOnlinePlayers().forEach((player1 -> {
                    if (savedInventories.containsKey(player1)){
                        player1.getInventory().clear();
                        player1.getInventory().addItem(removeNullFromInventoryContents(savedInventories.get(player1), true).toArray(ItemStack[]::new));
                    }else{
                        player1.getInventory().clear();
                    }
                }));
                savedInventories.clear();

                sendMsg("&acleaning up pvp world...", getServer());
                worldManager.cleanUpPvpWorld();

            }
        }else if(task == Task.QUIZ){
            task = Task.GATHER;
        }else if(task == Task.IDLE){
            bossBarManager.setTitle("&b<-- &aGet ready for the next challange! &b|&d " + idleTime + " &aseconds left &b-->");
            --idleTime;
            if(idleTime < 1){
                setTaskRandom(Task.IDLE);
                idleTime = DEFAULT_IDLETIME;
            }
        }
        if(timeLeft < 1){
            toGather = null;
            if(task == Task.FIGHT){
                alivePlayers.clear();
                int y = worldManager.playWorld.getHighestBlockYAt(0, 0)+1;
                Location location = new Location(worldManager.playWorld, 0, y, 0);
                getServer().getOnlinePlayers().forEach((player1 -> {
                    if (savedPosition.containsKey(player1)){
                        player1.teleport(savedPosition.get(player1));
                    }else{
                        player1.teleport(location);
                    }
                }));
                savedPosition.clear();
                getServer().getOnlinePlayers().forEach((player1 -> {
                    if (savedInventories.containsKey(player1)){
                        player1.getInventory().clear();
                        player1.getInventory().addItem(removeNullFromInventoryContents(savedInventories.get(player1), true).toArray(ItemStack[]::new));
                    }else{
                        player1.getInventory().clear();
                    }
                }));
                savedInventories.clear();
                sendMsg("&acleaning up pvp world...", getServer());
                worldManager.cleanUpPvpWorld();
            }

            timeLeft = DEFAULT_TIMELEFT;
            setTaskRandom(task);
        }

    }

    public void stop(boolean shutdown){
        sendMsg("&3Stopping..", getServer());
        status = Status.NOT_STARTED;
        if(currentTicker != null){
            currentTicker.cancel();
        }
        playingPlayers.clear();
        spectatingPlayers.clear();
        scores.clear();
        toGather = null;
        alivePlayers.clear();
        idleTime = DEFAULT_IDLETIME;
        timeLeft = DEFAULT_TIMELEFT;
        countdown = DEFAULT_COUNTDOWN;
        World world = getServer().getWorld("world");
        int y = world.getHighestBlockYAt(0, 0)+1;
        Location location = new Location(world, 0, y, 0);
        getServer().getOnlinePlayers().forEach((player) -> {
            player.teleport(location);
            player.setGameMode(GameMode.CREATIVE);

            player.getInventory().clear();
            player.setHealth(20);
            player.setSaturation(20);
            Netherboard.instance().getBoard(player).clear();
            Netherboard.instance().deleteBoard(player);
            Netherboard.instance().removeBoard(player);
            BPlayerBoard board = Netherboard.instance().createBoard(player, formatString("&b<----&a&lBattleBoys&b&l--->"));
            board.set(" ", 1);

        });
        worldManager.cleanUpPvpWorld();
        File playWorld = Bukkit.getWorld("play_world").getWorldFolder();
        Bukkit.unloadWorld(worldManager.playWorld, false);
        deleteDirectory(playWorld);

        bossBarManager.setTitleOverride(formatString("&4&lLAG WARNING! REGENERATING PLAYING WORLD"));
        scoreboardManager.setScoreboardOverride(formatString("&4&lLAG WARNING!"));
        if(!shutdown)
            worldManager.playWorld = worldManager.createOrGetPlayWorld();
        bossBarManager.setTitleOverride(null);
        scoreboardManager.setScoreboardOverride(null);
        sendMsg("&3Stopped.", getServer());

    }

    @Override
    public void onDisable() {
        stop(true);

    }
}
