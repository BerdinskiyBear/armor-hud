package ru.berdinskiybear.armorhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import ru.berdinskiybear.armorhud.ArmorHudMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ArmorHudConfig {

    private Anchor anchor;
    private Side side;
    private int offsetX;
    private int offsetY;
    private boolean fullWidgetShown;
    private OffhandSlotBehavior offhandSlotBehavior;
    private boolean reversed;

    public ArmorHudConfig() {
        this.anchor = Anchor.HOTBAR;
        this.side = Side.LEFT;
        this.offsetX = 0;
        this.offsetY = 0;
        this.fullWidgetShown = false;
        this.offhandSlotBehavior = OffhandSlotBehavior.ADHERE;
        this.reversed = true;
    }

    public static ArmorHudConfig readConfigFile() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), ArmorHudMod.MOD_ID + ".json");
        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                return gson.fromJson(fileReader, ArmorHudConfig.class);
            } catch (IOException e) {
                ArmorHudMod.log(Level.ERROR, "Config file " + configFile.getAbsolutePath() + " can't be read or has disappeared.");
                ArmorHudMod.log(Level.ERROR, e.getLocalizedMessage());
                return new ArmorHudConfig();
            }
        } else {
            ArmorHudMod.log("Config file is missing, creating new one...");
            return createNewConfigFile();
        }
    }

    public static void writeConfigFile(ArmorHudConfig config) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeSpecialFloatingPointValues().create();
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), ArmorHudMod.MOD_ID + ".json");
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            gson.toJson(config, ArmorHudConfig.class, fileWriter);
        } catch (IOException e) {
            ArmorHudMod.log(Level.ERROR, "Config file " + configFile.getAbsolutePath() + " can't be written so it probably wasn't written.");
            ArmorHudMod.log(Level.ERROR, e.getLocalizedMessage());
        }
    }

    private static ArmorHudConfig createNewConfigFile() {
        ArmorHudConfig config = new ArmorHudConfig();
        writeConfigFile(config);
        return config;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public boolean isFullWidgetShown() {
        return fullWidgetShown;
    }

    public void setFullWidgetShown(boolean fullWidgetShown) {
        this.fullWidgetShown = fullWidgetShown;
    }

    public OffhandSlotBehavior getOffhandSlotBehavior() {
        return offhandSlotBehavior;
    }

    public void setOffhandSlotBehavior(OffhandSlotBehavior offhandSlotBehavior) {
        this.offhandSlotBehavior = offhandSlotBehavior;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public enum Anchor {
        TOP_CENTER,
        TOP,
        BOTTOM,
        HOTBAR;

        public boolean isTop() {
            return this == TOP || this == TOP_CENTER;
        }
    }

    public enum Side {
        RIGHT,
        LEFT
    }

    public enum OffhandSlotBehavior {
        ALWAYS_IGNORE,
        ADHERE,
        ALWAYS_LEAVE_SPACE
    }
}
