package ru.berdinskiybear.armorhud.mixin;

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

import java.util.ArrayList;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

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
        int step = 20;
        int width = 24;
        int height = 24;
        int center = this.scaledWidth / 2;
        int defaultHorizontalOffset = 96;
        int offhandSlotOffset = playerEntity.getOffHandStack().isEmpty() ? 0 : 29;
        int amount = 0;
        for (ItemStack itemStack : playerEntity.getArmorItems()) if (!itemStack.isEmpty()) amount++;
        if (amount > 0) {


            offhandSlotOffset = playerEntity.getMainArm() == Arm.RIGHT ? offhandSlotOffset : 0;
            this.drawTexture(matrices, center - defaultHorizontalOffset - width - ((amount - 1) * step) - offhandSlotOffset, this.scaledHeight - height + 2, 0, 0, amount * step + 1, height);
            this.drawTexture(matrices, center - defaultHorizontalOffset - width + step - offhandSlotOffset, this.scaledHeight - height + 2, 180, 0, width - step, height);
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V", ordinal = 0, shift = At.Shift.AFTER))
    public void renderArmorItems(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        PlayerEntity playerEntity = this.getCameraPlayer();
        int step = 20;
        int y = this.scaledHeight - 19;
        int defaultHorizontalOffset = 96;
        int center = this.scaledWidth / 2;
        int offhandSlotOffset = playerEntity.getOffHandStack().isEmpty() ? 0 : 29;
        ArrayList<ItemStack> armorItems = new ArrayList<>(4);
        for (ItemStack itemStack : playerEntity.getArmorItems()) if (!itemStack.isEmpty()) armorItems.add(itemStack);


        offhandSlotOffset = playerEntity.getMainArm() == Arm.RIGHT ? offhandSlotOffset : 0;
        for (int i = 0; i < armorItems.size(); i++) {
            this.renderHotbarItem(center - defaultHorizontalOffset - ((i + 1) * step) - offhandSlotOffset - 1, y, tickDelta, playerEntity, armorItems.get(i));
        }
    }
}
