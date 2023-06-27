package com.adsbhatial.camelrespawner.util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.adsbhatial.camelrespawner.CamelRespawner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Builder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {

    private boolean addPrefix = true;
    private List<Component> message = new ArrayList<Component>();
    private Set<CommandSender> recipients = new HashSet<CommandSender>();
    private Boolean sendToConsole = true;
    private String configColor = LegacyColors.DARK_AQUA;
    private NamedTextColor defaultColor = NamedTextColor.DARK_AQUA;
    private CamelRespawner camelRespawner;

    public static MessageUtil Builder(final CamelRespawner plugin) {
        return new MessageUtil(plugin);
    }

    public MessageUtil(final CamelRespawner plugin) {
        camelRespawner = plugin;
    }

    public MessageUtil addPrefix(boolean prefix) {
        addPrefix = prefix;
        return this;
    }

    public MessageUtil text(String text) {

        TextColor color = LegacyComponentSerializer.legacyAmpersand().deserialize(configColor).color();

        if (color == null) {
            color = defaultColor;
        }
        message.add(Component.text(text, color));
        return this;
    }

    public MessageUtil setConfigColor(String color){
        configColor = color;
        return this;
    }
    public MessageUtil emText(String text) {
        message.add(Component.text(text, NamedTextColor.GOLD));
        return this;
    }

    /*
     * Parses & adds MiniMessage formatted text to the message
     * @param input the MiniMessage text
     * @return the calling MessageUtil's instance
     */
    public MessageUtil mmText(String text) {
        TextColor color = LegacyComponentSerializer.legacyAmpersand().deserialize(configColor).color();
        if (color == null) {
            color = defaultColor;
        }
        message.add(MiniMessage.miniMessage().deserialize("<color:" + color.asHexString() + ">" + text));
        return this;
    }

    /*
     * Parses & adds MiniMessage formatted text to the message
     * @param input the MiniMessage text
     * @param placeholders optional MiniMessage placeholders
     * @return the calling MessageUtil's instance
     */
    public MessageUtil mmText(String text, String... placeholders) {
        Builder builder = TagResolver.builder();
        for (int i = 0; i < placeholders.length; i += 2) {
            builder.resolver(Placeholder.parsed(placeholders[i], placeholders[i + 1]));
        }

        TextColor color = LegacyComponentSerializer.legacyAmpersand().deserialize(configColor).color();
        if (color == null) {
            color = defaultColor;
        }
        message.add(MiniMessage.miniMessage().deserialize("<color:" + color.asHexString() + ">" + text, builder.build()));
        return this;
    }

    /*
     * Parses & adds MiniMessage formatted text to the message
     * @param input the MiniMessage text
     * @param templates optional {@code Template}
     * @return the calling MessageUtil's instance
     */
    public MessageUtil mmText(String text, String title, Component content) {
        TextColor color = LegacyComponentSerializer.legacyAmpersand().deserialize(configColor).color();
        if (color == null) {
            color = defaultColor;
        }
        message.add(MiniMessage.miniMessage().deserialize("<color:" + color.asHexString() + ">" + text, TagResolver.resolver(Placeholder.component(title, content))));
        return this;
    }

    public MessageUtil text(Component component) {
        message.add(component);
        return this;
    }

    /*
     * Adds a player to the list of recipients
     * @param player the player to be added to the recipients
     * @return the calling MessageUtil's instance
     */
    public MessageUtil to(CommandSender player) {
        recipients.add(player);
        return this;
    }

    /*
     * Adds a list of players to the list of recipients
     * @param players the list of players to be added to the recipients
     * @return the calling MessageUtil's instance
     */
    public MessageUtil to(List<CommandSender> players) {
        for (CommandSender player : players) {
            recipients.add(player);
        }
        return this;
    }


    /*
     * Set whether or not if the message should be sent to console
     * @param value boolean
     * @return the calling MessageUtil's instance
     */
    public MessageUtil toConsole(boolean value) {
        sendToConsole = value;
        return this;
    }

    /*
     * Adds all online players to the list of recipients
     * @return the calling MessageUtil's instance
     */
    public MessageUtil all() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            recipients.add(p);
        }
        return this;
    }

    /*
     * Sends the message to the recipients
     */
    public void send() {
        JoinConfiguration seperator = JoinConfiguration.separator(Component.text(" "));
        Component builtComponent = Component.join(seperator, message);
        if (addPrefix) {
            builtComponent = prefixMessage(builtComponent);
        }

        if (sendToConsole) recipients.add(Bukkit.getConsoleSender());

        for (CommandSender player : recipients) {
            if (player == null) {
                continue;
            }
            camelRespawner.adventure().sender(player).sendMessage(builtComponent);
        }
    }


    /**
     * Prefixes the specified message with the plugin name
     * @param message the message to prefix
     * @return the prefixed message
     */
    private static Component prefixMessage(Component message) {

        return Component.text(translateMessageColors(LegacyColors.YELLOW +"[CR] ")).append(message);
    }

    /**
     * Translates the color codes in the specified message to the type used internally
     * @param message the message to translate
     * @return the translated message
     */
    public static String translateMessageColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}