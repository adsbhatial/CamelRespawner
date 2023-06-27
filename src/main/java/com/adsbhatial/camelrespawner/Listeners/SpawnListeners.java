package com.adsbhatial.camelrespawner.Listeners;

import com.adsbhatial.camelrespawner.CamelRespawner;
import com.adsbhatial.camelrespawner.util.CamelRespawnerLib;
import com.adsbhatial.camelrespawner.util.LegacyColors;
import com.adsbhatial.camelrespawner.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpawnListeners implements Listener {
    CamelRespawner camelRespawner;
    String THIS_VERSION;
    String THIS_NAME;
    String pluginName;
    boolean debug;
    public final static Logger logger = Bukkit.getLogger();
    public SpawnListeners(final CamelRespawner plugin){
        camelRespawner = plugin;
        debug          = plugin.debug;
        THIS_NAME      = plugin.getDescription().getName();
        THIS_VERSION   = plugin.getDescription().getVersion();
        if(!plugin.getConfig().getBoolean("console.longpluginname", true)) {
            pluginName = "CR";
        }else {
            pluginName = THIS_NAME;
        }
    }
    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event){
        double spawnChance  = (double)  camelRespawner.getConfig().get("cat_to_camel_chance", 0.75);
        boolean checkCamels = (boolean) camelRespawner.getConfig().get("camels.check_camels", true);
        int searchRadius    = (int)     camelRespawner.getConfig().get("camels.search_radius", 20);
        int maxCamels       = (int)     camelRespawner.getConfig().get("camels.max_camels_nearby", 1);;

        Entity entity = event.getEntity();
        if(entity.getWorld().getEnvironment() != Environment.NORMAL){
            return;
        }
        else{
            if(entity instanceof Cat){
                Cat cat = (Cat) entity;
                Location catLocation = cat.getLocation();
                Biome catBiome = catLocation.getBlock().getBiome();
                World catWorld = cat.getWorld();
                if(catBiome == Biome.DESERT){
                    if (!cat.isTamed()){
                        if(debug){
                            logDebug("SpawnChance : " + spawnChance);
                            logDebug("searchRadius : " + searchRadius);
                            logDebug("maxCamels : " + maxCamels);
                            logDebug("checkCamels : " + checkCamels);
                            logDebug("A valid natural cat has spawned");
                        }

                        boolean insideDesertVillage = CamelRespawnerLib.entityInsideDesertVillage(cat,false);

                        if(insideDesertVillage){
                            if(debug){logDebug("Cat is inside a desert village structure");}
                            if(!checkCamels || !checkCamelsInRadius(cat,searchRadius,maxCamels)){
                                if(isCamelSpawnable(spawnChance)){
                                    event.setCancelled(true);
                                    catWorld.spawn(catLocation, Camel.class);
                                    if(debug){ logDebug("Camel has been spawned");}
                                    Collection<Entity> collection = catWorld.getNearbyEntities(catLocation, 2, 2, 2);
                                    for (Entity e : collection) {
                                        if(e instanceof Camel) {
                                            e.getPersistentDataContainer().set(camelRespawner.NAME_KEY, PersistentDataType.STRING, "CamelRespawner");
                                        }
                                    }
                                }
                            }
                            else{
                                if(debug){ logDebug("There are more camels than max camels within search radius: " + searchRadius);}
                                return;
                            }
                        }
                    }
                    else{
                        if(debug){ logDebug("A unnatural cat is spawned inside a village");}
                        return;
                    }
                }
                else{
                    if(debug){ logDebug("Cat spawned in non desert");}
                    return;
                }
            }
            else{
                return;
            }
        }
    }

    private boolean checkCamelsInRadius(Entity entity, int radius, int maxCamels){
        int camelsInRadius = 0;
        Block spawnBlock = entity.getLocation().getBlock();
        for(Camel camel : spawnBlock.getWorld().getEntitiesByClass(Camel.class)) {
            double distance = camel.getLocation().distance(spawnBlock.getLocation());
            if(distance < radius) {
                camelsInRadius++;
                if (camelsInRadius >= maxCamels){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCamelSpawnable(double chance){
        double chancepercent = Math.random();
        if (chance > chancepercent){
            return true;
        }
        return false;
    }

    public  void log(String logMessage){
        //logger.info(ChatColor.YELLOW + "" + pluginName + ChatColor.RESET + " " + logMessage + ChatColor.RESET);
        MessageUtil.Builder(camelRespawner)
                .mmText(logMessage)
                .addPrefix(true)
                .setConfigColor(LegacyColors.YELLOW)
                .toConsole(true)
                .send();
    }
    public  void logDebug(String logMessage){
        //log(" " + THIS_VERSION + ChatColor.RED + ChatColor.BOLD + " [DEBUG] " + ChatColor.RESET + logMessage);
        MessageUtil.Builder(camelRespawner)
                .mmText("[DEBUG] " + logMessage)
                .addPrefix(true)
                .setConfigColor(LegacyColors.RED)
                .toConsole(true)
                .send();
    }
    public void logWarn(String logMessage){
        //log(" " + THIS_VERSION + ChatColor.RED + ChatColor.BOLD + " [WARNING] " + ChatColor.RESET + logMessage);
        MessageUtil.Builder(camelRespawner)
                .mmText("[WARNING] " + logMessage)
                .addPrefix(true)
                .setConfigColor(LegacyColors.RED)
                .toConsole(true)
                .send();

    }
    public	void log(Level level, String logMessage){
        //logger.log(level, ChatColor.YELLOW + "" + logMessage );
    }
}
