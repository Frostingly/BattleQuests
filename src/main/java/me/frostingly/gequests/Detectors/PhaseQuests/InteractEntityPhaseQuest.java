package me.frostingly.gequests.Detectors.PhaseQuests;

import me.clip.placeholderapi.PlaceholderAPI;
import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.EntityData;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.QuestData.InteractQuestData;
import me.frostingly.gequests.Quests.API.Messages.QuestFinished;
import me.frostingly.gequests.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractEntityPhaseQuest implements Listener {

    private final GEQuests plugin;

    public InteractEntityPhaseQuest(GEQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractEntityE(PlayerInteractEntityEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
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
                            if (phaseSection.getString(id + ".type").equalsIgnoreCase("InteractEntity")) {
                                if (playerData.getInteractQuestData().getEntitiesToInteractWith() == null || playerData.getInteractQuestData().getEntitiesToInteractWith().get(id) == null) {
                                    InteractQuestData newInteractQuestData = new InteractQuestData();
                                    Map<Object, Map<String, Integer>> entitiesToInteractWith = new HashMap<>();
                                    newInteractQuestData.setEntitiesToInteractWith(entitiesToInteractWith);
                                    playerData.setInteractQuestData(newInteractQuestData);
                                }
                                if (playerData.getInteractQuestData().getEntitiesToInteractWith() == null || playerData.getInteractQuestData().getEntitiesToInteractWith().get(1) == null) {
                                    ConfigurationSection entitySection = phaseSection.getConfigurationSection(id + ".neededEntities");
                                    entitySection.getKeys(false).forEach(entityID -> {
                                        String entityName = entitySection.getString(entityID + ".entity_name");
                                        int amount = entitySection.getInt(entityID + ".amount");
                                        EntityType entityType = EntityType.valueOf(entitySection.getString(entityID + ".entity_type"));
                                        Map<String, Integer> entitiesToInteractWith = new HashMap<>();
                                        entitiesToInteractWith.put(entityName, amount);
                                        playerData.getInteractQuestData().getEntitiesToInteractWith().put(id, entitiesToInteractWith);
                                        EntityData newEntity = new EntityData(entityName, entityName, entityType);
                                        playerData.getEntities().add(newEntity);
                                    });
                                }
                                String entityName = e.getRightClicked().getName();
                                String customEntityName = e.getRightClicked().getCustomName();
                                for (String key : playerData.getInteractQuestData().getEntitiesToInteractWith().get(id).keySet()) {
                                    for (int i = 0; i < playerData.getEntities().size(); i++) {
                                        if (e.getRightClicked().getType() == playerData.getEntities().get(i).getEntityType()) {
                                            if (entityName.equalsIgnoreCase(Utilities.format(key)) || customEntityName != null && customEntityName.equalsIgnoreCase(Utilities.format(key))) {
                                                //code if entity name or custom name is equals to key
                                                if (!Utilities.isEqualMap(playerData.getInteractQuestData().getEntitiesToInteractWith().get(id), 0)) {
                                                    if (playerData.getInteractQuestData().getEntitiesToInteractWith().get(id).get(key) > 0) {
                                                        playerData.getInteractQuestData().getEntitiesToInteractWith().get(id).replace(key, (playerData.getInteractQuestData().getEntitiesToInteractWith().get(id).get(key) - 1));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (Utilities.isEqualMap(playerData.getInteractQuestData().getEntitiesToInteractWith().get(id), 0)) {
                                    if (phaseSection.contains(id + ".rewards")) {
                                        phaseSection.getStringList(id + ".rewards").forEach(reward -> {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(p, reward));
                                        });
                                    }
                                    playerData.getInteractQuestData().getEntitiesToInteractWith().clear();
                                    playerData.setPhaseQuestID(playerData.getPhaseQuestID() + 1);
                                    p.sendMessage(Utilities.format(phaseSection.getString(id + ".dialog")));
                                }
                            }
                        } else {
                            new QuestFinished().sendQuestFinished(p);
                            playerData.setQuest(null);
                            playerData.setPhaseQuestID(0);
                        }
                    }
                }
            }
        }
    }

}
