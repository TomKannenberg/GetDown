package net.gc.getdown;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.*;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.lang.reflect.Field;
import java.util.*;


public final class GetDown extends JavaPlugin implements Listener {

    ArrayList<Material> materials = new ArrayList<Material>();

    boolean jumpmode = true;

    HashMap<Player, Integer> bottomCounter = new HashMap<Player, Integer>();
    private PlayerInteractEvent event;

    public final static int originRadius = 50;
    int radius = originRadius;
    int height = 150;

    @Override
    public void onEnable() {
        createScoreboard();
        scoreboardLoop();
        createInventories();

        //-212 -22 -464
        pvpspawns.add(new Location(Bukkit.getWorld("world"), -212, -22, -464));
        //-128 -17 -494
        pvpspawns.add(new Location(Bukkit.getWorld("world"), -128, -17, -494));
        //-217 -17 -508
        pvpspawns.add(new Location(Bukkit.getWorld("world"), -217, -17, -508));
        //-161 -16 -428
        pvpspawns.add(new Location(Bukkit.getWorld("world"), -161, -16, -428));

        //variety of red blocks
        materials.add(Material.WHITE_WOOL);
        materials.add(Material.RED_WOOL);
        materials.add(Material.ORANGE_WOOL);
        materials.add(Material.YELLOW_WOOL);
        materials.add(Material.LIME_WOOL);
        materials.add(Material.GREEN_WOOL);
        materials.add(Material.CYAN_WOOL);
        materials.add(Material.MAGENTA_WOOL);
        materials.add(Material.PINK_WOOL);
        materials.add(Material.RED_CONCRETE);
        materials.add(Material.ORANGE_CONCRETE);
        materials.add(Material.YELLOW_CONCRETE);
        materials.add(Material.LIME_CONCRETE);
        materials.add(Material.GREEN_CONCRETE);
        materials.add(Material.CYAN_CONCRETE);
        materials.add(Material.MAGENTA_CONCRETE);
        materials.add(Material.PINK_CONCRETE);

        //lucky blocks
        lblockTypes.add(Material.TERRACOTTA);
        lblockTypes.add(Material.WHITE_TERRACOTTA);
        lblockTypes.add(Material.LIGHT_GRAY_TERRACOTTA);
        lblockTypes.add(Material.GRAY_TERRACOTTA);
        lblockTypes.add(Material.BLACK_TERRACOTTA);
        lblockTypes.add(Material.BROWN_TERRACOTTA);
        lblockTypes.add(Material.RED_TERRACOTTA);
        lblockTypes.add(Material.PINK_TERRACOTTA);
        lblockTypes.add(Material.MAGENTA_TERRACOTTA);
        lblockTypes.add(Material.PURPLE_TERRACOTTA);
        lblockTypes.add(Material.BLUE_TERRACOTTA);
        lblockTypes.add(Material.CYAN_TERRACOTTA);
        lblockTypes.add(Material.LIGHT_BLUE_TERRACOTTA);
        lblockTypes.add(Material.GREEN_TERRACOTTA);
        lblockTypes.add(Material.LIME_TERRACOTTA);
        lblockTypes.add(Material.YELLOW_TERRACOTTA);
        lblockTypes.add(Material.ORANGE_TERRACOTTA);

        //create 50 radius cylinder hollow
        World world = getServer().getWorld("world");
        Location loc = new Location(world, 0, 5, 0);
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        ArrayList<Material> materials = new ArrayList<Material>();
        //white and solid blocks (no glass)
        materials.add(Material.WHITE_WOOL);
        materials.add(Material.WHITE_CONCRETE);
        materials.add(Material.WHITE_CONCRETE_POWDER);
        materials.add(Material.QUARTZ_BLOCK);
        materials.add(Material.QUARTZ_BRICKS);
        materials.add(Material.QUARTZ_PILLAR);
        materials.add(Material.WHITE_GLAZED_TERRACOTTA);
        materials.add(Material.BONE_BLOCK);
        materials.add(Material.IRON_BLOCK);

        for (int i = 0; i < height; i++) {
            for (int x1 = -radius; x1 < radius; x1++) {
                for (int z1 = -radius; z1 < radius; z1++) {
                    if (inRadius(x1, 0, z1, radius) && !inRadius(x1, 0, z1, radius - 1)) {
                        Block block = world.getBlockAt(x + x1, y + i, z + z1);
                        block.setType(materials.get(random.nextInt(materials.size())));
                    }
                }
            }
        }

        //create 50 radius circle at bottom
        for (x = -radius; x < radius; x++) {
            for (z = -radius; z < radius; z++) {
                if (inRadius(x, 0, z, radius)) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(materials.get(random.nextInt(materials.size())));
                }
            }
        }
        materials.remove(Material.WHITE_CONCRETE_POWDER);

        //create 5 radius circle 80% height
        radius = 5;
        height = (int) (height * 0.8);
        for (x = -radius; x < radius; x++) {
            for (z = -radius; z < radius; z++) {
                if (inRadius(x, 0, z, radius)) {
                    Block block = world.getBlockAt(x, y + height, z);
                    block.setType(materials.get(random.nextInt(materials.size())));
                }
            }
        }

        //listener
        getServer().getPluginManager().registerEvents(this, this);
        x = 0;
        y = 5;
        z = 0;

        //set spawn at top platform
        world.setSpawnLocation(x, y + height + 1, z);

        GenerateBlocks();

        //enchantment table lv 20, 10 bookshelves
        world.getBlockAt(originRadius + 2, 110, originRadius - 1).setType(Material.BOOKSHELF); //1
        world.getBlockAt(originRadius + 2, 110, originRadius).setType(Material.BOOKSHELF); //2
        world.getBlockAt(originRadius + 2, 110, originRadius + 1).setType(Material.BOOKSHELF); //3
        world.getBlockAt(originRadius + 1, 110, originRadius + 2).setType(Material.BOOKSHELF); //4
        world.getBlockAt(originRadius, 110, originRadius + 2).setType(Material.BOOKSHELF); //5
        world.getBlockAt(originRadius - 1, 110, originRadius + 2).setType(Material.BOOKSHELF); //6
        world.getBlockAt(originRadius + 2, 111, originRadius).setType(Material.BOOKSHELF); //7
        world.getBlockAt(originRadius, 111, originRadius + 2).setType(Material.BOOKSHELF); //8
        world.getBlockAt(originRadius + 2, 111, originRadius + 1).setType(Material.BOOKSHELF); //9
        world.getBlockAt(originRadius + 1, 111, originRadius + 2).setType(Material.BOOKSHELF); //10





        //enchantment table lv 30, 15 bookshelves
        world.getBlockAt(originRadius + 2, 120, originRadius - 2).setType(Material.BOOKSHELF); //1
        world.getBlockAt(originRadius + 2, 120, originRadius - 1).setType(Material.BOOKSHELF); //2
        world.getBlockAt(originRadius + 2, 120, originRadius).setType(Material.BOOKSHELF); //3
        world.getBlockAt(originRadius + 2, 120, originRadius + 1).setType(Material.BOOKSHELF); //4
        world.getBlockAt(originRadius + 1, 120, originRadius + 2).setType(Material.BOOKSHELF); //5
        world.getBlockAt(originRadius, 120, originRadius + 2).setType(Material.BOOKSHELF); //6
        world.getBlockAt(originRadius - 1, 120, originRadius + 2).setType(Material.BOOKSHELF); //7
        world.getBlockAt(originRadius - 2, 120, originRadius + 2).setType(Material.BOOKSHELF); //8
        world.getBlockAt(originRadius + 2, 121, originRadius).setType(Material.BOOKSHELF); //9
        world.getBlockAt(originRadius, 121, originRadius + 2).setType(Material.BOOKSHELF); //10
        world.getBlockAt(originRadius + 2, 121, originRadius + 1).setType(Material.BOOKSHELF); //11
        world.getBlockAt(originRadius + 1, 121, originRadius + 2).setType(Material.BOOKSHELF); //12
        world.getBlockAt(originRadius + 2, 121, originRadius - 1).setType(Material.BOOKSHELF); //13
        world.getBlockAt(originRadius + 2, 121, originRadius - 2).setType(Material.BOOKSHELF); //14
        world.getBlockAt(originRadius + 1, 121, originRadius - 2).setType(Material.BOOKSHELF); //15


        GameLogicLoop();
        playerSchedule();
    }

    boolean inRadius(int x, int y, int z, int radius) {
        if (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) < radius) {
            return true;
        }
        return false;
    }

    public abstract class RepeatingTask implements Runnable {

        private int taskId;

        public RepeatingTask(JavaPlugin plugin, int arg1, int arg2) {
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, arg1, arg2);
        }

        public void canncel() {
            Bukkit.getScheduler().cancelTask(taskId);
        }

    }

    void message(String message) {
        Bukkit.broadcastMessage(message);
    }

    ArrayList<Location> pvpspawns = new ArrayList<Location>();

    void shopSchedule() {
        new RepeatingTask(this, 0, 20) {
            int countdown = 180;
            @Override
            public void run() {
                if (countdown == 120) {
                    Bukkit.broadcastMessage("§aShopPhase endet in §6" + countdown + " §aSekunden!");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playNote(p.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.A));
                    }
                }
                if (countdown == 60) {
                    Bukkit.broadcastMessage("§aShopPhase endet in §6" + countdown + " §aSekunden!");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playNote(p.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.A));
                    }
                }
                if (countdown == 30) {
                    Bukkit.broadcastMessage("§aShopPhase endet in §6" + countdown + " §aSekunden!");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playNote(p.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.A));
                    }
                }
                if (countdown == 10) {
                    Bukkit.broadcastMessage("§aShopPhase endet in §6" + countdown + " §aSekunden!");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playNote(p.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.A));
                    }
                }
                if (countdown < 6 && countdown > 0) {
                    Bukkit.broadcastMessage("§aShopPhase endet in §6" + countdown + " §aSekunden!");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playNote(p.getLocation(), Instrument.PLING, Note.natural(0, Note.Tone.A));
                    }
                }
                if (countdown == 0) {
                    Bukkit.broadcastMessage("§aShopPhase beendet!");
                    shopphase = false;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (pvpspawns.size() == 0) {
                            Bukkit.broadcastMessage("§cEs sind nicht genügend Spawnpoints für die PvPPhase vorhanden!");
                        }
                        int r = new Random().nextInt(pvpspawns.size());
                        p.teleport(pvpspawns.get(r));
                        pvpspawns.remove(r);
                        dead.put(p, false);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    }
                    playercount = Bukkit.getOnlinePlayers().size();
                    winnerSchedule();
                    canncel();
                }
                countdown--;
            }
        };
    }

    int playercount = 0;
    HashMap<Player,Boolean> dead = new HashMap<>();
    void winnerSchedule() {

        playerCompassSchedule();
        new RepeatingTask(this, 0, 20) {
            Boolean won = false;
            Player winner = null;
            @Override
            public void run() {
                if (playercount == 1) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!dead.get(p)) {
                            Bukkit.broadcastMessage("§aDer Gewinner ist §6" + p.getName() + "§a!");
                            winner = p;
                            won = true;
                            playercount = -199;
                            for (Player p2 : Bukkit.getOnlinePlayers()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "amusic play " + p2.getName() + " 0");
                            }
                        }
                    }
                }
                if (won) {
                    Bukkit.broadcastMessage("§aGlückwunsch §6" + winner.getName() + "§a! Du hast das Spiel gewonnen!");
                    //spawn 3 fireworks above the winner
                    for (int i = 0; i < 10; i++) {
                        Location loc = winner.getLocation().clone().add(0, 3, 0);
                        spawnRandomFireWork(loc);
                    }
                }
            }
        };
    }

    void spawnRandomFireWork(Location loc) {
        //random offset x and z (max 2) (min -2)
        int x = new Random().nextInt(5) - 2;
        int z = new Random().nextInt(5) - 2;
        loc.add(x, 0, z);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        Random r = new Random();
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;
        if (rt == 1) {
            type = Type.BALL;
        }
        if (rt == 2) {
            type = Type.BALL_LARGE;
        }
        if (rt == 3) {
            type = Type.BURST;
        }
        if (rt == 4) {
            type = Type.CREEPER;
        }
        if (rt == 5) {
            type = Type.STAR;
        }
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = getColor(r1i);
        Color c2 = getColor(r2i);
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        fwm.addEffect(effect);
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);
        fw.setFireworkMeta(fwm);
    }

    Color getColor(int i) {
        Color c = null;
        if (i == 1) c = Color.AQUA;
        if (i == 2) c = Color.BLACK;
        if (i == 3) c = Color.BLUE;
        if (i == 4) c = Color.FUCHSIA;
        if (i == 5) c = Color.GRAY;
        if (i == 6) c = Color.GREEN;
        if (i == 7) c = Color.LIME;
        if (i == 8) c = Color.MAROON;
        if (i == 9) c = Color.NAVY;
        if (i == 10) c = Color.OLIVE;
        if (i == 11) c = Color.ORANGE;
        if (i == 12) c = Color.PURPLE;
        if (i == 13) c = Color.RED;
        if (i == 14) c = Color.SILVER;
        if (i == 15) c = Color.TEAL;
        if (i == 16) c = Color.WHITE;
        if (i == 17) c = Color.YELLOW;
        return c;
    }

    void playerSchedule() {
        Plugin plugin = this;
        new RepeatingTask(this, 0, 2) {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Location below = p.getLocation().clone().subtract(0, 0.1, 0);
                    if (!p.isDead() && below.getBlock().getType() == Material.RESPAWN_ANCHOR) {
                        int c = coins.get(p);
                        //calculate a random amount from 75 to 125 then subtract 1 / (1 + 𝑒 ^(−coins + 𝑒))
                        int rando = (int) (Math.random() * 50 + 75);
                        int amount = (int) (rando - (1 / (1 + Math.pow(Math.E, ((-c + 100) / 500 + Math.E)))) * rando);
                        coins.put(p, c + amount);
                        p.sendMessage("§aDu hast §6" + amount + "§a Coins erhalten!");
                        below.getBlock().setType(materials.get((int) (Math.random() * materials.size())));
                        Location rounded = below.getBlock().getLocation().clone().add(0.5, 0.9, 0.5);
                        //particle like /particle minecraft:soul ~ ~ ~ 0.25 0.01 0.25 0.01 400
                        p.spawnParticle(Particle.SOUL, rounded, 400, 0.25, 0.01, 0.25, 0.01);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    } else if (lblocks.contains(below.getBlock().getLocation())) {
                        //if the player gets on a lucky block, he gets a random effect (out of 20) (10 potion effects) (10 special effects (rocket ride, anvil rain, block to air, fire, math question, etc.))
                        int r = (int) (Math.random() * 10);
                        switch (r) {
                            case 0:
                                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0));
                                p.sendMessage("§cDer Burger aus aus §6Jays Burger Bude§c war doch nicht soo geil!");
                                break;
                            case 1:
                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0));
                                p.sendMessage("§cDu hast §6Lack§c gesofn!");
                                break;
                            case 2:
                                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 0));
                                p.sendMessage("§cDu hast §6Koks§c gesofn!");
                                break;
                            case 3:
                                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 1));
                                p.sendMessage("§cDein §6Körper§c ist voller §6Flüssig-Geld§c!");
                                break;
                            case 4:
                                p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1200, 1));
                                p.sendMessage("§cAn apple a day keeps the §6Doctor§c away!");
                                break;
                            case 5:
                                // Special effect: rocket ride, summon a firework, and make the player as passenger
                                Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                                fw.setPassenger(p);
                                p.sendMessage("§cGuten Flug!");
                                break;
                            case 6:
                                // Special effect: anvil rain, set the 10 blocks above the player to air and then the last one to an anvil
                                for (int i = 0; i < 10; i++) {
                                    Location loc = p.getLocation().clone().add(0, i, 0);
                                    if (loc.getBlock().getType() != Material.AIR) {
                                        loc.getBlock().setType(Material.AIR);
                                    }
                                }
                                p.getLocation().clone().add(0, 10, 0).getBlock().setType(Material.ANVIL);
                                p.sendMessage("§c... und über ihnen können sie nun die §6Eisernen§c-Regenbögen sehen!");
                                break;
                            case 7:
                                // Special effect: block to air, set the block the player is standing on to air
                                p.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.AIR);
                                p.sendMessage("§cNicht vergessen ihren §6Fallschirm§c zu benutzen!");
                                break;
                            case 8:
                                // Special effect: fire
                                p.setFireTicks(120);
                                p.sendMessage("§cYour style is §6on fire§c!");
                                break;
                            case 9:
                                // Special effect: math question, ask the player a math question and if he answers correctly, he gets a reward
                                int a = (int) (Math.random() * 100);
                                int b = (int) (Math.random() * 100);
                                int c = a + b;
                                p.sendMessage("§aWas ist §6" + a + " + " + b + "§a? §cDu hast 5 Sekunden Zeit oder du stirbst!");
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    p.sendMessage("§aHast du ehrlich versucht es mit dem Controller einzugeben? (つ≧▽≦)つ");
                                }, 100);
                                break;
                            default:
                                break;
                        }
                        //remove the lucky block
                        lblocks.remove(below.getBlock().getLocation());
                    }
                    if (!jumpmode) {
                        this.canncel();
                    }
                }
            }
        };
    }

    //when anvil lands, replace it with air
    @EventHandler
    public void onLand(EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof FallingBlock) {
            FallingBlock fb = (FallingBlock) e.getEntity();
            if (fb.getBlockData().getMaterial() == Material.ANVIL) {
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    e.getBlock().setType(Material.AIR);
                }, 20);
            }
        }
    }

    @EventHandler
    public void onUnmount(EntityDismountEvent e) {
        if (e.getDismounted() instanceof Player) {
            Player p = (Player) e.getDismounted();
            if (e.getEntity() instanceof Firework) {
                event.setCancelled(true);
            }
        }
    }

    void playerCompassSchedule() {
        new RepeatingTask(this, 0, 50) {
            @Override
            public void run() {
                if (playercount == 1) {
                    this.canncel();
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getInventory().contains(Material.RECOVERY_COMPASS)) {
                        //make compass point to nearest player
                        Player nearest = null;
                        double distance = 0;
                        for (Player p2 : Bukkit.getOnlinePlayers()) {
                            if (p2 != p && !dead.get(p2)) {
                                if (nearest != null && nearest == p) {
                                    continue;
                                }
                                if (nearest == null) {
                                    nearest = p2;
                                    distance = p.getLocation().distance(p2.getLocation());
                                } else {
                                    double d = p.getLocation().distance(p2.getLocation());
                                    if (d < distance) {
                                        nearest = p2;
                                        distance = d;
                                    }
                                }
                            }
                        }
                        if (nearest != null) {
                            for (ItemStack i : p.getInventory().getContents()) {
                                if (i != null && i.getType() == Material.RECOVERY_COMPASS) {
                                    p.getInventory().setItem(p.getInventory().first(i), new ItemStack(Material.COMPASS));
                                }
                                if (i != null && i.getType() == Material.COMPASS) {
                                    CompassMeta cm = (CompassMeta) i.getItemMeta();
                                    cm.setLodestone(nearest.getLocation());
                                    cm.setLodestoneTracked(true);
                                    cm.setDisplayName("§6§lNähester Spieler ist " + nearest.getName());
                                    i.setItemMeta(cm);
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    Boolean[] lblock = {false, false, false};
    ArrayList<Location> lblocks = new ArrayList<Location>();
    ArrayList<Material> lblockTypes = new ArrayList<Material>();
    void luckyBlockSchedule(final int round) {
        //white -> red ... rainbow colors
        new RepeatingTask(this, 0, 5) {
            int i = 0;
            @Override
            public void run() {
                if (++i < lblockTypes.size()) {

                } else {
                    i = 0;
                }
                if (lblock[round]) {
                    for (Location block : lblocks) {
                        block.getBlock().setType(lblockTypes.get(i));
                    }
                } else {
                    this.canncel();
                }

            }
        };
    }

    ArrayList<Location> blocks = new ArrayList<Location>();
    void GenerateBlocks() {
        if (blocks.size() > 0) {
            for (Location block : blocks) {
                block.getBlock().setType(Material.AIR);
            }
        }
        blocks.clear();
        if (round < 1) {
            lblock[round - 2] = false;
        }
        lblocks.clear();
        //generate blocks with a 0.4% chance everywhere inside the cylinder from 0% +5 to 80% -3
        World world = getServer().getWorld("world");
        Location loc = new Location(world, 0, 5, 0);
        int radius = 48;
        int height = (int) (150 * 0.8 - 1);
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        for (int x1 = -radius; x1 < radius; x1++) {
            for (int z1 = -radius; z1 < radius; z1++) {
                for (int i = 5; i < height; i++) {
                    if (inRadius(x1, 0, z1, radius) && check3blocksbelow(x + x1, y + i, z + z1, world)) {
                        if (Math.random() < 0.0075) {
                            Block block = world.getBlockAt(x + x1, y + i, z + z1);
                            if (Math.random() < 0.05) {
                                if (Math.random() < 0.5) {
                                    //coin block
                                    block.setType(Material.RESPAWN_ANCHOR);
                                    //make the respawn anchor level 4
                                    BlockData data = block.getBlockData();
                                    ((RespawnAnchor) data).setCharges(4);
                                    block.setBlockData(data);
                                    blocks.add(block.getLocation());


                                } else {
                                    //lucky block
                                    block.setType(Material.TERRACOTTA);
                                    blocks.add(block.getLocation());
                                    lblocks.add(block.getLocation());

                                }
                            } else {
                                block.setType(materials.get((int) (Math.random() * materials.size())));
                                blocks.add(block.getLocation());
                            }
                        }
                    }
                }
            }
        }
        lblock[round - 1] = true;
        luckyBlockSchedule(round - 1);
    }

    void clearBlocks() {
        lblocks.clear();
        if (blocks.size() > 0) {
            for (Location block : blocks) {
                block.getBlock().setType(Material.AIR);
            }
        }
        blocks.clear();
    }

    int round = 1;
    void GameLogicLoop() {
        new RepeatingTask(this, 0, 20) {
            @Override
            public void run() {
                if (jumpmode) {
                    for (Player p : getServer().getOnlinePlayers()) {
                        if (p.getLocation().getBlockY() < 8.25 && p.getHealth() > 0) {
                            if (bottomCounter.get(p) == null) {
                                bottomCounter.put(p, 0);
                            } else {
                                bottomCounter.put(p, bottomCounter.get(p) + 1);
                            }
                            if (bottomCounter.get(p) > 3) {
                                if (++round <= 3) {
                                    //give the winner 150 coins
                                    bottomCounter.put(p, 0);
                                    coins.put(p, coins.get(p) + 150);
                                    p.sendMessage("§aDu hast diese Runde gewonnen! Du bekommst 150 Coins!");
                                    //teleport everyone to the spawn
                                    for (Player p2 : getServer().getOnlinePlayers()) {
                                        //teleport th
                                        p2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 100));
                                        p2.teleport(new Location(getServer().getWorld("world"), 0, 6 + height, 0));
                                        p2.sendMessage("§aRunde " + round + "!");
                                        p2.setHealth(20);
                                        GenerateBlocks();
                                    }
                                } else {
                                    //give the winner 150 coins
                                    bottomCounter.put(p, 0);
                                    coins.put(p, coins.get(p) + 150);
                                    p.sendMessage("§aDu hast diese Runde gewonnen! Du bekommst 150 Coins!");
                                    int lowestCoins = 999999999;
                                    Player lowestPlayer = null;

                                    //shopphase
                                    for (Player p2 : getServer().getOnlinePlayers()) {
                                        if (coins.get(p2) < lowestCoins) {
                                            lowestCoins = coins.get(p2);
                                            lowestPlayer = p2;
                                        }
                                        p2.sendMessage("§aShopphase!");
                                        p2.teleport(new Location(getServer().getWorld("world"), -175, 35, -465));
                                        p2.setHealth(20);
                                        p2.getInventory().clear();
                                        p2.getInventory().setItem(4, new ItemStack(Material.RECOVERY_COMPASS));
                                    }
                                    lowestPlayer.sendMessage("§6Onkel Babo: §aKumpel, schon wieder verloren? Hier, nimm das, das wird dir helfen!");
                                    Bukkit.broadcastMessage("§c" + lowestPlayer.getName() + "§a erhielt 200 groschen von Onkel Babo!");
                                    coins.put(lowestPlayer, coins.get(lowestPlayer) + 200);
                                    jumpmode = false;
                                    shopphase = true;
                                    //jumpmodeObjective.unregister();
                                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                                    clearBlocks();
                                    shopSchedule();
                                    canncel();
                                }
                            }
                        } else {
                            bottomCounter.put(p, 0);
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onDisable() {
        lblock[0] = false;
        lblock[1] = false;
        lblock[2] = false;
        clearBlocks();
        for (Player p : getServer().getOnlinePlayers()) {
            //kick everyone with the message "Server is restarting"
            p.kickPlayer("§cServer is restarting");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (jumpmode) {
            event.setRespawnLocation(new Location(getServer().getWorld("world"), 0, 6 + height, 0));
        }
    }

    boolean check3blocksbelow(int x, int y, int z, World world) {
        Block block1 = world.getBlockAt(x, y - 1, z);
        Block block2 = world.getBlockAt(x, y - 2, z);
        Block block3 = world.getBlockAt(x, y - 3, z);
        if (block1.getType() == Material.AIR && block2.getType() == Material.AIR && block3.getType() == Material.AIR) {
            return true;
        }
        return false;
    }



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (jumpmode) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNaturalSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
            return;
        }
    }

    Scoreboard scoreboard;
    Objective objective;

    Objective jumpmodeObjective;
    void createScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("Coins", "dummy");
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        objective.setDisplayName("§bCoins");
        jumpmodeObjective = scoreboard.registerNewObjective("GetDown", "dummy");
        jumpmodeObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        jumpmodeObjective.setDisplayName("§bGet§3Down");
    }

    void scoreboardLoop() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (getServer().getOnlinePlayers().size() > 0) {
                    for (Player p : getServer().getOnlinePlayers()) {
                        if (coins.get(p) == null) {
                            coins.put(p, 0);
                        }
                        objective.getScore(p).setScore(coins.get(p));
                        if (jumpmode) {
                            jumpmodeObjective.getScore(p).setScore(5 - p.getLocation().getBlockY());
                        }
                    }
                }
            }
        }, 0, 20);
    }

    ArrayList<Inventory> invs = new ArrayList<Inventory>();
    void createInventories() {
        //this will be 9 inventories, Waffen, Leder Rüstung, Eisen Rüstung, Diamant Rüstung, XP, Verzauberungen, Essen, Tränke, Spezial
        //cyan color
        invs.add(Bukkit.createInventory(null, 9, "§bShop"));
        invs.add(Bukkit.createInventory(null, 18, "§bWaffen"));
        invs.add(Bukkit.createInventory(null, 18, "§bKetten Rüstung"));
        invs.add(Bukkit.createInventory(null, 18, "§bDiamant Rüstung"));
        invs.add(Bukkit.createInventory(null, 18, "§bNetherite Rüstung"));
        invs.add(Bukkit.createInventory(null, 18, "§bXP"));
        invs.add(Bukkit.createInventory(null, 18, "§bVerzauberungen"));
        invs.add(Bukkit.createInventory(null, 18, "§bEssen"));
        invs.add(Bukkit.createInventory(null, 18, "§bTränke"));
        invs.add(Bukkit.createInventory(null, 18, "§bSpezial"));

        //add items to inventories
        setItemwithLore(invs.get(0), 0, Material.STONE_AXE, "§bWaffen", "§7Klicke um zu Waffen zu gelangen");
        setItemwithLore(invs.get(0), 1, Material.CHAINMAIL_CHESTPLATE, "§bKetten Rüstung", "§7Klicke um zu Ketten Rüstung zu gelangen");
        setItemwithLore(invs.get(0), 2, Material.DIAMOND_CHESTPLATE, "§bDiamant Rüstung", "§7Klicke um zu Diamant Rüstung zu gelangen");
        setItemwithLore(invs.get(0), 3, Material.NETHERITE_CHESTPLATE, "§bNetherite Rüstung", "§7Klicke um zu Netherite Rüstung zu gelangen");
        setItemwithLore(invs.get(0), 4, Material.EXPERIENCE_BOTTLE, "§bXP", "§7Klicke um zu XP zu gelangen");
        setItemwithLore(invs.get(0), 5, Material.ENCHANTING_TABLE, "§bVerzauberungen", "§7Klicke um zu Verzauberungen zu gelangen");
        setItemwithLore(invs.get(0), 6, Material.CAKE, "§bEssen", "§7Klicke um zu Essen zu gelangen");
        setItemwithLore(invs.get(0), 7, Material.POTION, "§bTränke", "§7Klicke um zu Tränke zu gelangen");
        setItemwithLore(invs.get(0), 8, Material.GOAT_HORN, "§bSpezial", "§7Klicke um zu Spezial zu gelangen");

        //add items to Waffen
        copyFirst9Items(invs.get(0), invs.get(1));
        setItemwithLore(invs.get(1), 9, Material.STONE_SWORD, "§bSteinschwert", "§7Kosten: §b100");
        setItemwithLore(invs.get(1), 10, Material.IRON_SWORD, "§bEisenschwert", "§7Kosten: §b200");
        setItemwithLore(invs.get(1), 11, Material.DIAMOND_SWORD, "§bDiamantschwert", "§7Kosten: §b300");
        setItemwithLore(invs.get(1), 12, Material.WOODEN_AXE, "§bHolzaxt", "§7Kosten: §b100");
        setItemwithLore(invs.get(1), 13, Material.STONE_AXE, "§bSteinaxt", "§7Kosten: §b200");
        setItemwithLore(invs.get(1), 14, Material.IRON_AXE, "§bEisenaxt", "§7Kosten: §b300");
        setItemwithLore(invs.get(1), 15, Material.CROSSBOW, "§bArmbrust", "§7Kosten: §b40");
        setItemwithLore(invs.get(1), 16, Material.ARROW, "§bBolzen", "§7Kosten: §b4");
        setItemwithLore(invs.get(1), 17, Material.TIPPED_ARROW, "§bGiftbolzen", "§7Kosten: §b25");
        ItemStack item = invs.get(1).getItem(17);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 65, 0), true);
        item.setItemMeta(meta);

        //add items to
        copyFirst9Items(invs.get(0), invs.get(2));
        setItemwithLore(invs.get(2), 10, Material.CHAINMAIL_HELMET, "§bKettenhelm", "§7Kosten: §b20");
        setItemwithLore(invs.get(2), 11, Material.CHAINMAIL_LEGGINGS, "§bKettenhose", "§7Kosten: §b20");
        setItemwithLore(invs.get(2), 12, Material.CHAINMAIL_BOOTS, "§bKettenschuhe", "§7Kosten: §b20");
        setItemwithLore(invs.get(2), 14, Material.CHAINMAIL_CHESTPLATE, "§bKettenhemd", "§7Kosten: §b50");

        //add items to
        copyFirst9Items(invs.get(0), invs.get(3));
        setItemwithLore(invs.get(3), 10, Material.DIAMOND_HELMET, "§bDiamanthelm", "§7Kosten: §b50");
        setItemwithLore(invs.get(3), 11, Material.DIAMOND_LEGGINGS, "§bDiamanthose", "§7Kosten: §b50");
        setItemwithLore(invs.get(3), 12, Material.DIAMOND_BOOTS, "§bDiamantstiefel", "§7Kosten: §b50");
        setItemwithLore(invs.get(3), 14, Material.DIAMOND_CHESTPLATE, "§bDiamantbrustplatte", "§7Kosten: §b90");

        //add items to
        copyFirst9Items(invs.get(0), invs.get(4));
        setItemwithLore(invs.get(4), 10, Material.NETHERITE_HELMET,     "§bNetheritehelm", "§7Kosten: §b70");
        setItemwithLore(invs.get(4), 11, Material.NETHERITE_LEGGINGS,   "§bNetheritehose", "§7Kosten: §b90");
        setItemwithLore(invs.get(4), 12, Material.NETHERITE_BOOTS,      "§bNetheritestiefel", "§7Kosten: §b90");
        setItemwithLore(invs.get(4), 14, Material.NETHERITE_CHESTPLATE, "§bNetheritebrustplatte", "§7Kosten: §b140");

        //add items to XP, 1xp, 10xp, 20xp, 30xp
        copyFirst9Items(invs.get(0), invs.get(5));
        setItemwithLore(invs.get(5), 10, Material.EXPERIENCE_BOTTLE, "§b1 XP", "§7Kosten: §b20", 1);
        setItemwithLore(invs.get(5), 12, Material.EXPERIENCE_BOTTLE, "§b10 XP", "§7Kosten: §b140", 10);
        setItemwithLore(invs.get(5), 14, Material.EXPERIENCE_BOTTLE, "§b20 XP", "§7Kosten: §b280", 20);
        setItemwithLore(invs.get(5), 16, Material.EXPERIENCE_BOTTLE, "§b30 XP", "§7Kosten: §b410", 30);

        //add items to Verzauberungen
        copyFirst9Items(invs.get(0), invs.get(6));
        setItemwithLore(invs.get(6), 10, Material.ENCHANTED_BOOK, "§bLevel I", "§7Enchanter bis level 10");
        setItemwithLore(invs.get(6), 13, Material.ENCHANTED_BOOK, "§bLevel II", "§7Enchanter bis level 20");
        setItemwithLore(invs.get(6), 16, Material.ENCHANTED_BOOK, "§bLevel III", "§7Enchanter bis level 30");

        //add items to Essen
        copyFirst9Items(invs.get(0), invs.get(7));
        setItemwithLore(invs.get(7), 10, Material.GLOW_BERRIES, "§bGlowberries", "§7Kosten: §b1");
        setItemwithLore(invs.get(7), 11, Material.COOKED_BEEF, "§bVeggi-Steak", "§7Kosten: §b3");
        setItemwithLore(invs.get(7), 12, Material.COOKED_PORKCHOP, "§bSchnitzel", "§7Kosten: §b2");
        setItemwithLore(invs.get(7), 13, Material.COOKED_CHICKEN, "§bGoldene Karotte", "§7Kosten: §b6");
        setItemwithLore(invs.get(7), 14, Material.CAKE, "§bKuchen", "§7Kosten: §b10");
        setItemwithLore(invs.get(7), 16, Material.GOLDEN_APPLE, "§bGoldener Apfel", "§7Kosten: §b50");

        //add items to Tränke
        copyFirst9Items(invs.get(0), invs.get(8));
        setItemwithLore(invs.get(8), 11, Material.POTION, "§bHeiltrank", "§7Kosten: §b25");
        ItemStack item2 = invs.get(8).getItem(11);
        PotionMeta meta2 = (PotionMeta) item2.getItemMeta();
        meta2.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        item2.setItemMeta(meta2);
        setItemwithLore(invs.get(8), 12, Material.POTION, "§bHeiltrank II", "§7Kosten: §b50");
        item2 = invs.get(8).getItem(12);
        meta2 = (PotionMeta) item2.getItemMeta();
        meta2.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, true));
        item2.setItemMeta(meta2);
        setItemwithLore(invs.get(8), 13, Material.POTION, "§bSpeedtrank", "§7Kosten: §b40");
        item2 = invs.get(8).getItem(13);
        meta2 = (PotionMeta) item2.getItemMeta();
        meta2.setBasePotionData(new PotionData(PotionType.SPEED));
        item2.setItemMeta(meta2);
        setItemwithLore(invs.get(8), 14, Material.POTION, "§bSpeedtrank II", "§7Kosten: §b80");
        item2 = invs.get(8).getItem(14);
        meta2 = (PotionMeta) item2.getItemMeta();
        meta2.setBasePotionData(new PotionData(PotionType.SPEED, false, true));
        item2.setItemMeta(meta2);
        setItemwithLore(invs.get(8), 15, Material.POTION, "§bFeuerresi", "§7Kosten: §b50");
        item2 = invs.get(8).getItem(15);
        meta2 = (PotionMeta) item2.getItemMeta();
        meta2.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
        item2.setItemMeta(meta2);

        //add items to Spezial
        copyFirst9Items(invs.get(0), invs.get(9));
        setItemwithLore(invs.get(9), 10, Material.BLAZE_ROD, "§bBlitzschlag", "§7Kosten: §b150");
        setItemwithLore(invs.get(9), 11, Material.GOLDEN_HOE, "§bBlitzschlag II", "§7Kosten: §b99990");
        setItemwithLore(invs.get(9), 12, Material.FISHING_ROD, "§bEnterhaken", "§7Kosten: §b150");
        ItemStack enterhaken = invs.get(9).getItem(12);
        ItemMeta enterhakenmeta = enterhaken.getItemMeta();
        enterhakenmeta.addEnchant(Enchantment.DURABILITY, 3, true);
        enterhakenmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        enterhaken.setItemMeta(enterhakenmeta);
        setItemwithLore(invs.get(9), 13, Material.SHIELD, "§bSchild", "§7Kosten: §b100");
        setItemwithLore(invs.get(9), 14, Material.TOTEM_OF_UNDYING, "§bTotem", "§7Kosten: §b1000");
        setItemwithLore(invs.get(9), 15, Material.TRIDENT, "§bLoyaler Dreizack", "§7Kosten: §b400");
        ItemStack item1 = invs.get(9).getItem(15);
        ItemMeta meta1 = item1.getItemMeta();
        meta1.addEnchant(Enchantment.LOYALTY, 3, true);
        meta1.addEnchant(Enchantment.DURABILITY, 3, true);
        meta1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item1.setItemMeta(meta1);
        setItemwithLore(invs.get(9), 16, Material.ROSE_BUSH, "§bExtra Herz", "§7Kosten: §b100");


    }

    void setItemwithLore(Inventory inv, int slot, Material material, String name, String lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> lorelist = new ArrayList<String>();
        lorelist.add(lore);
        meta.setLore(lorelist);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    void setItemwithLore(Inventory inv, int slot, Material material, String name, String lore, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> lorelist = new ArrayList<String>();
        lorelist.add(lore);
        meta.setLore(lorelist);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    void copyFirst9Items(Inventory inv1, Inventory inv2) {
        for (int i = 0; i < 9; i++) {
            inv2.setItem(i, inv1.getItem(i));
        }
    }

    int quitamount = 0;
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        counterprep.put(p, 0);
        if (quitamount != 0) {
            coins.put(p, quitamount);
        } else if (coins.get(p) == null) {
            coins.put(p, 100);
        }
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        p.setScoreboard(scoreboard);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        p.setLevel(0);
        p.setExp(0);
        p.setHealth(20);
        p.setFoodLevel(20);
        //remove air time so they don't get fall damage
        p.setRemainingAir(0);
        p.teleport(new Location(Bukkit.getWorld("world"), 0, 6 + height, 0));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (jumpmode) {
            Player p = event.getPlayer();
            int c = coins.get(p);
            int amount = (int) ((1 / (1 + Math.pow(Math.E, ((-c + 100) / 500 + Math.E)))) * 100);
            coins.put(p, c - amount);
        }
        quitamount = coins.get(event.getPlayer());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        //unless it's their inventory or shopphase / jumpmode
        if (!shopphase && !jumpmode) {
            if (event.getInventory().getType() != InventoryType.PLAYER) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ENCHANTING) {
            //remove all lapis lazuli
            for (int i = 0; i < event.getInventory().getSize(); i++) {
                if (event.getInventory().getItem(i) != null) {
                    if (event.getInventory().getItem(i).getType() == Material.LAPIS_LAZULI) {
                        event.getInventory().setItem(i, null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    Random random = new Random();

    HashMap<Player, Integer> counterprep = new HashMap<Player, Integer>();
    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        counterprep.put(event.getEnchanter(), counterprep.get(event.getEnchanter()) + 1);
        if (counterprep.get(event.getEnchanter()) == 3) {
            //(EntityHuman) ((CraftPlayer)player).getHandle();
            Player player = event.getEnchanter();
            event.getView().getTopInventory().setItem(1, new ItemStack(Material.LAPIS_LAZULI, 3));
            try {
                EntityHuman entityHuman = (EntityHuman) ((CraftPlayer)player).getHandle();
                //entityHuman.onEnchantDone((ItemStack) null, 0);
                entityHuman.a((net.minecraft.world.item.ItemStack) null, 0);

                //check if FakeEnchant or ContainerEnchantTable ContainerEnchantTable container = (ContainerEnchantTable) ((CraftInventoryView) event.getView()).getHandle();
                if (((CraftInventoryView) event.getView()).getHandle() instanceof FakeEnchant) {
                   FakeEnchant container = ((FakeEnchant) ((CraftInventoryView) event.getView()).getHandle());
                   container.getInventory().e();
                   container.getContainerProperty().a(entityHuman.cf);
                   container.a(container.getInventory());
                } else if (((CraftInventoryView) event.getView()).getHandle() instanceof ContainerEnchantTable) {
                    ContainerEnchantTable container = (ContainerEnchantTable) ((CraftInventoryView) event.getView()).getHandle();
                    //IInventory iinventory = container.n; doesn't work because n is private so we use reflection
                    Field fieldN = container.getClass().getDeclaredField("n");
                    fieldN.setAccessible(true);
                    IInventory iinventory = (IInventory) fieldN.get(container);
                    //ContainerEnchantmentTable#i#a(int)
                    //ContainerProperty containerProperty = container.i; doesn't work because i is private so we use reflection
                    Field containerProperty = container.getClass().getDeclaredField("q");
                    containerProperty.setAccessible(true);
                    ((ContainerProperty) containerProperty.get(container)).a(entityHuman.cf);
                    container.a(iinventory);
                }
                counterprep.put(event.getEnchanter(), 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    boolean checkCoins(Player player, int cost) {
        if (coins.get(player) >= cost) {
            coins.put(player, coins.get(player) - cost);
            player.sendMessage("§aDu hast §e" + cost + "§a Coins ausgegeben!");
            return false;
        } else {
            notEnoughCoins(player, cost);
            return true;
        }
    }
    void notEnoughCoins(Player player, int cost) {
        player.sendMessage("§cDu hast nicht genug Coins! Dir fehlen §e" + (cost - coins.get(player)) + "§c Coins!");
    }

    HashMap<Player, HashMap<String, Integer>> shopdata = new HashMap<Player, HashMap<String, Integer>>();
    HashMap<Player, Integer> coins = new HashMap<Player, Integer>();
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getCurrentItem() != null) {

            if (event.getCurrentItem().getType() == Material.LAPIS_LAZULI) {
                event.setCancelled(true);
                return;
            }

            if (event.getCurrentItem().getType() == Material.ENCHANTED_BOOK && event.getSlot() > 9) {
                Player player = (Player) event.getWhoClicked();
                final EntityPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                int containerCounter = serverPlayer.nextContainerCounter();
                int level = 0;
                int cslot = event.getSlot();

                if (cslot == 10) {
                    level = 1;
                } else if (cslot == 13) {
                    level = 2;
                } else if (cslot == 16) {
                    level = 3;
                }
                FakeEnchant enchant = new FakeEnchant(containerCounter, player, level);

                //bP is ContainerMenu containerMenu of the player, bO is InventoryMenu of the player
                serverPlayer.bP = serverPlayer.bO;
                IChatBaseComponent title = CraftChatMessage.fromStringOrNull("§aGet§eDown");

                //b is PlayerConnection, a is send(Packet), m is ENCHANTMENT_TABLE's ContainerType
                serverPlayer.b.a(new PacketPlayOutOpenWindow(containerCounter, Containers.m, title));
                serverPlayer.bP = enchant;

                //a is initMenu
                serverPlayer.a(enchant);
                event.setCancelled(true);
                return;

            }
            if (event.getSlot() < 9 && invs.contains(event.getClickedInventory())) {
                event.getWhoClicked().openInventory(invs.get(event.getSlot() + 1));
                event.setCancelled(true);
            } else if (event.getSlot() > 8 && invs.contains(event.getClickedInventory())) {
                if (event.getCurrentItem().getType() == Material.EXPERIENCE_BOTTLE) {
                    int amount = 0;
                    int cslot = event.getSlot();
                    Player player = (Player) event.getWhoClicked();
                    if (cslot == 10) {
                        amount = 1;
                        if (checkCoins(player, 20)) {
                            event.setCancelled(true);
                            return;
                        }
                    } else if (cslot == 12) {
                        amount = 10;
                        if (checkCoins(player, 140)) {
                            event.setCancelled(true);
                            return;
                        }
                    } else if (cslot == 14) {
                        amount = 20;
                        if (checkCoins(player, 280)) {
                            event.setCancelled(true);
                            return;
                        }
                    } else if (cslot == 16) {
                        amount = 30;
                        if (checkCoins(player, 410)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    player.giveExpLevels(amount);
                } else if (event.getCurrentItem().getType() == Material.ROSE_BUSH) {
                    Player player = (Player) event.getWhoClicked();
                    if (shopdata.get(player) == null) {
                        shopdata.put(player, new HashMap<String, Integer>());
                    }
                    if (shopdata.get(player).get("herz") == null) {
                        shopdata.get(player).put("herz", 0);
                    }
                    if (checkCoins(player, 100 + (shopdata.get(player).get("herz")))) {
                        event.setCancelled(true);
                        return;
                    }
                    shopdata.get(player).put("herz", shopdata.get(player).get("herz") + 10);
                    AttributeInstance a = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    a.setBaseValue(a.getBaseValue() + 2);
                } else {
                    int lorepos = event.getCurrentItem().getItemMeta().getLore().size() - 1;
                    String name = event.getCurrentItem().getItemMeta().getLore().get(lorepos);

                    //create a new itemstack with the same data as the added item
                    ItemStack item = new ItemStack(event.getCurrentItem().getType(), 1, event.getCurrentItem().getDurability());
                    item.setItemMeta(event.getCurrentItem().getItemMeta());
                    //remove the price from the lore
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(lore.size() - 1);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    //check if the player has either space for a new item or if the item is stackable a space for the amount of items
                    if (event.getWhoClicked().getInventory().firstEmpty() == -1) {
                        if (event.getWhoClicked().getInventory().contains(item.getType())) {
                            for (ItemStack i : event.getWhoClicked().getInventory().getContents()) {
                                if (i != null && i.getType() == item.getType()) {
                                    //check if they match (itemstack are not always the same)
                                    if (i.getDurability() == item.getDurability() && i.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
                                        if (i.getAmount() + item.getAmount() <= i.getMaxStackSize()) {
                                            event.getWhoClicked().sendMessage("§cDu hast nicht genug Platz im Inventar!");
                                            event.setCancelled(true);
                                            return;
                                        }
                                    }
                                }
                            }
                        } else {
                            event.getWhoClicked().sendMessage("§cDu hast nicht genug Platz im Inventar!");
                            event.setCancelled(true);
                            return;
                        }
                    }
                    //check if the player has enough coins
                    if (checkCoins((Player) event.getWhoClicked(), Integer.parseInt(name.substring(name.indexOf("§b") + 2)))) {
                        event.setCancelled(true);
                        return;
                    }
                    //add the new itemstack to the player's inventory
                    event.getWhoClicked().getInventory().addItem(item);
                }
                event.setCancelled(true);
            }
        }
    }

    int recursiveCheck(String temp) {
        if (temp.length() == 0) {
            return 999999999;
        }
        if (temp.matches("[0-9]+")) { //the regex checks if the string is only numbers
            return Integer.parseInt(temp);
        } else {
            return recursiveCheck(temp.substring(1));
        }
    }



    @EventHandler
    public void entityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (shopphase) {
                event.setCancelled(true);
                return;
            }
            Player p = (Player) event.getEntity();
            if (jumpmode && event.getDamage() >= p.getHealth()) {
                int c = coins.get(p);
                int amount = (int) ((1 / (1 + Math.pow(Math.E, ((-c + 100) / 500 + Math.E)))) * 100);
                coins.put(p, c - amount);
                p.sendMessage("§cDu hast §6" + amount + "§c Coins verloren!");
                p.teleport(new Location(p.getWorld(), 0, 128, 0));
                p.setHealth(20);
                p.setFallDistance(0);
                event.setCancelled(true);
            }
            //check if the player has a totem in either hand
            if (!jumpmode && !shopphase && p.getInventory().getItemInMainHand().getType() != Material.TOTEM_OF_UNDYING && p.getInventory().getItemInOffHand().getType() != Material.TOTEM_OF_UNDYING && event.getDamage() >= p.getHealth()) {
                //drop all items
                for (ItemStack item : p.getInventory().getContents()) {
                    if (item != null) {
                        p.getWorld().dropItemNaturally(p.getLocation(), item);
                    }
                }
                for (ItemStack item : p.getInventory().getArmorContents()) {
                    if (item != null) {
                        p.getWorld().dropItemNaturally(p.getLocation(), item);
                    }
                }
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.setGameMode(GameMode.SPECTATOR);
                //set the velocity to negative forward + up * 2
                p.setVelocity(p.getLocation().getDirection().multiply(-0.75).add(new Vector(0, 1.25, 0)));
                p.sendMessage("§cDu bist gestorben... kauf dir'n Döner.");
                p.playSound(p.getLocation(), Sound.AMBIENT_BASALT_DELTAS_MOOD, 1, 1);
                dead.put(p, true);
                playercount--;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileShoot(ProjectileLaunchEvent event) {
        if (shopphase) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Trident) {
            scheduleTrident((Trident) event.getEntity(), (Entity) event.getEntity().getShooter());
        }
        if (event.getEntity() instanceof FishHook) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(2));
        }
    }

    void scheduleTrident(Trident t, Entity owner) {
        //as long as the trident exists, it will move all the items nearby to the tridents location
        new RepeatingTask(this, 0, 1) {
            ArrayList<Entity> entities = new ArrayList<Entity>();
            @Override
            public void run() {
                if (t == null || t.isDead()) {
                    for (Entity e : entities) {
                        if (owner != null && !e.isDead()) {
                            e.teleport(owner.getLocation());
                        }
                    }
                    canncel();
                }
                for (Entity e : t.getNearbyEntities(2, 3, 2)) {
                    if (e instanceof Item || e instanceof ExperienceOrb) {
                        if (!entities.contains(e)) {
                            entities.add(e);
                        }
                    }
                }
                for (Entity e : entities) {
                    e.teleport(t.getLocation());
                }
            }
        };
    }

    void scheduleLightnings(Location loc, final int amount) {
        //schedule, amount-- and spawn lightning when amount is 0 stop the task
        new RepeatingTask(this, 0, 5) {
            int amount2 = amount;
            @Override
            public void run() {
                if (amount2 == 0) {
                    canncel();
                }
                loc.getWorld().strikeLightning(loc);
                amount2--;
            }
        };
    }

    boolean shopphase = false;
    @EventHandler
    public void reelIn(PlayerFishEvent event) {
        if (shopphase) {
            event.setCancelled(true);
        }
        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY || event.getState() == PlayerFishEvent.State.IN_GROUND) {
            Location hook = new Location(event.getPlayer().getWorld(), event.getHook().getLocation().getX(), event.getHook().getLocation().getY() - 2, event.getHook().getLocation().getZ());
            //if the player hooked an entity
            if (event.getHook().getHookedEntity() != null) {
                hook = event.getHook().getHookedEntity().getLocation();
                Location player = event.getPlayer().getLocation();
                //push the hooked entity up and towards the player
                Vector vector = player.toVector().subtract(hook.toVector()).normalize().multiply(1.5);
                vector.setY(vector.getY() + player.distance(hook) / 25);
                event.getHook().getHookedEntity().setVelocity(vector);
            //if the player hooked nothing (hook is on the ground)
            } else {
                hook = event.getHook().getLocation();
                Location player = event.getPlayer().getLocation();
                //push the player up and towards the hook
                Vector vector = hook.toVector().subtract(player.toVector()).normalize().multiply(1.5);
                vector.setY(vector.getY() + hook.distance(player) / 20);
                event.getPlayer().setVelocity(vector);
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == Material.RECOVERY_COMPASS && shopphase) {
                    Player player = event.getPlayer();
                    player.openInventory(invs.get(0));
                    event.setCancelled(true);
                } else if (event.getItem().getType() == Material.GOLDEN_HOE) {
                    if (shopphase) {
                        event.setCancelled(true);
                        return;
                    }
                    //check what block the player is looking at and spawn 3 lightning bolts
                    Player player = event.getPlayer();
                    BlockIterator iterator = new BlockIterator(player, 100);
                    Block block = null;
                    while (iterator.hasNext()) {
                        block = iterator.next();
                        if (block.getType() != Material.AIR) {
                            break;
                        }
                    }
                    Location loc = null;
                    if (block != null) {
                        loc = block.getLocation();
                    } else {
                        return;
                    }
                    scheduleLightnings(loc, 3);
                    //remove item from (amount is always 1 so we can just remove it)
                    player.getInventory().removeItem(event.getItem());
                } else if (event.getItem().getType() == Material.BLAZE_ROD) {
                    if (shopphase) {
                        event.setCancelled(true);
                        return;
                    }
                    //same as above but 1 lightning and item is stackable
                    Player player = event.getPlayer();
                    BlockIterator iterator = new BlockIterator(player, 100);
                    Block block = null;
                    while (iterator.hasNext()) {
                        block = iterator.next();
                        if (block.getType() != Material.AIR) {
                            break;
                        }
                    }
                    Location loc = null;
                    if (block != null) {
                        loc = block.getLocation();
                    } else {
                        return;
                    }
                    scheduleLightnings(loc, 1);
                    //remove item from (amount is stackable so we have to check if it is 1 or more)
                    if (event.getItem().getAmount() > 1) {
                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                    } else {
                        player.getInventory().removeItem(event.getItem());
                    }
                }
            }
        }
        if (shopphase || jumpmode) {
            event.setCancelled(true);
        }
    }
}
