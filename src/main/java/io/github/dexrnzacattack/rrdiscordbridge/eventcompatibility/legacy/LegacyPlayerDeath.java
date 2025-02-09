package io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.legacy;

import io.github.dexrnzacattack.rrdiscordbridge.ReflectionHelper;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.awt.*;

public class LegacyPlayerDeath implements Listener {
    public static boolean isSupported = ReflectionHelper.doesClassExist("org.bukkit.event.entity.PlayerDeathEvent");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathLegacy(PlayerDeathEvent event) {
        DiscordBot.sendEvent(Settings.Events.PLAYER_DEATH, new MessageEmbed.AuthorInfo(event.getDeathMessage(), null, null, null), null,  Color.RED, null);
    }
}
