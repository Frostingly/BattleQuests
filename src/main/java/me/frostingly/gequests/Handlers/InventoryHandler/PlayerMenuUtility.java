package me.frostingly.gequests.Handlers.InventoryHandler;

import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private Player owner;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
