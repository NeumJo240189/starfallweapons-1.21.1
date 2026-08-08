package com.jonas.starfallweapons.client.tooltip;

import java.util.List;

import net.minecraft.resources.ResourceLocation;

/** One navigable page in the client-side weapon tooltip. */
public record WeaponTooltipPage(String title, List<String> lines, List<ResourceLocation> iconTextures, int selectedIconIndex) {
	public WeaponTooltipPage {
		lines = List.copyOf(lines);
		iconTextures = List.copyOf(iconTextures);
	}
}
