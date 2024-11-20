package io.github.dexrnzacattack.rrdiscordbridge.chat.extensions;

import club.minnced.discord.webhook.send.*;
import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.chat.extensions.waypoints.JVMapWaypoints;
import io.github.dexrnzacattack.rrdiscordbridge.chat.extensions.waypoints.Waypoint;
import io.github.dexrnzacattack.rrdiscordbridge.chat.extensions.waypoints.XaerosWaypoints;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.settings;
import static io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot.webhookClient;

public class ChatExtensions {
    // TODO: actual api n stuff for this, not sure how this would work yet.

    /**
     * Tries to parse the message to check if any extensions support it.
     * Returns the modified string, whether to send to mc, whether to send to discord.
     */
    public static Object[] tryParse(String str, String playerName) {
        if (!RRDiscordBridge.settings.useChatExtensions)
            return new Object[]{str, true, true};

        if (RRDiscordBridge.settings.enabledChatExtensions.contains(Settings.ChatExtensions.WAYPOINTS) && (XaerosWaypoints.isWaypoint(str) || (str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']'))) {
            boolean didEmbed = waypointEmbed(str, playerName);
            return new Object[]{str, true, !didEmbed};
        }

        return new Object[]{str, true, true};
    }

    public static boolean waypointEmbed(String str, String playerName) {
        try {
            Waypoint waypoint;
            String type = "a ";

            AllowedMentions allowedMentions = new AllowedMentions()
                    .withParseUsers(true)
                    .withParseEveryone(false);

            WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

            if (XaerosWaypoints.isWaypoint(str)) {
                XaerosWaypoints xWaypoint = XaerosWaypoints.fromString(str);
                waypoint = new Waypoint(xWaypoint.name, xWaypoint.x, xWaypoint.y, xWaypoint.z, xWaypoint.color, xWaypoint.group);
                // ehhhhhhhh weird way of doing this but it works
                type = "an Xaero's Minimap ";
            } else if ((str.charAt(0) == '[' && str.charAt(str.length() - 1) == ']')) {
                JVMapWaypoints jWaypoint = JVMapWaypoints.fromString(str);
                waypoint = new Waypoint(jWaypoint.name, Integer.toString(jWaypoint.x), Integer.toString(jWaypoint.y), Integer.toString(jWaypoint.z), null, jWaypoint.dim);
                type = "a JourneyMap/VoxelMap ";
            } else {
                throw new IllegalArgumentException("Not a waypoint!");
            }

            embedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(String.format("%s shared %swaypoint", playerName, type), null, null));
            if (waypoint.color != null)
                embedBuilder.setColor(waypoint.color.getRGB());
            embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(waypoint.name, null));
            embedBuilder.addField(new WebhookEmbed.EmbedField(true, "X", waypoint.x));
            embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Y", waypoint.y));
            embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Z", waypoint.z));
            embedBuilder.setFooter(new WebhookEmbed.EmbedFooter(waypoint.dimension, null));

            WebhookEmbed embed = embedBuilder.build();

            WebhookMessage wMessage = new WebhookMessageBuilder()
                    .setUsername(playerName)
                    .setAvatarUrl(String.format(settings.skinProvider, playerName))
                    .addEmbeds(embed)
                    .setAllowedMentions(allowedMentions)
                    .build();


            webhookClient.send(wMessage);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
