package ru.berdinskiybear.armorhud.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

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
            this.drawTexture(matrices, center - defaultHorizontalOffset - width - ((amount - 1) * step) - offhandSlotOffset, this.scaledHeight - height + 2, 0, 0, amount * step + 1, height);
            this.drawTexture(matrices, center - defaultHorizontalOffset - width + step - offhandSlotOffset, this.scaledHeight - height + 2, 180, 0, width - step, height);
        }
    }
}
