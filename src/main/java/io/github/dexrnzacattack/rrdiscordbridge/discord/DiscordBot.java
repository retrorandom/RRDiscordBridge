package io.github.dexrnzacattack.rrdiscordbridge.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.settings;

public class DiscordBot extends ListenerAdapter {
    private static TextChannel channel;
    private static WebhookClient webhookClient;
    public static JDA jda;

    /**
     * Starts the bot
     */
    public static void start() throws InterruptedException {
        if (settings.botToken.isEmpty()) {
            Bukkit.broadcastMessage("Failed to load RRDiscordBridge, please check the console logs.");
            throw new RuntimeException(String.format("Please set the bot token in %s", Settings.CONFIG_PATH));
        }

        if (settings.channelId.isEmpty()) {
            Bukkit.broadcastMessage("Failed to load RRDiscordBridge, please check the console logs.");
            throw new RuntimeException(String.format("Please set the channel id (of the relay channel) in %s", Settings.CONFIG_PATH));
        }

        if (settings.webhookId == 0 || settings.webhookToken.isEmpty()) {
            Bukkit.broadcastMessage("Failed to load RRDiscordBridge, please check the console logs.");
            throw new RuntimeException(String.format("Please set the webhook token and ID of a webhook in the relay channel (you may have to create one) in %s", Settings.CONFIG_PATH) +
                    "\nThe webhook ID is the numbers after /webhooks/ in the URL, and the token is a long string of seemingly random characters after the ID.");
        }

        JDALogger.setFallbackLoggerEnabled(false);
        WebhookClientBuilder builder = new WebhookClientBuilder(settings.webhookId, settings.webhookToken);
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("io_github_dexrnzacattack_rrdiscordbridge_DiscordBot");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        webhookClient = builder.build();

        jda = JDABuilder.createDefault(settings.botToken)
                .addEventListeners(new DiscordBot(), new PlayersCommand(), new AboutCommand())
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build()
                .awaitReady();

        setPlayerCount();

        channel.getGuild().updateCommands().addCommands(
                Commands.slash("players", "Shows you every player that is online."),
                Commands.slash("about", "Server Info")
        ).queue();
    }

    /**
     * Stops the bot
     */
    public static void stop() {
        if (webhookClient != null) {
            webhookClient.close();
            webhookClient = null;
        }

        if (channel != null) {
            channel.getJDA().shutdownNow();
        }
    }

    /**
     * Sets the RPC status
     */
    public static void setPlayerCount() {
        Activity activity = Activity.playing(String.format("with %s %s", RRDiscordBridge.getOnlinePlayers().length, RRDiscordBridge.getOnlinePlayers().length != 1 ? "players" : "player"));
        jda.getPresence().setActivity(activity);
    }

    /**
     * Runs when the bot is ready for use
     */
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        channel = event.getJDA().getTextChannelById(settings.channelId);
    }

    /** Runs when someone runs a command in the channel the bot is watching */
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (event.getChannel().getId().equals(settings.channelId) && !event.getInteraction().getMember().getId().equals(event.getJDA().getSelfUser().getId()) && !event.getInteraction().getMember().getId().equals(Long.toString(settings.webhookId))) {
            String author = settings.useDisplayNames ? event.getUser().getGlobalName() : event.getUser().getName();
            Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s ran Discord command \"/%s\".", author, event.getFullCommandName()));
        }
    }

    public static String getName(Member member) {
        String nickname = member.getNickname();

        if (member.getUser().isBot()) {
            return settings.useNicknames && nickname != null && !nickname.isEmpty() ? nickname : member.getUser().getName();
        }


        // explanation:
        // if useNicknames is enabled, and there is a nickname, use the nickname, otherwise, if useDisplayNames is enabled, use their displayname, otherwise use their username.
        return settings.useNicknames && nickname != null && !nickname.isEmpty() ? nickname : settings.useDisplayNames ? member.getUser().getGlobalName() : member.getUser().getName();
    };

    public static String getName(User user) {
        if (user.isBot()) {
            return user.getName();
        }

        // explanation:
        // if useDisplayNames is enabled, use their displayname, otherwise use their username.
        return settings.useDisplayNames ? user.getGlobalName() : user.getName();
    };

    /**
     * Runs when a message is received in the channel the bot is watching
     */
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        // this method kinda sucks
        if (event.getChannel().getId().equals(settings.channelId) && !event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()) && !event.getAuthor().getId().equals(Long.toString(settings.webhookId))) {
            // so basically traits are things about the message, for example, if a message got trimmed for being too long, it will have (T) added to the message inside MC.
            // if a message is both trimmed and has attachments as well as is replying to something, it will look like (2, T, R)
            List<String> traits = new java.util.ArrayList<>(Collections.emptyList());
            Message message = event.getMessage();
            String author;
            if (message.getMember() != null) author = getName(message.getMember());
            else author = getName(message.getAuthor());

            Message replyingTo = null;
            if (message.getMessageReference() != null)
                replyingTo = message.getReferencedMessage();

            String messageTrimmed = "§cMessage is empty or null";

            if (!message.getAttachments().isEmpty()) messageTrimmed = "";

            if (!message.getContentDisplay().isEmpty()) {
                messageTrimmed = message.getContentDisplay().substring(0, Math.min(settings.maxMessageSize, message.getContentDisplay().length()));
            }

            String replyAuthor = "";
            if (replyingTo != null) {
                if (replyingTo.getMember() != null) {
                    replyAuthor = getName(replyingTo.getMember());
                } else {
                    replyAuthor = getName(replyingTo.getAuthor());
                }
            };

            boolean hasAttachment = false;
            int attachmentCount = 0;

            // if the message is some system message we should send the correct thing and return cuz I doubt the message will have more than that
            switch (message.getType()) {
                case GUILD_MEMBER_JOIN:
                    if (settings.enabledDiscordEvents.contains(Settings.DiscordEvents.USER_JOIN))
                        Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s has joined the Discord server.", author));
                    return;
                case GUILD_MEMBER_BOOST:
                    if (settings.enabledDiscordEvents.contains(Settings.DiscordEvents.USER_BOOST))
                        Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s has boosted the Discord server.", author));
                    return;
                case THREAD_CREATED:
                    if (settings.enabledDiscordEvents.contains(Settings.DiscordEvents.THREAD_CREATION))
                        Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s has created the thread \"%s\".", author, messageTrimmed));
                    return;
                case CHANNEL_PINNED_ADD:
                    if (settings.enabledDiscordEvents.contains(Settings.DiscordEvents.MESSAGE_PIN))
                        Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s has pinned a message to the channel.", author));
                    return;
                case POLL_RESULT:
                    if (!settings.enabledDiscordEvents.contains(Settings.DiscordEvents.POLL_ENDED))
                        return;

                    // for some reason we can't get the reference directly but it does give us the ids so we have to use that... bruh
                    String channelId = message.getMessageReference().getChannelId();
                    TextChannel pollChannel = message.getGuild().getTextChannelById(channelId);

                    // the actual poll message
                    Message poll = pollChannel.retrieveMessageById(message.getMessageReference().getMessageId()).complete();
                    Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s's poll \"%s\" has ended.\nResults:", author, poll.getPoll().getQuestion().getText()));
                    poll.getPoll().getAnswers().forEach(answer -> {
                        Bukkit.getServer().broadcastMessage(String.format("§3%s: §b%s", answer.getText(), answer.getVotes()));
                    });
                    return;
                case CONTEXT_COMMAND:
                    if (!settings.enabledDiscordEvents.contains(Settings.DiscordEvents.USER_APP))
                        return;

                    if (message.getInteraction() == null) return;

                    // those weird activity messages and user crapps
                    Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s used app \"%s\".", message.getInteraction().getUser().getGlobalName(), message.getAuthor().getName()));
                    return;
            }

            // message forwarding
            if (message.getMessageReference() != null && message.getMessageReference().getType() == MessageReference.MessageReferenceType.FORWARD) {
                if (!settings.enabledDiscordEvents.contains(Settings.DiscordEvents.FORWARDED_MESSAGE))
                    return;

                Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s forwarded a message.", author));
                return;
            }

            // polls
            if (message.getPoll() != null) {
                if (!settings.enabledDiscordEvents.contains(Settings.DiscordEvents.POLL_CREATION))
                    return;

                Bukkit.getServer().broadcastMessage(String.format("§d[Discord] §e%s has created a poll \"%s\".", author, message.getPoll().getQuestion().getText()));
                return;
            }

            if (!settings.enabledDiscordEvents.contains(Settings.DiscordEvents.USER_MESSAGE))
                return;

            // check for attachments
            if (!event.getMessage().getAttachments().isEmpty() || !event.getMessage().getStickers().isEmpty()) {
                hasAttachment = true;
                attachmentCount = event.getMessage().getAttachments().size() + event.getMessage().getStickers().size();
                traits.add(Integer.toString(attachmentCount));
            }

            // add trait if message is too long
            if (event.getMessage().getContentDisplay().length() > settings.maxMessageSize) {
                traits.add("T");
                event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDCCF")).queue();
            }

            Bukkit.getServer().broadcastMessage(
                    String.format("§d[Discord]%s%s §e%s§f: %s%s",
                            ((replyingTo != null && !replyAuthor.isEmpty()) ? String.format(" §b(RE: §e%s§b)", replyAuthor) : ""),
                            (!traits.isEmpty()
                                    ? String.format(" §6(%s)", String.join(", ", traits))
                                    : ""),
                            author,
                            messageTrimmed,
                            (messageTrimmed.isEmpty() && hasAttachment ? String.format("§6%s attachment(s)", attachmentCount) : "")
                    )
            );
        }
    }

    /**
     * Sends a message using a webhook that uses the player's name and skin.
     * For bukkit 1.2.5 and lower.
     *
     * @param event PlayerChatEvent which can be obtained from onPlayerChat
     */
    public static void sendPlayerMessageLegacy(PlayerChatEvent event) {
        if (!settings.enabledEvents.contains(Settings.Events.PLAYER_CHAT))
            return;

        // disallows @everyone lol
        AllowedMentions allowedMentions = new AllowedMentions()
                .withParseUsers(true)
                .withParseEveryone(false);

        WebhookMessage message = new WebhookMessageBuilder()
                .setUsername(event.getPlayer().getName())
                .setAvatarUrl(String.format(settings.skinProvider, event.getPlayer().getName()))
                .setContent(event.getMessage())
                .setAllowedMentions(allowedMentions)
                .build();
        webhookClient.send(message);
    }

    /**
     * Sends a message using a webhook that uses the player's name and skin.
     *
     * @param event AsyncPlayerChatEvent which can be obtained from onPlayerChat
     */
    public static void sendPlayerMessage(AsyncPlayerChatEvent event) {
        if (!settings.enabledEvents.contains(Settings.Events.PLAYER_CHAT))
            return;

        // disallows @everyone lol
        AllowedMentions allowedMentions = new AllowedMentions()
                .withParseUsers(true)
                .withParseEveryone(false);

        WebhookMessage message = new WebhookMessageBuilder()
                .setUsername(event.getPlayer().getName())
                .setAvatarUrl(String.format(settings.skinProvider, event.getPlayer().getName()))
                .setContent(event.getMessage())
                .setAllowedMentions(allowedMentions)
                .build();
        webhookClient.send(message);
    }

    /**
     * Sends a message using a webhook that uses the player's name and skin.
     *
     * @param playerName The player name
     * @param message The message
     */
    public static void sendPlayerMessage(String playerName, String message) {
        if (!settings.enabledEvents.contains(Settings.Events.PLAYER_CHAT))
            return;

        AllowedMentions allowedMentions = new AllowedMentions()
                .withParseUsers(true)
                .withParseEveryone(false);

        WebhookMessage wMessage = new WebhookMessageBuilder()
                .setUsername(playerName)
                .setAvatarUrl(String.format(settings.skinProvider, playerName))
                .setContent(message)
                .setAllowedMentions(allowedMentions)
                .build();
        webhookClient.send(wMessage);
    }


    /**
     * Sends a message using a webhook that uses the player's name and skin.
     *
     * @param eventType The event type (used for Settings.enabledEvents).
     * @param playerName The player name
     * @param message The message
     */
    public static void sendPlayerMessage(Settings.Events eventType, String playerName, String message) {
        if (!settings.enabledEvents.contains(eventType))
            return;

        AllowedMentions allowedMentions = new AllowedMentions()
                .withParseUsers(true)
                .withParseEveryone(false);

        WebhookMessage wMessage = new WebhookMessageBuilder()
                .setUsername(playerName)
                .setAvatarUrl(String.format(settings.skinProvider, playerName))
                .setContent(message)
                .setAllowedMentions(allowedMentions)
                .build();
        webhookClient.send(wMessage);
    }

    /**
     * Sends a message using a webhook that uses the player's name and skin.
     *
     * @param playerName The player name
     * @param playerSkinName The name of the player that you want to use as the skin
     * @param message The message
     */
    public static void sendPlayerMessage(String playerName, String playerSkinName, String message) {
        if (!settings.enabledEvents.contains(Settings.Events.PLAYER_CHAT))
            return;

        AllowedMentions allowedMentions = new AllowedMentions()
                .withParseUsers(true)
                .withParseEveryone(false);

        WebhookMessage wMessage = new WebhookMessageBuilder()
                .setUsername(playerName)
                .setAvatarUrl(String.format(settings.skinProvider, playerSkinName))
                .setContent(message)
                .setAllowedMentions(allowedMentions)
                .build();
        webhookClient.send(wMessage);
    }

    /**
     * Sends a message using a webhook that uses the player's name and skin.
     *
     * @param eventType The event type (used for Settings.enabledEvents).
     * @param playerName The player name
     * @param playerSkinName The name of the player that you want to use as the skin
     * @param message The message
     */
    public static void sendPlayerMessage(Settings.Events eventType, String playerName, String playerSkinName, String message) {
        if (!settings.enabledEvents.contains(eventType))
            return;

        AllowedMentions allowedMentions = new AllowedMentions()
                .withParseUsers(true)
                .withParseEveryone(false);

        WebhookMessage wMessage = new WebhookMessageBuilder()
                .setUsername(playerName)
                .setAvatarUrl(String.format(settings.skinProvider, playerSkinName))
                .setContent(message)
                .setAllowedMentions(allowedMentions)
                .build();
        webhookClient.send(wMessage);
    }


    /**
     * Sends an embed that uses the player's info as the author info to the Discord channel
     *
     * @param eventType The event type (used for Settings.enabledEvents).
     * @param playerName The name of the player that you want to be used for the author info.
     * @param description Message in the description part of the embed.
     * @param color Color of the side of the embed.
     * @param title Message in the title part of the embed.
     */
    public static void sendPlayerEvent(Settings.Events eventType, String playerName, String description, Color color, String title) {
        if (!settings.enabledEvents.contains(eventType))
            return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setDescription(description)
                .setTitle(title)
                .setTimestamp(java.time.Instant.now())
                .setAuthor(playerName, null, String.format(settings.skinProvider, playerName));

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Sends an embed that uses the player's skin as the author picture to the Discord channel
     *
     * @param eventType The event type (used for Settings.enabledEvents).
     * @param playerName The name of the player that you want to be used for the author picture.
     * @param authorName The name of the author that you want to be used for the author name.
     * @param description Message in the description part of the embed.
     * @param color Color of the side of the embed.
     * @param title Message in the title part of the embed.
     */
    public static void sendPlayerEvent(Settings.Events eventType, String playerName, String authorName, String description, Color color, String title) {
        if (!settings.enabledEvents.contains(eventType))
            return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setDescription(description)
                .setTitle(title)
                .setTimestamp(java.time.Instant.now())
                .setAuthor(authorName, null, String.format(settings.skinProvider, playerName));

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Sends an embed for an event
     *
     * @param eventType The event type (used for Settings.enabledEvents).
     * @param author The author info.
     * @param description Message in the description part of the embed.
     * @param color Color of the side of the embed.
     * @param title Message in the title part of the embed.
     */
    public static void sendEvent(Settings.Events eventType, MessageEmbed.AuthorInfo author, String description, Color color, String title) {
        if (!settings.enabledEvents.contains(eventType))
            return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setDescription(description)
                .setTitle(title)
                .setTimestamp(java.time.Instant.now())
                .setAuthor(author.getName(), null, author.getIconUrl());

        channel.sendMessageEmbeds(embed.build()).queue();
    }


    /**
     * Sends a message to the channel
     *
     * @param message The message
     */
    public static void sendMessage(String message) {
        if (channel != null) {
            channel.sendMessage(message).queue();
        } else {
            System.err.println("bot isn't ready!");
        }
    }
}
