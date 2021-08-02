package me.frostingly.gequests.Quests.API.Scripting.VFX;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Quests.Functions.Global.EffectFunctions;
import me.frostingly.gequests.Utilities;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class QuestFinishedVFX {

    private final GEQuests plugin;

    public QuestFinishedVFX(GEQuests plugin) {
        this.plugin = plugin;
    }

    public void executeQuestFinishedVFX(Player p) {
        PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
        if (playerData != null) {
            if (playerData.getQuest() != null) {
                FileConfiguration questConfig = playerData.getQuest().getQuestConfig();
                ConfigurationSection questEventTypeSection = questConfig.getConfigurationSection("quest.scripting.vfx");
                questEventTypeSection.getKeys(false).forEach(questEventTypeID -> {
                    if (questEventTypeID.equalsIgnoreCase("QUEST_FINISHED")) {
                        ConfigurationSection vfxSection = questConfig.getConfigurationSection("quest.scripting.vfx." + questEventTypeID);
                        if (vfxSection != null) {
                            vfxSection.getKeys(false).forEach(vfxID -> {
                                if (vfxID != null) {
                                    if (vfxSection.getString(vfxID + ".type").equalsIgnoreCase("SPAWN_PARTICLE")) {
                                        if (vfxSection.getString(vfxID + ".location").equalsIgnoreCase("PLAYER_LOCATION")) {
                                            double radiusX = 0;
                                            double radiusY = 0;
                                            double radiusZ = 0;
                                            if (vfxSection.getString(vfxID + ".radiusX") != null)
                                                radiusX = vfxSection.getDouble(vfxID + ".radiusX");
                                            if (vfxSection.getString(vfxID + ".radiusY") != null)
                                                radiusY = vfxSection.getDouble(vfxID + ".radiusY");
                                            if (vfxSection.getString(vfxID + ".radiusZ") != null)
                                                radiusZ = vfxSection.getDouble(vfxID + ".radiusZ");
                                            for (double x = p.getLocation().getX() - radiusX; x <= p.getLocation().getX() + radiusX; x++) {
                                                for (double y = p.getLocation().getY() - radiusY; y <= p.getLocation().getY() + radiusY; y++) {
                                                    for (double z = p.getLocation().getZ() - radiusZ; z <= p.getLocation().getZ() + radiusZ; z++) {
                                                        p.getWorld().spawnParticle(Particle.valueOf(vfxSection.getString(vfxID + ".particle_name")), x, y, z, vfxSection.getInt(vfxID + ".amount"));
                                                    }
                                                }
                                            }
                                        } else {
                                            String defaultString = vfxSection.getString(vfxID + ".location");
                                            String[] strings = defaultString.split(",");
                                            double radiusX = 0;
                                            double radiusY = 0;
                                            double radiusZ = 0;
                                            if (vfxSection.getString(vfxID + ".radiusX") != null)
                                                radiusX = vfxSection.getDouble(vfxID + ".radiusX");
                                            if (vfxSection.getString(vfxID + ".radiusY") != null)
                                                radiusY = vfxSection.getDouble(vfxID + ".radiusY");
                                            if (vfxSection.getString(vfxID + ".radiusZ") != null)
                                                radiusZ = vfxSection.getDouble(vfxID + ".radiusZ");
                                            for (double x = Double.parseDouble(strings[0].trim()) - radiusX; x <= Double.parseDouble(strings[0].trim()) + radiusX; x++) {
                                                for (double y = Double.parseDouble(strings[1].trim()) - radiusY; y <= Double.parseDouble(strings[1].trim()) + radiusY; y++) {
                                                    for (double z = Double.parseDouble(strings[2].trim()) - radiusZ; z <= Double.parseDouble(strings[2].trim()) + radiusZ; z++) {
                                                        p.getWorld().spawnParticle(Particle.valueOf(vfxSection.getString(vfxID + ".particle_name")), x, y, z, vfxSection.getInt(vfxID + ".amount"));
                                                    }
                                                }
                                            }
                                        }
                                    } else if (vfxSection.getString(vfxID + ".type").equalsIgnoreCase("SPAWN_BLOCK")) {
                                        if (vfxSection.getString(vfxID + ".location").equalsIgnoreCase("PLAYER_LOCATION")) {
                                            int radius = 0;
                                            int radiusX = 0;
                                            int radiusY = 0;
                                            int radiusZ = 0;
                                            if (vfxSection.getString(vfxID + ".radius") != null)
                                                radius = vfxSection.getInt(vfxID + ".radius");
                                            if (vfxSection.getString(vfxID + ".radiusX") != null)
                                                radiusX = vfxSection.getInt(vfxID + ".radiusX");
                                            if (vfxSection.getString(vfxID + ".radiusY") != null)
                                                radiusY = vfxSection.getInt(vfxID + ".radiusY");
                                            if (vfxSection.getString(vfxID + ".radiusZ") != null)
                                                radiusZ = vfxSection.getInt(vfxID + ".radiusZ");
                                            new EffectFunctions(plugin).setBlocksInRadius(p.getLocation(), radius, Material.EMERALD_BLOCK);
                                        } else {
                                            int radius = 0;
                                            if (vfxSection.getString(vfxID + ".radius") != null)
                                                radius = vfxSection.getInt(vfxID + ".radius");
                                            String defaultString = vfxSection.getString(vfxID + ".location");
                                            String[] strings = defaultString.split(",");
                                            Location location = new Location(p.getWorld(), Integer.parseInt(strings[0].trim()), Integer.parseInt(strings[1].trim()), Integer.parseInt(strings[2].trim()));
                                            new EffectFunctions(plugin).setBlocksInRadius(location, radius, Material.DIAMOND_BLOCK);
                                        }
                                    } else if (vfxSection.getString(vfxID + ".type").equalsIgnoreCase("DESPAWN_NPC")) {
                                        NPCRegistry registry = CitizensAPI.getNPCRegistry();
                                        NPC npc = registry.getById(vfxSection.getInt(vfxID + ".npc_id"));
                                        Player newNPC = (Player) npc.getEntity();
                                        for (Player player : Bukkit.getOnlinePlayers()) {
                                            if (player.equals(p)) {
                                                player.hidePlayer(plugin, newNPC);
                                            }
                                        }
                                    }
                                } else {
                                    p.sendMessage(Utilities.format("&cNo VFX section found for this quest. [Error code:]"));
                                }
                            });
                        } else {
                            p.sendMessage(Utilities.format("&cNo VFX section found for this quest. [Error code:]"));
                        }
                    }
                });
            } else {
                p.sendMessage(Utilities.format("&cNo VFX section found for this quest. [Error code:]"));
            }
        }
    }
}
