package me.frostingly.gequests;

import me.frostingly.gequests.Detectors.BranchQuests.*;
import me.frostingly.gequests.Detectors.PhaseQuests.*;
import me.frostingly.gequests.Detectors.Regular.*;
import me.frostingly.gequests.Features.QuestLog;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestPlayerData;
import me.frostingly.gequests.Features.Dialogue;
import me.frostingly.gequests.Quests.Functions.Global.InventoryFunctions;
import me.frostingly.gequests.Quests.Functions.Load.RegisterNPCsAndQuests;
import me.frostingly.gequests.Quests.OpenQuestMenus;
import me.frostingly.gequests.commands.QuestCommands.AbandonQuestCMD;
import me.frostingly.gequests.commands.QuestCommands.CancelQuestCMD;
import me.frostingly.gequests.commands.GEQuestsCMD;
import me.frostingly.gequests.commands.QuestCommands.StartQuestCMD;
import me.frostingly.gequests.events.InventoryClick;
import me.frostingly.gequests.events.Join;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class GEQuests extends JavaPlugin{

    private InventoryClick inventoryClick;

    private QuestPlayerData questData;
    private InventoryFunctions fns;

    //API

    public Dialogue sendDialogue;

    public QuestLog questLog;

    private static GEQuests GEQuests;

    public static GEQuests getInstance() {
        return GEQuests;
    }

    public Map<String, Integer> questDialogMessagesNPC = new HashMap<>();

    private List<QuestData> quests = new ArrayList<>();
    private Map<UUID, PlayerData> playerData = new HashMap<>();
    private Map<UUID, List<QuestData>> playerPrevQuests = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        inventoryClick = new InventoryClick(this);

        questData = new QuestPlayerData(this);
        fns = new InventoryFunctions(this);

        sendDialogue = new Dialogue(this);

        GEQuests = this;
        loadEvents(Bukkit.getServer().getPluginManager());
        loadCMDs();
        this.saveDefaultConfig();

        loadFinishing();
        new RegisterNPCsAndQuests(this).registerNPCsAndQuests();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadFinishing() {
        new BukkitRunnable() {
            @Override
            public void run() {
                new GatheringQuest(getInstance()).finishQuest();
                new GatheringBranchQuest(getInstance()).finishQuest();
                new GatheringPhaseQuest(getInstance()).finishQuest();
                //new GatheringBranchPhaseQuest(getInstance()).finishQuest();
            }
        }.runTaskTimer(this, 0L, 40L);
    }

    private void loadEvents(PluginManager pm) {
        pm.registerEvents(new InventoryClick(this), this);

        pm.registerEvents(new Join(this), this);

        pm.registerEvents(new OpenQuestMenus(this), this);

        pm.registerEvents(new LocationQuest(this), this);
        pm.registerEvents(new LocationPhaseQuest(this), this);
        pm.registerEvents(new LocationBranchQuest(this), this);

        pm.registerEvents(new MiningQuest(this), this);
        pm.registerEvents(new MiningPhaseQuest(this), this);
        pm.registerEvents(new MiningBranchQuest(this), this);

        pm.registerEvents(new HuntingQuest(this), this);
        pm.registerEvents(new HuntingPhaseQuest(this), this);
        pm.registerEvents(new HuntingBranchQuest(this), this);

        pm.registerEvents(new CraftingQuest(this), this);
        pm.registerEvents(new CraftingPhaseQuest(this), this);
        pm.registerEvents(new CraftingBranchQuest(this), this);

        pm.registerEvents(new FishingQuest(this), this);
        pm.registerEvents(new FishingPhaseQuest(this), this);
        pm.registerEvents(new FishingBranchQuest(this), this);

        pm.registerEvents(new ForagingQuest(this), this);
        pm.registerEvents(new ForagingPhaseQuest(this), this);
        pm.registerEvents(new ForagingBranchQuest(this), this);

        pm.registerEvents(new InteractEntityQuest(this), this);
        pm.registerEvents(new InteractEntityPhaseQuest(this), this);
        pm.registerEvents(new InteractEntityBranchQuest(this), this);

        //pm.registerEvents(new TestEvent(), this);
    }

    private void loadCMDs() {
        getCommand("gequests").setExecutor(new GEQuestsCMD(this));
        getCommand("startquest").setExecutor(new StartQuestCMD(this));
        getCommand("cancelquest").setExecutor(new CancelQuestCMD(this));
        getCommand("abandonquest").setExecutor(new AbandonQuestCMD(this));
    }

    public InventoryClick getInventoryClick() {
        return inventoryClick;
    }

    public QuestPlayerData getQuestData() {
        return questData;
    }

    public InventoryFunctions getFns() {
        return fns;
    }

    public Dialogue getSendDialogue() {
        return sendDialogue;
    }


    public List<QuestData> getQuests() {
        return quests;
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return playerData;
    }

    public QuestLog getQuestLog() {
        return questLog;
    }

    public Map<UUID, List<QuestData>> getPlayerPrevQuests() {
        return playerPrevQuests;
    }
}
