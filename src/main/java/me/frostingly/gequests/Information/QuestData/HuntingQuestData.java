package me.frostingly.gequests.Information.QuestData;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class HuntingQuestData {

    private Map<Object, Map<EntityType, Integer>> entitiesToKill = new HashMap<>();

    public Map<Object, Map<EntityType, Integer>> getEntitiesToKill() {
        return entitiesToKill;
    }

    public void setEntitiesToKill(Map<Object, Map<EntityType, Integer>> entitiesToKill) {
        this.entitiesToKill = entitiesToKill;
    }
}
