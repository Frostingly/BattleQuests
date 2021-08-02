package me.frostingly.gequests.events;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

    private final GEQuests plugin;

    public Join(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoinE(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (plugin.getPlayerData().get(p.getUniqueId()) == null) {
            PlayerData newPlayer = new PlayerData(false);
            plugin.getPlayerData().put(p.getUniqueId(), newPlayer);
        }
    }

}
