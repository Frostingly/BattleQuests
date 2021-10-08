package me.frostingly.gequests.Detectors.PhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatheringPhaseQuest {

    private final GEQuests plugin;

    public GatheringPhaseQuest(GEQuests plugin) {
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
                                if (playerData.getQuest().getQuestType().equalsIgnoreCase("Phase")) {
                                    Integer id = playerData.getPhaseQuestID();
                                    List<Integer> availablePhaseQuestsAmount = new ArrayList<>();
                                    availablePhaseQuestsAmount.add(questConfig.getConfigurationSection("quest.phaseQuests").getKeys(false).size());
                                    if (id <= availablePhaseQuestsAmount.size()) {
                                        ConfigurationSection phaseSection = questConfig.getConfigurationSection("quest.phaseQuests." + id);
                                        if (questConfig.getString("quest.phaseQuests." + id + ".type").equalsIgnoreCase("Gathering")) {
                                            ConfigurationSection suppliesSection = phaseSection.getConfigurationSection(".neededSupplies");
                                            List<ItemStack> supplies = new ArrayList<>();
                                            suppliesSection.getKeys(false).forEach(supplyName -> {
                                                Material material = Material.getMaterial(suppliesSection.getString(supplyName + ".material"));
                                                int amount = suppliesSection.getInt(supplyName + ".amount");
                                                ItemStack itemStack = new ItemStack(material, amount);
                                                supplies.add(itemStack);
                                            });

                                            boolean hasEnough = true;
                                            for (ItemStack itemStack : supplies) {
                                                if (!p.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                                                    hasEnough = false;
                                                }
                                            }

                                            if (hasEnough) {
                                                boolean complete = true;
                                                for (ItemStack itemStack : supplies) {
                                                    if (p.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                                                        p.getInventory().removeItem(itemStack);
                                                        p.updateInventory();
                                                    } else {
                                                        complete = false;
                                                    }
                                                }
                                                if (complete) {
                                                    if (phaseSection.contains(".rewards")) {
                                                        phaseSection.getStringList(".rewards").forEach(reward -> {
                                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                                        });
                                                    }
                                                    playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                                    p.sendMessage(Utilities.format(phaseSection.getString(".dialog")));
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
                }
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }
}
