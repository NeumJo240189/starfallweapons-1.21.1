package com.jonas.starfallweapons.weapon;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;

/**
 * Immutable data for one active weapon skill.
 *
 * <p>{@code iconTexture} points directly at a resource-pack texture, for example
 * {@code starfallweapons:textures/screens/starfall_skill_test.png}. This keeps pixel-art assets
 * independent from Java code and makes them easy to replace as resource-pack assets.</p>
 */
public record WeaponSkill(ResourceLocation id, String name, String description, ResourceLocation iconTexture) {
	public WeaponSkill {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(name, "name");
		Objects.requireNonNull(description, "description");
		Objects.requireNonNull(iconTexture, "iconTexture");
	}
}
