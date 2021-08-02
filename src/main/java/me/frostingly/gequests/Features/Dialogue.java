package me.frostingly.gequests.Features;

import me.frostingly.gequests.GEQuests;
import me.frostingly.gequests.Information.Data.PlayerData;
import me.frostingly.gequests.Information.Data.QuestData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Dialogue {

    private final GEQuests plugin;

    public Dialogue(GEQuests plugin) {
        this.plugin = plugin;
    }

    public void sendDialogue(Player p) {
        for (QuestData quest : plugin.getQuests()) {
            PlayerData playerData = plugin.getPlayerData().get(p.getUniqueId());
            if (playerData != null) {
                if (playerData.getQuest() != null) {
                    if (playerData.getQuest().getQuestName().equalsIgnoreCase(quest.getQuestName())) {
                        FileConfiguration questConfig = quest.getQuestConfig();
                        ConfigurationSection dialogueSection = questConfig.getConfigurationSection("quest.dialogue.npc");
                        ConfigurationSection dialogueComponentSection = questConfig.getConfigurationSection("quest.dialogue.components");
                        if (dialogueSection != null) {
                            dialogueSection.getKeys(false).forEach(messageID -> {
                                if (playerData.getQuest().getQuestDialogueMessageAt() < playerData.getQuest().getQuestDialogueMessagesMax()) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (playerData.getQuest().getQuestDialogueMessageAt().equals(playerData.getQuest().getQuestDialogueMessagesMax())) {
                                                BaseComponent yesComponent = null;
                                                BaseComponent noComponent = null;
                                                if (dialogueComponentSection.getString("noComponent.display") != null) {
                                                    noComponent = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', " " + dialogueComponentSection.getString("noComponent.display"))).getCurrentComponent();
                                                }
                                                if (dialogueComponentSection.getString("noComponent.command") != null) {
                                                    noComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, dialogueComponentSection.getString("noComponent.command") + " " + ChatColor.stripColor(playerData.getQuest().getQuestName())));
                                                }
                                                if (dialogueComponentSection.getString("noComponent.hover_text") != null) {
                                                    noComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                            new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', dialogueComponentSection.getString("noComponent.hover_text"))).create()));
                                                }

                                                // YES component code

                                                if (dialogueComponentSection.getString("yesComponent.display") != null) {
                                                    yesComponent = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', " " + dialogueComponentSection.getString("yesComponent.display"))).getCurrentComponent();
                                                }
                                                if (dialogueComponentSection.getString("yesComponent.command") != null) {
                                                    yesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, dialogueComponentSection.getString("yesComponent.command") + " " + ChatColor.stripColor(playerData.getQuest().getQuestName())));
                                                }

                                                if (dialogueComponentSection.getString("yesComponent.hover_text") != null) {
                                                    yesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                            new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', dialogueComponentSection.getString("yesComponent.hover_text"))).create()));
                                                }

                                                // Final component code

                                                BaseComponent[] a = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', dialogueSection.getString(playerData.getQuest().getQuestDialogueMessageAt() + ".message")))
                                                        .append(yesComponent)
                                                        .append(noComponent)
                                                        .create();
                                                p.spigot().sendMessage(a);
                                            } else {
                                                BaseComponent[] a = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', dialogueSection.getString(playerData.getQuest().getQuestDialogueMessageAt() + ".message"))).create();
                                                p.spigot().sendMessage(a);
                                            }
                                            playerData.getQuest().setQuestDialogueMessageAt(playerData.getQuest().getQuestDialogueMessageAt() + 1);
                                        }
                                    }.runTaskLater(plugin, dialogueSection.getLong(messageID + ".delay") * 20);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
