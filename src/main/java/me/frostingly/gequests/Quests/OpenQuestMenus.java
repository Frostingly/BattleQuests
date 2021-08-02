package me.frostingly.gequests.Quests;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Inventories.QuestMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class OpenQuestMenus implements Listener {

    private final GEQuests plugin;

    public OpenQuestMenus(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void RightClick(NPCRightClickEvent e) {
        Player p = e.getClicker();
        if (plugin.getQuestData().isQuestActive(p.getUniqueId()) == null)
            plugin.getQuestData().setQuestActive(p.getUniqueId(), false);
        if (plugin.getPlayerData().get(p.getUniqueId()) == null) {
            PlayerData playerData = new PlayerData(false);
            plugin.getPlayerData().put(p.getUniqueId(), playerData);
        }
        File root = new File(plugin.getDataFolder(), "Quests");
        if (root.listFiles() != null) {
            for (File questGiverNames : root.listFiles()) {
                String newNPCName = e.getNPC().getName();
                if (ChatColor.stripColor(newNPCName).equalsIgnoreCase(questGiverNames.getName())) {
                    PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
                    playerData.setClickedNPCName(e.getNPC().getName());
                    playerData.setCurrentMenuPageNum(1);
                    playerData.setQuestMenuAddition(0);
                    playerData.setSlotPlus(10);
                    playerData.setSlotMinus(0);
                    QuestMenu createMenu = new QuestMenu(playerData.getPlayerMenuUtility(), plugin);
                    playerData.getQuestMenuInventories().clear();
                    Map<Integer, QuestMenu> inventories = new HashMap<>();
                    inventories.put(1, createMenu);
                    playerData.setQuestMenuInventories(inventories);
                    createMenu.open(p);
                }
            }
        } else {
            plugin.getLogger().log(Level.SEVERE, "Could not find any NPCs located in the root folder. [0x20NQFE]");
        }
    }
}
