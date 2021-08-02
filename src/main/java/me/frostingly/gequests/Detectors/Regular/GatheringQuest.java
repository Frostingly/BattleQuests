package me.frostingly.gequests.Detectors.Regular;

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

public class GatheringQuest {

    private final GEQuests plugin;

    public GatheringQuest(GEQuests plugin) {
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
                                if (playerData.getQuest().getQuestType().equalsIgnoreCase("Gathering")) {
                                    ConfigurationSection suppliesSection = questConfig.getConfigurationSection("quest.neededSupplies");
                                    List<ItemStack> supplies = new ArrayList<>();
                                    suppliesSection.getKeys(false).forEach(supplyID -> {
                                        Material material = Material.valueOf(suppliesSection.getString(supplyID + ".material"));
                                        int amount = suppliesSection.getInt(supplyID + ".amount");
                                        ItemStack itemStack = new ItemStack(material, amount);
                                        supplies.add(itemStack);
                                    });

                                    boolean hasEnough = true;
                                    for (ItemStack item : supplies) {
                                        if (!p.getInventory().containsAtLeast(item, item.getAmount())) {
                                            hasEnough = false;
                                        }
                                    }

                                    if (hasEnough) {
                                        boolean complete = true;
                                        for (ItemStack item : supplies) {
                                            if (p.getInventory().containsAtLeast(item, item.getAmount())) {
                                                p.getInventory().removeItem(item);
                                                p.updateInventory();
                                            } else {
                                                complete = false;
                                            }
                                        }
                                        if (complete) {
                                            if (questConfig.contains("quest.rewards")) {
                                                questConfig.getStringList("quest.rewards").forEach(reward -> {
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
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }
}