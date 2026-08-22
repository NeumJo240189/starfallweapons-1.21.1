package com.jonas.starfallweapons.registry;

import com.jonas.starfallweapons.StarfallWeapons;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Creative inventory integration for Starfall content. */
public final class ModCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, StarfallWeapons.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STARFALL_WEAPONS = REGISTRY.register("weapons",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.starfallweapons.weapons"))
					.icon(() -> ModItems.LAST_FERRYMAN.get().getDefaultInstance())
					.displayItems((parameters, output) -> {
						output.accept(ModItems.LAST_FERRYMAN.get());
						output.accept(ModItems.TEST_SWORD.get());
					})
					.build());

	private ModCreativeTabs() {
	}
}
