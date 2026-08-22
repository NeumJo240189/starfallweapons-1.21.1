package com.jonas.starfallweapons.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jonas.starfallweapons.weapon.WeaponDefinition;
import com.jonas.starfallweapons.weapon.WeaponRarity;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Ferryman weapon with custom M1 slash and the Departure radial skill. */
public class LastFerrymanItem extends SwordItem {
	private static final double M1_RANGE = 4.0D;
	private static final double M1_ARC_DOT = 0.22D;
	private static final float M1_DAMAGE = 6.0F;
	private static final int M1_SWING_INTERVAL_TICKS = 8;

	private static final double DEPARTURE_RADIUS = 15.0D;
	private static final float DEPARTURE_DAMAGE = 8.0F;
	private static final int DEPARTURE_STUN_TICKS = 34;
	private static final int DEPARTURE_COOLDOWN_TICKS = 160;

	private static final Vector3f COLOR_DEEP = rgb(0x04, 0x32, 0x3F);
	private static final Vector3f COLOR_TEAL = rgb(0x07, 0x7E, 0x7A);
	private static final Vector3f COLOR_MINT = rgb(0x0B, 0xBD, 0x8B);
	private static final Vector3f COLOR_LIGHT = rgb(0xD9, 0xFC, 0xCB);

	private static final Map<UUID, Long> NEXT_M1_TICK = new HashMap<>();

	public LastFerrymanItem(WeaponDefinition definition) {
		super(Tiers.DIAMOND, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.DIAMOND, definition.damage(), -2.7F)).rarity(toVanillaRarity(definition.rarity())));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		if (!(entity instanceof Player player) || player.getMainHandItem() != stack) {
			return super.onEntitySwing(stack, entity);
		}
		Level level = player.level();
		if (level.isClientSide) {
			return false;
		}
		if (!(level instanceof ServerLevel serverLevel)) {
			return false;
		}
		long now = serverLevel.getGameTime();
		long nextTick = NEXT_M1_TICK.getOrDefault(player.getUUID(), 0L);
		if (now < nextTick) {
			return false;
		}
		NEXT_M1_TICK.put(player.getUUID(), now + M1_SWING_INTERVAL_TICKS);
		performFerrymanSlash(serverLevel, player);
		return false;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			return InteractionResultHolder.pass(stack);
		}
		if (player.getCooldowns().isOnCooldown(this)) {
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
		}
		if (level instanceof ServerLevel serverLevel) {
			castDeparture(serverLevel, player);
			player.getCooldowns().addCooldown(this, DEPARTURE_COOLDOWN_TICKS);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return super.hurtEnemy(stack, target, attacker);
	}

	private void performFerrymanSlash(ServerLevel level, Player player) {
		Vec3 origin = player.getEyePosition().add(0.0D, -0.35D, 0.0D);
		Vec3 look = player.getLookAngle();
		AABB area = player.getBoundingBox().inflate(M1_RANGE, 1.8D, M1_RANGE).expandTowards(look.scale(M1_RANGE));
		List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, candidate -> isEnemy(player, candidate)
				&& candidate.distanceToSqr(player) <= M1_RANGE * M1_RANGE
				&& isInsideSlashCone(origin, look, candidate));

		for (LivingEntity target : targets) {
			target.hurt(level.damageSources().playerAttack(player), M1_DAMAGE);
			Vec3 push = target.position().subtract(player.position()).normalize().scale(0.35D);
			target.push(push.x, 0.12D, push.z);
		}

		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.85F, 0.75F);
		spawnSlashParticles(level, player, look);
	}

	private void castDeparture(ServerLevel level, Player player) {
		AABB area = player.getBoundingBox().inflate(DEPARTURE_RADIUS);
		List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
				candidate -> isEnemy(player, candidate) && candidate.distanceToSqr(player) <= DEPARTURE_RADIUS * DEPARTURE_RADIUS);

		for (LivingEntity target : targets) {
			target.hurt(level.damageSources().playerAttack(player), DEPARTURE_DAMAGE);
			applyStun(target);
			Vec3 outward = target.position().subtract(player.position());
			if (outward.lengthSqr() > 0.0001D) {
				Vec3 normalized = outward.normalize().scale(0.55D);
				target.push(normalized.x, 0.2D, normalized.z);
			}
		}

		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.9F, 0.7F);
		spawnDepartureWave(level, player);
	}

	private static boolean isEnemy(Player source, LivingEntity candidate) {
		if (candidate == source || !candidate.isAlive() || candidate.isSpectator()) {
			return false;
		}
		return !candidate.isAlliedTo(source);
	}

	private static boolean isInsideSlashCone(Vec3 origin, Vec3 look, LivingEntity candidate) {
		Vec3 toTarget = candidate.getBoundingBox().getCenter().subtract(origin);
		Vec3 planarDirection = new Vec3(look.x, 0.0D, look.z).normalize();
		Vec3 planarTarget = new Vec3(toTarget.x, 0.0D, toTarget.z);
		if (planarTarget.lengthSqr() < 0.0001D) {
			return true;
		}
		Vec3 normalizedTarget = planarTarget.normalize();
		return normalizedTarget.dot(planarDirection) >= M1_ARC_DOT;
	}

	private static void applyStun(LivingEntity target) {
		target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DEPARTURE_STUN_TICKS, 10, false, true, true));
		target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, DEPARTURE_STUN_TICKS, 2, false, true, true));
		target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DEPARTURE_STUN_TICKS, 1, false, true, true));
		target.setDeltaMovement(target.getDeltaMovement().multiply(0.12D, 0.0D, 0.12D));
		target.hurtMarked = true;
	}

	private static void spawnSlashParticles(ServerLevel level, Player player, Vec3 look) {
		double yaw = Math.atan2(look.z, look.x);
		for (int i = 0; i <= 24; i++) {
			double spread = Math.toRadians(-56.0D + (112.0D * i / 24.0D));
			double distance = 1.0D + (M1_RANGE - 1.0D) * (i / 24.0D);
			double x = player.getX() + Math.cos(yaw + spread) * distance;
			double z = player.getZ() + Math.sin(yaw + spread) * distance;
			double y = player.getY() + 1.0D + 0.15D * Math.sin(i * 0.7D);
			level.sendParticles(new DustParticleOptions(i % 2 == 0 ? COLOR_MINT : COLOR_TEAL, 1.1F), x, y, z, 1, 0.01D, 0.01D, 0.01D, 0.0D);
			level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y + 0.05D, z, 1, 0.01D, 0.01D, 0.01D, 0.0D);
		}
	}

	private static void spawnDepartureWave(ServerLevel level, Player player) {
		double centerX = player.getX();
		double centerY = player.getY() + 0.15D;
		double centerZ = player.getZ();

		for (int ring = 1; ring <= 4; ring++) {
			double radius = ring * 3.75D;
			Vector3f color = ring == 1 ? COLOR_DEEP : ring == 2 ? COLOR_TEAL : ring == 3 ? COLOR_MINT : COLOR_LIGHT;
			int points = 20 + ring * 6;
			for (int i = 0; i < points; i++) {
				double angle = (Math.PI * 2.0D * i) / points;
				double x = centerX + Math.cos(angle) * radius;
				double z = centerZ + Math.sin(angle) * radius;
				double y = centerY + 0.1D * Math.sin(angle * 3.0D + ring);
				level.sendParticles(new DustParticleOptions(color, 1.25F), x, y, z, 1, 0.02D, 0.03D, 0.02D, 0.0D);
				if ((i & 1) == 0) {
					level.sendParticles(ParticleTypes.SOUL, x, y + 0.1D, z, 1, 0.01D, 0.02D, 0.01D, 0.0D);
				}
			}
		}
	}

	private static Rarity toVanillaRarity(WeaponRarity rarity) {
		return switch (rarity) {
			case COMMON -> Rarity.COMMON;
			case UNCOMMON -> Rarity.UNCOMMON;
			case RARE -> Rarity.RARE;
			case EPIC, LEGENDARY, MYTHIC -> Rarity.EPIC;
		};
	}

	private static Vector3f rgb(int red, int green, int blue) {
		return new Vector3f(red / 255.0F, green / 255.0F, blue / 255.0F);
	}
}
