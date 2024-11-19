package io.github.dexrnzacattack.rrdiscordbridge;

import io.github.dexrnzacattack.rrdiscordbridge.bukkit.DiscordCommand;
import io.github.dexrnzacattack.rrdiscordbridge.bukkit.FancyBroadcastCommand;
import io.github.dexrnzacattack.rrdiscordbridge.bukkit.ReloadConfigCommand;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.IOException;

public final class RRDiscordBridge extends JavaPlugin {
    public static Color REAL_ORANGE = new Color(255, 100, 0);
    public static Settings settings;
    public static String version;
    public static long serverStartTime;

    @Override
    public void onEnable() {
        serverStartTime = System.currentTimeMillis();
        version = getDescription().getVersion();
        try {
            settings = new Settings().loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            DiscordBot.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        getCommand("reloadrdbconfig").setExecutor(new ReloadConfigCommand());
        getCommand("dcbroadcast").setExecutor(new FancyBroadcastCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getServer().getPluginManager().registerEvents(new RREventHandler(settings), this);
        getLogger().info(String.format("RRDiscordBridge v%s has started.", version));
        DiscordBot.sendEvent(Settings.Events.SERVER_START, new MessageEmbed.AuthorInfo(null, null, null, null), null, Color.GREEN, "Server started!");
    }

    @Override
    public void onDisable() {
        DiscordBot.sendEvent(Settings.Events.SERVER_STOP, new MessageEmbed.AuthorInfo(null, null, null, null), null, Color.RED, "Server stopped!");
        DiscordBot.stop();
    }
}