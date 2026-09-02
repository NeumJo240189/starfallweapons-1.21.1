package com.jonas.starfallweapons;

import com.jonas.starfallweapons.weapon.WeaponRegistry;
import com.jonas.starfallweapons.network.StarfallPayloads;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Entry point for Starfall's handwritten systems.
 *
 * <p>The bootstrap contains only lifecycle wiring; gameplay code remains in focused subsystems.</p>
 */
public final class StarfallWeaponsBootstrap {
	private StarfallWeaponsBootstrap() {
	}

	/** Connects handwritten lifecycle listeners to the NeoForge mod event bus. */
	public static void initialize(IEventBus modEventBus) {
		modEventBus.addListener(StarfallWeaponsBootstrap::onCommonSetup);
		modEventBus.addListener(StarfallPayloads::register);
	}

	public static void onCommonSetup(FMLCommonSetupEvent event) {
		WeaponRegistry.bootstrap();
	}
}
