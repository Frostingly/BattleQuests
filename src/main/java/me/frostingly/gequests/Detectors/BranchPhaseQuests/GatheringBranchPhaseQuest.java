package me.frostingly.gequests.Detectors.BranchPhaseQuests;

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

public class GatheringBranchPhaseQuest {

    private final GEQuests plugin;

    public GatheringBranchPhaseQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    /*
        branchQuests:
          amount: 1
          1stBranchQuest:
            type: Phase
            maxPhaseQuests: 2

              phaseQuests:
                1:
                  type: Gathering
     */

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
                                Integer id = playerData.getPhaseQuestID();
                                ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                                if (branchQuestSection != null)  {
                                    if (questConfig.getInt("quest.maxBranchQuests") == 1) {
                                        if (branchQuestSection.getString(".1stBranchQuest.type").equalsIgnoreCase("Phase")) {
                                            if (id <= branchQuestSection.getInt(".1stBranchQuest.maxPhaseQuests")) {
                                                if (branchQuestSection.getString(".1stBranchQuest.phaseQuests." + id + ".type").equalsIgnoreCase("Gathering")) {
                                                    ConfigurationSection phaseSection = branchQuestSection.getConfigurationSection(".1stBranchQuest.phaseQuests." + id);
                                                    ConfigurationSection supplySection = phaseSection.getConfigurationSection(".neededSupplies");
                                                    List<ItemStack> supplies = new ArrayList<>();
                                                    supplySection.getKeys(false).forEach(supplyID -> {
                                                        Material material = Material.getMaterial(supplySection.getString(supplyID + ".material"));
                                                        int amount = supplySection.getInt(supplyID + ".amount");
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
                                                            if (phaseSection.getConfigurationSection(".rewards") != null) {
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
                                                playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
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
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }
}
