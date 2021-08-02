package me.frostingly.gequests.Quests.API.Scripting.SFX;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Utilities;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class QuestFinishedSFX {

    private final GEQuests plugin;

    public QuestFinishedSFX(GEQuests plugin) {
        this.plugin = plugin;
    }

    public void executeQuestFinishedFX(Player p) {
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                ConfigurationSection questEventTypeSection = questConfig.getConfigurationSection("quest.scripting.sfx");
                questEventTypeSection.getKeys(false).forEach(questEventTypeID -> {
                    if (questEventTypeID.equalsIgnoreCase("QUEST_FINISHED")) {
                        ConfigurationSection sfxSection = questConfig.getConfigurationSection("quest.scripting.sfx." + questEventTypeID);
                        if (sfxSection != null) {
                            sfxSection.getKeys(false).forEach(sfxID -> {
                                if (sfxID != null) {
                                    if (sfxSection.getString(sfxID + ".location").equalsIgnoreCase("PLAYER_LOCATION")) {
                                        String soundName = sfxSection.getString(sfxID + ".sound_name");
                                        float volume = Float.valueOf(sfxSection.getString(sfxID + ".volume"));
                                        float pitch = Float.valueOf(sfxSection.getString(sfxID + ".pitch"));
                                        if (sfxSection.getString(sfxID + ".location").equalsIgnoreCase("PLAYER_LOCATION")) {
                                            p.playSound(p.getLocation(), Sound.valueOf(soundName), volume, pitch);
                                        } else {
                                            String defaultString = sfxSection.getString(sfxID + ".location");
                                            String[] strings = defaultString.split(",");
                                            Location location = new Location(p.getWorld(), Integer.parseInt(strings[0].trim()), Integer.parseInt(strings[1].trim()), Integer.parseInt(strings[2].trim()));
                                            p.playSound(location, Sound.valueOf(soundName), volume, pitch);
                                        }
                                    }
                                } else {
                                    p.sendMessage(Utilities.format("&cNo SFX section found for this quest. [0x0QFSFXB19NULL]"));
                                }
                            });
                        } else {
                            p.sendMessage(Utilities.format("&cNo SFX section found for this quest. [0x0QFSFXB20NULL]"));
                        }
                    }
                });
            } else {
                p.sendMessage(Utilities.format("&cNo SFX section found for this quest. [0x0QFSFXB21NULL]"));
            }
        }
    }
}
