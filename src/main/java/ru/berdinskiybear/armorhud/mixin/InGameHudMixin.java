package ru.berdinskiybear.armorhud.mixin;

import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
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

    private int armorWidgetX;
    private int armorWidgetY;
    ArmorHudConfig currentConfig;

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack);

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setZOffset(I)V", ordinal = 1))
    public void renderArmorWidget(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        currentConfig = MinecraftClient.getInstance().currentScreen instanceof AbstractConfigScreen && MinecraftClient.getInstance().currentScreen.getTitle() == ArmorHudConfigScreenBuilder.title ? ArmorHudConfigScreenBuilder.previewConfig : ArmorHudMod.getCurrentConfig();
        int step = 20;
        int width = 22;
        int height = 22;
        int center = this.scaledWidth / 2;
        int defaultHotbarOffset = 98;
        int defaultOffhandSlotOffset = 29;

        int amount = 0;
        for (ItemStack itemStack : playerEntity.getArmorItems())
            if (!itemStack.isEmpty())
                amount++;

        if (amount > 0 || currentConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.ALWAYS) {
            int sideMultiplier;
            int sideOffsetMultiplier;
            if ((currentConfig.getAnchor() == ArmorHudConfig.Anchor.HOTBAR && currentConfig.getSide() == ArmorHudConfig.Side.LEFT) || (currentConfig.getAnchor() != ArmorHudConfig.Anchor.HOTBAR && currentConfig.getSide() == ArmorHudConfig.Side.RIGHT)) {
                sideMultiplier = -1;
                sideOffsetMultiplier = -1;
            } else {
                sideMultiplier = 1;
                sideOffsetMultiplier = 0;
            }

            int verticalMultiplier;
            switch (currentConfig.getAnchor()) {
                case TOP:
                case TOP_CENTER:
                    verticalMultiplier = 1;
                    break;
                case HOTBAR:
                case BOTTOM:
                    verticalMultiplier = -1;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currentConfig.getAnchor());
            }

            int offhandSlotOffset;
            switch (currentConfig.getOffhandSlotBehavior()) {
                case ALWAYS_IGNORE:
                    offhandSlotOffset = 0;
                    break;
                case ALWAYS_LEAVE_SPACE:
                    offhandSlotOffset = defaultOffhandSlotOffset;
                    break;
                case ADHERE:
                    if (((playerEntity.getMainArm().getOpposite() == Arm.LEFT && currentConfig.getSide() == ArmorHudConfig.Side.LEFT) || (playerEntity.getMainArm().getOpposite() == Arm.RIGHT && currentConfig.getSide() == ArmorHudConfig.Side.RIGHT)) && !playerEntity.getOffHandStack().isEmpty())
                        offhandSlotOffset = defaultOffhandSlotOffset;
                    else
                        offhandSlotOffset = 0;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currentConfig.getOffhandSlotBehavior());
            }

            switch (currentConfig.getAnchor()) {
                case BOTTOM:
                case HOTBAR:
                    this.armorWidgetY = this.scaledHeight - height;
                    break;
                case TOP:
                case TOP_CENTER:
                    this.armorWidgetY = 0;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currentConfig.getAnchor());
            }

            int slots = currentConfig.getWidgetShown() == ArmorHudConfig.WidgetShown.NOT_EMPTY ? amount : 4;
            int widgetWidth = width + ((slots - 1) * step);

            switch (currentConfig.getAnchor()) {
                case TOP_CENTER:
                    this.armorWidgetX = center - (widgetWidth / 2);
                    break;
                case TOP:
                case BOTTOM:
                    this.armorWidgetX = (widgetWidth - this.scaledWidth) * sideOffsetMultiplier;
                    break;
                case HOTBAR:
                    this.armorWidgetX = center + ((defaultHotbarOffset + offhandSlotOffset) * sideMultiplier) + (widgetWidth * sideOffsetMultiplier);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currentConfig.getAnchor());
            }

            this.armorWidgetX += currentConfig.getOffsetX() * sideMultiplier;
            this.armorWidgetY += currentConfig.getOffsetY() * verticalMultiplier;

            int endPieceLength = 3;
            this.drawTexture(matrices, this.armorWidgetX, this.armorWidgetY, 0, 0, widgetWidth - endPieceLength, height);
            this.drawTexture(matrices, this.armorWidgetX + widgetWidth - endPieceLength, this.armorWidgetY, 179, 0, endPieceLength, height);
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V", ordinal = 0, shift = At.Shift.AFTER))
    public void renderArmorItems(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        ArrayList<ItemStack> armorItems = new ArrayList<>(4);
        for (ItemStack itemStack : playerEntity.getArmorItems())
            if (!itemStack.isEmpty() || currentConfig.getWidgetShown() != ArmorHudConfig.WidgetShown.NOT_EMPTY)
                armorItems.add(itemStack);

        int step = 20;
        for (int i = 0; i < armorItems.size(); i++) {
            int iReversed = currentConfig.isReversed() ? (armorItems.size() - i - 1) : i;
            if (!armorItems.get(i).isEmpty())
                this.renderHotbarItem(this.armorWidgetX + (step * iReversed) + 3, this.armorWidgetY + 3, tickDelta, playerEntity, armorItems.get(i));
        }
    }
}
