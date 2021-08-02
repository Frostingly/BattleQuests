package me.frostingly.gequests.Quests.API.Messages;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestDialogueOutOfTime {

    boolean executed = false;

    public void sendQuestDialogueOutOfTime(Player p) {
        if (!executed) {
            executed = true;
            p.sendMessage(Utilities.format(GEQuests.getInstance().getConfig().getString("messages.quest_dialogue_out_of_time").replace("<quest>", ChatColor.stripColor(GEQuests.getInstance().getPlayerData().get(p.getUniqueId()).getQuest().getQuestName()))));
            new BukkitRunnable() {
                @Override
                public void run() {
                    executed = false;
                }
            }.runTaskLater(GEQuests.getInstance(), 20L);
        }
    }

}
