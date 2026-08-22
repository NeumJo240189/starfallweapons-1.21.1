package com.jonas.starfallweapons.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Optional bridge for Photon 1.21.1 NeoForge effects.
 *
 * <p>Photon exposes different class names across builds, so this wrapper resolves the runtime API by
 * reflection instead of hardcoding a single version. When the mod is absent, it quietly falls back to the
 * vanilla particle system.</p>
 */
public final class PhotonCompat {
	private static final List<String> CANDIDATE_TYPES = List.of(
			"lowdragmc.photon.api.PhotonAPI",
			"lowdragmc.photon.Photon",
			"lowdragmc.photon.api.Photon",
			"com.lowdragmc.photon.api.PhotonAPI",
			"com.lowdragmc.photon.Photon");

	private PhotonCompat() {
	}

	public static void spawnSkillBurst(Player player, String effectId, float scale) {
		if (player == null || player.level() == null) {
			return;
		}
		Vec3 origin = player.position().add(0.0D, player.getBbHeight() * 0.5D, 0.0D);
		tryInvoke(player.level(), origin, effectId, scale, 12);
	}

	public static void spawnPassiveAura(Player player, String effectId, float scale) {
		if (player == null || player.level() == null) {
			return;
		}
		Vec3 origin = player.position().add(0.0D, 0.6D, 0.0D);
		tryInvoke(player.level(), origin, effectId, scale, 5);
	}

	private static void tryInvoke(Level level, Vec3 origin, String effectId, float scale, int count) {
		Object bridge = findApiInstance();
		if (bridge == null) {
			return;
		}
		for (Method method : collectMethods(bridge.getClass())) {
			try {
				Class<?>[] parameterTypes = method.getParameterTypes();
				Object[] args = buildArguments(parameterTypes, level, origin, effectId, scale, count);
				if (args == null) {
					continue;
				}
				method.invoke(bridge, args);
				return;
			} catch (ReflectiveOperationException ignored) {
				// Photon differs across build revisions; keep trying the next candidate.
			}
		}
	}

	private static Object findApiInstance() {
		for (String typeName : CANDIDATE_TYPES) {
			try {
				Class<?> type = Class.forName(typeName);
				for (String fieldName : List.of("INSTANCE", "API", "CLIENT", "MANAGER")) {
					try {
						Field field = type.getField(fieldName);
						return field.get(null);
					} catch (ReflectiveOperationException ignored) {
					}
				}
				for (Method method : type.getDeclaredMethods()) {
					if (method.getName().equals("getInstance") || method.getName().equals("getApi") || method.getName().equals("api")) {
						try {
							method.setAccessible(true);
							return method.invoke(null);
						} catch (ReflectiveOperationException ignored) {
						}
					}
				}
			} catch (ClassNotFoundException ignored) {
				// Photon is not present.
			}
		}
		return null;
	}

	private static List<Method> collectMethods(Class<?> type) {
		List<Method> methods = new ArrayList<>();
		for (Method method : type.getMethods()) {
			String name = method.getName();
			if (name.equals("spawn") || name.equals("emit") || name.equals("spawnEffect") || name.equals("spawnFx")
					|| name.equals("fire") || name.equals("createAndSpawn")) {
				methods.add(method);
			}
		}
		return methods;
	}

	private static Object[] buildArguments(Class<?>[] parameterTypes, Level level, Vec3 origin, String effectId, float scale, int count) {
		if (parameterTypes.length == 0) {
			return null;
		}
		Object[] values = new Object[parameterTypes.length];
		for (int index = 0; index < values.length; index++) {
			Class<?> type = parameterTypes[index];
			if (type == Level.class) {
				values[index] = level;
			} else if (type == String.class || type == Object.class) {
				values[index] = effectId;
			} else if (type == float.class || type == Float.class) {
				values[index] = scale;
			} else if (type == double.class || type == Double.class) {
				if (index == 0) {
					values[index] = origin.x;
				} else if (index == 1) {
					values[index] = origin.y;
				} else {
					values[index] = origin.z;
				}
			} else if (type == int.class || type == Integer.class) {
				values[index] = count;
			} else if (type.getSimpleName().equals("Vec3") || type.getName().endsWith("Vec3")) {
				values[index] = origin;
			} else if (type == boolean.class || type == Boolean.class) {
				values[index] = Boolean.FALSE;
			} else {
				return null;
			}
		}
		return values;
	}
}
