package me.frostingly.gequests.Features;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class QuestLog {

    private final GEQuests plugin;

    public QuestLog(GEQuests plugin) {
        this.plugin = plugin;
    }

    int id = 0;

    public List<ItemStack> getAllPreviousQuests(Player p) {
        List<ItemStack> quests = new ArrayList<>();
        PlayerData playerData = new PlayerData(false);
        if (plugin.getPlayerData().get(p.getUniqueId()) == null) {
            plugin.getPlayerData().put(p.getUniqueId(), playerData);
        }
        for (QuestData quest : plugin.getPlayerData().get(p.getUniqueId()).getPrevQuests()) {
            id++;
            FileConfiguration questConfig = quest.getQuestConfig();
            String questNPCName = quest.getQuestNPCName();
            String questName = quest.getQuestName();
            String questObjective = quest.getQuestObjective();
            String questType = quest.getQuestType();
            //itemstack stuff
            ItemStack itemStack = new ItemStack(quest.getQuestMaterial());
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(Utilities.format("&bQuest #" + id));

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");

            List<String> questLore = new ArrayList<>();
            questLore.add(" ");
            questLore.add("&7Quest NPC name: &f" + questNPCName);
            questLore.add("&7Quest name: &f" + questName);
            questLore.add("&7Quest objective: &f" + questObjective);
            questLore.add("&7Quest type: &f" + questType);
            if (!quest.getQuestLoreWP().isEmpty() || !quest.getQuestLoreWOUTP().isEmpty()) {
                if (p.hasPermission(questConfig.getString("quest.permission"))) {
                    if (quest.getQuestLoreWP() != null) {
                        questLore.addAll(quest.getQuestLoreWP());
                    }
                } else {
                    if (quest.getQuestLoreWOUTP()!= null) {
                        questLore.addAll(quest.getQuestLoreWOUTP());
                    }
                }
            }
            questLore.add(" ");
            if (quest.getQuestObjective() != null)
                questLore.add(Utilities.format("&8&o" + format.format(quest.getQuestCompletedDate())));
            itemMeta.setLore(Utilities.formatList(questLore));
            itemStack.setItemMeta(itemMeta);
            quests.add(itemStack);
        }
        return quests;
    }

}
