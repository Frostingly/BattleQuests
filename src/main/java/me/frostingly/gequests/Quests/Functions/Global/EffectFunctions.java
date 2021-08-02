package me.frostingly.gequests.Quests.Functions.Global;

import me.frostingly.gequests.GEQuests;
import org.bukkit.Location;
import org.bukkit.Material;

public class EffectFunctions {

    private final GEQuests plugin;

    public EffectFunctions(GEQuests plugin) {
        this.plugin = plugin;
    }

    public void setBlocksInRadius(Location startPoint, double radius, Material replacement) {
        for(double x = startPoint.getX() - radius; x <= startPoint.getX() + radius; x++){
            for(double y = startPoint.getY() - radius; y <= startPoint.getY() + radius; y++){
                for(double z = startPoint.getZ() - radius; z <= startPoint.getZ() + radius; z++){
                    Location loc = new Location(startPoint.getWorld(), x, y, z);
                    loc.getBlock().setType(replacement);
                }
            }
        }
    }

    public void setBlocksInRadiusX(Location startPoint, double radiusX, Material replacement) {
        for(double x = startPoint.getX() - radiusX; x <= startPoint.getX() + radiusX; x++) {
            Location loc = new Location(startPoint.getWorld(), x, startPoint.getY(), startPoint.getZ());
            loc.getBlock().setType(replacement);
        }
    }

    public void setBlocksInRadiusY(Location startPoint, double radiusY, Material replacement) {
        for(double y = startPoint.getY() - radiusY; y <= startPoint.getY() + radiusY; y++) {
            Location loc = new Location(startPoint.getWorld(), startPoint.getX(), y, startPoint.getZ());
            loc.getBlock().setType(replacement);
        }
    }

    public void setBlocksInRadiusZ(Location startPoint, double radiusZ, Material replacement) {
        for(double z = startPoint.getZ() - radiusZ; z <= startPoint.getZ() + radiusZ; z++) {
            Location loc = new Location(startPoint.getWorld(), startPoint.getX(), startPoint.getY(), z);
            loc.getBlock().setType(replacement);
        }
    }

}
