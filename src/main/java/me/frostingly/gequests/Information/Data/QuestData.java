package me.frostingly.gequests.Information.Data;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Date;
import java.util.List;

public final class QuestData implements Cloneable{

    private String questNPCName, questName, questType, questObjective;
    private Integer questDialogueMessagesMax, questDialogueMessageAt, seconds;
    private Boolean questActive;
    private Material questMaterial;
    private List<String> questLoreWP; // with
    private List<String> questLoreWOUTP; // without
    private Date questCompletedDate;
    private FileConfiguration questConfig;

    public QuestData(String questNPCName, String questName, FileConfiguration questConfig) {
        this.questNPCName = questNPCName;
        this.questName = questName;
        this.questConfig = questConfig;
    }

    public String getQuestNPCName() {
        return questNPCName;
    }

    public void setQuestNPCName(String questNPCName) {
        this.questNPCName = questNPCName;
    }

    public String getQuestName() {
        return questName;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public String getQuestObjective() {
        return questObjective;
    }

    public void setQuestObjective(String questObjective) {
        this.questObjective = questObjective;
    }

    public List<String> getQuestLoreWP() {
        return questLoreWP;
    }

    public void setQuestLoreWP(List<String> questLoreWP) {
        this.questLoreWP = questLoreWP;
    }

    public List<String> getQuestLoreWOUTP() {
        return questLoreWOUTP;
    }

    public void setQuestLoreWOUTP(List<String> questLoreWOUTP) {
        this.questLoreWOUTP = questLoreWOUTP;
    }

    public Integer getQuestDialogueMessagesMax() {
        return questDialogueMessagesMax;
    }

    public void setQuestDialogueMessagesMax(Integer questDialogueMessagesMax) {
        this.questDialogueMessagesMax = questDialogueMessagesMax;
    }

    public Integer getQuestDialogueMessageAt() {
        return questDialogueMessageAt;
    }

    public void setQuestDialogueMessageAt(Integer questDialogueMessageAt) {
        this.questDialogueMessageAt = questDialogueMessageAt;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Boolean isQuestActive() {
        return questActive;
    }

    public void setQuestActive(Boolean questActive) {
        this.questActive = questActive;
    }

    public Material getQuestMaterial() {
        return questMaterial;
    }

    public void setQuestMaterial(Material questMaterial) {
        this.questMaterial = questMaterial;
    }

    public Date getQuestCompletedDate() {
        return questCompletedDate;
    }

    public void setQuestCompletedDate(Date questCompletedDate) {
        this.questCompletedDate = questCompletedDate;
    }

    public FileConfiguration getQuestConfig() {
        return questConfig;
    }

    public void setQuestConfig(FileConfiguration questConfig) {
        this.questConfig = questConfig;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
        QuestData quest = (QuestData) obj;

        return (quest.questNPCName.equals(this.questNPCName) && quest.questName.equals(this.questName));
    }
}
