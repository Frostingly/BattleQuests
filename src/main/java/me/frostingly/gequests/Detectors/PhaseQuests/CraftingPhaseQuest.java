package me.frostingly.gequests.Detectors.PhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.QuestData.CraftingQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingPhaseQuest implements Listener {

    private final GEQuests plugin;

    public CraftingPhaseQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraftE(CraftItemEvent e) {
        Player p = (Player) e.getWhoClicked();
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
                            if (phaseSection.getString(id + ".type").equalsIgnoreCase("Crafting")) {
                                if (playerData.getCraftingQuestData() == null) {
                                    CraftingQuestData newCraftingQuestData = new CraftingQuestData();
                                    Map<Object, Map<ItemStack, Integer>> craftsToMake = new HashMap<>();
                                    newCraftingQuestData.setCraftsToMake(craftsToMake);
                                    playerData.setCraftingQuestData(newCraftingQuestData);
                                }
                                if (playerData.getCraftingQuestData().getCraftsToMake() == null || playerData.getCraftingQuestData().getCraftsToMake().get(id) == null) {
                                    ConfigurationSection craftSection = phaseSection.getConfigurationSection(id + ".neededCrafts");
                                    craftSection.getKeys(false).forEach(craftID -> {
                                        if (craftSection.getString(craftID + ".itemStack") != null) {
                                            //creates an itemstack with meta.
                                            ItemStack itemStack = new ItemStack(Material.valueOf(craftSection.getString(craftID + ".itemStack.material"))); // creates the actual material itemstack
                                            ItemMeta itemMeta = itemStack.getItemMeta();

                                            if (craftSection.getString(craftID + ".displayName") != null)
                                                itemMeta.setDisplayName(craftSection.getString(craftID + ".displayName"));
                                            if (craftSection.getStringList(craftID + ".lore") != null) {
                                                itemMeta.setLore(craftSection.getStringList(craftID + ".lore"));
                                            }
                                            if (craftSection.getStringList(craftID + ".enchants") != null) {
                                                // - "DAMAGE_ALL, 1, false"
                                                craftSection.getStringList(craftID + ".enchants").forEach(enchant -> {
                                                    String[] strings = enchant.split(",");
                                                    itemMeta.addEnchant(Enchantment.getByName(strings[0].trim()), Integer.parseInt(strings[1].trim()), Boolean.getBoolean(strings[2].trim()));
                                                });
                                            }

                                            if (craftSection.getStringList(craftID + ".itemFlags") != null) {
                                                craftSection.getStringList(craftID + ".itemFlags").forEach(itemFlag -> {
                                                    itemMeta.addItemFlags(ItemFlag.valueOf(itemFlag));
                                                });
                                            }
                                            itemStack.setItemMeta(itemMeta);
                                            int amount = craftSection.getInt(craftID + ".amount");
                                            Map<ItemStack, Integer> craftsToMake = new HashMap<>();
                                            craftsToMake.put(itemStack, amount);
                                            playerData.getCraftingQuestData().getCraftsToMake().put(id, craftsToMake);
                                        } else {
                                            ItemStack itemStack = new ItemStack(Material.valueOf(craftSection.getString(craftID + ".material")));
                                            int amount = craftSection.getInt(craftID + ".amount");
                                            Map<ItemStack, Integer> craftsToMake = new HashMap<>();
                                            craftsToMake.put(itemStack, amount);
                                            playerData.getCraftingQuestData().getCraftsToMake().put(id, craftsToMake);
                                        }
                                    });
                                }
                                for (ItemStack key : playerData.getCraftingQuestData().getCraftsToMake().get(id).keySet()) {
                                    if (e.getInventory().getResult() == null) return;
                                    if (e.getInventory().getResult().equals(key)) {
                                        if (!Utilities.isEqualMap(playerData.getCraftingQuestData().getCraftsToMake().get(id), 0)) {
                                            if (playerData.getCraftingQuestData().getCraftsToMake().get(id).get(key) > 0) {
                                                playerData.getCraftingQuestData().getCraftsToMake().get(id).replace(key, (playerData.getCraftingQuestData().getCraftsToMake().get(id).get(key) - 1));
                                            }
                                        }
                                    }
                                }

                                if (Utilities.isEqualMap(playerData.getCraftingQuestData().getCraftsToMake().get(id), 0)) {
                                    if (phaseSection.contains(id + ".rewards")) {
                                        phaseSection.getStringList(id + ".rewards").forEach(reward -> {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                        });
                                    }
                                    playerData.getCraftingQuestData().getCraftsToMake().clear();
                                    playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                    p.sendMessage(Utilities.format(phaseSection.getString(id + ".dialog")));
                                }
                            }
                        } else {
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
