package me.frostingly.gequests.Detectors.PhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
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

public class HuntingPhaseQuest implements Listener {

    private final GEQuests plugin;

    public HuntingPhaseQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityKillE(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            Player p = e.getEntity().getKiller();
            PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
            if (playerData != null) {
                if (playerData.getQuest() != null) {
                    if (playerData.getQuest().isQuestActive()) {
                        FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                        if (playerData.getQuest().getQuestType().equalsIgnoreCase("Phase")) {
                            Integer id = playerData.getPhaseQuestID();
                            List<Integer> availablePhaseQuestsAmount = new ArrayList<>();
                            availablePhaseQuestsAmount.add(questConfig.getConfigurationSection("quest.phaseQuests").getKeys(false).size());
                            if (id <= availablePhaseQuestsAmount.size()) {
                                ConfigurationSection phaseSection = questConfig.getConfigurationSection("quest.phaseQuests");
                                if (phaseSection.getString(id + ".type").equalsIgnoreCase("Hunting")) {
                                    if (playerData.getHuntingQuestData() == null) {
                                        HuntingQuestData newHuntingQuestData = new HuntingQuestData();
                                        Map<Object, Map<EntityType, Integer>> entitiesToKill = new HashMap<>();
                                        newHuntingQuestData.setEntitiesToKill(entitiesToKill);
                                        playerData.setHuntingQuestData(newHuntingQuestData);
                                    }
                                    if (playerData.getHuntingQuestData().getEntitiesToKill() == null || playerData.getHuntingQuestData().getEntitiesToKill().get(id) == null) {
                                        ConfigurationSection entitiesSection = phaseSection.getConfigurationSection(id + ".neededEntities");
                                        entitiesSection.getKeys(false).forEach(entityID -> {
                                            EntityType entityType = EntityType.valueOf(entitiesSection.getString(entityID + ".entity"));
                                            int amount = entitiesSection.getInt(entityID + ".amount");
                                            Map<EntityType, Integer> entitiesToKill = new HashMap<>();
                                            entitiesToKill.put(entityType, amount);
                                            playerData.getHuntingQuestData().getEntitiesToKill().put(id, entitiesToKill);
                                        });
                                    }

                                    for (EntityType key : playerData.getHuntingQuestData().getEntitiesToKill().get(id).keySet()) {
                                        if (e.getEntity().getType() == key) {
                                            if (!Utilities.isEqualMap(playerData.getHuntingQuestData().getEntitiesToKill().get(id), 0)) {
                                                if (playerData.getHuntingQuestData().getEntitiesToKill().get(id).get(key) > 0) {
                                                    playerData.getHuntingQuestData().getEntitiesToKill().get(id).replace(key, (playerData.getHuntingQuestData().getEntitiesToKill().get(id).get(key) - 1));
                                                }
                                            }
                                        }
                                    }

                                    if (Utilities.isEqualMap(playerData.getHuntingQuestData().getEntitiesToKill().get(id), 0)) {
                                        if (phaseSection.contains(id + ".rewards")) {
                                            phaseSection.getStringList(id + ".rewards").forEach(reward -> {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                            });
                                        }
                                        playerData.getHuntingQuestData().getEntitiesToKill().clear();
                                        playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                        p.sendMessage(Utilities.format(phaseSection.getString(id + ".dialog")));
                                    }
                                }
                            } else {
                                new QuestFinished().sendQuestFinished(p);
                                playerData.setQuest(null);
                                playerData.setPhaseQuestID(0);
                            }
                        }
                    }
                }
            }
        }
    }
}
