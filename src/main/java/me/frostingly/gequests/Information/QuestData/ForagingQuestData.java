package me.frostingly.gequests.Information.QuestData;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ForagingQuestData {

    private Map<Object, Map<Material, Integer>> logsToMine = new HashMap<>();

    public Map<Object, Map<Material, Integer>> getLogsToMine() {
        return logsToMine;
    }

    public void setLogsToMine(Map<Object, Map<Material, Integer>> logsToMine) {
        this.logsToMine = logsToMine;
    }
}
