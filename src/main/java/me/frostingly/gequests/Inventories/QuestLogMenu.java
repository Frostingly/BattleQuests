package me.frostingly.gequests.Inventories;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Handlers.InventoryHandler.InventoryHandler;
import me.frostingly.gequests.Handlers.InventoryHandler.PlayerMenuUtility;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Utilities;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class QuestLogMenu extends InventoryHandler {

    private final GEQuests plugin;

    public QuestLogMenu(PlayerMenuUtility playerMenuUtility, GEQuests plugin) {
        super(playerMenuUtility);
        this.plugin = plugin;
    }

    @Override
    public String getInventoryName(Player player) {
        PlayerData playerData = plugin.getPlayerData().get(player.getUniqueId());
        return Utilities.format("&b&lQuest Log &r(Page " + playerData.getCurrentMenuPageNum() + ")");
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
                        playerData.setQuestMenuAddition(18);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(18);
                    } else if (playerData.getCurrentMenuPageNum() == 2) {
                        playerData.setQuestMenuAddition(playerData.getQuestMenuAddition() + 18);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(playerData.getQuestMenuAddition() + 18);
                    } else {
                        playerData.setQuestMenuAddition(playerData.getQuestMenuAddition() + 18);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(playerData.getSlotMinus() + 18);
                    }
                    playerData.setCurrentMenuPageNum(playerData.getCurrentMenuPageNum() + 1);
                    Map<Integer, QuestLogMenu> questLogMenuInventories = playerData.getQuestLogMenuInventories();
                    QuestLogMenu questLogMenu = new QuestLogMenu(GEQuests.getInstance().getPlayerData().get(p.getUniqueId()).getPlayerMenuUtility(), plugin);
                    questLogMenuInventories.put(playerData.getCurrentMenuPageNum(), questLogMenu);
                    questLogMenu.open(p);
                }
                break;
            case 22:
                if (e.getCurrentItem().isSimilar(plugin.getFns().createBarrier())) {
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
                break;
            case 18:
                if (e.getCurrentItem().isSimilar(plugin.getFns().createPreviousArrow(p))) {
                    if (playerData.getCurrentMenuPageNum() == 2) {
                        playerData.setQuestMenuAddition(0);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(0);
                    } else {
                        playerData.setQuestMenuAddition(playerData.getQuestMenuAddition() - 18);
                        playerData.setSlotPlus(0);
                        playerData.setSlotMinus(playerData.getSlotMinus() - 18);
                    }
                    playerData.setCurrentMenuPageNum(playerData.getCurrentMenuPageNum() - 1);
                    playerData.getQuestLogMenuInventories().get(playerData.getCurrentMenuPageNum()).open(p);
                }
                break;
        }
    }

    @Override
    public void setMenuItems(Player player) {
        PlayerData playerData = plugin.getPlayerData().get(player.getUniqueId());
        List<ItemStack> items = plugin.getFns().getPrevQuests(player);
        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, plugin.getFns().createPanes());
        }
        for (int i = (0 + (playerData.getQuestMenuAddition())); i < items.size() && i < 18 + (playerData.getQuestMenuAddition()); i++) {
            inventory.setItem((i - (playerData.getQuestMenuAddition())), items.get(i));
        }

        if (items.size() > 18 + (playerData.getQuestMenuAddition())) {
            inventory.setItem(26, plugin.getFns().createNextArrow(player));
        }

        if (playerData.getCurrentMenuPageNum() > 1) {
            inventory.setItem(18, plugin.getFns().createPreviousArrow(player));
        }

        inventory.setItem(22, plugin.getFns().createBarrier());
    }
}
