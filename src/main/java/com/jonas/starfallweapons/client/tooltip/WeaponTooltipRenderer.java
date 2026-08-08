package com.jonas.starfallweapons.client.tooltip;

import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Axis;
import com.jonas.starfallweapons.weapon.WeaponDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Renders the compact Starfall information panel for registered weapon items. */
public final class WeaponTooltipRenderer {
	private static final int WIDTH = 220;
	private static final int PADDING = 7;
	private static final int SKILL_ICON_SIZE = 18;
	private static final int BACKGROUND = 0xF0141024;
	private static final int BORDER = 0xFF62518C;
	private static final int TEXT = 0xFFF3EFFA;
	private static final int MUTED_TEXT = 0xFFBBB2CD;
	private static final int COSMIC_PURPLE = 0xFFA56CFF;
	private static final int COSMIC_LAVENDER = 0xFFE4D7FF;

	private WeaponTooltipRenderer() {
	}

	public static void render(GuiGraphics graphics, Font font, ItemStack stack, WeaponDefinition weapon, WeaponTooltipPage page, int mouseX, int mouseY) {
		List<String> bodyLines = wrapLines(font, page.lines());
		int height = PADDING * 2 + font.lineHeight * (bodyLines.size() + 4) + SKILL_ICON_SIZE + 26;
		int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int x = Math.min(mouseX + 12, screenWidth - WIDTH - 4);
		int y = Math.min(mouseY - 12, screenHeight - height - 4);
		x = Math.max(4, x);
		y = Math.max(4, y);

		graphics.pose().pushPose();
		graphics.pose().translate(0.0F, 0.0F, 400.0F);
		float time = net.minecraft.Util.getMillis() / 1000.0F;
		float sway = (float) Math.sin(time * 1.7F) * 0.55F;
		graphics.pose().translate(x + WIDTH / 2.0F, y + height / 2.0F, 0.0F);
		graphics.pose().mulPose(Axis.ZP.rotationDegrees(sway));
		graphics.pose().translate(-x - WIDTH / 2.0F, -y - height / 2.0F, 0.0F);
		float appearance = smoothStep(TooltipPageState.appearanceProgress());
		graphics.fill(x - 1, y - 1, x + WIDTH + 1, y + height + 1, withAlpha(BORDER, 150 + (int) (105 * appearance)));
		graphics.fill(x, y, x + WIDTH, y + height, withAlpha(BACKGROUND, 180 + (int) (60 * appearance)));
		drawCosmicParticles(graphics, x, y, WIDTH, height, time, appearance);

		int textX = x + PADDING;
		int textY = y + PADDING;
		drawShimmeringName(graphics, font, stack.getHoverName().getString(), textX, textY, time);
		textY += font.lineHeight + 2;
		graphics.fill(textX, textY - 1, x + WIDTH - PADDING, textY, withAlpha(COSMIC_PURPLE, 150));
		graphics.drawString(font, "Damage: " + weapon.damage(), textX, textY, TEXT, false);
		textY += font.lineHeight + 4;
		textY = drawAbilityIcons(graphics, page, weapon.rarity().color(), textX, textY);
		graphics.drawString(font, page.title(), textX, textY, TEXT, false);
		textY += font.lineHeight + 2;

		for (String line : bodyLines) {
			graphics.drawString(font, line, textX, textY, MUTED_TEXT, false);
			textY += font.lineHeight;
		}

		String controls = "Shift: Skills/Passives  •  Mouse Wheel: Ability";
		graphics.drawString(font, controls, textX, y + height - PADDING - font.lineHeight, withAlpha(COSMIC_LAVENDER, 220), false);

		graphics.pose().popPose();
	}

	/** Draws the active ability list and a subtle pulse around the selected icon. */
	private static int drawAbilityIcons(GuiGraphics graphics, WeaponTooltipPage page, int rarityColor, int x, int y) {
		int iconX = x;
		for (int index = 0; index < page.iconTextures().size(); index++) {
			if (iconX + SKILL_ICON_SIZE > x + WIDTH - PADDING * 2) {
				break;
			}
			boolean selected = index == page.selectedIconIndex();
			float pulse = selected ? 0.5F + 0.5F * (float) Math.sin(net.minecraft.Util.getMillis() / 110.0D) : 0.0F;
			int borderColor = selected ? withAlpha(rarityColor, 185 + (int) (70 * pulse)) : 0xFF403957;
			int inset = selected ? 2 : 1;
			if (selected) {
				graphics.fill(iconX - 4, y - 4, iconX + SKILL_ICON_SIZE + 4, y + SKILL_ICON_SIZE + 4, withAlpha(COSMIC_PURPLE, 35 + (int) (55 * pulse)));
			}
			graphics.fill(iconX - inset, y - inset, iconX + SKILL_ICON_SIZE + inset, y + SKILL_ICON_SIZE + inset, borderColor);
			graphics.pose().pushPose();
			float scale = selected ? 1.0F + 0.06F * pulse : 1.0F;
			graphics.pose().translate(iconX + SKILL_ICON_SIZE / 2.0F, y + SKILL_ICON_SIZE / 2.0F, 0.0F);
			graphics.pose().scale(scale, scale, 1.0F);
			graphics.pose().translate(-iconX - SKILL_ICON_SIZE / 2.0F, -y - SKILL_ICON_SIZE / 2.0F, 0.0F);
			graphics.blit(page.iconTextures().get(index), iconX, y, 0, 0.0F, 0.0F, SKILL_ICON_SIZE, SKILL_ICON_SIZE, SKILL_ICON_SIZE, SKILL_ICON_SIZE);
			graphics.pose().popPose();
			iconX += SKILL_ICON_SIZE + 3;
		}
		return y + SKILL_ICON_SIZE + 4;
	}

	private static int withAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | (Math.clamp(alpha, 0, 255) << 24);
	}

	private static float smoothStep(float value) {
		return value * value * (3.0F - 2.0F * value);
	}

	private static void drawShimmeringName(GuiGraphics graphics, Font font, String name, int x, int y, float time) {
		int cursor = x;
		for (int index = 0; index < name.length(); index++) {
			float phase = 0.5F + 0.5F * (float) Math.sin(time * 3.0F + index * 0.7F);
			int color = lerpColor(COSMIC_LAVENDER, 0xFFFFFFFF, phase);
			int offsetY = (int) Math.round(Math.sin(time * 2.2F + index * 0.55F) * 0.55F);
			String character = String.valueOf(name.charAt(index));
			graphics.drawString(font, character, cursor, y + offsetY, color, false);
			cursor += font.width(character);
		}
	}

	private static void drawCosmicParticles(GuiGraphics graphics, int x, int y, int width, int height, float time, float appearance) {
		for (int index = 0; index < 14; index++) {
			float horizontal = (index * 47 % width) + 3.0F * (float) Math.sin(time * (0.8F + index * 0.05F) + index);
			float vertical = (index * 29 % height) + 2.0F * (float) Math.cos(time * (0.9F + index * 0.04F) + index * 2.0F);
			int alpha = (int) ((35 + 40 * (0.5F + 0.5F * Math.sin(time * 2.0F + index))) * appearance);
			int size = index % 4 == 0 ? 2 : 1;
			graphics.fill(x + (int) horizontal, y + (int) vertical, x + (int) horizontal + size, y + (int) vertical + size, withAlpha(COSMIC_LAVENDER, alpha));
		}
	}

	private static int lerpColor(int from, int to, float progress) {
		int red = (int) (((from >> 16) & 0xFF) + (((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * progress);
		int green = (int) (((from >> 8) & 0xFF) + (((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * progress);
		int blue = (int) ((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * progress);
		return 0xFF000000 | (red << 16) | (green << 8) | blue;
	}

	private static List<String> wrapLines(Font font, List<String> lines) {
		List<String> wrapped = new ArrayList<>();
		int maxWidth = WIDTH - PADDING * 2;
		for (String line : lines) {
				if (line.isEmpty()) {
					wrapped.add("");
					continue;
				}
				String remainder = line;
				while (!remainder.isEmpty()) {
					String part = font.plainSubstrByWidth(remainder, maxWidth);
					if (part.isEmpty()) {
						break;
					}
					wrapped.add(part);
					remainder = remainder.substring(part.length()).stripLeading();
				}
		}
		return wrapped;
	}
}
