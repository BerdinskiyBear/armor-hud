package ru.berdinskiybear.armorhud;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArmorHudMod implements ModInitializer {

    public static final String MOD_ID = "armor_hud";
    public static final String MOD_NAME = "BerdinskiyBear's ArmorHUD";
    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {

    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message){
        LOGGER.log(level, message);
    }

}