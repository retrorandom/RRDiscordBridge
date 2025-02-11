package io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.legacy;

import com.legacyminecraft.poseidon.event.PlayerDeathEvent;
import io.github.dexrnzacattack.rrdiscordbridge.helpers.ReflectionHelper;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;

public class PLegacyPlayerDeath implements Listener {
    public static boolean isSupported = ReflectionHelper.doesClassExist("com.legacyminecraft.poseidon.event.PlayerDeathEvent");

    @EventHandler
    public void onPlayerDeathLegacy(PlayerDeathEvent event) {
        String message = event.getDeathMessage();

        if (message != null && !message.trim().isEmpty()) {
            DiscordBot.sendEvent(Settings.Events.PLAYER_DEATH, new MessageEmbed.AuthorInfo(message, null, null, null), null, Color.RED, null);
        }
    }
}
