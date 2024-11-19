package io.github.dexrnzacattack.rrdiscordbridge.discord;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Sends info about how many players are online and their names.
 */
public class PlayersCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("players")) {
            Player[] players = Bukkit.getServer().getOnlinePlayers();
            StringBuilder playerList = new StringBuilder();

            EmbedBuilder builder = new EmbedBuilder().setTitle(String.format("%s player(s) online", Bukkit.getServer().getOnlinePlayers().length));

                if (RRDiscordBridge.settings.publicPlayerNames) {
                    if (players.length == 0)
                        playerList.append("No players online.");

                    Arrays.stream(players).forEach(player -> {
                        playerList.append(String.format(" - %s%s\n", player.isOp() && RRDiscordBridge.settings.publicOperatorNames ? "[OP]" : "", player.getName()));
                    });

                    builder.setDescription(playerList);
                }

            MessageEmbed embed = builder.build();
            event.replyEmbeds(embed).queue();
        }
    }
}
