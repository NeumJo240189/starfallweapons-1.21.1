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
	public static final WeaponDefinition TEST_SWORD_DEFINITION = new WeaponDefinition(
			ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "test_sword"),
			WeaponRarity.RARE,
			10.0F,
			"Starfall Sword",
			List.of(
					new WeaponPassive(
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "star_energy"),
							"Star Energy",
							"While held, the blade absorbs ambient cosmic energy and hums with a faint celestial glow.",
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/starfall_skill_test.png")),
					new WeaponPassive(
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "nova_guard"),
							"Nova Guard",
							"Passive placeholder: nearby enemies feel the pressure of the sword's unstable starfire field.",
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/starfall_skill_test_2.png"))),
			List.of(
					new WeaponSkill(
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "star_slash"),
							"Star Slash",
							"Primary ability placeholder: left click unleashes a fast arc of stellar force.",
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/starfall_skill_test.png")),
					new WeaponSkill(
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "meteor_impact"),
							"Meteor Impact",
							"Secondary ability placeholder: shift + right click calls a blazing impact from the sky.",
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/starfall_skill_test_2.png"))),
			"Celestial Collapse");
	public static final WeaponDefinition LAST_FERRYMAN_DEFINITION = new WeaponDefinition(
			ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "last_ferryman"),
			WeaponRarity.MYTHIC,
			7.5F,
			"A soulbound oarblade that ferries the fallen across still waters.",
			List.of(),
			List.of(
					new WeaponSkill(
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "departure"),
							"Departure",
							"Release a ferry wave in a 15-block radius, damaging and briefly stunning all enemies once.",
							ResourceLocation.fromNamespaceAndPath(StarfallWeapons.MOD_ID, "textures/screens/ferryman_first_skill_icon.png"))),
			"Those who hear the bell have already crossed.");
	private static boolean bootstrapped;

	private WeaponRegistry() {
	}

	/** Registers all built-in weapon definitions once during common setup. */
	public static void bootstrap() {
		if (bootstrapped) {
			return;
		}
		bootstrapped = true;
		register(TEST_SWORD_DEFINITION);
		register(LAST_FERRYMAN_DEFINITION);
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
