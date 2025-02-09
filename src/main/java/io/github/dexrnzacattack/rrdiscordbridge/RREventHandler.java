package io.github.dexrnzacattack.rrdiscordbridge;

import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

import java.awt.*;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.REAL_ORANGE;

public class RREventHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        // /me
        if (event.getMessage().toLowerCase().startsWith("/me ") && event.getMessage().length() > 4 && RRDiscordBridge.settings.enabledEvents.contains(Settings.Events.ME_COMMAND))
            DiscordBot.sendPlayerMessage(Settings.Events.ME_COMMAND, event.getPlayer().getName(), String.format("_%s %s_", event.getPlayer().getName(), event.getMessage().substring(4)));

        // /say
        if (event.getMessage().toLowerCase().startsWith("/say ") && event.getMessage().length() > 5 && RRDiscordBridge.settings.enabledEvents.contains(Settings.Events.SAY_BROADCAST))
            DiscordBot.sendPlayerMessage(Settings.Events.SAY_BROADCAST, "Server (Broadcast)", RRDiscordBridge.settings.broadcastSkinName, event.getMessage().substring(5));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordBot.setPlayerCount();
        Player plr = event.getPlayer();
        if (ReflectionHelper.doesMethodExist("org.bukkit.entity.Player", "hasPlayedBefore") && !plr.hasPlayedBefore()) {
            DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_JOIN, plr.getName(), String.format("%s joined the game for the first time.", plr.getName()), null, Color.GREEN, null);
        } else {
            DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_JOIN, plr.getName(), String.format("%s joined the game.", event.getPlayer().getName()), null, Color.GREEN, null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().toLowerCase().startsWith("say ") && event.getCommand().length() > 4 && RRDiscordBridge.settings.enabledEvents.contains(Settings.Events.SAY_BROADCAST))
            DiscordBot.sendPlayerMessage(Settings.Events.SAY_BROADCAST, "Server (Broadcast)", RRDiscordBridge.settings.broadcastSkinName, event.getCommand().substring(4));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        // bug, needs to run after the player has left.
        // duct tape fix
        DiscordBot.setPlayerCount(RRDiscordBridge.getOnlinePlayers().length - 1);
        DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_LEAVE, event.getPlayer().getName(), String.format("%s left the game.", event.getPlayer().getName()), null, REAL_ORANGE, null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        DiscordBot.setPlayerCount(RRDiscordBridge.getOnlinePlayers().length - 1);
        DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_KICK, event.getPlayer().getName(), String.format("%s was kicked.", event.getPlayer().getName()), event.getReason(), REAL_ORANGE, null);
    }

}