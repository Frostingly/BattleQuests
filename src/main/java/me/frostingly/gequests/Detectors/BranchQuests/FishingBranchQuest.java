package me.frostingly.gequests.Detectors.BranchQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.FishingQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FishingBranchQuest implements Listener {

    private final GEQuests plugin;

    public FishingBranchQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFishE(PlayerFishEvent e) {
        Player p = (Player) e.getPlayer();
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (playerData.getQuest().isQuestActive()) {
                    FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                    ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                    if (questConfig.contains("quest.branchQuests")) {
                        branchQuestSection.getKeys(false).forEach(branchID -> {
                            if (branchQuestSection.getString(branchID + ".type").equalsIgnoreCase("Fishing")) {
                                if (playerData.getFishingQuestData() == null) {
                                    FishingQuestData newFishingQuestData = new FishingQuestData();
                                    Map<Object, Map<ItemStack, Integer>> entitiesToCatch = new HashMap<>();
                                    newFishingQuestData.setEntitiesToCatch(entitiesToCatch);
                                    playerData.setFishingQuestData(newFishingQuestData);
                                }
                                if (e.getState() == PlayerFishEvent.State.CAUGHT_ENTITY || e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
                                    if (!(e.getCaught() instanceof Item)) return;
                                    Item item = (Item) e.getCaught();
                                    if (playerData.getFishingQuestData().getEntitiesToCatch() == null || playerData.getFishingQuestData().getEntitiesToCatch().get(branchID) == null) {
                                        Map<ItemStack, Integer> entitiesToCatch = new HashMap<>();
                                        ConfigurationSection entitiesSection = branchQuestSection.getConfigurationSection(branchID + ".neededEntities");
                                        entitiesSection.getKeys(false).forEach(entityID -> {
                                            if (entitiesSection.getString(entityID + ".itemStack") != null) {
                                                ItemStack itemStack = new ItemStack(Material.valueOf(entitiesSection.getString(entityID + ".itemStack.material"))); // creates the actual material itemstack
                                                ItemMeta itemMeta = itemStack.getItemMeta();

                                                if (entitiesSection.getString(entityID + ".displayName") != null)
                                                    itemMeta.setDisplayName(entitiesSection.getString(entityID + ".displayName"));
                                                if (entitiesSection.getStringList(entityID + ".lore") != null) {
                                                    itemMeta.setLore(entitiesSection.getStringList(entityID + ".lore"));
                                                }
                                                if (entitiesSection.getStringList(entityID + ".enchants") != null) {
                                                    // - "DAMAGE_ALL, branchID, false"
                                                    entitiesSection.getStringList(entityID + ".enchants").forEach(enchant -> {
                                                        String[] strings = enchant.split(",");
                                                        itemMeta.addEnchant(Enchantment.getByName(strings[0].trim()), Integer.parseInt(strings[1].trim()), Boolean.getBoolean(strings[2].trim()));
                                                    });
                                                }

                                                if (entitiesSection.getStringList(entityID + ".itemFlags") != null) {
                                                    entitiesSection.getStringList(entityID + ".itemFlags").forEach(itemFlag -> {
                                                        itemMeta.addItemFlags(ItemFlag.valueOf(itemFlag));
                                                    });
                                                }
                                                itemStack.setItemMeta(itemMeta);
                                                int amount = entitiesSection.getInt(entityID + ".amount");
                                                entitiesToCatch.put(itemStack, amount);
                                                playerData.getFishingQuestData().getEntitiesToCatch().put(branchID, entitiesToCatch);
                                            } else {
                                                ItemStack itemStack = new ItemStack(Material.valueOf(entitiesSection.getString(entityID + ".material")));
                                                int amount = entitiesSection.getInt(entityID + ".amount");
                                                entitiesToCatch.put(itemStack, amount);
                                            }
                                        });
                                        playerData.getFishingQuestData().getEntitiesToCatch().put(branchID, entitiesToCatch);
                                        for (ItemStack key : playerData.getFishingQuestData().getEntitiesToCatch().get(branchID).keySet()) {
                                            if (item.getItemStack().equals(key)) {
                                                if (!Utilities.isEqualMap(playerData.getFishingQuestData().getEntitiesToCatch().get(branchID), 0)) {
                                                    if (playerData.getFishingQuestData().getEntitiesToCatch().get(branchID).get(key) > 0) {
                                                        playerData.getFishingQuestData().getEntitiesToCatch().get(branchID).replace(key, (playerData.getFishingQuestData().getEntitiesToCatch().get(branchID).get(key) - 1));
                                                    }
                                                }
                                            }
                                        }
                                        if (Utilities.isEqualMap(playerData.getFishingQuestData().getEntitiesToCatch().get(branchID), 0)) {
                                            if (branchQuestSection.contains(branchID + ".rewards")) {
                                                branchQuestSection.getStringList(branchID + ".rewards").forEach(reward -> {
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                                });
                                            }
                                            playerData.getFishingQuestData().getEntitiesToCatch().clear();
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
                        });
                    }
                }
            }
        }
    }

}
