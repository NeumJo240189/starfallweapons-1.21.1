package com.jonas.starfallweapons.mixin.client;

import com.jonas.starfallweapons.client.tooltip.TooltipPageState;
import com.jonas.starfallweapons.client.tooltip.WeaponTooltipRenderer;
import com.jonas.starfallweapons.weapon.WeaponRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Replaces vanilla item tooltips only for items known by {@link WeaponRegistry}. */
@Mixin(GuiGraphics.class)
public class GuiGraphicsTooltipMixin {
	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
	private void starfall$renderWeaponTooltip(Font font, ItemStack stack, int mouseX, int mouseY, CallbackInfo callback) {
		starfall$tryRenderWeaponTooltip(font, stack, mouseX, mouseY, callback);
	}

	/**
	 * Inventory screens call this full overload directly, bypassing the short ItemStack overload
	 * above. Both hooks deliberately share the same renderer.
	 */
	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
	private void starfall$renderWeaponTooltipFromComponents(Font font, List<Component> lines, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY,
			CallbackInfo callback) {
		starfall$tryRenderWeaponTooltip(font, stack, mouseX, mouseY, callback);
	}

	private void starfall$tryRenderWeaponTooltip(Font font, ItemStack stack, int mouseX, int mouseY, CallbackInfo callback) {
		WeaponRegistry.find(stack).ifPresent(weapon -> TooltipPageState.updateAndGetPage(stack).ifPresent(page -> {
			WeaponTooltipRenderer.render((GuiGraphics) (Object) this, font, stack, weapon, page, mouseX, mouseY);
			callback.cancel();
		}));
	}
}
