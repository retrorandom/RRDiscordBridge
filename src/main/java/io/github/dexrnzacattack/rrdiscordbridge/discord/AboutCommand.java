package io.github.dexrnzacattack.rrdiscordbridge.discord;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.stream.Collectors;

public class AboutCommand extends ListenerAdapter {

    public String getUptime() {
        long elapsedMillis = System.currentTimeMillis() - RRDiscordBridge.serverStartTime;

        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / (1000 * 60)) % 60;
        long hours = (elapsedMillis / (1000 * 60 * 60));

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("about")) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("About")
                    .addField("Name", Bukkit.getServer().getServerName(), true)
                    .addField("MOTD", Bukkit.getServer().getMotd(), true)
                    .addField("Version", Bukkit.getServer().getVersion(), true);

            if (RRDiscordBridge.settings.publicOperatorNames)
                builder.addField("Operators", (!Bukkit.getServer().getOperators().isEmpty()
                        ? " - " + Bukkit.getServer().getOperators().stream()
                        .map(OfflinePlayer::getName)
                        .collect(Collectors.joining("\n - "))
                        : "No operators"), false);

            builder.addField("Uptime", getUptime(), false);

            builder.setFooter(String.format("RRDiscordBridge v%s", RRDiscordBridge.version));

            MessageEmbed embed = builder.build();
            event.replyEmbeds(embed).queue();
        }
    }
}
