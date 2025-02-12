package io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.legacy;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class LegacyPlayerChat implements Listener {
    @EventHandler
    public void onPlayerChatLegacy(PlayerChatEvent event) {
        if (RRDiscordBridge.settings.enabledEvents.contains(Settings.Events.PLAYER_CHAT))
            DiscordBot.sendPlayerMessage(event.getPlayer().getName(), event.getMessage(), event);
    }
}
