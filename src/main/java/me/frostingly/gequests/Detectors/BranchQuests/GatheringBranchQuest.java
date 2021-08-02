package me.frostingly.gequests.Detectors.BranchQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GatheringBranchQuest {

    private final GEQuests plugin;

    public GatheringBranchQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    public void finishQuest() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
                    if (playerData != null) {
                        if (playerData.getQuest() != null) {
                            if (playerData.getQuest().isQuestActive()) {
                                FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                                Map<Object, List<ItemStack>> supplies = new HashMap<>();
                                ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                                if (questConfig.contains("quest.branchQuests")) {
                                    branchQuestSection.getKeys(false).forEach(branchID -> {
                                        if (branchQuestSection.getString(branchID + ".type").equalsIgnoreCase("Gathering")) {
                                            List<ItemStack> suppliesList = new ArrayList<>();
                                            ConfigurationSection supplySection = branchQuestSection.getConfigurationSection(branchID + ".neededSupplies");
                                            supplySection.getKeys(false).forEach(supplyID -> {
                                                Material material = Material.valueOf(supplySection.getString(supplyID + ".material"));
                                                int amount = supplySection.getInt(supplyID + ".amount");
                                                ItemStack itemStack = new ItemStack(material, amount);
                                                suppliesList.add(itemStack);
                                            });
                                            supplies.put(branchID, suppliesList);
                                            boolean hasEnough = true;
                                            for (ItemStack item : supplies.get(branchID)) {
                                                if (!p.getInventory().containsAtLeast(item, item.getAmount())) {
                                                    hasEnough = false;
                                                }
                                            }

                                            if (hasEnough) {
                                                boolean complete = true;
                                                for (ItemStack item : supplies.get(branchID)) {
                                                    if (p.getInventory().containsAtLeast(item, item.getAmount())) {
                                                        p.getInventory().removeItem(item);
                                                        p.updateInventory();
                                                    } else {
                                                        complete = false;
                                                    }
                                                }
                                                if (complete) {
                                                    if (branchQuestSection.contains(branchID + ".rewards")) {
                                                        branchQuestSection.getStringList(branchID + ".rewards").forEach(reward -> {
                                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                                        });
                                                    }
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
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }
}