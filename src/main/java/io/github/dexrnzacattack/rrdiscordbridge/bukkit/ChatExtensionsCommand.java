package io.github.dexrnzacattack.rrdiscordbridge.bukkit;

import io.github.dexrnzacattack.rrdiscordbridge.chat.extensions.IChatExtension;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static io.github.dexrnzacattack.rrdiscordbridge.RRDiscordBridge.extensions;

public class ChatExtensionsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings == null || strings.length < 1) {
            commandSender.sendMessage("§cNo subcommand given, valid options are:");
            commandSender.sendMessage("§eList§b - §fLists all chat extensions");
            commandSender.sendMessage("§eEnable <ext>§b - §fEnables a chat extension");
            commandSender.sendMessage("§eDisable <ext>§b - §fDisables a chat extension");
            commandSender.sendMessage("§eHelp§b - §fShows all subcommands");
            return true;
        }

        String commandName = strings[0];

        switch (commandName.toLowerCase()) {
            case "help":
                commandSender.sendMessage("§eList§b - §fLists all chat extensions");
                commandSender.sendMessage("§eEnable <ext>§b - §fEnables a chat extension");
                commandSender.sendMessage("§eDisable <ext>§b - §fDisables a chat extension");
                commandSender.sendMessage("§eHelp§b - §fShows this message");
                break;
            case "info":
                String iExtName = strings[1];

                if (iExtName == null) {
                    commandSender.sendMessage("§cNo extension name was provided.");
                    return true;
                }

                IChatExtension iExt = extensions.getExtension(iExtName);

                if (iExt == null) {
                    commandSender.sendMessage(String.format("§cUnknown extension '%s'", commandName));
                    return true;
                }

                commandSender.sendMessage(String.format("%s%s§b - §r%s", extensions.isEnabled(iExt) ? "§a" : "§c", iExt.getName(), iExt.getDescription()));
                break;
            case "list":
                commandSender.sendMessage(String.format("§bChat Extensions (%s enabled, %s total)", extensions.enabledExtensions.size(), extensions.extensions.size()));
                // print enabled first
                for (IChatExtension ext : extensions.enabledExtensions) {
                    commandSender.sendMessage("§a" + ext.getName());
                }

                for (IChatExtension ext : extensions.extensions) {
                    if (!extensions.enabledExtensions.contains(ext))
                        commandSender.sendMessage("§c" + ext.getName());
                }
                break;
            case "disable":
                String dExtName = strings[1];

                if (dExtName == null) {
                    commandSender.sendMessage("§cNo extension name was provided.");
                    return true;
                }

                IChatExtension dExt = extensions.getExtension(dExtName);

                if (dExt == null) {
                    commandSender.sendMessage(String.format("§cUnknown extension '%s'", commandName));
                    return true;
                }

                extensions.disable(extensions.getExtension(dExtName));
                commandSender.sendMessage(String.format("§bDisabled extension '%s'", dExt.getName()));
                break;
            case "enable":
                String extName = strings[1];

                if (extName == null) {
                    commandSender.sendMessage("§cNo extension name was provided.");
                    return true;
                }

                IChatExtension ext = extensions.getExtension(extName);

                if (ext == null) {
                    commandSender.sendMessage(String.format("§cUnknown extension '%s'", commandName));
                    return true;
                }

                extensions.enable(extensions.getExtension(extName));
                commandSender.sendMessage(String.format("§bEnabled extension '%s'", ext.getName()));
                break;
            default:
                commandSender.sendMessage(String.format("§cUnknown subcommand '%s'", commandName));
        }
        return true;
    }
}
