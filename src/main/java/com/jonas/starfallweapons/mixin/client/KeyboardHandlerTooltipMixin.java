package com.jonas.starfallweapons.mixin.client;

import com.jonas.starfallweapons.client.tooltip.TooltipPageState;
import net.minecraft.client.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Changes the active tooltip ability list on a discrete Shift key press. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerTooltipMixin {
	@Inject(method = "keyPress", at = @At("HEAD"))
	private void starfall$toggleAbilityMode(long window, int key, int scanCode, int action, int modifiers, CallbackInfo callback) {
		if (action == GLFW.GLFW_PRESS && (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
			TooltipPageState.toggleAbilityMode();
		}
	}
}
