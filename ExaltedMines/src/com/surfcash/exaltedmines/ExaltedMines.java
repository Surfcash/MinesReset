package com.surfcash.exaltedmines;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class ExaltedMines extends JavaPlugin implements Listener{

    private WorldEditPlugin worldEdit;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        CmdHandler handler = new CmdHandler(this,worldEdit);
        return handler.run(sender,cmd,commandLabel,args);
    }

    @Override
    public void onEnable()
    {
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        getConfig().options().copyDefaults(false);
        saveConfig();
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable()
    {
        saveConfig();
    }

}
