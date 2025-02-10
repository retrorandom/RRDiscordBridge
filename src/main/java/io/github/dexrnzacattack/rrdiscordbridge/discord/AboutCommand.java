package io.github.dexrnzacattack.rrdiscordbridge.discord;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.ReflectionHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.logger;

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
        if (!event.getName().equals("about"))
            return;

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("About");

        if (ReflectionHelper.isServerIconSupported && !Bukkit.getIp().isEmpty() && RRDiscordBridge.settings.showServerIcon)
            builder.setThumbnail(String.format(RRDiscordBridge.settings.serverIconProvider, Bukkit.getIp(), Bukkit.getPort()));

        if (ReflectionHelper.isServerNameSupported)
            builder.addField("Name", Bukkit.getServer().getServerName(), true);

        // doesn't work in 1.1-
        if (ReflectionHelper.isMotdSupported)
            builder.addField("MOTD", Bukkit.getServer().getMotd(), true);

        builder.addField("Version", Bukkit.getServer().getVersion(), true);

        if (RRDiscordBridge.settings.publicOperatorNames) {
            if (ReflectionHelper.isServerOperatorsSupported) {
                Set<OfflinePlayer> ops = Bukkit.getServer().getOperators();
                builder.addField("Operators", (!ops.isEmpty()
                        ? " - " + ops.stream()
                        .map(OfflinePlayer::getName)
                        .collect(Collectors.joining("\n - "))
                        : "No operators"), false);
            } else {
                String dir = new File(".").getAbsolutePath();
                Path opsTxt = Paths.get(dir, "ops.txt");
                List<String> ops = null;
                try {
                    if (opsTxt.toFile().exists()) {
                        ops = Files.readAllLines(opsTxt.toAbsolutePath());
                        builder.addField("Operators", (!ops.isEmpty()
                                ? " - " + String.join("\n - ", ops)
                                : "No operators"), false);
                    }

                } catch (IOException e) {
                    logger.log(Level.WARNING, String.format("Couldn't get the OPs list at %s", opsTxt.toAbsolutePath()), e);
                }
            }
        }

        builder.addField("Uptime", getUptime(), false);

        builder.setFooter(String.format("RRDiscordBridge v%s running on %s", RRDiscordBridge.version, Bukkit.getName()));

        MessageEmbed embed = builder.build();
        event.replyEmbeds(embed).queue();
    }
}
