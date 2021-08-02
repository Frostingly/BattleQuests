package me.frostingly.gequests.Information;

import me.frostingly.gequests.GEQuests;

import java.util.*;

public class QuestPlayerData {

    private final GEQuests plugin;
    
    public QuestPlayerData(GEQuests plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, Boolean> questActivated = new HashMap<>();
    public Map<UUID, String> questNameInfo = new HashMap<>();
    public Map<UUID, String> questNPCNameInfo = new HashMap<>();
    public List<String> quests = new ArrayList<>();

    public Map<UUID, Integer> messageAtByPlayer = new HashMap<>();

    /*
                                PHASE QUEST DATA
     */

    public Map<Integer, UUID> phaseQuestPlayerByID = new HashMap<>();
    public Map<UUID, Integer> phaseQuestIDByPlayer = new HashMap<>();
    public Map<UUID, String> phaseQuestNameByPlayer = new HashMap<>();
    
    public Boolean isQuestActive(UUID p) {
        return questActivated.get(p);
    }

    public String getQuestName(UUID p) {
        return questNameInfo.get(p);
    }

    public String getQuestNPCName(UUID p) {
        return questNPCNameInfo.get(p);
    }

    public void setQuestActive(UUID p, Boolean bool) {
        questActivated.put(p, bool);
    }

    public void setQuestName(UUID p, String s) {
        questNameInfo.put(p, s);
    }

    public void setQuestNPCName(UUID p, String s) {
        questNPCNameInfo.put(p, s);
    }

    public void setMessageAtByPlayer(UUID uuid, Integer integer) {
        messageAtByPlayer.put(uuid, integer);
    }

    public Integer getMessageAtByPlayer(UUID uuid) {
        return messageAtByPlayer.get(uuid);
    }

    /*
                                PHASE QUEST DATA
     */

    public String getPhaseQuestNameByPlayer(UUID uuid) {
        return phaseQuestNameByPlayer.get(uuid);
    }

    public Integer getPhaseQuestIDByPlayer(UUID uuid) {
        return phaseQuestIDByPlayer.get(uuid);
    }

    public void setPhaseQuestPlayerByID(int id, UUID uuid) {
        phaseQuestPlayerByID.put(id, uuid);
    }

    public void setPhaseQuestNameByPlayer(UUID uuid, String name) {
        phaseQuestNameByPlayer.put(uuid, name);
    }

    public void setPhaseQuestIDByPlayer(UUID uuid, int id) {
        phaseQuestIDByPlayer.put(uuid, id);
    }

}
