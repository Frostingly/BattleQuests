package me.frostingly.gequests.Detectors.BranchQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.EntityData;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import me.frostingly.gequests.Information.QuestData.InteractQuestData;
import me.frostingly.gequests.Information.QuestData.LocationQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class LocationBranchQuest implements Listener {

    private final GEQuests plugin;

    public LocationBranchQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMoveE(PlayerMoveEvent e) {
        Player p = (Player) e.getPlayer();
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (playerData.getQuest().isQuestActive()) {
                    FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                    ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                    if (questConfig.contains("quest.branchQuests")) {
                        branchQuestSection.getKeys(false).forEach(branchID -> {
                            if (branchQuestSection.getString(branchID + ".type").equalsIgnoreCase("Location")) {
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
                                        if (playerData.getLocationQuestData().getLocationsToGo() == null || playerData.getLocationQuestData().getLocationsToGo().get(branchID) == null) {
                                            ConfigurationSection locationSection = branchQuestSection.getConfigurationSection(branchID + ".neededLocations");
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
                                                playerData.getLocationQuestData().getLocationsToGo().put(branchID, locationsToGo);
                                            });
                                        }
                                        for (Location key : playerData.getLocationQuestData().getLocationsToGo().get(branchID).keySet()) {
                                            if ((int) playerX == key.getX() && (int) playerY == key.getY() && (int) playerZ == key.getZ() && p.getLocation().getWorld().getName().equalsIgnoreCase(key.getWorld().getName())) {
                                                if (!Utilities.isEqualMap(playerData.getLocationQuestData().getLocationsToGo(), 0)) {
                                                    if (playerData.getLocationQuestData().getLocationsToGo().get(branchID).get(key) > 0) {
                                                        playerData.getLocationQuestData().getLocationsToGo().get(branchID).replace(key, (playerData.getLocationQuestData().getLocationsToGo().get(branchID).get(key) - 1));
                                                    }
                                                }
                                            }
                                        }
                                        
                                        if (Utilities.isEqualMap(playerData.getLocationQuestData().getLocationsToGo().get(branchID), 0)) {
                                            if (branchQuestSection.contains(branchID + ".rewards")) {
                                                branchQuestSection.getStringList(branchID + ".rewards").forEach(reward -> {
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
                        });
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRegionEnterE(RegionEnteredEvent e) {
        Player p = (Player) e.getPlayer();
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                if (playerData.getQuest().isQuestActive()) {
                    FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                    ConfigurationSection branchQuestSection = questConfig.getConfigurationSection("quest.branchQuests");
                    if (questConfig.contains("quest.branchQuests")) {
                        branchQuestSection.getKeys(false).forEach(branchID -> {
                            if (branchQuestSection.getString(branchID + ".type").equalsIgnoreCase("Location")) {
                                if (branchQuestSection.getString(branchID + ".region") != null) {
                                    if (playerData.getLocationQuestData() == null) {
                                        LocationQuestData newLocationQuestData = new LocationQuestData();
                                        Map<Object, Map<String, Integer>> regionsToGo = new HashMap<>();
                                        newLocationQuestData.setRegionsToGo(regionsToGo);
                                        playerData.setLocationQuestData(newLocationQuestData);
                                    }
                                    if (playerData.getLocationQuestData().getRegionsToGo() == null || playerData.getLocationQuestData().getRegionsToGo().get(branchID) == null) {
                                        ConfigurationSection regionSection = branchQuestSection.getConfigurationSection(branchID + ".neededRegions");
                                        regionSection.getKeys(false).forEach(regionID -> {
                                            String regionName = regionSection.getString(regionID + ".region");
                                            int amount = regionSection.getInt(regionID + ".amount");
                                            Map<String, Integer> regionsToGo = new HashMap<>();
                                            regionsToGo.put(regionName, amount);
                                            playerData.getLocationQuestData().getRegionsToGo().put(branchID, regionsToGo);
                                        });
                                    }
                                    for (String key : playerData.getLocationQuestData().getRegionsToGo().get(branchID).keySet()) {
                                        if (e.getRegionName().equalsIgnoreCase(key)) {
                                            if (!Utilities.isEqualMap(playerData.getLocationQuestData().getRegionsToGo(), 0)) {
                                                if (playerData.getLocationQuestData().getRegionsToGo().get(branchID).get(key) > 0) {
                                                    playerData.getLocationQuestData().getRegionsToGo().get(branchID).replace(key, (playerData.getLocationQuestData().getRegionsToGo().get(branchID).get(key) - 1));
                                                }
                                            }
                                        }
                                    }
                                    if (Utilities.isEqualMap(playerData.getLocationQuestData().getRegionsToGo().get(branchID), 0)) {
                                        if (branchQuestSection.contains(branchID + ".rewards")) {
                                            branchQuestSection.getStringList(branchID + ".rewards").forEach(reward -> {
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
                        });
                    }
                }
            }
        }
    }

}
