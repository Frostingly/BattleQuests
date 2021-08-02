package me.frostingly.gequests.Handlers.InventoryHandler;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryHandler implements InventoryHolder {

    protected Inventory inventory;

    protected PlayerMenuUtility playerMenuUtility;

    public InventoryHandler(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getInventoryName(Player player);

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e) throws CloneNotSupportedException;

    public abstract void setMenuItems(Player player);

    public void open(Player player) {
        PlayerData playerData = new PlayerData(false);
        PlayerMenuUtility playerMenuUtility = new PlayerMenuUtility(player);
        if (GEQuests.getInstance().getPlayerData().get(player.getUniqueId()) == null) {
            playerData.setPlayerMenuUtility(playerMenuUtility);
            GEQuests.getInstance().getPlayerData().put(player.getUniqueId(), playerData);
        }
        if (GEQuests.getInstance().getPlayerData().get(player.getUniqueId()).getPlayerMenuUtility() == null)
            GEQuests.getInstance().getPlayerData().get(player.getUniqueId()).setPlayerMenuUtility(playerMenuUtility);
        inventory = Bukkit.createInventory(this, getSlots(), getInventoryName(player));
        this.setMenuItems(player);
        GEQuests.getInstance().getPlayerData().get(player.getUniqueId()).getPlayerMenuUtility().getOwner().openInventory(inventory);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
