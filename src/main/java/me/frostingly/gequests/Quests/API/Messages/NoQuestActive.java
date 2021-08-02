package me.frostingly.gequests.Quests.API.Messages;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NoQuestActive {

    boolean executed = false;

    public void sendNoQuestActive(Player p) {
        if (!executed) {
            executed = true;
            p.sendMessage(Utilities.format(GEQuests.getInstance().getConfig().getString("messages.no_quest_active")));
            new BukkitRunnable() {
                @Override
                public void run() {
                    executed = false;
                }
            }.runTaskLater(GEQuests.getInstance(), 20L);
        }
    }

}
