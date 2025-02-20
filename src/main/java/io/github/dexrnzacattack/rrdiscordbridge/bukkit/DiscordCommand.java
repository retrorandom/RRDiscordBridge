package io.github.dexrnzacattack.rrdiscordbridge.bukkit;

import io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!RRDiscordBridge.settings.discordInvite.isEmpty())
                player.sendMessage(String.format("Join us on discord at §n%s", RRDiscordBridge.settings.discordInvite));
            else
                player.sendMessage("§cDiscord invite link is not set.");
        }
        return true;
    }
}
