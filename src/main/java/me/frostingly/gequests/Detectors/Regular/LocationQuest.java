package me.frostingly.gequests.Detectors.Regular;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.LocationQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class LocationQuest implements Listener {

    private final GEQuests plugin;

    public LocationQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWalkE(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (playerData.getQuest().isQuestActive()) {
                    FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                    if (playerData.getQuest().getQuestType().equalsIgnoreCase("Location")) {
                        Location fromLocation = e.getFrom();
                        Location toLocation = e.getTo();

                        double fromXValue = fromLocation.getX();
                        double fromYValue = fromLocation.getY();
                        double fromZValue = fromLocation.getZ();

                        double toXValue = toLocation.getX();
                        double toYValue = toLocation.getY();
                        double toZValue = toLocation.getZ();
                        if ((int) fromXValue != (int) toXValue
                                || (int) fromYValue != (int) toYValue
                                || (int) fromZValue != (int) toZValue) {
                            if (questConfig.getString("quest.region") == null) {
                                if (playerData.getLocationQuestData() == null) {
                                    LocationQuestData newLocationQuestData = new LocationQuestData();
                                    Map<Object, Map<Location, Integer>> locationsToGo = new HashMap<>();
                                    newLocationQuestData.setLocationsToGo(locationsToGo);
                                    playerData.setLocationQuestData(newLocationQuestData);
                                }
                                double playerX = p.getLocation().getX();
                                double playerY = p.getLocation().getY();
                                double playerZ = p.getLocation().getZ();
                                if (playerData.getLocationQuestData().getLocationsToGo() == null || playerData.getLocationQuestData().getLocationsToGo().get(1) == null) {
                                    ConfigurationSection locationSection = questConfig.getConfigurationSection("quest.neededLocations");
                                    locationSection.getKeys(false).forEach(locationID -> {
                                        String defaultString = locationSection.getString(locationID + ".location");
                                        String[] strings = defaultString.split(",");
                                        double x = Double.parseDouble(strings[1].trim());
                                        double y = Double.parseDouble(strings[2].trim());
                                        double z = Double.parseDouble(strings[3].trim());
                                        String world = strings[0].trim();
                                        Location newLocation = new Location(Bukkit.getWorld(world), x, y, z);
                                        int amount = locationSection.getInt(locationID + ".amount");
                                        Map<Location, Integer> locationsToGo = new HashMap<>();
                                        locationsToGo.put(newLocation, amount);
                                        playerData.getLocationQuestData().getLocationsToGo().put(1, locationsToGo);
                                    });
                                }
                                for (Location key : playerData.getLocationQuestData().getLocationsToGo().get(1).keySet()) {
                                    if ((int) playerX == key.getX() && (int) playerY == key.getY() && (int) playerZ == key.getZ() && p.getLocation().getWorld().getName().equalsIgnoreCase(key.getWorld().getName())) {
                                        if (!Utilities.isEqualMap(playerData.getLocationQuestData().getLocationsToGo(), 0)) {
                                            if (playerData.getLocationQuestData().getLocationsToGo().get(1).get(key) > 0) {
                                                playerData.getLocationQuestData().getLocationsToGo().get(1).replace(key, (playerData.getLocationQuestData().getLocationsToGo().get(1).get(key) - 1));
                                            }
                                        }
                                    }
                                }
                                if (Utilities.isEqualMap(playerData.getLocationQuestData().getLocationsToGo(), 0)) {
                                    if (questConfig.contains("quest.rewards")) {
                                        questConfig.getStringList("quest.rewards").forEach(reward -> {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                        });
                                    }
                                    playerData.getLocationQuestData().getLocationsToGo().clear();
                                    Date date = new Date();
                                    playerData.getQuest().setQuestCompletedDate(date);
                                    if (playerData.getPrevQuests().size() == 0) {
                                        List<QuestData> prevQuests = new ArrayList<>();
                                        prevQuests.add(playerData.getQuest());
                                        plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                                    } else {
                                        List<QuestData> prevQuests = plugin.getPlayerData().get(p.getUniqueId()).getPrevQuests();
                                        prevQuests.add(playerData.getQuest());
                                        plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                                    }
                                    new QuestFinished().sendQuestFinished(p);
                                    playerData.setQuest(null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRegionEnterE(RegionEnteredEvent e) {
        Player p = e.getPlayer();
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (playerData.getQuest().isQuestActive()) {
                    FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                    if (playerData.getQuest().getQuestType().equalsIgnoreCase("Location")) {
                        if (questConfig.getString("quest.region") != null) {
                            if (playerData.getLocationQuestData() == null) {
                                LocationQuestData newLocationQuestData = new LocationQuestData();
                                Map<Object, Map<String, Integer>> regionsToGo = new HashMap<>();
                                newLocationQuestData.setRegionsToGo(regionsToGo);
                                playerData.setLocationQuestData(newLocationQuestData);
                            }
                            if (playerData.getLocationQuestData().getRegionsToGo() == null || playerData.getLocationQuestData().getRegionsToGo().get(1) == null) {
                                ConfigurationSection regionSection = questConfig.getConfigurationSection("quest.neededRegions");
                                regionSection.getKeys(false).forEach(regionID -> {
                                    String regionName = regionSection.getString(regionID + ".region");
                                    int amount = regionSection.getInt(regionID + ".amount");
                                    Map<String, Integer> regionsToGo = new HashMap<>();
                                    regionsToGo.put(regionName, amount);
                                    playerData.getLocationQuestData().getRegionsToGo().put(1, regionsToGo);
                                });
                            }
                            for (String key : playerData.getLocationQuestData().getRegionsToGo().get(1).keySet()) {
                                if (e.getRegionName().equalsIgnoreCase(key)) {
                                    if (!Utilities.isEqualMap(playerData.getLocationQuestData().getRegionsToGo(), 0)) {
                                        if (playerData.getLocationQuestData().getRegionsToGo().get(1).get(key) > 0) {
                                            playerData.getLocationQuestData().getRegionsToGo().get(1).replace(key, (playerData.getLocationQuestData().getRegionsToGo().get(1).get(key) - 1));
                                        }
                                    }
                                }
                            }
                            if (Utilities.isEqualMap(playerData.getLocationQuestData().getRegionsToGo().get(1), 0)) {
                                if (questConfig.contains("quest.rewards")) {
                                    questConfig.getStringList("quest.rewards").forEach(reward -> {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                    });
                                }
                                playerData.getLocationQuestData().getRegionsToGo().clear();
                                Date date = new Date();
                                playerData.getQuest().setQuestCompletedDate(date);
                                if (playerData.getPrevQuests().size() == 0) {
                                    List<QuestData> prevQuests = new ArrayList<>();
                                    prevQuests.add(playerData.getQuest());
                                    plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                                } else {
                                    List<QuestData> prevQuests = plugin.getPlayerData().get(p.getUniqueId()).getPrevQuests();
                                    prevQuests.add(playerData.getQuest());
                                    plugin.getPlayerData().get(p.getUniqueId()).setPrevQuests(prevQuests);
                                }
                                new QuestFinished().sendQuestFinished(p);
                                playerData.setQuest(null);
                            }
                        }
                    }
                }
            }
        }
    }
}
