package me.frostingly.gequests.Detectors.BranchPhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.CraftingQuestData;
import me.frostingly.gequests.Information.QuestData.HuntingQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HuntingBranchPhaseQuest implements Listener {

    private final GEQuests plugin;

    public HuntingBranchPhaseQuest(GEQuests plugin) {
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
                        Integer id = playerData.getPhaseQuestID();
                        ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                        if (questConfig.contains("quest.branchQuests")) {
                            branchQuestSection.getKeys(false).forEach(branchID -> {
                                if (branchQuestSection.getString(branchID + ".type").equalsIgnoreCase("Phase")) {
                                    List<Integer> availablePhaseQuestsAmount = new ArrayList<>();
                                    availablePhaseQuestsAmount.add(branchQuestSection.getConfigurationSection(branchID + ".phaseQuests").getKeys(false).size());
                                    if (id <= availablePhaseQuestsAmount.size()) {
                                        ConfigurationSection phaseSection = branchQuestSection.getConfigurationSection(branchID + ".phaseQuests." + id);
                                        if (phaseSection.getString(".type").equalsIgnoreCase("Hunting")) {
                                            if (playerData.getHuntingQuestData() == null) {
                                                HuntingQuestData newHuntingQuestData = new HuntingQuestData();
                                                Map<Object, Map<EntityType, Integer>> entitiesToKill = new HashMap<>();
                                                newHuntingQuestData.setEntitiesToKill(entitiesToKill);
                                                playerData.setHuntingQuestData(newHuntingQuestData);
                                            }
                                            if (playerData.getHuntingQuestData().getEntitiesToKill() == null || playerData.getHuntingQuestData().getEntitiesToKill().get(branchID) == null) {
                                                Map<EntityType, Integer> entitiesToKill = new HashMap<>();
                                                ConfigurationSection entitiesSection = phaseSection.getConfigurationSection(".neededEntities");
                                                entitiesSection.getKeys(false).forEach(entityID -> {
                                                    EntityType entityType = EntityType.valueOf(entitiesSection.getString(entityID + ".entity"));
                                                    int amount = entitiesSection.getInt(entityID + ".amount");
                                                    entitiesToKill.put(entityType, amount);
                                                });
                                                playerData.getHuntingQuestData().getEntitiesToKill().put(branchID, entitiesToKill);
                                            }

                                            for (EntityType key : playerData.getHuntingQuestData().getEntitiesToKill().get(branchID).keySet()) {
                                                if (e.getEntity().getType() == key) {
                                                    if (!Utilities.isEqualMap(playerData.getHuntingQuestData().getEntitiesToKill().get(branchID), 0)) {
                                                        if (playerData.getHuntingQuestData().getEntitiesToKill().get(branchID).get(key) > 0) {
                                                            playerData.getHuntingQuestData().getEntitiesToKill().get(branchID).replace(key, (playerData.getHuntingQuestData().getEntitiesToKill().get(branchID).get(key) - 1));
                                                        }
                                                    }
                                                }
                                            }

                                            if (Utilities.isEqualMap(playerData.getHuntingQuestData().getEntitiesToKill().get(branchID), 0)) {
                                                if (phaseSection.contains(".rewards")) {
                                                    phaseSection.getStringList(".rewards").forEach(reward -> {
                                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                                    });
                                                }
                                                playerData.getHuntingQuestData().getEntitiesToKill().clear();
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

}
