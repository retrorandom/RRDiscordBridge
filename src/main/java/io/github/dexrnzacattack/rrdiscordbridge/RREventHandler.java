package io.github.dexrnzacattack.rrdiscordbridge;

import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

import java.awt.*;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.REAL_ORANGE;

public class RREventHandler implements Listener {
    private final Settings settings;

    public RREventHandler(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (this.settings.enabledEvents.contains(Settings.Events.PLAYER_CHAT))
            DiscordBot.sendPlayerMessage(event);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        // /me
        if (event.getMessage().toLowerCase().startsWith("/me ") && event.getMessage().length() > 4 && settings.enabledEvents.contains(Settings.Events.ME_COMMAND))
            DiscordBot.sendPlayerMessage(Settings.Events.ME_COMMAND, event.getPlayer().getName(), String.format("_%s %s_", event.getPlayer().getName(), event.getMessage().substring(4)));

        // /say
        if (event.getMessage().toLowerCase().startsWith("/say ") && event.getMessage().length() > 5 && settings.enabledEvents.contains(Settings.Events.SAY_BROADCAST))
            DiscordBot.sendPlayerMessage(Settings.Events.SAY_BROADCAST, "Server (Broadcast)", settings.broadcastSkinName, event.getMessage().substring(5));
    }

    @EventHandler
    public void onCommand(ServerCommandEvent event) {
        if (event.getCommand().toLowerCase().startsWith("say ") && event.getCommand().length() > 4 && settings.enabledEvents.contains(Settings.Events.SAY_BROADCAST))
            DiscordBot.sendPlayerMessage(Settings.Events.SAY_BROADCAST, "Server (Broadcast)", settings.broadcastSkinName, event.getCommand().substring(4));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_DEATH, event.getEntity().getName(), event.getDeathMessage(), null, Color.RED, null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordBot.setPlayerCount();
        DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_JOIN, event.getPlayer().getName(), String.format("%s joined the game.", event.getPlayer().getName()), null, Color.GREEN, null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        DiscordBot.setPlayerCount();
        DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_LEAVE, event.getPlayer().getName(), String.format("%s left the game.", event.getPlayer().getName()), null, REAL_ORANGE, null);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        DiscordBot.setPlayerCount();
        DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_KICK, event.getPlayer().getName(), String.format("%s was kicked.", event.getPlayer().getName()), event.getReason(), REAL_ORANGE, null);
    }

}