package io.github.dexrnzacattack.rrdiscordbridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Settings {
    public static final String CONFIG_PATH = "plugins/RRDiscordBridge/config.json";
    /** For the JSON file */
    public final String SETTINGS_VERSION = RRDiscordBridge.version;

    /** The bot token */
    public String botToken;
    /** The channel ID for the bot to listen and send messages in for the relay */
    public String channelId;
    /** The token of the webhook that is in the same channel as the channel ID links to. */
    public String webhookToken;
    /** The ID of the webhook that is in the same channel as the channel ID links to. */
    public long webhookId;
    /** The invite link to the relay's discord server (must be filled manually) */
    public String discordInvite;
    /** Use display names instead of discord usernames when relaying Discord messages to MC */
    public boolean useDisplayNames;
    /** Use nicknames instead of discord usernames/display names when relaying Discord messages to MC */
    public boolean useNicknames;
    /** Maximum message size that will be relayed to the MC chat */
    public int maxMessageSize;
    /** Allow players names to be sent when the /players command is used in Discord */
    public boolean publicPlayerNames;
    /**
     * Allow operators to be highlighted when the /players command is used in Discord
     * Also shown when /about is ran.
     */
    public boolean publicOperatorNames;
    /** Changes what URL the skin images are grabbed from */
    public String skinProvider;
    /** Changes what URL is used to grab the server's icon */
    public String serverIconProvider;
    /** Show the server icon when /about is used in Discord */
    public boolean showServerIcon;
    /** Skin to use when /say or /dcbroadcast is used. */
    public String broadcastSkinName;
    /** Use chat extensions */
    public boolean useChatExtensions;
    /** Optional extensions for things like embeds for waypoints */
    public List<ChatExtensions> enabledChatExtensions;
    /** Events that the bot will send to the relay channel */
    public List<Events> enabledEvents;
    /** Events that the bot will relay from the relay channel */
    public List<DiscordEvents> enabledDiscordEvents;

    /**
     * Everytime one of these events happen a message is sent in the relay channel.
     * Events can be disabled with the enabledEvents array
     */
    public enum Events {
        /** Sends an event message on player join */
        PLAYER_JOIN,
        /** Sends an event message on player leave */
        PLAYER_LEAVE,
        /** Sends an event message on player kick */
        PLAYER_KICK,
        /** Sends an event message on player death */
        PLAYER_DEATH,
        /** Sends a message on player chat */
        PLAYER_CHAT,
        /** Sends an event message on server start */
        SERVER_START,
        /** Sends an event message on server stop */
        SERVER_STOP,
        /** When /say is used */
        SAY_BROADCAST,
        /** When /dcbroadcast is used */
        FANCY_BROADCAST,
        /** When /me is used */
        ME_COMMAND,
        /** Sends an event message when other events happen */
        GENERIC_OTHER
    }

    public enum DiscordEvents {
        /** When a user sends a message to the channel */
        USER_MESSAGE,
        /** When a user joins the server (only works if the watched channel is also the system messages channel) */
        USER_JOIN,
        /** When a user boosts the server (only works if the watched channel is also the system messages channel) */
        USER_BOOST,
        /** When a user creates a thread in the channel */
        THREAD_CREATION,
        /** When a message in the channel is pinned */
        MESSAGE_PIN,
        /** When a poll is created in the channel */
        POLL_CREATION,
        /** When a poll in the channel ends */
        POLL_ENDED,
        /** When a bot command is used in the channel */
        SLASH_COMMAND,
        /**
         * When a user app is used in the channel
         * Activities are also considered user apps.
         */
        USER_APP,
        /** When a message is forwarded to the channel */
        FORWARDED_MESSAGE,
    }

    public enum ChatExtensions {
        WAYPOINTS
    }

    /** Settings constructor, uses default values until Settings.loadConfig() is called. */
    public Settings() {
        useDisplayNames = true;
        useNicknames = false;
        enabledEvents = Arrays.asList(Events.values());
        enabledDiscordEvents = Arrays.asList(DiscordEvents.values());
        maxMessageSize = 300;
        channelId = "";
        webhookId = 0;
        webhookToken = "";
        botToken = "";
        publicPlayerNames = true;
        publicOperatorNames = true;
        discordInvite = "";
        skinProvider = "https://mc-heads.net/avatar/%s.png";
        serverIconProvider = "https://api.mcsrvstat.us/icon/%s:%s";
        broadcastSkinName = "CONSOLE";
        useChatExtensions = true;
        enabledChatExtensions = Arrays.asList(ChatExtensions.values());
        showServerIcon = true;
    }

    public Settings loadConfig() throws IOException {
        Gson gson = new Gson();
        Settings settings;

        File configFile = new File(CONFIG_PATH);

        if (!configFile.exists())
            createConfig();

        try (FileReader reader = new FileReader(CONFIG_PATH)) {
            settings = gson.fromJson(reader, Settings.class);
        } catch (IOException e) {
            System.err.println("Exception while reading the config file: " + e.getMessage());
            throw e;
        }

        settings.writeConfig();
        return settings;
    }

    public void writeConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(this));
        } catch (IOException e) {
            System.err.println("Exception while writing the config: " + e.getMessage());
        }
    }

    public void createConfig() {
        File configFile = new File(CONFIG_PATH);
        File parent = configFile.getParentFile();

        if (parent != null && !parent.exists())
            parent.mkdirs();

        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    writeConfig();
                }
            } catch (IOException e) {
                System.err.println("Exception while creating the config file: " + e.getMessage());
            }
        }
    }
}
