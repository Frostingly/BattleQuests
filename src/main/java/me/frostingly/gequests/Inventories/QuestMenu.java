package me.frostingly.gequests.Inventories;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Handlers.InventoryHandler.InventoryHandler;
import me.frostingly.gequests.Handlers.InventoryHandler.PlayerMenuUtility;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Quests.API.Messages.NoPermission;
import me.frostingly.gequests.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestMenu extends InventoryHandler {

    private final GEQuests plugin;

    public QuestMenu(PlayerMenuUtility playerMenuUtility, GEQuests plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    @Override
    public String getInventoryName(Player player) {
        PlayerData playerData = plugin.getPlayerData().get(player.getUniqueId());
        return Utilities.format(playerData.getClickedNPCName() + " &r(Page " + playerData.getCurrentMenuPageNum() + ")");
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) throws CloneNotSupportedException {
        Player p = (Player) e.getWhoClicked();
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        e.setCancelled(true);
        switch (e.getSlot()) {
            case 26:
                if (e.getCurrentItem().isSimilar(plugin.getFns().createNextArrow(p))) {
                    if (playerData.getCurrentMenuPageNum() == 1) {
                        playerData.setQuestMenuAddition(7);
                        playerData.setSlotPlus(3);
                        playerData.setSlotMinus(0);
                    } else if (playerData.getCurrentMenuPageNum() == 2) {
                        playerData.setQuestMenuAddition(playerData.getQuestMenuAddition() + 7);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(4);
                    } else {
                        playerData.setQuestMenuAddition(playerData.getQuestMenuAddition() + 7);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(playerData.getSlotMinus() + 7);
                    }
                    playerData.setCurrentMenuPageNum(playerData.getCurrentMenuPageNum() + 1);
                    Map<Integer, QuestMenu> questMenuInventories = playerData.getQuestMenuInventories();
                    QuestMenu createMenu = new QuestMenu(GEQuests.getInstance().getPlayerData().get(p.getUniqueId()).getPlayerMenuUtility(), plugin);
                    questMenuInventories.put(playerData.getCurrentMenuPageNum(), createMenu);
                    createMenu.open(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                break;
            case 22:
                if (e.getCurrentItem().isSimilar(plugin.getFns().createBarrier())) {
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                break;
            case 21:
                if (e.getCurrentItem().isSimilar(plugin.getFns().createQuestLog(p))) {
                    if (plugin.getPlayerData().get(p.getUniqueId()).getPrevQuests().size() > 0) {
                        playerData.setCurrentMenuPageNum(1);
                        playerData.setQuestMenuAddition(0);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(0);
                        QuestLogMenu questLogMenu = new QuestLogMenu(playerData.getPlayerMenuUtility(), plugin);
                        playerData.getQuestMenuInventories().clear();
                        playerData.getQuestLogMenuInventories().clear();
                        Map<Integer, QuestLogMenu> questLogMenuInventories = new HashMap<>();
                        questLogMenuInventories.put(1, questLogMenu);
                        playerData.setQuestLogMenuInventories(questLogMenuInventories);
                        questLogMenu.open(p);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    } else {
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
                break;
            case 18:
                if (e.getCurrentItem().isSimilar(plugin.getFns().createPreviousArrow(p))) {
                    if (playerData.getCurrentMenuPageNum() == 2) {
                        playerData.setQuestMenuAddition(0);
                        playerData.setSlotPlus(10);
                        playerData.setSlotMinus(0);
                    } else {
                        playerData.setQuestMenuAddition(playerData.getQuestMenuAddition() - 7);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(playerData.getSlotMinus() - 7);
                    }
                    playerData.setCurrentMenuPageNum(playerData.getCurrentMenuPageNum() - 1);
                    playerData.getQuestMenuInventories().get(playerData.getCurrentMenuPageNum()).open(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                break;
            default:
                if (e.getCurrentItem().getType() == Material.BOOK) {
                    String questName = e.getCurrentItem().getItemMeta().getDisplayName();

                    for (QuestData quest : plugin.getQuests()) {
                        if (quest.getQuestName().equalsIgnoreCase(questName)) {
                            FileConfiguration questConfig = quest.getQuestConfig();
                            if (plugin.getPlayerData().get(p.getUniqueId()) == null) {
                                PlayerData newPlayer = new PlayerData(false);
                                plugin.getPlayerData().put(p.getUniqueId(), newPlayer);
                            }
                            if (!plugin.getPlayerData().get(p.getUniqueId()).isDialogueOpened()) {
                                if (playerData.getQuest() == null || !playerData.getQuest().isQuestActive()) {
                                    if (questConfig.getString("quest.permission") != null) {
                                        if (p.hasPermission(questConfig.getString("quest.permission"))) {
                                            playerData.setQuest(null);
                                            QuestData newQuest = (QuestData) quest.clone();
                                            playerData.setPhaseQuestID(1);
                                            playerData.setQuest(newQuest);
                                            playerData.getQuest().setQuestDialogueMessageAt(1);
                                            playerData.getQuest().setQuestMaterial(Material.valueOf(questConfig.getString("quest.material")));
                                            playerData.getQuest().setSeconds(0);
                                            plugin.sendDialogue.sendDialogue(p);
                                            plugin.getPlayerData().get(p.getUniqueId()).setDialogueOpened(true);
                                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                            p.closeInventory();
                                        } else {
                                            new NoPermission().sendNoPermission(p);
                                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                            p.closeInventory();
                                        }
                                    } else {
                                        playerData.setQuest(null);
                                        QuestData newQuest = (QuestData) quest.clone();
                                        playerData.setPhaseQuestID(1);
                                        playerData.setQuest(newQuest);
                                        playerData.getQuest().setQuestDialogueMessageAt(1);
                                        playerData.getQuest().setQuestMaterial(Material.valueOf(questConfig.getString("quest.material")));
                                        playerData.getQuest().setSeconds(0);
                                        plugin.sendDialogue.sendDialogue(p);
                                        plugin.getPlayerData().get(p.getUniqueId()).setDialogueOpened(true);
                                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                        p.closeInventory();
                                    }
                                } else {
                                    p.sendMessage(Utilities.format(plugin.getConfig().getString("messages.quest_already_started").replace("<quest>", ChatColor.stripColor(playerData.getQuest().getQuestName()))));
                                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                    p.closeInventory();
                                }
                            } else {
                                p.sendMessage(Utilities.format("&cYou have a quest dialogue opened, please cancel the quest dialogue first."));
                                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                                p.closeInventory();
                            }
                        }
                    }

                    plugin.getInventoryClick().canClick.put(p.getUniqueId(), true);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (playerData == null) this.cancel();
                            if (playerData.getQuest() == null) this.cancel();
                            if (playerData.getQuest().getSeconds() == null) this.cancel();
                            if (!playerData.getQuest().isQuestActive()) this.cancel();
                            if (plugin.getInventoryClick().canClick == null) this.cancel();
                            if (playerData.getQuest().getSeconds() < 60 && !playerData.getQuest().isQuestActive() && plugin.getInventoryClick().canClick.get(p.getUniqueId())) {
                                playerData.getQuest().setSeconds(playerData.getQuest().getSeconds() + 1);
                                plugin.getInventoryClick().canClick.put(p.getUniqueId(), true);
                            } else {
                                plugin.getInventoryClick().canClick.put(p.getUniqueId(), false);
                                this.cancel();
                                playerData.getQuest().setQuestDialogueMessageAt(null);
                                plugin.getPlayerData().get(p.getUniqueId()).setDialogueOpened(false);
                                playerData.getQuest().setSeconds(0);
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                }
                break;
        }
    }

    @Override
    public void setMenuItems(Player player) {
        PlayerData playerData = plugin.getPlayerData().get(player.getUniqueId());
        List<ItemStack> quests = plugin.getFns().getQuestsForNPC(ChatColor.stripColor(playerData.getClickedNPCName()), player);
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, plugin.getFns().createPanes());
        }

        for (int i = 10; i < 17; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        for (int i = (0 + (playerData.getQuestMenuAddition())); i < quests.size() && i < 7 + (playerData.getQuestMenuAddition()); i++) {
            inventory.setItem((i + (playerData.getSlotPlus() - playerData.getSlotMinus())), quests.get(i));
        }

        if (quests.size() > 7 + (playerData.getQuestMenuAddition())) {
            inventory.setItem(26, plugin.getFns().createNextArrow(player));
        }

        inventory.setItem(22, plugin.getFns().createBarrier());
        inventory.setItem(21, plugin.getFns().createQuestLog(player));
        if (playerData.getCurrentMenuPageNum() > 1) {
            inventory.setItem(18, plugin.getFns().createPreviousArrow(player));
        }

     }



    /*
    public void createMenu(Player p, String name) {
        Inventory inventory = Bukkit.createInventory(null, 27, Utilities.format(name));

        List<ItemStack> quests = plugin.getFns().getQuestsForNPC(name.replace("§a", ""), p);
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, plugin.getFns().createPanes(1));
        }

        for (int i = 10; i < 17; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

        for (int i = 0; i < quests.size() && i < 7; i++) {
            inventory.setItem((i + 10), quests.get(i));
        }

        if (quests.size() > 7) {
            inventory.setItem(26, plugin.getFns().createArrow());
        }

        inventory.setItem(22, plugin.getFns().createBarrier());
        p.openInventory(inventory);
    }*/

}
