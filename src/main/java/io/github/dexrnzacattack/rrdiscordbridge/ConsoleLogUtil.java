package io.github.dexrnzacattack.rrdiscordbridge;

import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.logger;
import static io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot.jda;

public class ConsoleLogUtil extends Handler {
    TextChannel channel;
    Message lastMessage;
    StringBuilder builder;
    private final String CODEBLOCK_TYPE = "```ansi";

    ConsoleLogUtil(TextChannel channel) {
        this.channel = channel;

        builder = new StringBuilder(CODEBLOCK_TYPE);
    }

    ConsoleLogUtil(String channel) {
        this.channel = jda.getTextChannelById(channel);
        if (this.channel == null) {
            logger.warning(String.format("Cannot find console channel by id %s", channel));
        }
        builder = new StringBuilder(CODEBLOCK_TYPE);
    }

    public static String getANSIFromLogLevel(Level level) {
        if (level == Level.INFO) {
            return "\u001B[0m";
        } else if (level == Level.WARNING) {
            return "\u001B[2;33m";
        } else if (level == Level.SEVERE) {
            return "\u001B[2;31m";
        }
        return "\u001B[0m";
    }

    @Override
    public void publish(LogRecord record) {
        if (channel == null)
            return;

        // new message if it gets too long
        if (builder.length() + record.getMessage().length() + 7 + 2 + getANSIFromLogLevel(record.getLevel()).length() > 2000) {
            builder.setLength(0);
            builder.append(CODEBLOCK_TYPE);
            lastMessage = null;
        }

        // TODO: may look ugly on some discord clients, should let operators decide whether or not it does ansi codes
        builder.append("\n").append(getANSIFromLogLevel(record.getLevel())).append("[").append(record.getLevel().toString().toUpperCase()).append("] ").append(record.getMessage());

        if (lastMessage == null) {
            lastMessage = DiscordBot.sendMessage(builder.toString() + "\n```", channel);
        } else {
            DiscordBot.editMessage(builder.toString() + "\n```", lastMessage);
        }
    }

    @Override
    public void flush() /*throws ToiletCloggedException*/ {}

    @Override
    public void close() throws SecurityException {}
}
