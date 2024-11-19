package io.github.dexrnzacattack.rrdiscordbridge.bukkit;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import io.github.dexrnzacattack.rrdiscordbridge.Settings;
import io.github.dexrnzacattack.rrdiscordbridge.discord.DiscordBot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FancyBroadcastCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getServer().broadcastMessage(String.format("§b[Server] %s", args[0]));
        DiscordBot.sendPlayerEvent(Settings.Events.FANCY_BROADCAST, RRDiscordBridge.settings.broadcastSkinName, "Server Broadcast", null, null, args[0]);
        return true;
    }
}
