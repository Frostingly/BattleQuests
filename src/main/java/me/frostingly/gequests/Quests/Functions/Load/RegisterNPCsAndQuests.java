package me.frostingly.gequests.Quests.Functions.Load;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Utilities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public class RegisterNPCsAndQuests {

    private final GEQuests plugin;

    public RegisterNPCsAndQuests(GEQuests plugin) {
        this.plugin = plugin;
    }

    int npcsLoaded = 0;
    int questTypesLoaded = 0;
    int questsLoaded = 0;

    public void registerNPCsAndQuests() {
        File root = new File(plugin.getDataFolder(), "Quests");
        if (!root.exists()) {
            root.mkdir();
        }
        if (root.listFiles().length > 0) {
            for (File file : root.listFiles()) {
                npcsLoaded = npcsLoaded + 1;
                if (file.isDirectory()) {
                    if (file.listFiles().length > 0) {
                        for (File file2 : file.listFiles()) {
                            if (file2.isDirectory()) {
                                questTypesLoaded = questTypesLoaded + 1;
                                if (file2.listFiles().length > 0) {
                                    for (File file3 : file2.listFiles()) {
                                        FileConfiguration questConfig = YamlConfiguration.loadConfiguration(file3);
                                        ConfigurationSection dialogSection = questConfig.getConfigurationSection("quest.dialogue.npc");
                                        if (dialogSection != null) {
                                            dialogSection.getKeys(false).forEach(messageID -> {
                                                plugin.questDialogMessagesNPC.put(questConfig.getString("quest.name"), Integer.parseInt(messageID));
                                            });
                                        }
                                        QuestData quest = new QuestData(file.getName(), Utilities.format(questConfig.getString("quest.name")), questConfig);
                                        quest.setQuestType(questConfig.getString("quest.type"));
                                        quest.setQuestDialogueMessagesMax(plugin.questDialogMessagesNPC.get(questConfig.getString("quest.name")));
                                        quest.setQuestActive(false);
                                        if (questConfig.getString("quest.objective") != null)
                                            quest.setQuestObjective(Utilities.format(questConfig.getString("quest.objective")));
                                        if (questConfig.getString("quest.lore") != null) {
                                            if (questConfig.getString("quest.lore.withPermission") != null) {
                                                quest.setQuestLoreWP(questConfig.getStringList("quest.lore.withPermission"));
                                            }
                                            if (questConfig.getString("quest.lore.withoutPermission") != null) {
                                                quest.setQuestLoreWOUTP(questConfig.getStringList("quest.lore.withoutPermission"));
                                            }
                                        }
                                        plugin.getQuests().add(quest);
                                        questsLoaded = questsLoaded + 1;
                                    }
                                } else {
                                    plugin.getLogger().log(Level.SEVERE, "No quests loaded, in " + file2.getName().toUpperCase() + " for " + file.getName().toUpperCase());
                                }
                            }
                        }
                    } else {
                        plugin.getLogger().log(Level.SEVERE, "No quest types loaded, for NPC " + file.getName().toUpperCase());
                    }
                }
            }
        }
        plugin.getLogger().log(Level.INFO, "Loaded " + npcsLoaded + " NPCs.");
        plugin.getLogger().log(Level.INFO, "Loaded " + questTypesLoaded + " quest types.");
        plugin.getLogger().log(Level.INFO, "Loaded " + questsLoaded + " quests.");
    }

}
