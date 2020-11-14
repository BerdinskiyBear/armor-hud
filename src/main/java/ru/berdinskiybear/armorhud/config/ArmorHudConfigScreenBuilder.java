package ru.berdinskiybear.armorhud.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.Anchor;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.OffhandSlotBehavior;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.Side;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.WidgetShown;

import java.util.Arrays;
import java.util.Optional;

import static ru.berdinskiybear.armorhud.ArmorHudMod.getCurrentConfig;

public class ArmorHudConfigScreenBuilder {

    private static ArmorHudConfig temporaryConfig;
    public static ArmorHudConfig previewConfig = new ArmorHudConfig();

    public static Screen create(Screen parentScreen) {
        ArmorHudConfig defaultConfig = new ArmorHudConfig();

        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setShouldListSmoothScroll(false)
                .setShouldTabsSmoothScroll(false)
                .transparentBackground()
                .setTitle(ArmorHudMod.CONFIG_SCREEN_NAME)
                .setAfterInitConsumer((screen) -> {
                    temporaryConfig = new ArmorHudConfig(getCurrentConfig());
                    previewConfig = new ArmorHudConfig(getCurrentConfig());
                })
                .setSavingRunnable(() -> {
                    ArmorHudMod.setCurrentConfig(temporaryConfig);
                    ArmorHudConfig.writeConfigFile(temporaryConfig);
                });

        ConfigCategory category = configBuilder.getOrCreateCategory(new TranslatableText("armorHud.configScreen.category"));
        ConfigEntryBuilder configEntryBuilder = configBuilder.entryBuilder();

        BooleanListEntry enabledBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.enable.name"), getCurrentConfig().isEnabled())
                .setDefaultValue(defaultConfig.isEnabled())
                .setSaveConsumer((Boolean value) -> temporaryConfig.setReversed(value))
                .setErrorSupplier((Boolean value) -> {
                    previewConfig.setEnabled(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(enabledBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.enable.description")).build());

        DropdownBoxEntry<Anchor> anchorDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.anchor.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getAnchor(), Anchor::valueOf, ArmorHudConfigScreenBuilder::anchorToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::anchorToText)
                )
                .setSelections(Arrays.asList(Anchor.values()))
                .setDefaultValue(defaultConfig.getAnchor())
                .setSaveConsumer((Anchor value) -> temporaryConfig.setAnchor(value))
                .setSuggestionMode(false)
                .setErrorSupplier((Anchor value) -> {
                    previewConfig.setAnchor(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(anchorDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.anchor.description", (Object[]) Anchor.values())).build());

        DropdownBoxEntry<Side> sideDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.side.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getSide(), Side::valueOf, ArmorHudConfigScreenBuilder::sideToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::sideToText)
                )
                .setSelections(Arrays.asList(Side.values()))
                .setDefaultValue(defaultConfig.getSide())
                .setSaveConsumer((Side value) -> temporaryConfig.setSide(value))
                .setSuggestionMode(false)
                .setErrorSupplier((Side value) -> {
                    previewConfig.setSide(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(sideDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.side.description")).build());

        DropdownBoxEntry<OffhandSlotBehavior> offhandSlotBehaviorDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.offhandSlot.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getOffhandSlotBehavior(), OffhandSlotBehavior::valueOf, ArmorHudConfigScreenBuilder::offhandToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::offhandToText)
                )
                .setSelections(Arrays.asList(OffhandSlotBehavior.values()))
                .setDefaultValue(defaultConfig.getOffhandSlotBehavior())
                .setSaveConsumer((OffhandSlotBehavior value) -> temporaryConfig.setOffhandSlotBehavior(value))
                .setSuggestionMode(false)
                .setErrorSupplier((OffhandSlotBehavior value) -> {
                    previewConfig.setOffhandSlotBehavior(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offhandSlotBehaviorDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.offhandSlot.description")).build());

        IntegerListEntry offsetXEntry = configEntryBuilder
                .startIntField(new TranslatableText("armorHud.configScreen.setting.offsetX.name"), getCurrentConfig().getOffsetX())
                .setDefaultValue(defaultConfig.getOffsetX())
                .setSaveConsumer((Integer value) -> temporaryConfig.setOffsetX(value))
                .setErrorSupplier((Integer value) -> {
                    previewConfig.setOffsetX(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetXEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.offsetX.description")).build());

        IntegerListEntry offsetYEntry = configEntryBuilder
                .startIntField(new TranslatableText("armorHud.configScreen.setting.offsetY.name"), getCurrentConfig().getOffsetY())
                .setDefaultValue(defaultConfig.getOffsetY())
                .setSaveConsumer((Integer value) -> temporaryConfig.setOffsetY(value))
                .setErrorSupplier((Integer value) -> {
                    previewConfig.setOffsetY(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetYEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.offsetY.description")).build());

        DropdownBoxEntry<WidgetShown> widgetShownDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.widgetShown.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(getCurrentConfig().getWidgetShown(), WidgetShown::valueOf, ArmorHudConfigScreenBuilder::widgetShownToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::widgetShownToText)
                )
                .setSelections(Arrays.asList(WidgetShown.values()))
                .setDefaultValue(defaultConfig.getWidgetShown())
                .setSaveConsumer((WidgetShown value) -> temporaryConfig.setWidgetShown(value))
                .setSuggestionMode(false)
                .setErrorSupplier((WidgetShown value) -> {
                    previewConfig.setWidgetShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(widgetShownDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.widgetShown.description", (Object[]) WidgetShown.values())).build());

        BooleanListEntry reversedBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.reversed.name"), getCurrentConfig().isReversed())
                .setDefaultValue(defaultConfig.isReversed())
                .setSaveConsumer((Boolean value) -> temporaryConfig.setReversed(value))
                .setErrorSupplier((Boolean value) -> {
                    previewConfig.setReversed(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(reversedBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.reversed.description")).build());

        BooleanListEntry iconsBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.iconsShown.name"), getCurrentConfig().getIconsShown())
                .setDefaultValue(defaultConfig.getIconsShown())
                .setSaveConsumer((Boolean value) -> temporaryConfig.setIconsShown(value))
                .setErrorSupplier((Boolean value) -> {
                    previewConfig.setIconsShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(iconsBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.iconsShown.description")).build());

        BooleanListEntry warningBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.warningShown.name"), getCurrentConfig().isWarningShown())
                .setDefaultValue(defaultConfig.isWarningShown())
                .setSaveConsumer((Boolean value) -> temporaryConfig.setWarningShown(value))
                .setErrorSupplier((Boolean value) -> {
                    previewConfig.setWarningShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(warningBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.warningShown.description")).build());

        IntegerListEntry minDurabilityValueEntry = configEntryBuilder
                .startIntField(new TranslatableText("armorHud.configScreen.setting.minDurabilityValue.name"), getCurrentConfig().getMinDurabilityValue())
                .setDefaultValue(defaultConfig.getMinDurabilityValue())
                .setSaveConsumer((Integer value) -> temporaryConfig.setMinDurabilityValue(value))
                .setErrorSupplier((Integer value) -> {
                    previewConfig.setMinDurabilityValue(value);
                    return Optional.empty();
                })
                .setMin(0)
                .build();
        category.addEntry(minDurabilityValueEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.minDurabilityValue.description")).build());

        DoubleListEntry minDurabilityPercentageEntry = configEntryBuilder
                .startDoubleField(new TranslatableText("armorHud.configScreen.setting.minDurabilityPercentage.name"), getCurrentConfig().getMinDurabilityPercentage() * 100.0D)
                .setDefaultValue(defaultConfig.getMinDurabilityPercentage() * 100.0D)
                .setSaveConsumer((Double value) -> temporaryConfig.setMinDurabilityPercentage(value / 100.0D))
                .setErrorSupplier((Double value) -> {
                    previewConfig.setMinDurabilityPercentage(value / 100.0D);
                    return Optional.empty();
                })
                .setMin(0.0D)
                .setMax(100.0D)
                .build();
        category.addEntry(minDurabilityPercentageEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.minDurabilityPercentage.description")).build());

        return configBuilder.build();
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

}
