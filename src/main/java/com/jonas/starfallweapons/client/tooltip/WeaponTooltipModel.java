package com.jonas.starfallweapons.client.tooltip;

import java.util.List;
import com.jonas.starfallweapons.weapon.WeaponDefinition;

/** Creates presentation pages from weapon data without knowing anything about GUI rendering. */
public final class WeaponTooltipModel {
	private WeaponTooltipModel() {
	}

	public static WeaponTooltipPage createPassivePage(WeaponDefinition weapon, int passiveIndex) {
		var passive = weapon.passives().get(Math.floorMod(passiveIndex, weapon.passives().size()));
		return new WeaponTooltipPage("Passive " + (Math.floorMod(passiveIndex, weapon.passives().size()) + 1) + "/" + weapon.passives().size() + ": " + passive.name(),
				List.of(passive.description()), weapon.passives().stream().map(passiveAbility -> passiveAbility.iconTexture()).toList(), passiveIndex);
	}

	public static WeaponTooltipPage createSkillPage(WeaponDefinition weapon, int skillIndex) {
		var skill = weapon.skills().get(Math.floorMod(skillIndex, weapon.skills().size()));
		return new WeaponTooltipPage("Skill " + (Math.floorMod(skillIndex, weapon.skills().size()) + 1) + "/" + weapon.skills().size() + ": " + skill.name(),
				List.of(skill.description()), weapon.skills().stream().map(weaponSkill -> weaponSkill.iconTexture()).toList(), skillIndex);
	}
}
