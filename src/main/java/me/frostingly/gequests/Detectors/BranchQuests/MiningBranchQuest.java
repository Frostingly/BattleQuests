package me.frostingly.gequests.Detectors.BranchQuests;

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

import java.util.*;

public class MiningBranchQuest implements Listener {

    private final GEQuests plugin;

    public MiningBranchQuest(GEQuests plugin) {
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
                    ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                    if (questConfig.contains("quest.branchQuests")) {
                        branchQuestSection.getKeys(false).forEach(branchID -> {
                            if (branchQuestSection.getString(branchID + ".type").equalsIgnoreCase("Mining")) {
                                if (playerData.getMiningQuestData() == null) {
                                    MiningQuestData newMiningQuestData = new MiningQuestData();
                                    Map<Object, Map<Material, Integer>> blocksToMine = new HashMap<>();
                                    newMiningQuestData.setBlocksToMine(blocksToMine);
                                    playerData.setMiningQuestData(newMiningQuestData);
                                }
                                ConfigurationSection blocksSection = branchQuestSection.getConfigurationSection(branchID + ".neededBlocks");
                                if (playerData.getMiningQuestData().getBlocksToMine() == null || playerData.getHuntingQuestData().getEntitiesToKill().get(branchID) == null) {
                                    blocksSection.getKeys(false).forEach(blockID -> {
                                        Material material = Material.valueOf(blocksSection.getString(blockID + ".material"));
                                        int amount = blocksSection.getInt(blockID + ".amount");
                                        Map<Material, Integer> blocksToMine = new HashMap<>();
                                        blocksToMine.put(material, amount);
                                        playerData.getMiningQuestData().getBlocksToMine().put(branchID, blocksToMine);
                                    });
                                }
                                for (Material key : playerData.getMiningQuestData().getBlocksToMine().get(branchID).keySet()) {
                                    if (e.getBlock().getType() == key) {
                                        if (!Utilities.isEqualMap(playerData.getMiningQuestData().getBlocksToMine(), 0)) {
                                            if (playerData.getMiningQuestData().getBlocksToMine().get(branchID).get(key) > 0) {
                                                playerData.getMiningQuestData().getBlocksToMine().get(branchID).replace(key, (playerData.getMiningQuestData().getBlocksToMine().get(branchID).get(key) - 1));
                                            }
                                        }
                                    }
                                }
                                if (Utilities.isEqualMap(playerData.getMiningQuestData().getBlocksToMine().get(branchID), 0)) {
                                    if (branchQuestSection.contains(branchID + ".rewards")) {
                                        branchQuestSection.getStringList(branchID + ".rewards").forEach(reward -> {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                        });
                                    }
                                    playerData.getMiningQuestData().getBlocksToMine().clear();
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
                        });
                    }
                }
            }
        }
    }

}
