package me.frostingly.gequests.commands;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Handlers.InventoryHandler.PlayerMenuUtility;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Inventories.QuestMenu;
import me.frostingly.gequests.Quests.API.Messages.NoQuestActive;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GEQuestsCMD implements CommandExecutor  {

    private final GEQuests plugin;

    public GEQuestsCMD(GEQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("gequests")) {
            Player p = (Player) sender;
            PlayerData playerData;
            playerData = plugin.getPlayerData().get(p.getUniqueId());
            if (playerData == null) {
                playerData = new PlayerData(false);
                plugin.getPlayerData().put(p.getUniqueId(), playerData);
            }
            switch (args[0]) {
                case "testbranch":
                    System.out.println("test");
                    Map<Object, List<ItemStack>> supplies = new HashMap<>();
                    ConfigurationSection sectionLol = plugin.getConfig().getConfigurationSection("sectionLol");
                    sectionLol.getKeys(false).forEach(id -> {
                        List<ItemStack> suppliesList = new ArrayList<>();
                        ConfigurationSection supplySection = sectionLol.getConfigurationSection(id + ".neededSupplies");
                        supplySection.getKeys(false).forEach(supplyID -> {
                            Material material = Material.valueOf(supplySection.getString(supplyID + ".material"));
                            int amount = supplySection.getInt(supplyID + ".amount");
                            ItemStack itemStack = new ItemStack(material, amount);
                            suppliesList.add(itemStack);
                        });
                        supplies.put(id, suppliesList);
                    });
                    break;
                case "reload":
                    if (p.hasPermission(plugin.getConfig().getString("commands.reloadCMD.permission"))) {
                        plugin.reloadConfig();
                        p.sendMessage(Utilities.format("&aSuccessfully reloaded the config file."));
                    } else {
                        p.sendMessage(Utilities.format(plugin.getConfig().getString("commands.reloadCMD.no_permission")));
                    }
                    break;
                case "info":
                    if (playerData.getQuest() != null || playerData.getQuest().isQuestActive()) {
                        String questNPCName = playerData.getQuest().getQuestNPCName();
                        String questName = playerData.getQuest().getQuestName();
                        String questType = playerData.getQuest().getQuestType();
                        String questObjective = playerData.getQuest().getQuestObjective();
                        Boolean questActive = playerData.getQuest().isQuestActive();
                        p.sendMessage(Utilities.format("&c&l    ---- Quest Information ----    "));
                        p.sendMessage(Utilities.format("&7Quest NPC name: &f" + questNPCName));
                        p.sendMessage(Utilities.format("&7Quest name: &f" + questName));
                        p.sendMessage(Utilities.format("&7Quest type: &f" + questType));
                        p.sendMessage(Utilities.format("&7Quest objective: &f" + questObjective));
                        p.sendMessage(Utilities.format("&7Quest active: &f" + questActive));
                    } else {
                        new NoQuestActive().sendNoQuestActive(p);
                    }
                    break;
                case "test":
                    QuestData quest = new QuestData("Tester", "Testing", null);
                    quest.setQuestType("boom!");
                    quest.setQuestActive(false);
                    quest.setQuestObjective("Test, a lot.");
                    quest.setQuestMaterial(Material.GOLD_BLOCK);
                    if (playerData.getPrevQuests().size() == 0) {
                        List<QuestData> prevQuests = new ArrayList<>();
                        prevQuests.add(quest);
                        plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                    } else {
                        List<QuestData> prevQuests = plugin.getPlayerData().get(p.getUniqueId()).getPrevQuests();
                        prevQuests.add(quest);
                        plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                    }
                    p.sendMessage("added");
                    break;
            }
        }
        return false;
    }
}
