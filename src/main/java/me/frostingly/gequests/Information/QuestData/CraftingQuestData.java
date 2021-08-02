package me.frostingly.gequests.Information.QuestData;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CraftingQuestData {

    private Map<Object, Map<ItemStack, Integer>> craftsToMake = new HashMap<>();

    public Map<Object, Map<ItemStack, Integer>> getCraftsToMake() {
        return craftsToMake;
    }

    public void setCraftsToMake(Map<Object, Map<ItemStack, Integer>> craftsToMake) {
        this.craftsToMake = craftsToMake;
    }
}
