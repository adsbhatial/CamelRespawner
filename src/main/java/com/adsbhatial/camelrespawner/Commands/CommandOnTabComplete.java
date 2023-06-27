package com.adsbhatial.camelrespawner.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandOnTabComplete implements TabCompleter {

    /**
     * Command tab completer
     *
     * @param player Player who sent command
     * @param cmd    Command that was sent
     * @param label  Command alias that was used
     * @param args   Arguments that followed command
     * @return List<String> of valid command tab options
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender player, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("camelrespawner")) {
            if (args.length == 1) {
                List<String> commandList = new ArrayList<>();
                commandList.add("help");
                commandList.add("commands");
                if (player.hasPermission("camelrespawner.viewversions")) commandList.add("version");
                if (player.hasPermission("camelrespawner.reloadConfig")) commandList.add("reloadconfig");
                if (player.hasPermission("camelrespawner.viewconfig")) commandList.add("viewconfig");
                return commandList;
            }
        }
        return null;
    }

}
