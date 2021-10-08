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

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&7Quest NPC name: &f" + questNPCName);
            lore.add("&7Quest name: &f" + questName);
            lore.add("&7Quest objective: &f" + questObjective);
            lore.add("&7Quest type: &f" + questType);
            if (questConfig.getString("quest.permission") != null) {
                if (p.hasPermission(questConfig.getString("quest.permission"))) {
                    if (questConfig.getStringList("quest.lore.withPermission") != null) {
                        lore.addAll(questConfig.getStringList("quest.lore.withPermission"));
                    }
                } else {
                    if (questConfig.getStringList("quest.lore.withoutPermission") != null) {
                        lore.addAll(questConfig.getStringList("quest.lore.withoutPermission"));
                    }
                }
            } else {
                if (questConfig.getStringList("quest.lore.withoutPermission") != null) {
                    lore.addAll(questConfig.getStringList("quest.lore.withoutPermission"));
                }
            }
            lore.add(" ");
            if (quest.getQuestObjective() != null)
                lore.add(Utilities.format("&8&o" + format.format(quest.getQuestCompletedDate())));
            itemMeta.setLore(Utilities.formatList(lore));
            itemStack.setItemMeta(itemMeta);
            quests.add(itemStack);
        }
        return quests;
    }

}
