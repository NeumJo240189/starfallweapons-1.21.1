package com.jonas.starfallweapons.item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jonas.starfallweapons.weapon.WeaponDefinition;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

/** Placeholder weapon item with two active abilities and two passive traits. */
public class StarfallSwordItem extends SwordItem {
	private static final Map<UUID, Long> LAST_PASSIVE_TICK = new HashMap<>();
	private final WeaponDefinition definition;

	public StarfallSwordItem(WeaponDefinition definition) {
		super(Tiers.IRON, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.IRON, definition.damage(), -2.8F)).rarity(Rarity.RARE));
		this.definition = definition;
	}

	public WeaponDefinition definition() {
		return definition;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker instanceof Player player) {
			triggerAbility(player, 0, true);
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			triggerAbility(player, 1, false);
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}
		triggerAbility(player, 0, false);
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);
		if (!level.isClientSide) {
			return;
		}
		if (!(entity instanceof Player player) || !isSelected || definition.passives().isEmpty()) {
			return;
		}
		long worldTime = level.getGameTime();
		if (stack == player.getMainHandItem() || stack == player.getOffhandItem()) {
			long lastPassiveTick = LAST_PASSIVE_TICK.getOrDefault(player.getUUID(), 0L);
			if (worldTime - lastPassiveTick >= 40L) {
				LAST_PASSIVE_TICK.put(player.getUUID(), worldTime);
				// Passive timing is intentionally kept for future Photon-backed passive logic.
			}
		}
	}

	private void triggerAbility(Player player, int skillIndex, boolean fromHit) {
		if (skillIndex < 0 || skillIndex >= definition.skills().size()) {
			return;
		}
		if (player.getCooldowns().isOnCooldown(this)) {
			return;
		}
		int cooldownTicks = fromHit ? 16 : (skillIndex == 1 ? 36 : 18);
		player.getCooldowns().addCooldown(this, cooldownTicks);
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.55F, 1.15F + skillIndex * 0.2F);
	}
}
