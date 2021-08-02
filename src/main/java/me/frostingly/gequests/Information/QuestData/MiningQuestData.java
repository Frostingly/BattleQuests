package me.frostingly.gequests.Information.QuestData;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class MiningQuestData {

    private Map<Object, Map<Material, Integer>> blocksToMine = new HashMap<>();

    public Map<Object, Map<Material, Integer>> getBlocksToMine() {
        return blocksToMine;
    }

    public void setBlocksToMine(Map<Object, Map<Material, Integer>> blocksToMine) {
        this.blocksToMine = blocksToMine;
    }
}
