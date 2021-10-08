package me.frostingly.gequests.Detectors.BranchPhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.EntityData;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.ForagingQuestData;
import me.frostingly.gequests.Information.QuestData.InteractQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InteractEntityBranchPhaseQuest implements Listener {

    private final GEQuests plugin;

    public InteractEntityBranchPhaseQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractEntityE(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (playerData.getQuest().isQuestActive()) {
                    FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                    Integer id = playerData.getPhaseQuestID();
                    ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                    if (questConfig.contains("quest.branchQuests")) {
                        branchQuestSection.getKeys(false).forEach(branchID -> {
                            if (branchQuestSection.getString(branchID + ".type").equalsIgnoreCase("Phase")) {
                                List<Integer> availablePhaseQuestsAmount = new ArrayList<>();
                                availablePhaseQuestsAmount.add(branchQuestSection.getConfigurationSection(branchID + ".phaseQuests").getKeys(false).size());
                                if (id <= availablePhaseQuestsAmount.size()) {
                                    ConfigurationSection phaseSection = branchQuestSection.getConfigurationSection(branchID + ".phaseQuests." + id);
                                    if (phaseSection.getString(".type").equalsIgnoreCase("InteractEntity")) {
                                        if (playerData.getInteractQuestData().getEntitiesToInteractWith() == null || playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID) == null) {
                                            InteractQuestData newInteractQuestData = new InteractQuestData();
                                            Map<Object, Map<String, Integer>> entitiesToInteractWith = new HashMap<>();
                                            newInteractQuestData.setEntitiesToInteractWith(entitiesToInteractWith);
                                            playerData.setInteractQuestData(newInteractQuestData);
                                        }
                                        if (playerData.getInteractQuestData().getEntitiesToInteractWith() == null || playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID) == null) {
                                            ConfigurationSection entitySection = phaseSection.getConfigurationSection(".neededEntities");
                                            Map<String, Integer> entitiesToInteractWith = new HashMap<>();
                                            entitySection.getKeys(false).forEach(entityID -> {
                                                String entityName = entitySection.getString(entityID + ".entity_name");
                                                int amount = entitySection.getInt(entityID + ".amount");
                                                EntityType entityType = EntityType.valueOf(entitySection.getString(entityID + ".entity_type"));
                                                entitiesToInteractWith.put(entityName, amount);
                                                EntityData newEntity = new EntityData(entityName, entityName, entityType);
                                                playerData.getEntities().add(newEntity);
                                            });
                                            playerData.getInteractQuestData().getEntitiesToInteractWith().put(branchID, entitiesToInteractWith);
                                        }
                                        String entityName = e.getRightClicked().getName();
                                        String customEntityName = e.getRightClicked().getCustomName();
                                        for (String key : playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID).keySet()) {
                                            for (int i = 0; i < playerData.getEntities().size(); i++) {
                                                if (e.getRightClicked().getType() == playerData.getEntities().get(i).getEntityType()) {
                                                    if (entityName.equalsIgnoreCase(Utilities.format(key)) || customEntityName != null && customEntityName.equalsIgnoreCase(Utilities.format(key))) {
                                                        //code if entity name or custom name is equals to key
                                                        if (!Utilities.isEqualMap(playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID), 0)) {
                                                            if (playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID).get(key) > 0) {
                                                                playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID).replace(key, (playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID).get(key) - 1));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (Utilities.isEqualMap(playerData.getInteractQuestData().getEntitiesToInteractWith().get(branchID), 0)) {
                                            if (phaseSection.contains(".rewards")) {
                                                phaseSection.getStringList(".rewards").forEach(reward -> {
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                                });
                                            }
                                            playerData.getInteractQuestData().getEntitiesToInteractWith().clear();
                                            playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                            p.sendMessage(Utilities.format(phaseSection.getString(".dialog")));
                                        }
                                    }
                                } else {
                                    if (!playerData.isExecuted()) {
                                        playerData.setExecuted(true);
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
                                        playerData.setPhaseQuestID(0);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                playerData.setExecuted(false);
                                            }
                                        }.runTaskLater(plugin, 20L);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

}
