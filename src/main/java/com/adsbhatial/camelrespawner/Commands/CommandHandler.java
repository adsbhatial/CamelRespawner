package com.adsbhatial.camelrespawner.Commands;

import com.adsbhatial.camelrespawner.CamelRespawner;
import com.adsbhatial.camelrespawner.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandHandler implements CommandExecutor {
    public static final String CHAT_KEYWORD = "camelrespawner";

    public static final String VIEW_VERSION = "camelrespawner.viewversions";
    public static final String RELOAD_CONFIG = "camelrespawner.reloadConfig";
    public static final String VIEW_CONFIG = "camelrespawner.viewconfig";

    private final CamelRespawner camelRespawner;

    /**
     * Handles commands sent by players
     * @param sender the player who sent command
     * @param command  the command that was sent
     * @param label the command alias that was used
     * @param args any arguments that followed the command
     * @return whether the command was handled
     */
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(CHAT_KEYWORD)) {
            return false;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                sendDocs(sender);
                break;
            case "reloadconfig":
                if (hasPerm(sender,RELOAD_CONFIG)){
                    camelRespawner.reloadConfig();
                    MessageUtil.Builder(camelRespawner).mmText("config-reloaded").to(sender).send();
                }else{
                    sendNoPerm(sender);
                }
                break;
            case "viewconfig":
                if (hasPerm(sender,VIEW_CONFIG)){
                    sendConfig(sender);
                }else {
                    sendNoPerm(sender);
                }
                break;
            case "version":
            case "v":
                if (hasPerm(sender,VIEW_VERSION)){
                    sendVersion(sender);
                }else{
                    sendNoPerm(sender);
                }
                break;
            case "commands":
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    public CommandHandler(CamelRespawner plugin){
        camelRespawner = plugin;
    }

    public void sendHelp(CommandSender player){
        MessageUtil.Builder(camelRespawner)
                .addPrefix(false)
                .mmText(
                        getMessage("camelrespawner-help-command"),
                        "header", getMessage("camelrespawner-command-header"))
                .to(player)
                .toConsole(false)
                .send();
    }

    public void sendDocs(CommandSender player){
        MessageUtil.Builder(camelRespawner)
                .addPrefix(false)
                .mmText(
                        getMessage("camelrespawner-docs-command"),
                        "header", getMessage("camelrespawner-command-header"))
                .to(player)
                .toConsole(false)
                .send();
    }

    public void sendVersion(CommandSender player){
        MessageUtil builder = MessageUtil.Builder(camelRespawner)
                .addPrefix(false)
                .mmText(
                        getMessage("camelrespawner-version-command"),
                        "header", getMessage("camelrespawner-command-header"),
                        "plugin-version",camelRespawner.getDescription().getVersion(),
                        "java-version", System.getProperty("java.version"),
                        "server-software", Bukkit.getName(),
                        "server-version", Bukkit.getVersion()
                );

        builder.to(player)
                .toConsole(false)
                .send();
    }

    public void sendConfig(CommandSender player){
        MessageUtil builder = MessageUtil.Builder(camelRespawner)
                .addPrefix(false)
                .mmText(
                        getMessage("camelrespawner-viewconfig-command"),
                        "header", getMessage("camelrespawner-command-header"),
                        "cat_to_camel_chance",camelRespawner.getConfig().getString("cat_to_camel_chance", "0.75"),
                        "camels_check_camels", camelRespawner.getConfig().getString("camels.check_camels", "true"),
                        "camels_search_radius", camelRespawner.getConfig().getString("camels.search_radius", "20"),
                        "camels_max_camels_nearby", camelRespawner.getConfig().getString("camels.max_camels_nearby", "1")
                );

        builder.to(player)
                .toConsole(false)
                .send();
    }

    public void sendNoPerm(CommandSender player){
        MessageUtil.Builder(camelRespawner).mmText(getMessage("no-perm")).to(player).toConsole(false).send();
    }

    public boolean hasPerm(CommandSender player, String permission) {
        return player.hasPermission(permission);
    }

    public String getMessage(String key){
        String text = camelRespawner.messages.getString(key);
        if (text == null || text.isEmpty()){
            return key;
        }else {
            return  text;
        }
    }
}
