package me.frostingly.gequests.Detectors.BranchPhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.ForagingQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ForagingBranchPhaseQuest implements Listener {

    private final GEQuests plugin;

    public ForagingBranchPhaseQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogMine(BlockBreakEvent e) {
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
                                    if (phaseSection.getString(".type").equalsIgnoreCase("Foraging")) {
                                        if (playerData.getForagingQuestData() == null) {
                                            ForagingQuestData newForagingQuestData = new ForagingQuestData();
                                            Map<Object, Map<Material, Integer>> logsToMine = new HashMap<>();
                                            newForagingQuestData.setLogsToMine(logsToMine);
                                            playerData.setForagingQuestData(newForagingQuestData);
                                        }

                                        if (e.getBlock().getType() == Material.ACACIA_LOG
                                                || e.getBlock().getType() == Material.BIRCH_LOG
                                                || e.getBlock().getType() == Material.OAK_LOG
                                                || e.getBlock().getType() == Material.SPRUCE_LOG
                                                || e.getBlock().getType() == Material.JUNGLE_LOG ||
                                                e.getBlock().getType() == Material.DARK_OAK_LOG) {
                                            //code if block is one of those
                                            if (playerData.getForagingQuestData().getLogsToMine() == null || playerData.getForagingQuestData().getLogsToMine().get(branchID) == null) {
                                                Map<Material, Integer> logsToMine = new HashMap<>();
                                                ConfigurationSection entitiesSection = phaseSection.getConfigurationSection(".neededLogs");
                                                entitiesSection.getKeys(false).forEach(logID -> {
                                                    Material material = Material.valueOf(entitiesSection.getString(logID + ".material"));
                                                    int amount = entitiesSection.getInt(logID + ".amount");
                                                    logsToMine.put(material, amount);
                                                });
                                                playerData.getForagingQuestData().getLogsToMine().put(branchID, logsToMine);
                                            }
                                            for (Material key : playerData.getForagingQuestData().getLogsToMine().get(branchID).keySet()) {
                                                if (e.getBlock().getType() == key) {
                                                    if (!Utilities.isEqualMap(playerData.getForagingQuestData().getLogsToMine().get(branchID), 0)) {
                                                        if (playerData.getForagingQuestData().getLogsToMine().get(branchID).get(key) > 0) {
                                                            playerData.getForagingQuestData().getLogsToMine().get(branchID).replace(key, (playerData.getForagingQuestData().getLogsToMine().get(branchID).get(key) - 1));
                                                        }
                                                    }
                                                }
                                            }

                                            if (Utilities.isEqualMap(playerData.getForagingQuestData().getLogsToMine().get(branchID), 0)) {
                                                if (phaseSection.contains(".rewards")) {
                                                    phaseSection.getStringList(".rewards").forEach(reward -> {
                                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                                    });
                                                }
                                                playerData.getForagingQuestData().getLogsToMine().clear();
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
                            }
                        });
                    }
                }
            }
        }
    }
}
