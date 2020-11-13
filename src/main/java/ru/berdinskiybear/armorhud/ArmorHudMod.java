package ru.berdinskiybear.armorhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;
import ru.berdinskiybear.armorhud.config.ArmorHudConfigReloader;

public class ArmorHudMod implements ClientModInitializer {

    public static final String MOD_ID = "armor_hud";
    public static final String MOD_NAME = "BerdinskiyBear's ArmorHUD";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Text CONFIG_SCREEN_NAME = Text.of("BerdinskiyBear's Armor HUD mod config");

    private static ArmorHudConfig currentConfig;

    public static ArmorHudConfig getCurrentConfig() {
        return currentConfig;
    }

    public static void setCurrentConfig(ArmorHudConfig currentConfig) {
        ArmorHudMod.currentConfig = currentConfig;
    }

    @Override
    public void onInitializeClient() {
        setCurrentConfig(ArmorHudConfig.readConfigFile());
        if (FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0")) {
            ArmorHudConfigReloader.register();
        }
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }
}
