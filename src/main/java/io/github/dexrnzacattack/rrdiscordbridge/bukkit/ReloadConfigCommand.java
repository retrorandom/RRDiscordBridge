package io.github.dexrnzacattack.rrdiscordbridge.bukkit;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class ReloadConfigCommand implements CommandExecutor  {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            DiscordBot.stop();
            RRDiscordBridge.settings = new Settings().loadConfig();
            DiscordBot.start();
        } catch (IOException e) {
            sender.sendMessage(String.format("Failed to reload the config: %s", e.getMessage()));
            return false;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage("RRDiscordBridge config reloaded.");
        return true;
    }
}
