package ru.berdinskiybear.armorhud.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig.*;
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

        ConfigCategory category = configBuilder.getOrCreateCategory(Text.translatable("armorHud.configScreen.category"));
        ConfigEntryBuilder configEntryBuilder = configBuilder.entryBuilder();

        AbstractConfigListEntry<Boolean> enabledEntry;
        AbstractConfigListEntry<Anchor> anchorEntry;
        AbstractConfigListEntry<Side> sideEntry;
        AbstractConfigListEntry<OffhandSlotBehavior> offhandSlotBehaviorEntry;
        AbstractConfigListEntry<Boolean> pushBossbarsEntry;
        AbstractConfigListEntry<Boolean> pushStatusEffectIconsEntry;
        AbstractConfigListEntry<Boolean> pushSubtitlesEntry;
        AbstractConfigListEntry<Integer> offsetXEntry;
        AbstractConfigListEntry<Integer> offsetYEntry;
        AbstractConfigListEntry<Style> styleEntry;
        AbstractConfigListEntry<WidgetShown> widgetShownEntry;
        AbstractConfigListEntry<Boolean> reversedEntry;
        AbstractConfigListEntry<Boolean> iconsEntry;
        AbstractConfigListEntry<Boolean> warningEntry;
        AbstractConfigListEntry<Integer> minDurabilityValueEntry;
        AbstractConfigListEntry<Double> minDurabilityPercentageEntry;
        AbstractConfigListEntry<Float> warningIconBobbingIntervalEntry;

        enabledEntry = configEntryBuilder
                .startBooleanToggle(Text.translatable("armorHud.configScreen.setting.enable.name"), ArmorHudMod.getCurrentConfig().isEnabled())
                .setDefaultValue(defaultConfig.isEnabled())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setEnabled(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setEnabled(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(enabledEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.enable.description").formatted(Formatting.GRAY)).build());

        anchorEntry = configEntryBuilder
                .startDropdownMenu(Text.translatable("armorHud.configScreen.setting.anchor.name"),
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
        category.addEntry(anchorEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.anchor.description", (Object[]) Anchor.values()).formatted(Formatting.GRAY)).build());

        sideEntry = configEntryBuilder
                .startEnumSelector(Text.translatable("armorHud.configScreen.setting.side.name"), Side.class, ArmorHudMod.getCurrentConfig().getSide())
                .setDefaultValue(defaultConfig.getSide())
                .setSaveConsumer((Side value) -> ArmorHudMod.temporaryConfig.setSide(value))
                .setErrorSupplier((Side value) -> {
                    ArmorHudMod.previewConfig.setSide(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(sideEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.side.description", (Object[]) Side.values()).formatted(Formatting.GRAY)).build());

        offhandSlotBehaviorEntry = configEntryBuilder
                .startDropdownMenu(Text.translatable("armorHud.configScreen.setting.offhandSlot.name"),
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
        category.addEntry(offhandSlotBehaviorEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.offhandSlot.description", (Object[]) OffhandSlotBehavior.values()).formatted(Formatting.GRAY)).build());

        pushBossbarsEntry = configEntryBuilder
                .startBooleanToggle(Text.translatable("armorHud.configScreen.setting.pushBossbars.name"), ArmorHudMod.getCurrentConfig().getPushBossbars())
                .setDefaultValue(defaultConfig.getPushBossbars())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setPushBossbars(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setPushBossbars(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(pushBossbarsEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.pushBossbars.description").formatted(Formatting.GRAY)).build());

        pushStatusEffectIconsEntry = configEntryBuilder
                .startBooleanToggle(Text.translatable("armorHud.configScreen.setting.pushStatusEffectIcons.name"), ArmorHudMod.getCurrentConfig().getPushStatusEffectIcons())
                .setDefaultValue(defaultConfig.getPushStatusEffectIcons())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setPushStatusEffectIcons(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setPushStatusEffectIcons(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(pushStatusEffectIconsEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.pushStatusEffectIcons.description").formatted(Formatting.GRAY)).build());

        pushSubtitlesEntry = configEntryBuilder
                .startBooleanToggle(Text.translatable("armorHud.configScreen.setting.pushSubtitles.name"), ArmorHudMod.getCurrentConfig().getPushSubtitles())
                .setDefaultValue(defaultConfig.getPushSubtitles())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setPushSubtitles(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setPushSubtitles(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(pushSubtitlesEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.pushSubtitles.description").formatted(Formatting.GRAY)).build());

        offsetXEntry = configEntryBuilder
                .startIntField(Text.translatable("armorHud.configScreen.setting.offsetX.name"), ArmorHudMod.getCurrentConfig().getOffsetX())
                .setDefaultValue(defaultConfig.getOffsetX())
                .setSaveConsumer((Integer value) -> ArmorHudMod.temporaryConfig.setOffsetX(value))
                .setErrorSupplier((Integer value) -> {
                    ArmorHudMod.previewConfig.setOffsetX(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetXEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.offsetX.description").formatted(Formatting.GRAY)).build());

        offsetYEntry = configEntryBuilder
                .startIntField(Text.translatable("armorHud.configScreen.setting.offsetY.name"), ArmorHudMod.getCurrentConfig().getOffsetY())
                .setDefaultValue(defaultConfig.getOffsetY())
                .setSaveConsumer((Integer value) -> ArmorHudMod.temporaryConfig.setOffsetY(value))
                .setErrorSupplier((Integer value) -> {
                    ArmorHudMod.previewConfig.setOffsetY(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(offsetYEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.offsetY.description").formatted(Formatting.GRAY)).build());

        styleEntry = configEntryBuilder
                .startDropdownMenu(Text.translatable("armorHud.configScreen.setting.style.name"),
                        DropdownMenuBuilder.TopCellElementBuilder.of(ArmorHudMod.getCurrentConfig().getStyle(), Style::valueOf, ArmorHudConfigScreenBuilder::styleToText),
                        DropdownMenuBuilder.CellCreatorBuilder.of(ArmorHudConfigScreenBuilder::styleToText)
                )
                .setSelections(Arrays.asList(Style.values()))
                .setDefaultValue(defaultConfig.getStyle())
                .setSaveConsumer((Style value) -> ArmorHudMod.temporaryConfig.setStyle(value))
                .setSuggestionMode(false)
                .setErrorSupplier((Style value) -> {
                    ArmorHudMod.previewConfig.setStyle(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(styleEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.style.description", (Object[]) Side.values()).formatted(Formatting.GRAY)).build());

        widgetShownEntry = configEntryBuilder
                .startDropdownMenu(Text.translatable("armorHud.configScreen.setting.widgetShown.name"),
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
        category.addEntry(widgetShownEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.widgetShown.description", (Object[]) WidgetShown.values()).formatted(Formatting.GRAY)).build());

        reversedEntry = configEntryBuilder
                .startBooleanToggle(Text.translatable("armorHud.configScreen.setting.reversed.name"), ArmorHudMod.getCurrentConfig().isReversed())
                .setDefaultValue(defaultConfig.isReversed())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setReversed(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setReversed(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(reversedEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.reversed.description").formatted(Formatting.GRAY)).build());

        iconsEntry = configEntryBuilder
                .startBooleanToggle(Text.translatable("armorHud.configScreen.setting.iconsShown.name"), ArmorHudMod.getCurrentConfig().getIconsShown())
                .setDefaultValue(defaultConfig.getIconsShown())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setIconsShown(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setIconsShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(iconsEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.iconsShown.description").formatted(Formatting.GRAY)).build());

        warningEntry = configEntryBuilder
                .startBooleanToggle(Text.translatable("armorHud.configScreen.setting.warningShown.name"), ArmorHudMod.getCurrentConfig().isWarningShown())
                .setDefaultValue(defaultConfig.isWarningShown())
                .setSaveConsumer((Boolean value) -> ArmorHudMod.temporaryConfig.setWarningShown(value))
                .setErrorSupplier((Boolean value) -> {
                    ArmorHudMod.previewConfig.setWarningShown(value);
                    return Optional.empty();
                })
                .build();
        category.addEntry(warningEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.warningShown.description").formatted(Formatting.GRAY)).build());

        minDurabilityValueEntry = configEntryBuilder
                .startIntField(Text.translatable("armorHud.configScreen.setting.minDurabilityValue.name"), ArmorHudMod.getCurrentConfig().getMinDurabilityValue())
                .setDefaultValue(defaultConfig.getMinDurabilityValue())
                .setSaveConsumer((Integer value) -> ArmorHudMod.temporaryConfig.setMinDurabilityValue(value))
                .setErrorSupplier((Integer value) -> {
                    ArmorHudMod.previewConfig.setMinDurabilityValue(value);
                    return Optional.empty();
                })
                .setMin(0)
                .build();
        category.addEntry(minDurabilityValueEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.minDurabilityValue.description").formatted(Formatting.GRAY)).build());

        minDurabilityPercentageEntry = configEntryBuilder
                .startDoubleField(Text.translatable("armorHud.configScreen.setting.minDurabilityPercentage.name"), ArmorHudMod.getCurrentConfig().getMinDurabilityPercentage() * 100.0D)
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
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.minDurabilityPercentage.description").formatted(Formatting.GRAY)).build());

        final float minWarningIconBobbingInterval = 0.2F;
        warningIconBobbingIntervalEntry = configEntryBuilder
                .startFloatField(Text.translatable("armorHud.configScreen.setting.warningIconBobbingIntervalEntry.name"), ArmorHudMod.getCurrentConfig().getWarningIconBobbingIntervalMs() / 1000.0F)
                .setDefaultValue(defaultConfig.getWarningIconBobbingIntervalMs() / 1000.0F)
                .setSaveConsumer((Float value) -> ArmorHudMod.temporaryConfig.setWarningIconBobbingIntervalMs(value * 1000.0F))
                .setErrorSupplier((Float value) -> {
                    if (value != 0.0F && value < minWarningIconBobbingInterval)//
                        return Optional.of(Text.translatable("text.cloth-config.error.too_small", minWarningIconBobbingInterval));
                    ArmorHudMod.previewConfig.setWarningIconBobbingIntervalMs(value * 1000.0F);
                    return Optional.empty();
                })
                //.setMin(minWarningIconBobbingInterval)
                .setMax(5.0F)
                .build();
        category.addEntry(warningIconBobbingIntervalEntry);
        category.addEntry(configEntryBuilder.startTextDescription(Text.translatable("armorHud.configScreen.setting.warningIconBobbingIntervalEntry.description").formatted(Formatting.GRAY)).build());

        return configBuilder.build();
    }

    private static Text anchorToText(Anchor value) {
        return Text.of(value.name());
    }

    private static Text offhandToText(OffhandSlotBehavior value) {
        return Text.of(value.name());
    }

    private static Text widgetShownToText(WidgetShown value) {
        return Text.of(value.name());
    }

    private static Text styleToText(Style value) {
        return Text.of(value.name());
    }
}
