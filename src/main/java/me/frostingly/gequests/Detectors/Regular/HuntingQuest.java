package me.frostingly.gequests.Detectors.Regular;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.HuntingQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.*;

public class HuntingQuest implements Listener {

    private final GEQuests plugin;

    public HuntingQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    boolean executed = false;

    @EventHandler
    public void onEntityKillE(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            Player p = e.getEntity().getKiller();
            PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
            if (playerData != null) {
                if (playerData.getQuest() != null) {
                    if (playerData.getQuest().isQuestActive()) {
                        FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                        if (playerData.getQuest().getQuestType().equalsIgnoreCase("Hunting")) {
                            if (playerData.getHuntingQuestData() == null) {
                                HuntingQuestData newHuntingQuestData = new HuntingQuestData();
                                Map<Object, Map<EntityType, Integer>> entitiesToKill = new HashMap<>();
                                newHuntingQuestData.setEntitiesToKill(entitiesToKill);
                                playerData.setHuntingQuestData(newHuntingQuestData);
                            }
                            if (playerData.getHuntingQuestData().getEntitiesToKill() == null || playerData.getHuntingQuestData().getEntitiesToKill().get(1) == null) {
                                ConfigurationSection entitiesSection = questConfig.getConfigurationSection("quest.neededEntities");
                                entitiesSection.getKeys(false).forEach(entityID -> {
                                    EntityType entityType = EntityType.valueOf(entitiesSection.getString(entityID + ".entity"));
                                    int amount = entitiesSection.getInt(entityID + ".amount");
                                    Map<EntityType, Integer> entitiesToKill = new HashMap<>();
                                    entitiesToKill.put(entityType, amount);
                                    playerData.getHuntingQuestData().getEntitiesToKill().put(1, entitiesToKill);
                                });
                            }
                            for (EntityType key : playerData.getHuntingQuestData().getEntitiesToKill().get(1).keySet()) {
                                if (e.getEntity().getType() == key) {
                                    if (!Utilities.isEqualMap(playerData.getHuntingQuestData().getEntitiesToKill().get(1), 0)) {
                                        if (playerData.getHuntingQuestData().getEntitiesToKill().get(1).get(key) > 0) {
                                            playerData.getHuntingQuestData().getEntitiesToKill().get(1).replace(key, (playerData.getHuntingQuestData().getEntitiesToKill().get(1).get(key) - 1));
                                        }
                                    }
                                }
                            }
                            if (Utilities.isEqualMap(playerData.getHuntingQuestData().getEntitiesToKill().get(1), 0)) {
                                if (questConfig.contains("quest.rewards")) {
                                    questConfig.getStringList("quest.rewards").forEach(reward -> {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                    });
                                }
                                playerData.getHuntingQuestData().getEntitiesToKill().clear();
                                Date date = new Date();
                                playerData.getQuest().setQuestCompletedDate(date);
                                if (playerData.getPrevQuests().size() == 0) {
                                    List<QuestData> prevQuests = new ArrayList<>();
                                    prevQuests.add(playerData.getQuest());
                                    plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                                } else {
                                    List<QuestData> prevQuests = plugin.getPlayerData().get(p.getUniqueId()).getPrevQuests();
                                    prevQuests.add(playerData.getQuest());
                                    plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                                }
                                new QuestFinished().sendQuestFinished(p);
                                playerData.setQuest(null);
                            }
                        }
                    }
                }
            }
        }
    }
}
