package io.github.dexrnzacattack.rrdiscordbridge.bukkit;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.chat.extensions.ChatExtensions;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand implements CommandExecutor  {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            DiscordBot.stop();
            RRDiscordBridge.settings = new Settings().loadConfig();
            RRDiscordBridge.extensions = new ChatExtensions();
            DiscordBot.start();
        } catch (Exception e) {
            sender.sendMessage(String.format("Failed to reload the config: %s", e.getMessage()));
            return false;
        }
        sender.sendMessage("§aRRDiscordBridge config reloaded.");
        return true;
    }
}
