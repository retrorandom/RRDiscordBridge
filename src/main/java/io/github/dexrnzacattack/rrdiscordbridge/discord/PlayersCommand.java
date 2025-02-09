package io.github.dexrnzacattack.rrdiscordbridge.discord;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Sends info about how many players are online and their names.
 */
public class PlayersCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("players")) {
            Player[] players = RRDiscordBridge.getOnlinePlayers();
            StringBuilder playerList = new StringBuilder();

            EmbedBuilder builder = new EmbedBuilder().setTitle(String.format("%s/%s player(s) online", RRDiscordBridge.getOnlinePlayers().length, Bukkit.getServer().getMaxPlayers()));

                if (RRDiscordBridge.settings.publicPlayerNames) {
                    Arrays.stream(players).forEach(player -> {
                        playerList.append(String.format(" - %s%s%s\n", player.isOp() && RRDiscordBridge.settings.publicOperatorNames ? "**[OP] " : "", player.getName(), player.isOp() && RRDiscordBridge.settings.publicOperatorNames ? "**" : ""));
                    });

                    builder.setDescription(playerList);
                }

            MessageEmbed embed = builder.build();
            event.replyEmbeds(embed).queue();
        }
    }
}
