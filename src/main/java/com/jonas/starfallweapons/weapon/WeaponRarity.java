package com.jonas.starfallweapons.weapon;

/** Rarity tiers used by weapon data, tooltips, loot and future balancing systems. */
public enum WeaponRarity {
	COMMON(0xFFB8BEC8),
	UNCOMMON(0xFF55D68A),
	RARE(0xFF4DA6FF),
	EPIC(0xFFA66CFF),
	LEGENDARY(0xFFFFB84D),
	MYTHIC(0xFFFF4F81);

	private final int color;

	WeaponRarity(int color) {
		this.color = color;
	}

	/** Returns the ARGB colour used by client-facing rarity renderers. */
	public int color() {
		return color;
	}
}
