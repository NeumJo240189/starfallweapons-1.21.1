package com.jonas.starfallweapons.weapon;

import java.util.List;
import java.util.Objects;

import net.minecraft.resources.ResourceLocation;

/**
 * Immutable gameplay and presentation data of one weapon type.
 *
 * <p>Definitions contain no mutable player state. Cooldowns, upgrades and combo progress will be
 * stored by their dedicated systems later.</p>
 */
public record WeaponDefinition(
		ResourceLocation itemId,
		WeaponRarity rarity,
		float damage,
		String description,
		List<WeaponPassive> passives,
		List<WeaponSkill> skills,
		String ultimate) {
	public WeaponDefinition {
		Objects.requireNonNull(itemId, "itemId");
		Objects.requireNonNull(rarity, "rarity");
		Objects.requireNonNull(description, "description");
		passives = List.copyOf(passives);
		skills = List.copyOf(skills);
		Objects.requireNonNull(ultimate, "ultimate");


		if (damage < 0.0F) {
			throw new IllegalArgumentException("damage cannot be negative");
		}
	}
}
