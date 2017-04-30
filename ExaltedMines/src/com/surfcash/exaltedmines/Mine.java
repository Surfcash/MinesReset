package com.surfcash.exaltedmines;

import com.sk89q.worldedit.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class Mine {
    ExaltedMines plugin;
    public String mineName = null;
    public World world = null;
    public double maxX = 0.0;
    public double maxY = 0.0;
    public double maxZ = 0.0;
    public double minX = 0.0;
    public double minY = 0.0;
    public double minZ = 0.0;
    public String baseblock = null;
    public String schematic = null;
    public double teleX = 0.0;
    public double teleY = 0.0;
    public double teleZ = 0.0;
    public float teleYaw = 0.0f;
    public float telePitch = 0.0f;
    public List<String> ores = null;

    public Mine(String name, ExaltedMines instance)
    {
        plugin = instance;
        if (getName(name))
        {
            mineName = name;
            world = Bukkit.getWorld(plugin.getConfig().getString("Mines."+name+".world"));
            maxX = plugin.getConfig().getDouble("Mines."+name+".max.x");
            maxY = plugin.getConfig().getDouble("Mines."+name+".max.y");
            maxZ = plugin.getConfig().getDouble("Mines."+name+".max.z");
            minX = plugin.getConfig().getDouble("Mines."+name+".min.x");
            minY = plugin.getConfig().getDouble("Mines."+name+".min.y");
            minZ = plugin.getConfig().getDouble("Mines."+name+".min.z");
            baseblock = plugin.getConfig().getString("Mines."+name+".baseblock");
            schematic = plugin.getConfig().getString("Mines."+name+".schematic");
            teleX = plugin.getConfig().getDouble("Mines."+name+".teleport.x");
            teleY = plugin.getConfig().getDouble("Mines."+name+".teleport.y");
            teleZ = plugin.getConfig().getDouble("Mines."+name+".teleport.z");
            if (teleExists())
            {
                teleYaw = Float.parseFloat(plugin.getConfig().getString("Mines." + name + ".teleport.yaw"));
                telePitch = Float.parseFloat(plugin.getConfig().getString("Mines." + name + ".teleport.pitch"));
            }
            ores = plugin.getConfig().getStringList("Mines."+name+".ores");
        }
    }
    public boolean getName(String name)
    {
        return plugin.getConfig().contains("Mines."+name);
    }
    public World getWorld()
    {
        return world;
    }
    public Vector getMax()
    {
        return new Vector(maxX,maxY,maxZ);
    }
    public Vector getMin()
    {
        return new Vector(minX,minY,minZ);
    }
    public String getBaseblock()
    {
        return baseblock;
    }
    public String getSchem()
    {
        return schematic;
    }
    public Location getTele()
    {
        return new Location(world,teleX,teleY,teleZ,teleYaw,telePitch);
    }
    public List<String> getOres()
    {
        return ores;
    }
    public void delMine()
    {
        plugin.getConfig().set("Mines."+mineName,null);
    }
    public boolean mineExists()
    {
        return(plugin.getConfig().contains("Mines."+mineName));
    }
    public boolean oresExist()
    {
        return(plugin.getConfig().contains("Mines."+mineName+".ores"));
    }
    public boolean baseblockExists()
    {
        return(plugin.getConfig().contains("Mines."+mineName+".baseblock"));
    }
    public boolean schemExists()
    {
        return(plugin.getConfig().contains("Mines."+mineName+".schematic"));
    }
    public boolean teleExists()
    {
        return(plugin.getConfig().contains("Mines."+mineName+".teleport"));
    }
    public void setWorld(String name,String world)
    {
        plugin.getConfig().set("Mines."+name+".world",world);
    }
    public void setMax(String name,double x,double y,double z)
    {
        plugin.getConfig().set("Mines."+name+".max.x",x);
        plugin.getConfig().set("Mines."+name+".max.y",y);
        plugin.getConfig().set("Mines."+name+".max.z",z);
    }
    public void setMin(String name,double x,double y,double z)
    {
        plugin.getConfig().set("Mines."+name+".min.x",x);
        plugin.getConfig().set("Mines."+name+".min.y",y);
        plugin.getConfig().set("Mines."+name+".min.z",z);
    }
    public void setBaseblock(String block)
    {
        plugin.getConfig().set("Mines."+mineName+".baseblock",block);
    }
    public void setSchem(String schem)
    {
        plugin.getConfig().set("Mines."+mineName+".schematic",schem);
    }
    public void setTele(double x,double y,double z,float yaw,float pitch)
    {
        plugin.getConfig().set("Mines."+mineName+".teleport.x",x);
        plugin.getConfig().set("Mines."+mineName+".teleport.y",y);
        plugin.getConfig().set("Mines."+mineName+".teleport.z",z);
        plugin.getConfig().set("Mines."+mineName+".teleport.yaw",yaw);
        plugin.getConfig().set("Mines."+mineName+".teleport.pitch",pitch);
    }
    public void addOre(String block,String data,String chance)
    {
        List<String> ores = plugin.getConfig().getStringList("Mines."+mineName+".ores");
        ores.add(block+":"+data+":"+chance);
        plugin.getConfig().set("Mines."+mineName+".ores",ores);
    }
    public void delOre(String block,String data,String chance)
    {
        List<String> ores = plugin.getConfig().getStringList("Mines."+mineName+".ores");
        ores.remove(block+":"+data+":"+chance);
        plugin.getConfig().set("Mines."+mineName+".ores",ores);
    }
}
