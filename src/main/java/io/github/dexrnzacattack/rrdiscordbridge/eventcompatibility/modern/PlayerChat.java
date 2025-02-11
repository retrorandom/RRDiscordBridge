package io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.modern;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.helpers.ReflectionHelper;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {
    public static boolean isSupported = ReflectionHelper.doesClassExist("org.bukkit.event.player.AsyncPlayerChatEvent");

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (RRDiscordBridge.settings.enabledEvents.contains(Settings.Events.PLAYER_CHAT)) {
            DiscordBot.sendPlayerMessage(event.getPlayer().getName(), event.getMessage(), event);
        }
    }
}
