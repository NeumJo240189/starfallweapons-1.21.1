package com.jonas.starfallweapons.weapon;

/** Rarity tiers used by weapon data, tooltips, loot and future balancing systems. */
public enum WeaponRarity {
	COMMON(0xFFE0E0E0),
	UNCOMMON(0xFF55D65A),
	RARE(0xFF4A90E2),
	EPIC(0xFFB05CFF),
	LEGENDARY(0xFFFFAA2B);

	private final int color;

	WeaponRarity(int color) {
		this.color = color;
	}

	/** Returns the ARGB colour used by client-facing rarity renderers. */
	public int color() {
		return color;
	}
}
