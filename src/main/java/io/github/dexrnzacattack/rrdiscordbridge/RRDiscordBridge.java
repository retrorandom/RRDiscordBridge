package io.github.dexrnzacattack.rrdiscordbridge;

import io.github.dexrnzacattack.rrdiscordbridge.bukkit.ChatExtensionsCommand;
import io.github.dexrnzacattack.rrdiscordbridge.chat.extensions.ChatExtensions;
import io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.legacy.LegacyPlayerChat;
import io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.legacy.LegacyPlayerDeath;
import io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.legacy.PLegacyPlayerDeath;
import io.github.dexrnzacattack.rrdiscordbridge.eventcompatibility.modern.*;
import io.github.dexrnzacattack.rrdiscordbridge.bukkit.DiscordCommand;
import io.github.dexrnzacattack.rrdiscordbridge.bukkit.FancyBroadcastCommand;
import io.github.dexrnzacattack.rrdiscordbridge.bukkit.ReloadConfigCommand;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public final class RRDiscordBridge extends JavaPlugin {
    public static Color REAL_ORANGE = new Color(255, 100, 0);
    public static Settings settings;
    public static String version;
    public static long serverStartTime;
    public static Logger logger;
    public static PluginManager pluginManager;
    public static ChatExtensions extensions;

    @Override
    public void onEnable() {
        logger = getServer().getLogger();
        version = getDescription().getVersion();
        try {
            settings = new Settings().loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            pluginManager = getServer().getPluginManager();
            serverStartTime = System.currentTimeMillis();
            try {
                DiscordBot.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            extensions = new ChatExtensions();
        }
        getCommand("reloadrdbconfig").setExecutor(new ReloadConfigCommand());
        getCommand("dcbroadcast").setExecutor(new FancyBroadcastCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        // cernel extension
        getCommand("cext").setExecutor(new ChatExtensionsCommand());


        if (!ReflectionHelper.isMotdSupported) logger.warning("MOTD is not supported on this version. There will be no MOTD when /about is used.");

        if (!ReflectionHelper.isServerIconSupported) logger.warning("server-icon.png is not supported on this version. There will be no icon when /about is used.");

        if (!ReflectionHelper.isServerNameSupported) logger.warning("server.properties' server-name is not supported on this version. There will be no server name when /about is used.");

        if (!ReflectionHelper.isServerOperatorsSupported) logger.warning("Getting the operators list is not supported on this version. Next best thing is directly reading ops.txt...");

        pluginManager.registerEvents(new RREventHandler(), this);

        if (PlayerChat.isSupported) {
            pluginManager.registerEvents(new PlayerChat(), this);
        } else {
            logger.info("Registering legacy PlayerChat handler.");
            pluginManager.registerEvents(new LegacyPlayerChat(),  this);
        }

        if (PlayerDeath.isSupported()) {
            pluginManager.registerEvents(new PlayerDeath(), this);
        } else if (PLegacyPlayerDeath.isSupported) {
            // specifically for poseidon
            logger.info("Registering Project Poseidon PlayerDeath handler.");
            pluginManager.registerEvents(new PLegacyPlayerDeath(), this);
        } else if (LegacyPlayerDeath.isSupported) {
            logger.info("Registering legacy PlayerDeath handler.");
            pluginManager.registerEvents(new LegacyPlayerDeath(), this);
        }

        logger.info(String.format("RRDiscordBridge v%s has started.", version));
        DiscordBot.sendEvent(Settings.Events.SERVER_START, new MessageEmbed.AuthorInfo(null, null, null, null), null, Color.GREEN, "Server started!");
    }

    /** Backwards compatible getOnlinePlayers since 1.8 changed the type to Collection */
    @SuppressWarnings({"ConstantConditions", "SuspiciousToArrayCall"})
    public static Player[] getOnlinePlayers() {
        try {
            Method getOnlinePlayers = Bukkit.class.getMethod("getOnlinePlayers");
            Object players = getOnlinePlayers.invoke(null);

            if (players instanceof Player[]) {
                return (Player[]) players;
            } else if (players instanceof Collection) {
                Collection<?> collection = (Collection<?>) players;
                return collection.toArray(new Player[0]);
            }
        } catch (Exception ignored) {}
        return new Player[0];
    }

    @Override
    public void onDisable() {
        CompletableFuture<Void> eventFuture = CompletableFuture.runAsync(() -> {
            DiscordBot.sendEvent(Settings.Events.SERVER_STOP, new MessageEmbed.AuthorInfo(null, null, null, null), null, Color.RED, "Server stopped!");
        });

        // java has some weird ass syntax, why is it C++ method syntax?
        eventFuture.thenRun(DiscordBot::stop);
    }
}