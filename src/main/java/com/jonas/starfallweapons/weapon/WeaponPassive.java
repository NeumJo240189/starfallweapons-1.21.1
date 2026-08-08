package com.jonas.starfallweapons.weapon;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;

/** Immutable data for one passive weapon ability and its tooltip icon. */
public record WeaponPassive(ResourceLocation id, String name, String description, ResourceLocation iconTexture) {
	public WeaponPassive {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(description, "description");
		Objects.requireNonNull(iconTexture, "iconTexture");
	}
}
