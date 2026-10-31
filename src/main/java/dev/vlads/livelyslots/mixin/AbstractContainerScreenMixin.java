package dev.vlads.livelyslots.mixin;

import dev.vlads.livelyslots.LivelySlots;
import dev.vlads.livelyslots.SlotAnimator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Wraps each slot's drawing in the offset and scale its animation asks for. */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Unique
    private final SlotAnimator livelySlots$animator = new SlotAnimator();
    @Unique
    private boolean livelySlots$pushed;

    @Inject(method = "init", at = @At("TAIL"))
    private void livelySlots$onInit(CallbackInfo ci) {
        livelySlots$animator.onOpen(Util.getMillis());
    }

    @Inject(method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V", at = @At("HEAD"))
    private void livelySlots$onSlotClicked(Slot slot, int slotId, int button, ContainerInput input, CallbackInfo ci) {
        if (slot != null && input == ContainerInput.QUICK_MOVE) {
            livelySlots$animator.onQuickMove(slot, Util.getMillis());
        }
    }

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void livelySlots$beforeSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        livelySlots$pushed = false;
        if (!LivelySlots.config.enabled) return;
        if (!LivelySlots.config.animateCreativeInventory && (Object) this instanceof CreativeModeInventoryScreen) return;

        SlotAnimator.Transform transform = livelySlots$animator.update(slot, Util.getMillis());
        if (transform == null) return;
        float centerX = slot.x + 8;
        float centerY = slot.y + 8;
        graphics.pose().pushMatrix();
        graphics.pose().translate(centerX + transform.dx(), centerY + transform.dy());
        graphics.pose().scale(transform.scale(), transform.scale());
        graphics.pose().translate(-centerX, -centerY);
        livelySlots$pushed = true;
    }

    @Inject(method = "extractSlot", at = @At("RETURN"))
    private void livelySlots$afterSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (livelySlots$pushed) {
            graphics.pose().popMatrix();
            livelySlots$pushed = false;
        }
    }
}
