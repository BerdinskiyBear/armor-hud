package ru.berdinskiybear.armorhud.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.*;
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

public class ArmorHudConfigScreenBuilder {

    public static Screen create(Screen parentScreen) {
        ArmorHudConfig defaultConfig = new ArmorHudConfig();

        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setShouldListSmoothScroll(false)
                .setShouldTabsSmoothScroll(false)
                .transparentBackground()
                .setTitle(ArmorHudMod.CONFIG_SCREEN_NAME)
                .setAfterInitConsumer((screen) -> {
                    ArmorHudMod.temporaryConfig = new ArmorHudConfig.MutableConfig(ArmorHudMod.getCurrentConfig());
                    ArmorHudMod.previewConfig = new ArmorHudConfig.MutableConfig(ArmorHudMod.getCurrentConfig()) {
                        @Override
                        public boolean isPreview() {
                            return true;
                        }
                    };
                })
                .setSavingRunnable(() -> {
                    ArmorHudMod.setCurrentConfig(new ArmorHudConfig(ArmorHudMod.temporaryConfig));
                    ArmorHudMod.writeCurrentConfig();
                });

        ConfigCategory category = configBuilder.getOrCreateCategory(new TranslatableText("armorHud.configScreen.category"));
        ConfigEntryBuilder configEntryBuilder = configBuilder.entryBuilder();

        BooleanListEntry enabledBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.enable.name"), ArmorHudMod.getCurrentConfig().isEnabled())
                .setDefaultValue(defaultConfig.isEnabled())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setReversed(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setEnabled(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(enabledBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.enable.description")).build());

        DropdownBoxEntry<Anchor> anchorDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.anchor.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(ArmorHudMod.getCurrentConfig().getAnchor(), Anchor::valueOf, ArmorHudConfigScreenBuilder::anchorToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::anchorToText)
                )
                .setSelections(Arrays.asList(Anchor.values()))
                .setDefaultValue(defaultConfig.getAnchor())
                .setSaveConsumer((Anchor value) -> ArmorHudMod.temporaryConfig.setAnchor(value))
                .setSuggestionMode(false)
                .setErrorSupplier((Anchor value) -> {
                    ArmorHudMod.previewConfig.setAnchor(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(anchorDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.anchor.description", (Object[]) Anchor.values())).build());

        DropdownBoxEntry<Side> sideDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.side.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(ArmorHudMod.getCurrentConfig().getSide(), Side::valueOf, ArmorHudConfigScreenBuilder::sideToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::sideToText)
                )
                .setSelections(Arrays.asList(Side.values()))
                .setDefaultValue(defaultConfig.getSide())
                .setSaveConsumer((Side value) -> ArmorHudMod.temporaryConfig.setSide(value))
                .setSuggestionMode(false)
                .setErrorSupplier((Side value) -> {
                    ArmorHudMod.previewConfig.setSide(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(sideDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.side.description", (Object[]) Side.values())).build());

        DropdownBoxEntry<OffhandSlotBehavior> offhandSlotBehaviorDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.offhandSlot.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(ArmorHudMod.getCurrentConfig().getOffhandSlotBehavior(), OffhandSlotBehavior::valueOf, ArmorHudConfigScreenBuilder::offhandToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::offhandToText)
                )
                .setSelections(Arrays.asList(OffhandSlotBehavior.values()))
                .setDefaultValue(defaultConfig.getOffhandSlotBehavior())
                .setSaveConsumer((OffhandSlotBehavior value) -> ArmorHudMod.temporaryConfig.setOffhandSlotBehavior(value))
                .setSuggestionMode(false)
                .setErrorSupplier((OffhandSlotBehavior value) -> {
                    ArmorHudMod.previewConfig.setOffhandSlotBehavior(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offhandSlotBehaviorDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.offhandSlot.description", (Object[]) OffhandSlotBehavior.values())).build());

        IntegerListEntry offsetXEntry = configEntryBuilder
                .startIntField(new TranslatableText("armorHud.configScreen.setting.offsetX.name"), ArmorHudMod.getCurrentConfig().getOffsetX())
                .setDefaultValue(defaultConfig.getOffsetX())
                .setSaveConsumer((Integer value) -> ArmorHudMod.temporaryConfig.setOffsetX(value))
                .setErrorSupplier((Integer value) -> {
                    ArmorHudMod.previewConfig.setOffsetX(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetXEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.offsetX.description")).build());

        IntegerListEntry offsetYEntry = configEntryBuilder
                .startIntField(new TranslatableText("armorHud.configScreen.setting.offsetY.name"), ArmorHudMod.getCurrentConfig().getOffsetY())
                .setDefaultValue(defaultConfig.getOffsetY())
                .setSaveConsumer((Integer value) -> ArmorHudMod.temporaryConfig.setOffsetY(value))
                .setErrorSupplier((Integer value) -> {
                    ArmorHudMod.previewConfig.setOffsetY(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetYEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.offsetY.description")).build());

        DropdownBoxEntry<WidgetShown> widgetShownDropdownBoxEntry = configEntryBuilder
                .startDropdownMenu(new TranslatableText("armorHud.configScreen.setting.widgetShown.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(ArmorHudMod.getCurrentConfig().getWidgetShown(), WidgetShown::valueOf, ArmorHudConfigScreenBuilder::widgetShownToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::widgetShownToText)
                )
                .setSelections(Arrays.asList(WidgetShown.values()))
                .setDefaultValue(defaultConfig.getWidgetShown())
                .setSaveConsumer((WidgetShown value) -> ArmorHudMod.temporaryConfig.setWidgetShown(value))
                .setSuggestionMode(false)
                .setErrorSupplier((WidgetShown value) -> {
                    ArmorHudMod.previewConfig.setWidgetShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(widgetShownDropdownBoxEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.widgetShown.description", (Object[]) WidgetShown.values())).build());

        BooleanListEntry reversedBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.reversed.name"), ArmorHudMod.getCurrentConfig().isReversed())
                .setDefaultValue(defaultConfig.isReversed())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setReversed(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setReversed(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(reversedBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.reversed.description")).build());

        BooleanListEntry iconsBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.iconsShown.name"), ArmorHudMod.getCurrentConfig().getIconsShown())
                .setDefaultValue(defaultConfig.getIconsShown())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setIconsShown(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setIconsShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(iconsBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.iconsShown.description")).build());

        BooleanListEntry warningBooleanListEntry = configEntryBuilder
                .startBooleanToggle(new TranslatableText("armorHud.configScreen.setting.warningShown.name"), ArmorHudMod.getCurrentConfig().isWarningShown())
                .setDefaultValue(defaultConfig.isWarningShown())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setWarningShown(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setWarningShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(warningBooleanListEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.warningShown.description")).build());

        IntegerListEntry minDurabilityValueEntry = configEntryBuilder
                .startIntField(new TranslatableText("armorHud.configScreen.setting.minDurabilityValue.name"), ArmorHudMod.getCurrentConfig().getMinDurabilityValue())
                .setDefaultValue(defaultConfig.getMinDurabilityValue())
                .setSaveConsumer((Integer value) -> ArmorHudMod.temporaryConfig.setMinDurabilityValue(value))
                .setErrorSupplier((Integer value) -> {
                    ArmorHudMod.previewConfig.setMinDurabilityValue(value);
                    return Optional.empty();
                })
                .setMin(0)
                .build();
        category.addEntry(minDurabilityValueEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.minDurabilityValue.description")).build());

        DoubleListEntry minDurabilityPercentageEntry = configEntryBuilder
                .startDoubleField(new TranslatableText("armorHud.configScreen.setting.minDurabilityPercentage.name"), ArmorHudMod.getCurrentConfig().getMinDurabilityPercentage() * 100.0D)
                .setDefaultValue(defaultConfig.getMinDurabilityPercentage() * 100.0D)
                .setSaveConsumer((Double value) -> ArmorHudMod.temporaryConfig.setMinDurabilityPercentage(value / 100.0D))
                .setErrorSupplier((Double value) -> {
                    ArmorHudMod.previewConfig.setMinDurabilityPercentage(value / 100.0D);
                    return Optional.empty();
                })
                .setMin(0.0D)
                .setMax(100.0D)
                .build();
        category.addEntry(minDurabilityPercentageEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.minDurabilityPercentage.description")).build());

        final float minWarningIconBobbingInterval = 0.2F;
        FloatListEntry warningIconBobbingIntervalEntry = configEntryBuilder
                .startFloatField(new TranslatableText("armorHud.configScreen.setting.warningIconBobbingIntervalEntry.name"), ArmorHudMod.getCurrentConfig().getWarningIconBobbingIntervalMs() / 1000.0F)
                .setDefaultValue(defaultConfig.getWarningIconBobbingIntervalMs() / 1000.0F)
                .setSaveConsumer((Float value) -> ArmorHudMod.temporaryConfig.setWarningIconBobbingIntervalMs(value * 1000.0F))
                .setErrorSupplier((Float value) -> {
                    if (value != 0.0F && value < minWarningIconBobbingInterval)//
                        return Optional.of(new TranslatableText("text.cloth-config.error.too_small", minWarningIconBobbingInterval));
                    ArmorHudMod.previewConfig.setWarningIconBobbingIntervalMs(value * 1000.0F);
                    return Optional.empty();
                })
                //.setMin(minWarningIconBobbingInterval)
                .setMax(5.0F)
                .build();
        category.addEntry(warningIconBobbingIntervalEntry);
        category.addEntry(configEntryBuilder.startTextDescription(new TranslatableText("armorHud.configScreen.setting.warningIconBobbingIntervalEntry.description")).build());

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
