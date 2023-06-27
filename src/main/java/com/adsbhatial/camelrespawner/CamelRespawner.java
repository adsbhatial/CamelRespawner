package com.adsbhatial.camelrespawner;

import com.adsbhatial.camelrespawner.Commands.CommandHandler;
import com.adsbhatial.camelrespawner.Commands.CommandOnTabComplete;
import com.adsbhatial.camelrespawner.Listeners.SpawnListeners;
import com.adsbhatial.camelrespawner.util.LegacyColors;
import com.adsbhatial.camelrespawner.util.MessageUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.nio.file.StandardCopyOption;
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

    YamlConfiguration oldConfig = new YamlConfiguration();
    public YamlConfiguration messages = new YamlConfiguration();
    String configVersion = "1.0.0";
    String pluginName = THIS_NAME;
    @Override
    public void onEnable() {
        // Plugin startup logic
        long startTime = System.currentTimeMillis();
        loadConfigs();
        getServer().getPluginManager().registerEvents(new SpawnListeners(this), this);
        getCommand("camelrespawner").setExecutor(new CommandHandler(this));
        getCommand("camelrespawner").setTabCompleter(new CommandOnTabComplete());
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

    public @NotNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public void loadConfigs(){
        this.loadConfig();
        this.loadMessages();
    }

    public void loadMessages(){
        messages = new YamlConfiguration();
        try{
            if(!this.getDataFolder().exists()){
                this.getDataFolder().mkdirs();
            }
            File file = new File(this.getDataFolder(), "messages.yml");
            //this.getLogger().info("" + file);
            if(!file.exists()){
                saveResource("messages.yml", true);
            }
        }catch(Exception exp){
            debug = true;
            exp.printStackTrace();
        }

        try {
            messages.load(new File(getDataFolder(), "messages.yml"));
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }
    }
    public void loadConfig(){
        debug = getConfig().getBoolean("debug", false);
        oldConfig = new YamlConfiguration();
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
            oldConfig.load(new File(getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }

        String checkConfigVersion = oldConfig.getString("version", "1.0.0");
        if (!checkConfigVersion.equalsIgnoreCase(configVersion)) {
            try {
                Path fileFrom = Paths.get(getDataFolder().getPath() + File.separatorChar + "config.yml");
                Path fileTo = Paths.get(getDataFolder().getPath() + File.separatorChar + "old_config.yml");
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
                oldConfig.load(new File(getDataFolder(), "old_config.yml"));
            } catch (IOException | InvalidConfigurationException e1) {
                e1.printStackTrace();
            }
            config.set("debug", oldConfig.get("debug", false));
            config.set("version", oldConfig.get("version", "1.0.0"));
            config.set("cat_to_camel_chance", oldConfig.get("cat_to_camel_chance", 0.75));
            config.set("camels.check_camels", oldConfig.get("camels.check_camels", true));
            config.set("camels.max_camels_nearby", oldConfig.get("camels.check_camels", 1));
            config.set("camels.search_radius", oldConfig.get("camels.search_radius", 20));
            try {
                config.save(new File(getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.loadMessages();
        saveDefaultConfig();
        config = (YamlConfiguration) getConfig();
        config.options().copyDefaults(true);
        saveConfig();
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
