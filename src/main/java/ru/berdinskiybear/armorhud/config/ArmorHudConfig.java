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

    protected boolean enabled;
    protected Anchor anchor;
    protected Side side;
    protected int offsetX;
    protected int offsetY;
    protected Style style;
    protected WidgetShown widgetShown;
    protected OffhandSlotBehavior offhandSlotBehavior;
    protected boolean pushBossbars;
    protected boolean pushStatusEffectIcons;
    protected boolean pushSubtitles;
    protected boolean reversed;
    protected boolean iconsShown;
    protected boolean warningShown;
    protected int minDurabilityValue;
    protected double minDurabilityPercentage;
    protected float warningIconBobbingIntervalMs;

    public ArmorHudConfig() {
        this.enabled = true;
        this.anchor = Anchor.HOTBAR;
        this.side = Side.LEFT;
        this.offsetX = 0;
        this.offsetY = 0;
        this.style = Style.STYLE_1_E;
        this.widgetShown = WidgetShown.NOT_EMPTY;
        this.offhandSlotBehavior = OffhandSlotBehavior.ADHERE;
        this.pushBossbars = true;
        this.pushStatusEffectIcons = true;
        this.pushSubtitles = true;
        this.reversed = true;
        this.iconsShown = true;
        this.warningShown = true;
        this.minDurabilityValue = 5;
        this.minDurabilityPercentage = 0.05D;
        this.warningIconBobbingIntervalMs = 2000.0F;
    }

    public ArmorHudConfig(ArmorHudConfig original) {
        this.enabled = original.enabled;
        this.anchor = original.anchor;
        this.side = original.side;
        this.offsetX = original.offsetX;
        this.offsetY = original.offsetY;
        this.style = original.style;
        this.widgetShown = original.widgetShown;
        this.offhandSlotBehavior = original.offhandSlotBehavior;
        this.pushBossbars = original.pushBossbars;
        this.pushStatusEffectIcons = original.pushStatusEffectIcons;
        this.pushSubtitles = original.pushSubtitles;
        this.reversed = original.reversed;
        this.iconsShown = original.iconsShown;
        this.warningShown = original.warningShown;
        this.minDurabilityValue = original.minDurabilityValue;
        this.minDurabilityPercentage = original.minDurabilityPercentage;
        this.warningIconBobbingIntervalMs = original.warningIconBobbingIntervalMs;
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

    public boolean isPreview() {
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public Side getSide() {
        return side;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public Style getStyle() {
        return style;
    }

    public WidgetShown getWidgetShown() {
        return widgetShown;
    }

    public OffhandSlotBehavior getOffhandSlotBehavior() {
        return offhandSlotBehavior;
    }

    public boolean getPushBossbars() {
        return this.pushBossbars;
    }

    public boolean getPushStatusEffectIcons() {
        return this.pushStatusEffectIcons;
    }

    public boolean getPushSubtitles() {
        return this.pushSubtitles;
    }

    public boolean isReversed() {
        return reversed;
    }

    public boolean getIconsShown() {
        return iconsShown;
    }

    public boolean isWarningShown() {
        return warningShown;
    }

    public int getMinDurabilityValue() {
        return minDurabilityValue;
    }

    public double getMinDurabilityPercentage() {
        return minDurabilityPercentage;
    }

    public float getWarningIconBobbingIntervalMs() {
        return warningIconBobbingIntervalMs;
    }

    public enum Anchor {
        TOP_CENTER,
        TOP,
        BOTTOM,
        HOTBAR
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

    public enum WidgetShown {
        ALWAYS,
        IF_ANY_PRESENT,
        NOT_EMPTY
    }

    public enum Style {
        STYLE_1_E,
        STYLE_1_H,
        STYLE_1_S,
        STYLE_2_E,
        STYLE_2_H,
        STYLE_2_S,
        STYLE_3
    }

    public static class MutableConfig extends ArmorHudConfig {

        public MutableConfig() {
            super();
        }

        public MutableConfig(ArmorHudConfig currentConfig) {
            super(currentConfig);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setAnchor(Anchor anchor) {
            this.anchor = anchor;
        }

        public void setSide(Side side) {
            this.side = side;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }

        public void setStyle(Style style) {
            this.style = style;
        }

        public void setWidgetShown(WidgetShown widgetShown) {
            this.widgetShown = widgetShown;
        }

        public void setOffhandSlotBehavior(OffhandSlotBehavior offhandSlotBehavior) {
            this.offhandSlotBehavior = offhandSlotBehavior;
        }

        public void setPushBossbars(boolean pushBossbars) {
            this.pushBossbars = pushBossbars;
        }

        public void setPushStatusEffectIcons(boolean pushStatusEffectIcons) {
            this.pushStatusEffectIcons = pushStatusEffectIcons;
        }

        public void setPushSubtitles(boolean pushSubtitles) {
            this.pushSubtitles = pushSubtitles;
        }

        public void setReversed(boolean reversed) {
            this.reversed = reversed;
        }

        public void setIconsShown(boolean iconsShown) {
            this.iconsShown = iconsShown;
        }

        public void setWarningShown(boolean warningShown) {
            this.warningShown = warningShown;
        }

        public void setMinDurabilityValue(int minDurabilityValue) {
            this.minDurabilityValue = minDurabilityValue;
        }

        public void setMinDurabilityPercentage(double minDurabilityPercentage) {
            this.minDurabilityPercentage = minDurabilityPercentage;
        }

        public void setWarningIconBobbingIntervalMs(float warningIconBobbingIntervalMs) {
            this.warningIconBobbingIntervalMs = warningIconBobbingIntervalMs;
        }
    }
}
