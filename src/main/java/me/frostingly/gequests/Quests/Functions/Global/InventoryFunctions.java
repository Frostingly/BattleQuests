package me.frostingly.gequests.Quests.Functions.Global;

import me.frostingly.gequests.Features.QuestLog;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Utilities;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InventoryFunctions {

    private GEQuests plugin;

    public InventoryFunctions(GEQuests plugin) {
        this.plugin = plugin;
    }

    public List<ItemStack> getQuestsForNPC(String npcName, Player p) {
        List<ItemStack> items = new ArrayList<>();
        if (plugin.getPlayerData().get(p.getUniqueId()) == null) {
            PlayerData playerData = new PlayerData(false);
            plugin.getPlayerData().put(p.getUniqueId(), playerData);
        }
        for (QuestData quest : plugin.getQuests()) {
            if (quest.getQuestNPCName().equalsIgnoreCase(npcName)) {
                PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
                FileConfiguration questConfig = quest.getQuestConfig();
                if (questConfig.getString("quest.material") != null)
                    quest.setQuestMaterial(Material.valueOf(questConfig.getString("quest.material")));
                ItemStack item = new ItemStack(Material.BOOK);
                ItemMeta itemMeta = item.getItemMeta();

                List<String> lore = new ArrayList<>();
                if (questConfig.getString("quest.permission") != null) {
                    if (p.hasPermission(questConfig.getString("quest.permission"))) {
                        lore.addAll(questConfig.getStringList("quest.lore.withPermission"));
                    } else {
                        lore.addAll(questConfig.getStringList("quest.lore.withoutPermission"));
                    }
                } else {
                    lore.addAll(questConfig.getStringList("quest.lore.defaultLore"));
                }
                itemMeta.setDisplayName(Utilities.format(questConfig.getString("quest.name")));
                itemMeta.setLore(Utilities.formatList(lore));
                item.setItemMeta(itemMeta);
                items.add(item);
            }
        }
        return items;
    }

    /**
     *
     * Get the completed quests until <code>lastQuestIndex</code> in a reversed order
     *
     * @param lastQuestIndex index of the last quest to return
     * @param p the player who's completed quests to search for
     * @return list of completed quests as ItemStack in a reversed order
     */
    public List<ItemStack> getPrevQuestsIndex(int lastQuestIndex, Player p) {
        List<ItemStack> items = new QuestLog(plugin).getAllPreviousQuests(p);
        items = items.subList(0, (items.size() >= lastQuestIndex ? lastQuestIndex : items.size())); // get part of the list making sure no IndexOutOfBoundException occurs
        Collections.sort(items, Collections.reverseOrder(Comparator.comparing(o -> Integer.parseInt(o.getItemMeta().getDisplayName().split("#")[1])))); // Sort items list for item displayname
        return items;
    }

    public List<ItemStack> getPrevQuests(Player p) {
        List<ItemStack> items = new QuestLog(plugin).getAllPreviousQuests(p);
        Collections.sort(items, Collections.reverseOrder(Comparator.comparing(o -> Integer.parseInt(o.getItemMeta().getDisplayName().split("#")[1])))); // Sort items list for item displayname
        return items;
    }

    public ItemStack createPanes() {
        ItemStack paneItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneItemMeta = paneItem.getItemMeta();
        paneItemMeta.setDisplayName(" ");

        paneItem.setItemMeta(paneItemMeta);
        return paneItem;
    }

    public ItemStack createPreviousArrow(Player player) {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(Utilities.format("&8Page " + (plugin.getPlayerData().get(player.getUniqueId()).getCurrentMenuPageNum() - 1)));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack createNextArrow(Player player) {
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(Utilities.format("&8Page " + (plugin.getPlayerData().get(player.getUniqueId()).getCurrentMenuPageNum() + 1)));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack createBarrier() {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(Utilities.format("&c&lCLOSE"));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack createQuestLog(Player player) {
        PlayerData playerData = plugin.getPlayerData().get(player.getUniqueId());
        if (playerData == null) plugin.getPlayerData().put(player.getUniqueId(), new PlayerData(false));
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(Utilities.format("&b&lQuest Log"));
        if (playerData.getPrevQuests().size() == 0) {
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add(Utilities.format("&cYou haven't completed a quest yet!"));
            itemMeta.setLore(lore);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
