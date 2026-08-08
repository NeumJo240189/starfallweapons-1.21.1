package com.jonas.starfallweapons.mixin.client;

import com.jonas.starfallweapons.client.tooltip.TooltipPageState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Consumes wheel input only while the tooltip is displaying a selected Starfall skill. */
@Mixin(MouseHandler.class)
public class MouseHandlerTooltipMixin {
	@Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
	private void starfall$changeTooltipPage(long window, double scrollX, double scrollY, CallbackInfo callback) {
		if (Minecraft.getInstance().screen != null && TooltipPageState.changePage(scrollY)) {
			callback.cancel();
		}
	}
}
