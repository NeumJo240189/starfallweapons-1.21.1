package com.jonas.starfallweapons;

import com.jonas.starfallweapons.registry.ModCreativeTabs;
import com.jonas.starfallweapons.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/** Main entry point for the code-only Starfall Weapons NeoForge mod. */
@Mod(StarfallWeapons.MOD_ID)
public final class StarfallWeaponsMod {
	public StarfallWeaponsMod(IEventBus modEventBus) {
		ModItems.REGISTRY.register(modEventBus);
		ModCreativeTabs.REGISTRY.register(modEventBus);
		StarfallWeaponsBootstrap.initialize(modEventBus);
	}
}
