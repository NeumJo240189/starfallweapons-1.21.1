package com.jonas.starfallweapons.weapon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jonas.starfallweapons.StarfallWeapons;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Connects NeoForge-registered item ids to Starfall weapon definitions.
 *
 * <p>This is intentionally a gameplay registry rather than a second Minecraft item registry:
 * {@code ModItems} remains responsible for creating and registering the actual item.</p>
 */
public final class WeaponRegistry {
	private static final Map<ResourceLocation, WeaponDefinition> DEFINITIONS = new HashMap<>();
	private static boolean bootstrapped;

	private WeaponRegistry() {
	}

	/** Registers all built-in weapon definitions once during common setup. */
	public static void bootstrap() {
		if (bootstrapped) {
			return;
		}
		bootstrapped = true;

		register(new WeaponDefinition(
				ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "test_sword"),
				WeaponRarity.RARE,
				7.0F,
				"A prototype blade that draws power from distant stars.",
				List.of(new WeaponPassive(
						ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "star_energy"),
						"Star Energy",
						"Every strike resonates with stored stellar energy.",
						ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/starfall_skill_test.png"))),
				List.of(
						new WeaponSkill(
								ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "star_slash"),
								"Star Slash",
								"A fast slash infused with stellar energy.",
								ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/starfall_skill_test.png")),
						new WeaponSkill(
								ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "meteor_impact"),
								"Meteor Impact",
								"Call down a meteor at the targeted location.",
								ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/starfall_skill_test_2.png"))),
				"Celestial Collapse"));
	}

	/** Adds one definition and rejects duplicate item ids during development. */
	public static void register(WeaponDefinition definition) {
		WeaponDefinition previous = DEFINITIONS.putIfAbsent(definition.itemId(), definition);
		if (previous != null) {
			throw new IllegalStateException("A weapon definition is already registered for " + definition.itemId());
		}
	}

	/** Resolves the definition for an item stack, if that item is a Starfall weapon. */
	public static Optional<WeaponDefinition> find(ItemStack stack) {
		if (stack.isEmpty()) {
			return Optional.empty();
		}
		return Optional.ofNullable(DEFINITIONS.get(BuiltInRegistries.ITEM.getKey(stack.getItem())));
	}
}
