package me.frostingly.gequests.Information.QuestData;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class FishingQuestData {

    private Map<Object, Map<ItemStack, Integer>> entitiesToCatch = new HashMap<>();

    public Map<Object, Map<ItemStack, Integer>> getEntitiesToCatch() {
        return entitiesToCatch;
    }

    public void setEntitiesToCatch(Map<Object, Map<ItemStack, Integer>> entitiesToCatch) {
        this.entitiesToCatch = entitiesToCatch;
    }
}
