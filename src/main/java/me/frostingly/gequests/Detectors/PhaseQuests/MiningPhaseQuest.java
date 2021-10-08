package me.frostingly.gequests.Detectors.PhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.MiningQuestData;
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

public class MiningPhaseQuest implements Listener {

    private final GEQuests plugin;

    public MiningPhaseQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMineE(BlockBreakEvent e) {
        Player p = (Player) e.getPlayer();
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
                            if (phaseSection.getString(id + ".type").equalsIgnoreCase("Mining")) {
                                if (playerData.getMiningQuestData() == null) {
                                    MiningQuestData newMiningQuestData = new MiningQuestData();
                                    Map<Object, Map<Material, Integer>> blocksToMine = new HashMap<>();
                                    newMiningQuestData.setBlocksToMine(blocksToMine);
                                    playerData.setMiningQuestData(newMiningQuestData);
                                }
                                ConfigurationSection blocksSection = phaseSection.getConfigurationSection(id + ".neededBlocks");
                                if (playerData.getMiningQuestData().getBlocksToMine() == null || playerData.getHuntingQuestData().getEntitiesToKill().get(id) == null) {
                                    blocksSection.getKeys(false).forEach(blockID -> {
                                        Material material = Material.valueOf(blocksSection.getString(blockID + ".material"));
                                        int amount = blocksSection.getInt(blockID + ".amount");
                                        Map<Material, Integer> blocksToMine = new HashMap<>();
                                        blocksToMine.put(material, amount);
                                        playerData.getMiningQuestData().getBlocksToMine().put(id, blocksToMine);
                                    });
                                }
                                for (Material key : playerData.getMiningQuestData().getBlocksToMine().get(id).keySet()) {
                                    if (e.getBlock().getType() == key) {
                                        if (!Utilities.isEqualMap(playerData.getMiningQuestData().getBlocksToMine(), 0)) {
                                            if (playerData.getMiningQuestData().getBlocksToMine().get(id).get(key) > 0) {
                                                playerData.getMiningQuestData().getBlocksToMine().get(id).replace(key, (playerData.getMiningQuestData().getBlocksToMine().get(id).get(key) - 1));
                                            }
                                        }
                                    }
                                }

                                if (Utilities.isEqualMap(playerData.getMiningQuestData().getBlocksToMine().get(id), 0)) {
                                    if (phaseSection.contains(id + ".rewards")) {
                                        phaseSection.getStringList(id + ".rewards").forEach(reward -> {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                        });
                                    }
                                    playerData.getMiningQuestData().getBlocksToMine().clear();
                                    playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                    p.sendMessage(Utilities.format(phaseSection.getString(id + ".dialog")));
                                }
                            }
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
    }

}
