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
import io.github.dexrnzacattack.rrdiscordbridge.helpers.ReflectionHelper;
import me.scarsz.jdaappender.ChannelLoggingHandler;
import me.scarsz.jdaappender.LogLevel;
import me.scarsz.jdaappender.adapter.JavaLoggingAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class RRDiscordBridge extends JavaPlugin {
    public static Color REAL_ORANGE = new Color(255, 100, 0);
    public static Settings settings;
    public static String version;
    public static long serverStartTime;
    public static Logger logger;
    public static PluginManager pluginManager;
    public static ChatExtensions extensions;
    public static ChannelLoggingHandler logHandler;

    @Override
    public void onEnable() {
        // setup the logger
        logger = getServer().getLogger();

        // set version shits
        version = getDescription().getVersion();
        try {
            // load settings
            settings = new Settings().loadConfig();
        } catch (IOException e) {
            // just throw if caught some weird error
            throw new RuntimeException(e);
        } finally {
            // plugin manager
            pluginManager = getServer().getPluginManager();
            // get server start time (for runtime stats)
            serverStartTime = System.currentTimeMillis();
            try {
                // start the discord bot
                logger.info("Starting Discord relay bot");
                DiscordBot.start();
                // register console channel handler thing (logs all console messages to discord if set up)
                if (!settings.consoleChannelId.isEmpty()) {
                    logger.info("Registering console channel handler");
                    logHandler = new ChannelLoggingHandler(() -> DiscordBot.jda.getTextChannelById(settings.consoleChannelId), config -> {
                        config.setLogLevels(EnumSet.allOf(LogLevel.class));
                        config.mapLoggerName("Minecraft", "");
                    });

                    try {
                        Class.forName("org.apache.logging.log4j.core.Logger");
                        logHandler.attachLog4jLogging();
                    } catch (Throwable ignored) {
                        logHandler.attachJavaLogging();

                        //Because for some reason instead of letting me simply do .attachJavaLogging("Minecraft"), I have to do this.
                        Logger global = Logger.getLogger("");
                        Handler[] handlers = global.getHandlers();

                        for (Handler handler : handlers) {
                            if (handler.getClass() == JavaLoggingAdapter.class) {
                                logger.addHandler(handler);
                            }
                        }
                    }
                    logHandler.schedule();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // setup chat extensions
            extensions = new ChatExtensions();
        }

        // register all the in-game commands
        getCommand("reloadrdbconfig").setExecutor(new ReloadConfigCommand());
        getCommand("dcbroadcast").setExecutor(new FancyBroadcastCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        // cernel extension
        getCommand("cext").setExecutor(new ChatExtensionsCommand());

        // support checking
        if (!ReflectionHelper.isMotdSupported) logger.warning("MOTD is not supported on this version. There will be no MOTD when /about is used.");

        if (!ReflectionHelper.isServerIconSupported) logger.warning("server-icon.png is not supported on this version. There will be no icon when /about is used.");

        if (!ReflectionHelper.isServerNameSupported) logger.warning("server.properties' server-name is not supported on this version. There will be no server name when /about is used.");

        if (!ReflectionHelper.isServerOperatorsSupported) logger.warning("Getting the operators list is not supported on this version. Next best thing is directly reading ops.txt...");

        // register our events
        pluginManager.registerEvents(new RREventHandler(), this);

        // then check and register the appropriate events for the env
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
        // send "Server Started"
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

        if (logHandler != null) {
            //Because of the hack above we have to manually remove the handler before shutting down the log manager.
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                if (handler.getClass() == JavaLoggingAdapter.class) {
                    logger.removeHandler(handler);
                }
            }

            logHandler.shutdown();
        }
    }
}