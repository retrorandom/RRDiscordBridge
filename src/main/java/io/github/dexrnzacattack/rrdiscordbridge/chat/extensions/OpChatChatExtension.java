package io.github.dexrnzacattack.rrdiscordbridge.chat.extensions;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.*;
import static io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot.*;

// opchatchatchatchatchatchatextension
public class OpChatChatExtension implements IChatExtension {
    private final String name;
    public WebhookClient opcWebhookClient;
    public Webhook opcWebhook;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "§bA MCGalaxy inspired extension that sends messages to all online OPs, and optionally a discord channel. §9Syntax: §a## §2<msg>";
    }

    @Override
    public void onEnable() {
        if (settings.opchatChannelId.isEmpty()) {
            logger.warning("OPChat channel ID was not specified. Disabling extension.");
            extensions.enabledExtensions.remove(this);
            onDisable();
            return;
        }

        TextChannel opcChannel = jda.getTextChannelById(settings.opchatChannelId);
        if (opcChannel == null) {
            logger.log(Level.WARNING, "Failed to find OPChat channel with ID " + settings.opchatChannelId);
            extensions.enabledExtensions.remove(this);
            onDisable();
            return;
        }

        List<Webhook> webhooks = opcChannel.retrieveWebhooks().complete();

        opcWebhook = webhooks.stream().filter(
                hook -> {
                    User owner = hook.getOwnerAsUser();
                    if (owner == null) return false;

                    return owner.getId().equals(jda.getSelfUser().getId());
                }).findFirst().orElseGet(() -> opcChannel.createWebhook("RRMCBridgeOpChat").complete());

        WebhookClientBuilder opcBuilder = new WebhookClientBuilder(opcWebhook.getUrl());
        opcBuilder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("RRDiscordBridgeBotOpChat");
            thread.setDaemon(true);
            return thread;
        });
        opcBuilder.setWait(true);
        opcWebhookClient = opcBuilder.build();
    }

    @Override
    public void onDisable() {
        opcWebhook = null;

        if (opcWebhookClient != null) {
            opcWebhookClient.close();
            opcWebhookClient = null;
        }
    }

    public OpChatChatExtension(String name) {
        this.name = name;
    }

    @Override
    public ChatExtensionResult onMCMessage(String message, String player) {
        if (!message.trim().startsWith("##"))
            return new ChatExtensionResult(message, true, true);

        message = message.trim().substring(2).trim();

        // in-case the player just sends "##" or "## "
        if (message.isEmpty())
            return new ChatExtensionResult(message, true, true);

        String opcMsg = String.format("§b[§r%s §b-> §6OPs§b] §r%s", player, message);

        // so that Console can display the messages
        logger.log(Level.INFO, opcMsg);

        Bukkit.getPlayer(player).sendMessage(opcMsg);
        for (Player p : getOnlinePlayers()) {
            // send the message to all ops (except for the player that sent the message, only if they're op.)
            if (p.isOp() && !p.equals(Bukkit.getPlayer(player)))
                p.sendMessage(opcMsg);
        }

        if (!settings.opchatChannelId.isEmpty()) {
            DiscordBot.sendPlayerMessage(player, message, opcWebhookClient);
        }

        return new ChatExtensionResult(message, false, false);
    }

    @Override
    public DiscordChatExtensionResult onDCMessage(Message message) {
        String author = message.getAuthor().getId();
        if (message.getChannelId().equals(settings.opchatChannelId) && !author.equals(self.getId()) && !author.equals(opcWebhook.getId()) && !author.equals(webhook.getId())) {
            String msg = String.format("§b[§6OPChat§b - §e%s§b]§r %s", DiscordBot.getName(message.getAuthor()), message.getContentRaw());

            logger.log(Level.INFO, msg);

            for (Player p : getOnlinePlayers()) {
                // send the message to all ops
                if (p.isOp() && !p.equals(Bukkit.getPlayer(msg)))
                    p.sendMessage(msg);
            }
        }

        return new DiscordChatExtensionResult(message, true);
    }
}
