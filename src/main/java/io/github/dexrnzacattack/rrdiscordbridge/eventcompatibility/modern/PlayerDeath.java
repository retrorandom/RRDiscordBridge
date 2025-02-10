package io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.modern;

import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.awt.*;

public class PlayerDeath implements Listener {
    public static boolean isSupported() {
        try {
            // pre 1.8
            Class<?> clazz = Class.forName("org.bukkit.event.player.PlayerDeathEvent");
            clazz.getMethod("getEntity");
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            try {
                // post 1.8
                Class<?> modernClazz = Class.forName("org.bukkit.event.entity.PlayerDeathEvent");
                modernClazz.getMethod("getEntity");
                return true;
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                // pre 1.3
                return false;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        DiscordBot.sendPlayerEvent(Settings.Events.PLAYER_DEATH, event.getEntity().getName(), event.getDeathMessage(), null, Color.RED, null);
    }
}
