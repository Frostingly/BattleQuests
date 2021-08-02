package me.frostingly.gequests.events;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Handlers.InventoryHandler.InventoryHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

public class InventoryClick implements Listener, Cloneable {

    private final GEQuests plugin;

    public InventoryClick(GEQuests plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, Boolean> canClick = new HashMap<>();

    @EventHandler
    public void InventoryClickE(InventoryClickEvent e) throws CloneNotSupportedException {
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getHolder() == null) return;
        InventoryHolder holder = e.getClickedInventory().getHolder();

        if (holder instanceof InventoryHandler) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;

            InventoryHandler inventoryHandler = (InventoryHandler) holder;
            inventoryHandler.handleMenu(e);
        }
    }
}
