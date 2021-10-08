package me.frostingly.gequests.commands.QuestCommands;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestAlreadyStarted;
import me.frostingly.gequests.Quests.API.Messages.QuestDialogueOutOfTime;
import me.frostingly.gequests.Quests.API.Messages.QuestStarted;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StartQuestCMD implements CommandExecutor {

    private final GEQuests plugin;

    public StartQuestCMD(GEQuests plugin) {
        this.plugin = plugin;
    }

    boolean executed = false;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) commandSender;
        String command = ChatColor.stripColor(StringUtils.join(args, " ", 0, args.length));
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (ChatColor.stripColor(playerData.getQuest().getQuestName()).equalsIgnoreCase(command)) {
                    if (!playerData.getQuest().isQuestActive()) {
                        if (plugin.getInventoryClick().canClick.get(p.getUniqueId())) {
                            new QuestStarted().sendQuestStarted(p);
                            playerData.getQuest().setQuestActive(true);
                            playerData.getQuest().setSeconds(null);
                            plugin.getInventoryClick().canClick.put(p.getUniqueId(), false);
                            plugin.getPlayerData().get(p.getUniqueId()).setDialogueOpened(false);
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        } else {
                            new QuestDialogueOutOfTime().sendQuestDialogueOutOfTime(p);
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        }
                    } else {
                        new QuestAlreadyStarted().sendQuestAlreadyStarted(p);
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
            }
        }
        return false;
    }
}
