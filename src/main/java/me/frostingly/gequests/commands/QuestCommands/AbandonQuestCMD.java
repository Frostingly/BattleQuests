package me.frostingly.gequests.commands.QuestCommands;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Quests.API.Messages.NoQuestActive;
import me.frostingly.gequests.Quests.API.Messages.QuestAbandoned;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbandonQuestCMD implements CommandExecutor {

    private final GEQuests plugin;

    public AbandonQuestCMD(GEQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player p = (Player) commandSender;
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (playerData.getQuest().isQuestActive()) {
                    new QuestAbandoned().sendQuestAbandoned(p);
                    plugin.getPlayerData().get(p.getUniqueId()).setQuest(null);
                } else {
                    new NoQuestActive().sendNoQuestActive(p);
                }
            } else {
                new NoQuestActive().sendNoQuestActive(p);
            }
        } else {
            new NoQuestActive().sendNoQuestActive(p);
        }
        return false;
    }
}
