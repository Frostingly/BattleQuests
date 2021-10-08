package me.frostingly.gequests.Detectors.PhaseQuests;

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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LocationPhaseQuest implements Listener {

    private final GEQuests plugin;

    public LocationPhaseQuest(GEQuests plugin) {
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
                    Integer id = playerData.getPhaseQuestID();
                    if (playerData.getQuest().getQuestType().equalsIgnoreCase("Phase")) {
                        List<Integer> availablePhaseQuestsAmount = new ArrayList<>();
                        availablePhaseQuestsAmount.add(questConfig.getConfigurationSection("quest.phaseQuests").getKeys(false).size());
                        if (id <= availablePhaseQuestsAmount.size()) {
                            ConfigurationSection phaseSection = questConfig.getConfigurationSection("quest.phaseQuests");
                            if (phaseSection.getString(id + ".type").equalsIgnoreCase("Location")) {
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
                                    if (phaseSection.getString(id + ".region") == null) {
                                        if (playerData.getLocationQuestData() == null) {
                                            LocationQuestData newLocationQuestData = new LocationQuestData();
                                            Map<Object, Map<Location, Integer>> locationsToGo = new HashMap<>();
                                            newLocationQuestData.setLocationsToGo(locationsToGo);
                                            playerData.setLocationQuestData(newLocationQuestData);
                                        }
                                        double playerX = p.getLocation().getX();
                                        double playerY = p.getLocation().getY();
                                        double playerZ = p.getLocation().getZ();
                                        if (playerData.getLocationQuestData().getLocationsToGo() == null || playerData.getLocationQuestData().getLocationsToGo().get(id) == null) {
                                            ConfigurationSection locationSection = phaseSection.getConfigurationSection(id + ".neededLocations");
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
                                                playerData.getLocationQuestData().getLocationsToGo().put(id, locationsToGo);
                                            });
                                        }
                                        for (Location key : playerData.getLocationQuestData().getLocationsToGo().get(id).keySet()) {
                                            if ((int) playerX == key.getX() && (int) playerY == key.getY() && (int) playerZ == key.getZ() && p.getLocation().getWorld().getName().equalsIgnoreCase(key.getWorld().getName())) {
                                                if (!Utilities.isEqualMap(playerData.getLocationQuestData().getLocationsToGo(), 0)) {
                                                    if (playerData.getLocationQuestData().getLocationsToGo().get(id).get(key) > 0) {
                                                        playerData.getLocationQuestData().getLocationsToGo().get(id).replace(key, (playerData.getLocationQuestData().getLocationsToGo().get(id).get(key) - 1));
                                                    }
                                                }
                                            }
                                        }

                                        if (Utilities.isEqualMap(playerData.getLocationQuestData().getLocationsToGo().get(id), 0)) {
                                            if (phaseSection.contains(id + ".rewards")) {
                                                phaseSection.getStringList(id + ".rewards").forEach(reward -> {
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                                });
                                            }
                                            playerData.getLocationQuestData().getLocationsToGo().clear();
                                            playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                            p.sendMessage(Utilities.format(phaseSection.getString(id + ".dialog")));
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!playerData.isExecuted()) {
                                playerData.setExecuted(true);
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
                                playerData.setPhaseQuestID(0);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        playerData.setExecuted(false);
                                    }
                                }.runTaskLater(plugin, 20L);
                            }
                        }
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
                    if (playerData.getQuest().getQuestType().equalsIgnoreCase("Phase")) {
                        Integer id = playerData.getPhaseQuestID();
                        List<Integer> availablePhaseQuestsAmount = new ArrayList<>();
                        availablePhaseQuestsAmount.add(questConfig.getConfigurationSection("quest.phaseQuests").getKeys(false).size());
                        if (id <= availablePhaseQuestsAmount.size()) {
                            ConfigurationSection phaseSection = questConfig.getConfigurationSection("quest.phaseQuests");
                            if (phaseSection.getString(id + ".type").equalsIgnoreCase("Location")) {
                                if (phaseSection.getString(id + ".region") != null) {
                                    if (playerData.getLocationQuestData() == null) {
                                        LocationQuestData newLocationQuestData = new LocationQuestData();
                                        Map<Object, Map<String, Integer>> regionsToGo = new HashMap<>();
                                        newLocationQuestData.setRegionsToGo(regionsToGo);
                                        playerData.setLocationQuestData(newLocationQuestData);
                                    }
                                    if (playerData.getLocationQuestData().getRegionsToGo() == null || playerData.getLocationQuestData().getRegionsToGo().get(id) == null) {
                                        ConfigurationSection regionSection = phaseSection.getConfigurationSection(id + ".neededRegions");
                                        regionSection.getKeys(false).forEach(regionID -> {
                                            String regionName = regionSection.getString(regionID + ".region");
                                            int amount = regionSection.getInt(regionID + ".amount");
                                            Map<String, Integer> regionsToGo = new HashMap<>();
                                            regionsToGo.put(regionName, amount);
                                            playerData.getLocationQuestData().getRegionsToGo().put(id, regionsToGo);
                                        });
                                    }
                                    for (String key : playerData.getLocationQuestData().getRegionsToGo().get(id).keySet()) {
                                        if (e.getRegionName().equalsIgnoreCase(key)) {
                                            if (!Utilities.isEqualMap(playerData.getLocationQuestData().getRegionsToGo(), 0)) {
                                                if (playerData.getLocationQuestData().getRegionsToGo().get(id).get(key) > 0) {
                                                    playerData.getLocationQuestData().getRegionsToGo().get(id).replace(key, (playerData.getLocationQuestData().getRegionsToGo().get(id).get(key) - 1));
                                                }
                                            }
                                        }
                                    }

                                    if (Utilities.isEqualMap(playerData.getLocationQuestData().getRegionsToGo().get(id), 0)) {
                                        if (phaseSection.getConfigurationSection(id + ".rewards") != null) {
                                            phaseSection.getStringList(id + ".rewards").forEach(reward -> {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                            });
                                        }
                                        playerData.getLocationQuestData().getRegionsToGo().clear();
                                        playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                        p.sendMessage(Utilities.format(phaseSection.getString(id + ".dialog")));
                                    }
                                }
                            }
                        }
                    } else {
                        if (!playerData.isExecuted()) {
                            playerData.setExecuted(true);
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
                            playerData.setPhaseQuestID(0);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    playerData.setExecuted(false);
                                }
                            }.runTaskLater(plugin, 20L);
                        }
                    }
                }
            }
        }
    }

}
