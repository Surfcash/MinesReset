package com.surfcash.exaltedmines;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class CmdHandler {

    ExaltedMines plugin;
    private WorldEditPlugin worldEdit;
    private String error = color("&4-&cImproper Command Syntax&4-");
    private String exists = color("&cThat mine already exists.");
    private String nexists = color("&cThat mine does not exist.");

    public CmdHandler(ExaltedMines instance, WorldEditPlugin weplugin)
    {
        plugin = instance;
        worldEdit = weplugin;
    }

    //Message Color
    private static String color(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @SuppressWarnings("deprecation")
    private void doReplace(String name)
    {
        Mine mine = new Mine(name,plugin);
        String[] base = mine.getBaseblock().split(":");
        Material baseBlock = Material.getMaterial(base[0]);
        Byte baseData =  new Byte(base[1]);
        teleportArea(name);
        loadArea(name);
        List<String> oreList = mine.getOres();
        for (String str : oreList)
        {
            String[] ores = str.split(":");
            Material ore = Material.getMaterial(ores[0]);
            Byte oreData =  new Byte(ores[1]);
            Double chance = Double.parseDouble(ores[2]);
            for (int x = mine.getMin().getBlockX(); x <= mine.getMax().getBlockX(); x++)
            {
                for (int y = mine.getMin().getBlockY(); y <= mine.getMax().getBlockY(); y++)
                {
                    for (int z = mine.getMin().getBlockZ(); z <= mine.getMax().getBlockZ(); z++)
                    {
                        Block blk = mine.getWorld().getBlockAt(x, y, z);
                        if ((blk.getType() == baseBlock) && (blk.getData() == baseData))
                        {
                            Random random = new Random();
                            if (random.nextDouble() <= chance/100)
                            {
                                blk.setType(ore);
                                blk.setData(oreData);
                            }
                        }
                    }
                }
            }
        }
    }
    private void teleportArea(String name)
    {
        Mine mine = new Mine(name,plugin);
        if (!mine.teleExists()) {return;}
        Location locTele = mine.getTele();
        for(Player players : plugin.getServer().getOnlinePlayers())
        {
            Location p = players.getLocation();
            if ((p.getBlockX() >= mine.getMin().getBlockX()) && (p.getBlockX() <= mine.getMax().getBlockX()) &&
                    (p.getBlockY() >= mine.getMin().getBlockY()) && (p.getBlockY() <= mine.getMax().getBlockY()) &&
                    (p.getBlockZ() >= mine.getMin().getBlockZ()) && (p.getBlockZ() <= mine.getMax().getBlockZ())) {players.teleport(locTele);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void loadArea(String name)
    {
        Mine mine = new Mine(name,plugin);
        Vector origin = new Vector(mine.getMin().getX()-1,mine.getMin().getY(),mine.getMin().getZ()-1);
        File file = new File("plugins/WorldEdit/schematics/" + mine.getSchem() +".schematic");
        EditSession session = worldEdit.getWorldEdit().getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(mine.getWorld()), 9999999);
        try {MCEditSchematicFormat.getFormat(file).load(file).paste(session, origin, false);}
        catch (MaxChangedBlocksException | com.sk89q.worldedit.data.DataException | IOException exception) {exception.printStackTrace();}
    }

    //Command Handler
    public boolean run(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // Permissions Check
        if (!sender.hasPermission("exaltedmine"))
        {
            sender.sendMessage(color("&4You do not have access to that command."));
            return true;
        }
        //Console Check
        if (!(sender instanceof Player))
        {
            sender.sendMessage("Cannot use Exalted Mine in console.");
            return true;
        }
        //Commands
        if (cmd.getName().equalsIgnoreCase("mine"))
        {
            Player p = (Player) sender;
            // -MINE HELP- //
            if (args.length == 0)
            {
                sender.sendMessage(color("&2&l&m+--&a&l Mine Help &2&l&m--+"));
                sender.sendMessage(color("&a/mine create [NAME]"));
                sender.sendMessage(color("&a/mine delete [NAME]"));
                sender.sendMessage(color("&a/mine addore [MINE] [BLOCK] [CHANCE]"));
                sender.sendMessage(color("&a/mine delore [MINE] [BLOCK]"));
                sender.sendMessage(color("&a/mine baseblock [MINE] [BLOCK]"));
                sender.sendMessage(color("&a/mine schem [MINE] [WE Schematic]"));
                sender.sendMessage(color("&a/mine teleport [MINE]"));
                sender.sendMessage(color("&a&m/mine frequency"));
                sender.sendMessage(color("&a/mine regen [MINE]"));
                sender.sendMessage(color("&a/mine info [MINE]"));
                sender.sendMessage(color("&a/mine list"));
                sender.sendMessage(color("&a/mine reload"));
                return true;
            }
            if (args.length == 1)
            {
                // -RELOAD- //
                if(args[0].equalsIgnoreCase("reload"))
                {
                    plugin.reloadConfig();
                    plugin.saveConfig();
                    sender.sendMessage(color("&aReloaded the Advanced Mine configuration."));
                    return true;
                }
                // -LIST- //
                if (args[0].equalsIgnoreCase("list"))
                {
                    sender.sendMessage(color("&2&l&m+--&a&l Mine List &2&l&m--+"));
                    if (plugin.getConfig().contains("Mines"))
                    {
                        for (String key : plugin.getConfig().getConfigurationSection("Mines").getKeys(false))
                        {
                            sender.sendMessage(color(" &2- &a" + key));
                        }
                    }
                    else sender.sendMessage(color("&cNo mines exist."));
                    return true;
                }
                // -FREQUENCY- //
                if (args[0].equalsIgnoreCase("frequency"))
                {
                    sender.sendMessage(color("&aPlaceholder Frequency Command."));
                    return true;
                }
                else sender.sendMessage(error);
                return true;
            }

            String name = args[1];
            Mine mine = new Mine(name, plugin);

            if (args.length == 2) {
                // -CREATE- //
                if (args[0].equalsIgnoreCase("create"))
                {
                    if (mine.mineExists())
                    {
                        sender.sendMessage(exists);
                        return true;
                    }
                    Selection sel = worldEdit.getSelection(p);
                    if (sel == null)
                    {
                        sender.sendMessage(color("&cYou must make a WorldEdit Selection first."));
                        return true;
                    }
                    Location min = sel.getMinimumPoint();
                    Location max = sel.getMaximumPoint();
                    World world = min.getWorld();
                    mine.setWorld(name, world.getName());
                    mine.setMax(name,max.getX(),max.getY(),max.getZ());
                    mine.setMin(name,min.getX(),min.getY(),min.getZ());
                    plugin.saveConfig();
                    sender.sendMessage(color("&aCreated " + name + "!"));
                    return true;
                }
                // -DELETE- //
                if (args[0].equalsIgnoreCase("delete"))
                {
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                    mine.delMine();
                    plugin.saveConfig();
                    sender.sendMessage(color("&a" + name + " has been deleted!"));
                    return true;
                }
                // -TELEPORT- //
                if (args[0].equalsIgnoreCase("teleport"))
                {
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                    Location tele = p.getLocation();
                    mine.setTele(tele.getBlockX()+0.5,tele.getBlockY(),tele.getBlockZ()+0.5,tele.getYaw(),tele.getPitch());
                    plugin.saveConfig();
                    sender.sendMessage(color("&a" + name + "'s teleport has been set!"));
                    return true;
                }
                // -INFO- //
                if (args[0].equalsIgnoreCase("info"))
                {
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                    sender.sendMessage(color("&2&l&m+--&a&l " + name + " &2&l&m--+"));
                    sender.sendMessage(color("&2World: &a" + mine.getWorld().getName()));
                    sender.sendMessage(color("&2Min: &a"
                            + mine.getMin().getBlockX() + ", "
                            + mine.getMin().getBlockY() + ", "
                            + mine.getMin().getBlockZ()));
                    sender.sendMessage(color("&2Max: &a"
                            + mine.getMax().getBlockX() + ", "
                            + mine.getMax().getBlockY() + ", "
                            + mine.getMax().getBlockZ()));
                    sender.sendMessage(color("&2Baseblock: &a" + mine.getBaseblock()));
                    sender.sendMessage(color("&2Schematic: &a" + mine.getSchem()));
                    sender.sendMessage(color("&2Teleport: &a"
                            + mine.getTele().getBlockX() + ", "
                            + mine.getTele().getBlockY() + ", "
                            + mine.getTele().getBlockZ() + ", ("
                            + mine.getTele().getYaw() + ", "
                            + mine.getTele().getPitch() + ")"));
                    sender.sendMessage(color("&2Ores: "));
                    if (!mine.oresExist()) sender.sendMessage(color("&4 - &cEMPTY"));
                    else {
                        List<String> orelist = mine.getOres();
                        for (String str : orelist) {
                            String[] ores = str.split(":");
                            sender.sendMessage(color(" &2- &a" + ores[0] + ":" + ores[1] + " " + ores[2] + "%"));
                        }
                    }
                    return true;
                }
                // -REGENERATE- //
                if (args[0].equalsIgnoreCase("regen"))
                {
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                        if (!mine.baseblockExists())
                        {
                            sender.sendMessage(color("&c"  + name + " doesn't have a baseblock set."));
                            return true;
                        }
                        if (!mine.schemExists())
                        {
                            sender.sendMessage(color("&c" + name + " doesn't have a schematic set."));
                            return true;
                        }
                        doReplace(args[1]);
                        sender.sendMessage(color("&a" + name + " has been regenerated!"));
                    return true;
                }
                else sender.sendMessage(error);
                return true;
            }

            if (args.length == 3)
            {
                // -DELETEORE- //
                if (args[0].equalsIgnoreCase("delore"))
                {
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                    List<String> orelist = mine.getOres();
                    for (String str : orelist) {
                        String[] ores = str.split(":");
                        String block = ores[0].toUpperCase();
                        String data = ores[1];
                        String chance = ores[2];
                        if (block.equals(args[2].toUpperCase()))
                        {
                            mine.delOre(block,data,chance);
                            plugin.saveConfig();
                            sender.sendMessage(color("&a" + args[2] + " has been deleted in " + name + "!"));
                            return true;
                        }
                    }
                    return true;
                }
                // - BASEBLOCK - //
                if (args[0].equalsIgnoreCase("baseblock"))
                {
                    String[] base = args[2].split(":");
                    String block = base[0].toUpperCase();
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                    if((Material.getMaterial(block.toUpperCase()) == null))
                    {
                        sender.sendMessage(color("&cInvalid Block Type."));
                        return true;
                    }
                    if (base.length == 1)mine.setBaseblock(block+":0");
                    else {
                        String data = base[1];
                        mine.setBaseblock(block+":"+data);
                    }
                    plugin.saveConfig();
                    sender.sendMessage(color("&a" + name + "'s base block has been changed!"));
                    return true;
                }
                // -SCHEMATIC- //
                if (args[0].equalsIgnoreCase("schem"))
                {
                    String schem = args[2];
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                    mine.setSchem(schem);
                    plugin.saveConfig();
                    sender.sendMessage(color("&a" + name + "'s schematic has been changed!"));
                    return true;
                }
                else sender.sendMessage(error);
                return true;
            }

            if (args.length == 4)
            {
                // -ADDORE- //
                if (args[0].equalsIgnoreCase("addore"))
                {
                    String[] ore = args[2].split(":");
                    String block = ore[0].toUpperCase();
                    String chance = args[3];
                    if (!mine.mineExists())
                    {
                        sender.sendMessage(nexists);
                        return true;
                    }
                    if((Material.getMaterial(block.toUpperCase()) == null))
                    {
                        sender.sendMessage(color("&cInvalid Block Type."));
                        return true;
                    }
                    if (ore.length == 1)mine.addOre(block,"0",chance);
                    else{
                        String data = ore[1];
                        mine.addOre(block,data,chance);
                    }
                    plugin.saveConfig();
                    sender.sendMessage(color("&aAdded an ore to " + args[1] + "!"));
                    return true;
                }
                else sender.sendMessage(error);
                return true;
            }
        }
        sender.sendMessage(color("&cCommand Doesn't Exist."));
        return true;
    }
}
