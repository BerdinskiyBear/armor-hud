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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;
import ru.berdinskiybear.armorhud.config.ArmorHudConfigScreenBuilder;

import java.util.ArrayList;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    private static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");

    private static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");

    private int armorWidgetX;
    private int armorWidgetY;
    private ArmorHudConfig currentArmorHudConfig;

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack);

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setZOffset(I)V", ordinal = 1))
    public void renderArmorWidget(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        currentArmorHudConfig = this.client.currentScreen != null && this.client.currentScreen.getTitle() == ArmorHudConfigScreenBuilder.title ? ArmorHudConfigScreenBuilder.previewConfig : ArmorHudMod.getCurrentConfig();
        if (currentArmorHudConfig.isEnabled()) {
            PlayerEntity playerEntity = this.getCameraPlayer();
            int step = 20;
            int width = 22;
            int height = 22;
            int center = this.scaledWidth / 2;
            int defaultHotbarOffset = 98;
            int defaultOffhandSlotOffset = 29;
            int defaultHotbarAttackIndicatorOffset = 23;

            int amount = 0;
            for (ItemStack itemStack : playerEntity.getArmorItems())
                if (!itemStack.isEmpty())
                    amount++;

            if (amount > 0 || currentArmorHudConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.ALWAYS) {
                int sideMultiplier;
                int sideOffsetMultiplier;
                if ((currentArmorHudConfig.getAnchor() == ArmorHudConfig.Anchor.HOTBAR && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.LEFT) || (currentArmorHudConfig.getAnchor() != ArmorHudConfig.Anchor.HOTBAR && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.RIGHT)) {
                    sideMultiplier = -1;
                    sideOffsetMultiplier = -1;
                } else {
                    sideMultiplier = 1;
                    sideOffsetMultiplier = 0;
                }

                int verticalMultiplier;
                switch (currentArmorHudConfig.getAnchor()) {
                    case TOP:
                    case TOP_CENTER:
                        verticalMultiplier = 1;
                        break;
                    case HOTBAR:
                    case BOTTOM:
                        verticalMultiplier = -1;
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
                        addedHotbarOffset = Math.max(defaultOffhandSlotOffset, defaultHotbarAttackIndicatorOffset);
                        break;
                    case ADHERE:
                        if (
                                (
                                        playerEntity.getMainArm().getOpposite() == Arm.LEFT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.LEFT
                                                || playerEntity.getMainArm().getOpposite() == Arm.RIGHT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.RIGHT
                                ) && !playerEntity.getOffHandStack().isEmpty()
                        )
                            addedHotbarOffset = defaultOffhandSlotOffset;
                        else if (
                                (
                                        playerEntity.getMainArm() == Arm.LEFT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.LEFT
                                                || playerEntity.getMainArm() == Arm.RIGHT && currentArmorHudConfig.getSide() == ArmorHudConfig.Side.RIGHT
                                ) && this.client.options.attackIndicator == AttackIndicator.HOTBAR
                        )
                            addedHotbarOffset = defaultHotbarAttackIndicatorOffset;
                        else
                            addedHotbarOffset = 0;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentArmorHudConfig.getOffhandSlotBehavior());
                }

                switch (currentArmorHudConfig.getAnchor()) {
                    case BOTTOM:
                    case HOTBAR:
                        this.armorWidgetY = this.scaledHeight - height;
                        break;
                    case TOP:
                    case TOP_CENTER:
                        this.armorWidgetY = 0;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentArmorHudConfig.getAnchor());
                }

                int slots = currentArmorHudConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.NOT_EMPTY ? amount : 4;
                int widgetWidth = width + ((slots - 1) * step);

                switch (currentArmorHudConfig.getAnchor()) {
                    case TOP_CENTER:
                        this.armorWidgetX = center - (widgetWidth / 2);
                        break;
                    case TOP:
                    case BOTTOM:
                        this.armorWidgetX = (widgetWidth - this.scaledWidth) * sideOffsetMultiplier;
                        break;
                    case HOTBAR:
                        this.armorWidgetX = center + ((defaultHotbarOffset + addedHotbarOffset) * sideMultiplier) + (widgetWidth * sideOffsetMultiplier);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentArmorHudConfig.getAnchor());
                }

                this.armorWidgetX += currentArmorHudConfig.getOffsetX() * sideMultiplier;
                this.armorWidgetY += currentArmorHudConfig.getOffsetY() * verticalMultiplier;

                int endPieceLength = 3;
                this.drawTexture(matrices, this.armorWidgetX, this.armorWidgetY, 0, 0, widgetWidth - endPieceLength, height);
                this.drawTexture(matrices, this.armorWidgetX + widgetWidth - endPieceLength, this.armorWidgetY, 179, 0, endPieceLength, height);
            }
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V", ordinal = 0, shift = At.Shift.AFTER))
    public void renderArmorItems(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (currentArmorHudConfig.isEnabled()) {
            PlayerEntity playerEntity = this.getCameraPlayer();
            ArrayList<ItemStack> armorItems = new ArrayList<>(4);
            for (ItemStack itemStack : playerEntity.getArmorItems())
                if (!itemStack.isEmpty() || currentArmorHudConfig.getWidgetShown() != ArmorHudConfig.WidgetShown.NOT_EMPTY)
                    armorItems.add(itemStack);

            int step = 20;
            int amount = 0;
            for (int i = 0; i < armorItems.size(); i++) {
                int iReversed = currentArmorHudConfig.isReversed() ? (armorItems.size() - i - 1) : i;
                if (!armorItems.get(i).isEmpty()) {
                    this.renderHotbarItem(this.armorWidgetX + (step * iReversed) + 3, this.armorWidgetY + 3, tickDelta, playerEntity, armorItems.get(i));
                    amount++;
                }
            }

            if (currentArmorHudConfig.getIconsShown())
                if (currentArmorHudConfig.getWidgetShown() != ArmorHudConfig.WidgetShown.NOT_EMPTY && (amount > 0 || currentArmorHudConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.ALWAYS)) {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
                    for (int i = 0; i < armorItems.size(); i++) {
                        if (armorItems.get(i).isEmpty()) {
                            Identifier spriteId;
                            switch (i) {
                                case 0:
                                    spriteId = EMPTY_BOOTS_SLOT_TEXTURE;
                                    break;
                                case 1:
                                    spriteId = EMPTY_LEGGINGS_SLOT_TEXTURE;
                                    break;
                                case 2:
                                    spriteId = EMPTY_CHESTPLATE_SLOT_TEXTURE;
                                    break;
                                case 3:
                                default:
                                    spriteId = EMPTY_HELMET_SLOT_TEXTURE;
                                    break;
                            }
                            Sprite sprite = this.client.getSpriteAtlas(BLOCK_ATLAS_TEXTURE).apply(spriteId);
                            this.client.getTextureManager().bindTexture(sprite.getAtlas().getId());

                            int iReversed = currentArmorHudConfig.isReversed() ? (armorItems.size() - i - 1) : i;
                            drawSprite(matrices, this.armorWidgetX + (step * iReversed) + 3, this.armorWidgetY + 3, getZOffset() + 1, 16, 16, sprite);
                        }
                    }
                    RenderSystem.defaultBlendFunc();
                }
        }
    }

    //todo add warning rendering
}
