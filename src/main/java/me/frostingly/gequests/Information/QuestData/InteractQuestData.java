package me.frostingly.gequests.Information.QuestData;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class InteractQuestData {

    private Map<Object, Map<String, Integer>> entitiesToInteractWith = new HashMap<>();

    public Map<Object, Map<String, Integer>> getEntitiesToInteractWith() {
        return entitiesToInteractWith;
    }

    public void setEntitiesToInteractWith(Map<Object, Map<String, Integer>> entitiesToInteractWith) {
        this.entitiesToInteractWith = entitiesToInteractWith;
    }
}
