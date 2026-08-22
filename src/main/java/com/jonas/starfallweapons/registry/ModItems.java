package com.jonas.starfallweapons.registry;

import com.jonas.starfallweapons.StarfallWeapons;
import com.jonas.starfallweapons.item.LastFerrymanItem;
import com.jonas.starfallweapons.item.StarfallSwordItem;
import com.jonas.starfallweapons.weapon.WeaponRegistry;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Vanilla-registry bindings for all Starfall item types. */
public final class ModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(StarfallWeapons.MOD_ID);

	public static final DeferredItem<Item> TEST_SWORD = REGISTRY.register("test_sword",
			() -> new StarfallSwordItem(WeaponRegistry.TEST_SWORD_DEFINITION));
	public static final DeferredItem<Item> LAST_FERRYMAN = REGISTRY.register("last_ferryman",
			() -> new LastFerrymanItem(WeaponRegistry.LAST_FERRYMAN_DEFINITION));

	private ModItems() {
	}
}
