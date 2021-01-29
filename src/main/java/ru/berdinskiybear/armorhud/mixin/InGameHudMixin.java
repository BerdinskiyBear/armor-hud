package ru.berdinskiybear.armorhud.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private Random random;

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    private static final Identifier armorHud_WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    private static final Identifier armorHud_EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    private static final Identifier armorHud_EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    private static final Identifier armorHud_EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    private static final Identifier armorHud_EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
    private static final Identifier armorHud_BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");

    private static final int armorHud_step = 20;
    private static final int armorHud_width = 22;
    private static final int armorHud_height = 22;
    private static final int armorHud_defaultHotbarOffset = 98;
    private static final int armorHud_defaultOffhandSlotOffset = 29;
    private static final int armorHud_defaultHotbarAttackIndicatorOffset = 23;
    private static final int armorHud_minWarningHeight = 2;
    private static final int armorHud_maxWarningHeight = 7;
    private static final int armorHud_warningHorizontalOffset = 7;

    private long armorHud_lastMeasuredTime;
    private long armorHud_measuredTime;
    private float[] armorHud_cycleProgress = null;
    private final List<ItemStack> armorHud_armorItems = new ArrayList<>(4);
    private final List<Integer> armorHud_armorItemIndexes = new ArrayList<>(4);
    private int armorHud_shift = 0;

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack);

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    public void armorHud_renderArmorHud(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        // add this to profiler
        this.client.getProfiler().push("armorHud");

        // getting current config
        ArmorHudConfig currentArmorHudConfig = this.armorHud_getCurrentArmorHudConfig();

        // updating measuring time fields
        {
            armorHud_lastMeasuredTime = armorHud_measuredTime;
            armorHud_measuredTime = Util.getMeasuringTimeMs();
        }

        // if current config tells that this mod should be enabled then the action starts
        if (currentArmorHudConfig.isEnabled()) {
            PlayerEntity playerEntity = this.getCameraPlayer();
            if (playerEntity != null) {
                int amount = 0;

                // here we count the items, save ones that we need to draw and their indexes
                {
                    List<ItemStack> armor = playerEntity.inventory.armor;
                    armorHud_armorItems.clear();
                    armorHud_armorItemIndexes.clear();
                    for (int i = 0; i < armor.size(); i++) {
                        ItemStack itemStack = armor.get(i);
                        if (!itemStack.isEmpty())
                            amount++;
                        if (!itemStack.isEmpty() || currentArmorHudConfig.getWidgetShown() != ArmorHudConfig.WidgetShown.NOT_EMPTY) {
                            armorHud_armorItems.add(itemStack);
                            armorHud_armorItemIndexes.add(i);
                        }
                    }
                }

                // if amount of armor items is not 0 or if we allow to show empty widget then we prepare and draw
                if (amount > 0 || currentArmorHudConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.ALWAYS) {
                    final int armorWidgetY;
                    final int armorWidgetX;
                    final int sideMultiplier;
                    final int sideOffsetMultiplier;
                    final int verticalMultiplier;
                    final int verticalOffsetMultiplier;
                    final int widgetWidth;
                    final int slots;

                    // here i calculate position of the widget, its width and all sorts of multipliers based of current config
                    {
                        if ((currentArmorHudConfig.getAnchor() == ArmorHudConfig.Anchor.HOTBAR && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.LEFT) || (currentArmorHudConfig.getAnchor() != ArmorHudConfig.Anchor.HOTBAR && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.RIGHT)) {
                            sideMultiplier = -1;
                            sideOffsetMultiplier = -1;
                        } else {
                            sideMultiplier = 1;
                            sideOffsetMultiplier = 0;
                        }

                        switch (currentArmorHudConfig.getAnchor()) {
                            case TOP:
                            case TOP_CENTER:
                                verticalMultiplier = 1;
                                verticalOffsetMultiplier = 0;
                                break;
                            case HOTBAR:
                            case BOTTOM:
                                verticalMultiplier = -1;
                                verticalOffsetMultiplier = -1;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + currentArmorHudConfig.getAnchor());
                        }

                        int addedHotbarOffset;
                        switch (currentArmorHudConfig.getOffhandSlotBehavior()) {
                            case ALWAYS_IGNORE:
                                addedHotbarOffset = 0;
                                break;
                            case ALWAYS_LEAVE_SPACE:
                                addedHotbarOffset = Math.max(armorHud_defaultOffhandSlotOffset, armorHud_defaultHotbarAttackIndicatorOffset);
                                break;
                            case ADHERE:
                                if (
                                        (
                                                playerEntity.getMainArm().getOpposite() == Arm.LEFT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.LEFT
                                                        || playerEntity.getMainArm().getOpposite() == Arm.RIGHT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.RIGHT
                                        ) && !playerEntity.getOffHandStack().isEmpty()
                                )
                                    addedHotbarOffset = armorHud_defaultOffhandSlotOffset;
                                else if (
                                        (
                                                playerEntity.getMainArm() == Arm.LEFT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.LEFT
                                                        || playerEntity.getMainArm() == Arm.RIGHT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.RIGHT
                                        ) && this.client.options.attackIndicator == AttackIndicator.HOTBAR
                                )
                                    addedHotbarOffset = armorHud_defaultHotbarAttackIndicatorOffset;
                                else
                                    addedHotbarOffset = 0;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + currentArmorHudConfig.getOffhandSlotBehavior());
                        }

                        int armorWidgetY1;
                        switch (currentArmorHudConfig.getAnchor()) {
                            case BOTTOM:
                            case HOTBAR:
                                armorWidgetY1 = this.scaledHeight - armorHud_height;
                                break;
                            case TOP:
                            case TOP_CENTER:
                                armorWidgetY1 = 0;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + currentArmorHudConfig.getAnchor());
                        }

                        slots = currentArmorHudConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.NOT_EMPTY ? amount : 4;
                        widgetWidth = armorHud_width + ((slots - 1) * armorHud_step);

                        int armorWidgetX1;
                        switch (currentArmorHudConfig.getAnchor()) {
                            case TOP_CENTER:
                                armorWidgetX1 = this.scaledWidth / 2 - (widgetWidth / 2);
                                break;
                            case TOP:
                            case BOTTOM:
                                armorWidgetX1 = (widgetWidth - this.scaledWidth) * sideOffsetMultiplier;
                                break;
                            case HOTBAR:
                                armorWidgetX1 = this.scaledWidth / 2 + ((armorHud_defaultHotbarOffset + addedHotbarOffset) * sideMultiplier) + (widgetWidth * sideOffsetMultiplier);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + currentArmorHudConfig.getAnchor());
                        }

                        armorWidgetX1 += currentArmorHudConfig.getOffsetX() * sideMultiplier;
                        armorWidgetY1 += currentArmorHudConfig.getOffsetY() * verticalMultiplier;

                        armorWidgetY = armorWidgetY1;
                        armorWidgetX = armorWidgetX1;
                    }

                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    this.client.getTextureManager().bindTexture(armorHud_WIDGETS_TEXTURE);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();

                    // here i draw the slots
                    switch (currentArmorHudConfig.getStyle()) {
                        case STYLE_1_E:
                            this.drawSlots1(matrices, armorWidgetY, armorWidgetX, widgetWidth, 3);
                            break;
                        case STYLE_1_H:
                            this.drawSlots1(matrices, armorWidgetY, armorWidgetX, widgetWidth, armorHud_width / 2);
                            break;
                        case STYLE_1_S:
                            this.drawSlots1(matrices, armorWidgetY, armorWidgetX, widgetWidth, (armorHud_width + armorHud_step) / 2);
                            break;
                        case STYLE_2_E:
                            this.drawSlots2(matrices, armorWidgetY, armorWidgetX, widgetWidth, 3);
                            break;
                        case STYLE_2_H:
                            this.drawSlots2(matrices, armorWidgetY, armorWidgetX, widgetWidth, armorHud_width / 2);
                            break;
                        case STYLE_2_S:
                            this.drawSlots2(matrices, armorWidgetY, armorWidgetX, widgetWidth, (armorHud_width + armorHud_step) / 2);
                            break;
                        case STYLE_3:
                            this.drawTexture(matrices, armorWidgetX, armorWidgetY, 24, 23, (armorHud_width - armorHud_step) / 2, armorHud_height);
                            for (int i = 0; i < slots; i++)
                                this.drawTexture(matrices, armorWidgetX + (armorHud_width - armorHud_step) / 2 + i * armorHud_step, armorWidgetY, 24 + (armorHud_width - armorHud_step) / 2, 23, armorHud_step, armorHud_height);
                            this.drawTexture(matrices, armorWidgetX + widgetWidth - (armorHud_width - armorHud_step) / 2, armorWidgetY, 24, 23, (armorHud_width - armorHud_step) / 2, armorHud_height);
                            break;
                    }

                    // here i draw warning icons if necessary
                    if (currentArmorHudConfig.isWarningShown()) {
                        for (int i = 0; i < armorHud_armorItems.size(); i++) {
                            int iReversed = currentArmorHudConfig.isReversed() ? (armorHud_armorItems.size() - i - 1) : i;
                            if (!armorHud_armorItems.get(i).isEmpty() && armorHud_armorItems.get(i).isDamageable()) {
                                int damage = armorHud_armorItems.get(i).getDamage();
                                int maxDamage = armorHud_armorItems.get(i).getMaxDamage();
                                if ((1.0F - ((float) damage) / ((float) maxDamage) <= currentArmorHudConfig.getMinDurabilityPercentage()) || (maxDamage - damage <= currentArmorHudConfig.getMinDurabilityValue())) {
                                    this.drawTexture(matrices,
                                            armorWidgetX + (armorHud_step * iReversed) + armorHud_warningHorizontalOffset,
                                            armorWidgetY
                                                    + (armorHud_height * (verticalOffsetMultiplier + 1))
                                                    + (8 * verticalOffsetMultiplier)
                                                    + ((armorHud_minWarningHeight + Math.round(Math.abs(this.armorHud_getCycleProgress(armorHud_armorItemIndexes.get(i), currentArmorHudConfig) * 2.0F - 1.0F) * armorHud_maxWarningHeight)) * verticalMultiplier),
                                            238,
                                            22,
                                            8,
                                            8);
                                }
                            }
                        }
                    }

                    // here i blend in slot icons if so tells the current config
                    if (currentArmorHudConfig.getIconsShown()) {
                        if (currentArmorHudConfig.getWidgetShown() != ArmorHudConfig.WidgetShown.NOT_EMPTY && (amount > 0 || currentArmorHudConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.ALWAYS)) {
                            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
                            for (int i = 0; i < armorHud_armorItems.size(); i++) {
                                if (armorHud_armorItems.get(i).isEmpty()) {
                                    Identifier spriteId;
                                    switch (i) {
                                        case 0:
                                            spriteId = armorHud_EMPTY_BOOTS_SLOT_TEXTURE;
                                            break;
                                        case 1:
                                            spriteId = armorHud_EMPTY_LEGGINGS_SLOT_TEXTURE;
                                            break;
                                        case 2:
                                            spriteId = armorHud_EMPTY_CHESTPLATE_SLOT_TEXTURE;
                                            break;
                                        case 3:
                                        default:
                                            spriteId = armorHud_EMPTY_HELMET_SLOT_TEXTURE;
                                            break;
                                    }
                                    Sprite sprite = this.client.getSpriteAtlas(armorHud_BLOCK_ATLAS_TEXTURE).apply(spriteId);
                                    this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());

                                    int iReversed = currentArmorHudConfig.isReversed() ? (armorHud_armorItems.size() - i - 1) : i;
                                    drawSprite(matrices, armorWidgetX + (armorHud_step * iReversed) + 3, armorWidgetY + 3, getZOffset() + 1, 16, 16, sprite);
                                }
                            }
                        }
                    }

                    RenderSystem.enableRescaleNormal();
                    RenderSystem.defaultBlendFunc();

                    // and at last i draw the armour items
                    for (int i = 0; i < armorHud_armorItems.size(); i++) {
                        int iReversed = currentArmorHudConfig.isReversed() ? (armorHud_armorItems.size() - i - 1) : i;
                        if (!armorHud_armorItems.get(i).isEmpty()) {
                            this.renderHotbarItem(armorWidgetX + (armorHud_step * iReversed) + 3, armorWidgetY + 3, tickDelta, playerEntity, armorHud_armorItems.get(i));
                        }
                    }
                }
            }
        }

        // pop this out of profiler
        this.client.getProfiler().pop();
    }

    private void drawSlots1(MatrixStack matrices, int armorWidgetY, int armorWidgetX, int widgetWidth, int endPieceLength) {
        this.drawTexture(matrices, armorWidgetX, armorWidgetY, 0, 0, widgetWidth - endPieceLength, armorHud_height);
        this.drawTexture(matrices, armorWidgetX + widgetWidth - endPieceLength, armorWidgetY, 182 - endPieceLength, 0, endPieceLength, armorHud_height);
    }

    private void drawSlots2(MatrixStack matrices, int armorWidgetY, int armorWidgetX, int widgetWidth, int endPieceLength) {
        this.drawTexture(matrices, armorWidgetX, armorWidgetY, 24, 23, endPieceLength, armorHud_height);
        if (widgetWidth > endPieceLength * 2)
            this.drawTexture(matrices, armorWidgetX + endPieceLength, armorWidgetY, endPieceLength, 0, widgetWidth - 2 * endPieceLength, armorHud_height);
        if (widgetWidth - endPieceLength < endPieceLength)
            endPieceLength = widgetWidth - endPieceLength;
        this.drawTexture(matrices, armorWidgetX + widgetWidth - endPieceLength, armorWidgetY, 24 + armorHud_width - endPieceLength, 23, endPieceLength, armorHud_height);
    }

    private float armorHud_getCycleProgress(int index, ArmorHudConfig currentArmorHudConfig) {
        // if warning icon bobbing cycle progress array wasn't initialised we do that now
        if (armorHud_cycleProgress == null) {
            armorHud_cycleProgress = new float[]{this.random.nextFloat(), this.random.nextFloat(), this.random.nextFloat(), this.random.nextFloat()};
        }

        // if interval was set to 0 then it means that icons should not bob so we always return 0.5 and don't update progress
        if (currentArmorHudConfig.getWarningIconBobbingIntervalMs() == 0.0F) {
            return 0.5F;
        }

        // we want progress updated only when we want icons to move, that is if game is not paused or config screen is open
        if (!this.client.isPaused() || currentArmorHudConfig.isPreview()) {
            armorHud_cycleProgress[index] += (armorHud_measuredTime - armorHud_lastMeasuredTime) / currentArmorHudConfig.getWarningIconBobbingIntervalMs();
            armorHud_cycleProgress[index] %= 1.0F;

            // just in case progress became less than 0 or NaN we set it to some random value
            if (armorHud_cycleProgress[index] < 0 || Float.isNaN(armorHud_cycleProgress[index]))
                armorHud_cycleProgress[index] = this.random.nextFloat();
        }

        return armorHud_cycleProgress[index];
    }

    /**
     * This function determines which config is supposed to be current. Usually the loaded config is considered current
     * but if config screen is open then the preview config is used as current.
     *
     * @return Current config
     */
    private ArmorHudConfig armorHud_getCurrentArmorHudConfig() {
        return this.client.currentScreen != null && this.client.currentScreen.getTitle() == ArmorHudMod.CONFIG_SCREEN_NAME ? ArmorHudMod.previewConfig : ArmorHudMod.getCurrentConfig();
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V", shift = At.Shift.AFTER))
    public void calculateStatusEffectIconsOffset(MatrixStack matrices, CallbackInfo ci) {
        ArmorHudConfig currentConfig = this.armorHud_getCurrentArmorHudConfig();
        if (currentConfig.isEnabled()) {
            int add = 0;
            if (currentConfig.getAnchor() == ArmorHudConfig.Anchor.TOP && currentConfig.getSide() == ArmorHudConfig.Side.RIGHT) {
                int amount = 0;
                PlayerEntity playerEntity = this.getCameraPlayer();
                if (playerEntity != null) {
                    for (ItemStack itemStack : playerEntity.inventory.armor) {
                        if (!itemStack.isEmpty())
                            amount++;
                    }

                    if (amount > 0 || currentConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.ALWAYS) {
                        add += 22 + currentConfig.getOffsetY();
                        if (currentConfig.isWarningShown() && this.armorHud_armorItems.stream().anyMatch((ItemStack itemStack) -> {
                            if (itemStack.isDamageable()) {
                                final int damage = itemStack.getDamage();
                                final int maxDamage = itemStack.getMaxDamage();
                                return ((1.0F - ((float) damage) / ((float) maxDamage) <= currentConfig.getMinDurabilityPercentage()) || (maxDamage - damage <= currentConfig.getMinDurabilityValue()));
                            }
                            return false;
                        })) {
                            add += 2 + 8;
                            if (currentConfig.getWarningIconBobbingIntervalMs() != 0.0F) {
                                add += 7;
                            }
                        }
                    }
                }
            }
            this.armorHud_shift = Math.max(add, 0);
        } else
            this.armorHud_shift = 0;
    }

    @ModifyVariable(method = "renderStatusEffectOverlay", at = @At(value = "STORE", ordinal = 0), ordinal = 3)
    public int statusEffectIconsOffset(int y) {
        return y + this.armorHud_shift;
    }
}
