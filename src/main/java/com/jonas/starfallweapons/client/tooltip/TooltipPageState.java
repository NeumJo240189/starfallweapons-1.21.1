package com.jonas.starfallweapons.client.tooltip;

import java.util.Optional;

import com.jonas.starfallweapons.weapon.WeaponDefinition;
import com.jonas.starfallweapons.weapon.WeaponRegistry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Holds the selected page only while a weapon tooltip is actively being rendered. */
public final class TooltipPageState {
	private static final long VISIBILITY_GRACE_MILLIS = 250L;

	private static ResourceLocation displayedItemId;
	private static WeaponDefinition displayedWeapon;
	private static int selectedSkillIndex;
	private static int selectedPassiveIndex;
	private static boolean passiveMode;
	private static long lastRenderTime;
	private static long visibleSince;

	private TooltipPageState() {
	}

	public static Optional<WeaponTooltipPage> updateAndGetPage(ItemStack stack) {
		Optional<WeaponDefinition> weapon = WeaponRegistry.find(stack);
		if (weapon.isEmpty()) {
			return Optional.empty();
		}

		ResourceLocation itemId = weapon.get().itemId();
		if (!itemId.equals(displayedItemId)) {
			displayedItemId = itemId;
			displayedWeapon = weapon.get();
			selectedSkillIndex = 0;
			selectedPassiveIndex = 0;
			passiveMode = false;
			visibleSince = Util.getMillis();
		}
		lastRenderTime = Util.getMillis();

		if (passiveMode && !weapon.get().passives().isEmpty()) {
			return Optional.of(WeaponTooltipModel.createPassivePage(weapon.get(), selectedPassiveIndex));
		}
		if (!weapon.get().skills().isEmpty()) {
			return Optional.of(WeaponTooltipModel.createSkillPage(weapon.get(), selectedSkillIndex));
		}
		return Optional.empty();
	}

	/** Toggles between skill and passive icon lists while the tooltip is visible. */
	public static boolean toggleAbilityMode() {
		if (displayedWeapon == null || Util.getMillis() - lastRenderTime > VISIBILITY_GRACE_MILLIS || displayedWeapon.passives().isEmpty()) {
			return false;
		}
		passiveMode = !passiveMode;
		return true;
	}

	/** Changes the selected ability in the active icon list. */
	public static boolean changePage(double scrollY) {
		if (displayedItemId == null || Util.getMillis() - lastRenderTime > VISIBILITY_GRACE_MILLIS || scrollY == 0.0D) {
			return false;
		}

		if (displayedWeapon == null) {
			return false;
		}

		if (passiveMode && displayedWeapon.passives().isEmpty()) {
			return false;
		}
		if (!passiveMode && displayedWeapon.skills().isEmpty()) {
			return false;
		}
		if (passiveMode) {
			selectedPassiveIndex = Math.floorMod(selectedPassiveIndex + (scrollY > 0.0D ? -1 : 1), displayedWeapon.passives().size());
		} else {
			selectedSkillIndex = Math.floorMod(selectedSkillIndex + (scrollY > 0.0D ? -1 : 1), displayedWeapon.skills().size());
		}
		return true;
	}

	/** Returns a short, smooth 0..1 tooltip opening animation value. */
	public static float appearanceProgress() {
		return Math.min(1.0F, (Util.getMillis() - visibleSince) / 140.0F);
	}
}
