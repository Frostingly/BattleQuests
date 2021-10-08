package me.frostingly.gequests.Information.Data;

import me.frostingly.gequests.Handlers.InventoryHandler.PlayerMenuUtility;
import me.frostingly.gequests.Information.QuestData.*;
import me.frostingly.gequests.Inventories.QuestLogMenu;
import me.frostingly.gequests.Inventories.QuestMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {

    private boolean dialogueOpened;
    private Integer questCount;
    private QuestData quest;
    private List<QuestData> prevQuests = new ArrayList<>();

    private PlayerMenuUtility playerMenuUtility;

    private String clickedNPCName;
    private Integer currentMenuPageNum;

    private Integer questMenuAddition;

    private Integer slotPlus;
    private Integer slotMinus;

    private Map<Integer, QuestMenu> questMenuInventories = new HashMap<>();
    private Map<Integer, QuestLogMenu> questLogMenuInventories = new HashMap<>();

    private boolean isExecuted = false;

    //Quest Data below

    private MiningQuestData miningQuestData;
    private HuntingQuestData huntingQuestData;
    private FishingQuestData fishingQuestData;
    private ForagingQuestData foragingQuestData;
    private CraftingQuestData craftingQuestData;
    private LocationQuestData locationQuestData;
    private InteractQuestData interactQuestData;

    private List<EntityData> entities = new ArrayList<>();

    //Phase data below

    private Integer phaseQuestID;

    public PlayerData(boolean dialogueOpened) {
        this.dialogueOpened = dialogueOpened;
    }

    public boolean isDialogueOpened() {
        return dialogueOpened;
    }

    public void setDialogueOpened(boolean dialogueOpened) {
        this.dialogueOpened = dialogueOpened;
    }

    public Integer getQuestCount() {
        return questCount;
    }

    public void setQuestCount(Integer questCount) {
        this.questCount = questCount;
    }

    public QuestData getQuest() {
        return quest;
    }

    public void setQuest(QuestData quest) {
        this.quest = quest;
    }

    public List<QuestData> getPrevQuests() {
        return prevQuests;
    }

    public void setPrevQuests(List<QuestData> prevQuests) {
        this.prevQuests = prevQuests;
    }

    public PlayerMenuUtility getPlayerMenuUtility() {
        return playerMenuUtility;
    }

    public void setPlayerMenuUtility(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
    }

    public String getClickedNPCName() {
        return clickedNPCName;
    }

    public void setClickedNPCName(String clickedNPCName) {
        this.clickedNPCName = clickedNPCName;
    }

    public Integer getCurrentMenuPageNum() {
        return currentMenuPageNum;
    }

    public void setCurrentMenuPageNum(Integer currentMenuPageNum) {
        this.currentMenuPageNum = currentMenuPageNum;
    }

    public Integer getQuestMenuAddition() {
        return questMenuAddition;
    }

    public void setQuestMenuAddition(Integer questMenuAddition) {
        this.questMenuAddition = questMenuAddition;
    }

    public Integer getSlotPlus() {
        return slotPlus;
    }

    public void setSlotPlus(Integer slotPlus) {
        this.slotPlus = slotPlus;
    }

    public Integer getSlotMinus() {
        return slotMinus;
    }

    public void setSlotMinus(Integer slotMinus) {
        this.slotMinus = slotMinus;
    }

    public Map<Integer, QuestMenu> getQuestMenuInventories() {
        return questMenuInventories;
    }

    public void setQuestMenuInventories(Map<Integer, QuestMenu> questMenuInventories) {
        this.questMenuInventories = questMenuInventories;
    }

    public Map<Integer, QuestLogMenu> getQuestLogMenuInventories() {
        return questLogMenuInventories;
    }

    public void setQuestLogMenuInventories(Map<Integer, QuestLogMenu> questLogMenuInventories) {
        this.questLogMenuInventories = questLogMenuInventories;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

    public MiningQuestData getMiningQuestData() {
        return miningQuestData;
    }

    public HuntingQuestData getHuntingQuestData() {
        return huntingQuestData;
    }

    public void setHuntingQuestData(HuntingQuestData huntingQuestData) {
        this.huntingQuestData = huntingQuestData;
    }

    public void setMiningQuestData(MiningQuestData miningQuestData) {
        this.miningQuestData = miningQuestData;
    }

    public FishingQuestData getFishingQuestData() {
        return fishingQuestData;
    }

    public void setFishingQuestData(FishingQuestData fishingQuestData) {
        this.fishingQuestData = fishingQuestData;
    }

    public ForagingQuestData getForagingQuestData() {
        return foragingQuestData;
    }

    public void setForagingQuestData(ForagingQuestData foragingQuestData) {
        this.foragingQuestData = foragingQuestData;
    }

    public CraftingQuestData getCraftingQuestData() {
        return craftingQuestData;
    }

    public void setCraftingQuestData(CraftingQuestData craftingQuestData) {
        this.craftingQuestData = craftingQuestData;
    }

    public LocationQuestData getLocationQuestData() {
        return locationQuestData;
    }

    public void setLocationQuestData(LocationQuestData locationQuestData) {
        this.locationQuestData = locationQuestData;
    }

    public InteractQuestData getInteractQuestData() {
        return interactQuestData;
    }

    public void setInteractQuestData(InteractQuestData interactQuestData) {
        this.interactQuestData = interactQuestData;
    }

    public List<EntityData> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityData> entities) {
        this.entities = entities;
    }

    public Integer getPhaseQuestID() {
        return phaseQuestID;
    }

    public void setPhaseQuestID(Integer phaseQuestID) {
        this.phaseQuestID = phaseQuestID;
    }
}
