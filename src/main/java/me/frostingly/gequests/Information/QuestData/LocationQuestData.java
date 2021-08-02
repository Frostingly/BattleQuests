package me.frostingly.gequests.Information.QuestData;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class LocationQuestData {

    private Map<Object, Map<Location, Integer>> locationsToGo = new HashMap<>();
    private Map<Object, Map<String, Integer>> regionsToGo = new HashMap<>();

    public Map<Object, Map<Location, Integer>> getLocationsToGo() {
        return locationsToGo;
    }

    public void setLocationsToGo(Map<Object, Map<Location, Integer>> locationsToGo) {
        this.locationsToGo = locationsToGo;
    }

    public Map<Object, Map<String, Integer>> getRegionsToGo() {
        return regionsToGo;
    }

    public void setRegionsToGo(Map<Object, Map<String, Integer>> regionsToGo) {
        this.regionsToGo = regionsToGo;
    }
}
