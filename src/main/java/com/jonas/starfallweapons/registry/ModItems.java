package com.jonas.starfallweapons.registry;

import com.jonas.starfallweapons.StarfallWeapons;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Vanilla-registry bindings for all Starfall item types. */
public final class ModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(StarfallWeapons.MOD_ID);

	public static final DeferredItem<Item> TEST_SWORD = REGISTRY.register("test_sword",
			() -> new SwordItem(Tiers.IRON, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.IRON, 3.0F, -3.0F))));

	private ModItems() {
	}
}
