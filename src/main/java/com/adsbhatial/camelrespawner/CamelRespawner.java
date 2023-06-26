package com.adsbhatial.camelrespawner;

import com.adsbhatial.camelrespawner.Listeners.SpawnListeners;
import com.adsbhatial.camelrespawner.util.LegacyColors;
import com.adsbhatial.camelrespawner.util.MessageUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.spigotmc.SpigotConfig.config;


@SuppressWarnings("unused")
public final class CamelRespawner extends JavaPlugin {
    public final NamespacedKey NAME_KEY = new NamespacedKey(this, "camel");
    public final static Logger logger = Logger.getLogger("Minecraft");
    static String THIS_NAME;
    static String THIS_VERSION;

    public static boolean debug;
    private BukkitAudiences adventure;

    YamlConfiguration oldconfig = new YamlConfiguration();
    String configVersion = "1.0.0";
    String pluginName = THIS_NAME;
    @Override
    public void onEnable() {
        // Plugin startup logic
        long startTime = System.currentTimeMillis();
        System.out.println("CamelRespawner Started");
        loadConfig();
        getServer().getPluginManager().registerEvents(new SpawnListeners(this), this);
        this.adventure = BukkitAudiences.create(this);
        consoleInfo("ENABLED - Loading took " + LoadTime(startTime));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        consoleInfo("DISABLED");
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
    public void loadConfig(){
        debug = getConfig().getBoolean("debug", false);
        oldconfig = new YamlConfiguration();
        THIS_NAME = this.getDescription().getName();
        THIS_VERSION = this.getDescription().getVersion();
        if(!getConfig().getBoolean("console.longpluginname", true)) {
            pluginName = "CR";
        }else {
            pluginName = THIS_NAME;
        }

        try{
            if(!this.getDataFolder().exists()){
                this.getDataFolder().mkdirs();
            }
            File file = new File(this.getDataFolder(), "config.yml");
            //this.getLogger().info("" + file);
            if(!file.exists()){
                saveResource("config.yml", true);
            }
        }catch(Exception exp){
            debug = true;
            exp.printStackTrace();
        }

        try {
            oldconfig.load(new File(getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }

        String checkConfigVersion = oldconfig.getString("version", "1.0.0");
        System.out.println("config version" + checkConfigVersion);
        if(checkConfigVersion != null) {
            if (!checkConfigVersion.equalsIgnoreCase(configVersion)) {
                try {
                    Path fileFrom = Paths.get(getDataFolder() + "" + File.separatorChar + "config.yml");
                    Path fileTo = Paths.get(getDataFolder() + "" + File.separatorChar + "old_config.yml");
                    CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES
                    };
                    Files.copy(fileFrom, fileTo,options);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveResource("config.yml", true);

                try {
                    config.load(new File(getDataFolder(), "config.yml"));
                } catch (IOException | InvalidConfigurationException e1) {
                    //logWarn("Could not load config.yml");
                    e1.printStackTrace();
                }
                try {
                    oldconfig.load(new File(getDataFolder(), "old_config.yml"));
                } catch (IOException | InvalidConfigurationException e1) {
                    e1.printStackTrace();
                }
                config.set("debug", oldconfig.get("debug", false));
                config.set("version", oldconfig.get("version", "1.0.0"));
                config.set("cat_to_camel_chance", oldconfig.get("cat_to_camel_chance", 0.75));
                config.set("camels.check_camels", oldconfig.get("camels.check_camels", true));
                config.set("camels.max_camels_nearby", oldconfig.get("camels.check_camels", 1));
                config.set("camels.search_radius", oldconfig.get("camels.search_radius", 20));
                try {
                    config.save(new File(getDataFolder(), "config.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String LoadTime(long startTime) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
        long milliseconds = elapsedTime % 1000;

        if (minutes > 0) {
            return String.format("%d min %d s %d ms.", minutes, seconds, milliseconds);
        } else if (seconds > 0) {
            return String.format("%d s %d ms.", seconds, milliseconds);
        } else {
            return String.format("%d ms.", elapsedTime);
        }
    }


    public  void log(String logMessage){
        MessageUtil.Builder(this)
                .setConfigColor(LegacyColors.YELLOW)
                .mmText(logMessage)
                .addPrefix(true)
                .toConsole(true)
                .send();
    }
    public  void logDebug(String logMessage){
        //log(" " + THIS_VERSION + ChatColor.RED + ChatColor.BOLD + " [DEBUG] " + ChatColor.RESET + logMessage);
        MessageUtil.Builder(this)
                .setConfigColor(LegacyColors.RED)
                .mmText("[DEBUG] " + logMessage)
                .addPrefix(true)
                .toConsole(true)
                .send();

    }
    public void logWarn(String logMessage){
        //log(" " + THIS_VERSION + ChatColor.RED + ChatColor.BOLD + " [WARNING] " + ChatColor.RESET + logMessage);
        MessageUtil.Builder(this)
                .setConfigColor(LegacyColors.RED)
                .mmText("[WARNING] " + logMessage)
                .addPrefix(true)
                .toConsole(true)
                .send();

    }
    public	void log(Level level, String dalog){
        //logger.log(level, ChatColor.YELLOW + "" + dalog );
    }

    public void consoleInfo(String state) {
        //logger.info(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
        //logger.info(ChatColor.GREEN + THIS_NAME + " v" + THIS_VERSION + ChatColor.RESET + " is " + state);
        //logger.info(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
        String logMessage = THIS_NAME + " v" + THIS_VERSION + " is " + state;
        String pattern = StringUtils.repeat("*",logMessage.length());
        MessageUtil.Builder(this)
                .setConfigColor(LegacyColors.YELLOW)
                .mmText(pattern)
                .addPrefix(true)
                .toConsole(true)
                .send();

        MessageUtil.Builder(this)
                .setConfigColor(LegacyColors.GREEN)
                .mmText(logMessage)
                .addPrefix(true)
                .toConsole(true)
                .send();

        MessageUtil.Builder(this)
                .setConfigColor(LegacyColors.YELLOW)
                .mmText(pattern)
                .addPrefix(true)
                .toConsole(true)
                .send();
    }
}
