package ru.berdinskiybear.armorhud.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.Anchor;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.OffhandSlotBehavior;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.Side;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.WidgetShown;

import java.util.Arrays;
import java.util.Optional;

import static ru.berdinskiybear.armorhud.ArmorHudMod.getCurrentConfig;

public class ArmorHudConfigScreenBuilder {
    private ArmorHudConfig temporaryConfig;
    public static ArmorHudConfig previewConfig = new ArmorHudConfig();
    public static final Text title = Text.of("BerdinskiyBear's Armor HUD mod config");

    public ArmorHudConfigScreenBuilder() {
        this.temporaryConfig = new ArmorHudConfig();
    }

    private static Text anchorToText(Anchor value) {
        return Text.of(value.name());
    }

    private static Text sideToText(Side value) {
        return Text.of(value.name());
    }

    private static Text offhandToText(OffhandSlotBehavior value) {
        return Text.of(value.name());
    }

    private static Text widgetShownToText(WidgetShown value) {
        return Text.of(value.name());
    }

    public Screen create(Screen parentScreen) {
        ArmorHudConfig defaultConfig = new ArmorHudConfig();

        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setShouldListSmoothScroll(false)
                .setShouldTabsSmoothScroll(false)
                .transparentBackground()
                .setTitle(title)
                .setAfterInitConsumer((screen) -> {
                    previewConfig = new ArmorHudConfig(getCurrentConfig());
                    temporaryConfig = new ArmorHudConfig(getCurrentConfig());
                })
                .setSavingRunnable(() -> {
                    ArmorHudMod.setCurrentConfig(this.temporaryConfig);
                    ArmorHudConfig.writeConfigFile(this.temporaryConfig);
                });

        ConfigCategory category = configBuilder.getOrCreateCategory(Text.of("Category"));
        ConfigEntryBuilder configEntryBuilder = configBuilder.entryBuilder();

        DropdownBoxEntry<Anchor> anchorDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(Text.of("Anchor"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getAnchor(), Anchor::valueOf, ArmorHudConfigScreenBuilder::anchorToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::anchorToText)
                )
                .setSelections(Arrays.asList(Anchor.values()))
                .setDefaultValue(defaultConfig.getAnchor())
                .setSaveConsumer(this.temporaryConfig::setAnchor)
                .setSuggestionMode(false)
                .setErrorSupplier((Anchor value) -> {
                    previewConfig.setAnchor(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(anchorDropdownBoxEntry);

        DropdownBoxEntry<Side> sideDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(Text.of("Side"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getSide(), Side::valueOf, ArmorHudConfigScreenBuilder::sideToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::sideToText)
                )
                .setSelections(Arrays.asList(Side.values()))
                .setDefaultValue(defaultConfig.getSide())
                .setSaveConsumer(this.temporaryConfig::setSide)
                .setSuggestionMode(false)
                .setErrorSupplier((Side value) -> {
                    previewConfig.setSide(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(sideDropdownBoxEntry);

        DropdownBoxEntry<OffhandSlotBehavior> offhandSlotBehaviorDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(Text.of("Offhand slot behaviour"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getOffhandSlotBehavior(), OffhandSlotBehavior::valueOf, ArmorHudConfigScreenBuilder::offhandToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::offhandToText)
                )
                .setSelections(Arrays.asList(OffhandSlotBehavior.values()))
                .setDefaultValue(defaultConfig.getOffhandSlotBehavior())
                .setSaveConsumer(this.temporaryConfig::setOffhandSlotBehavior)
                .setSuggestionMode(false)
                .setErrorSupplier((OffhandSlotBehavior value) -> {
                    previewConfig.setOffhandSlotBehavior(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offhandSlotBehaviorDropdownBoxEntry);

        IntegerListEntry offsetXEntry = configEntryBuilder
                .startIntField(Text.of("X offset"), getCurrentConfig().getOffsetX())
                .setDefaultValue(defaultConfig.getOffsetX())
                .setSaveConsumer(this.temporaryConfig::setOffsetX)
                .setErrorSupplier((Integer value) -> {
                    previewConfig.setOffsetX(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetXEntry);

        IntegerListEntry offsetYEntry = configEntryBuilder
                .startIntField(Text.of("Y offset"), getCurrentConfig().getOffsetY())
                .setDefaultValue(defaultConfig.getOffsetY())
                .setSaveConsumer(this.temporaryConfig::setOffsetY)
                .setErrorSupplier((Integer value) -> {
                    previewConfig.setOffsetY(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetYEntry);

        DropdownBoxEntry<WidgetShown> widgetShownDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(Text.of("Widget shown"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getWidgetShown(), WidgetShown::valueOf, ArmorHudConfigScreenBuilder::widgetShownToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::widgetShownToText)
                )
                .setSelections(Arrays.asList(WidgetShown.values()))
                .setDefaultValue(defaultConfig.getWidgetShown())
                .setSaveConsumer(this.temporaryConfig::setWidgetShown)
                .setSuggestionMode(false)
                .setErrorSupplier((WidgetShown value) -> {
                    previewConfig.setWidgetShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(widgetShownDropdownBoxEntry);

        BooleanListEntry reversedBooleanListEntry = configEntryBuilder
                .startBooleanToggle(Text.of("Reversed order"), getCurrentConfig().isReversed())
                .setDefaultValue(defaultConfig.isReversed())
                .setSaveConsumer(this.temporaryConfig::setReversed)
                .setErrorSupplier((Boolean value) -> {
                    previewConfig.setReversed(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(reversedBooleanListEntry);

        return configBuilder.build();
    }
}
