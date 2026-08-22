package com.jonas.starfallweapons.client.tooltip;

import java.util.ArrayList;
import java.util.List;

import com.jonas.starfallweapons.weapon.WeaponDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Premium, responsive tooltip renderer that keeps all content inside the panel boundaries. */
public final class WeaponTooltipRenderer {
	private static final int MIN_WIDTH = 180;
	private static final int MAX_WIDTH = 320;
	private static final int OUTER_PADDING = 10;
	private static final int ICON_SIZE = 18;
	private static final int ICON_SPACING = 4;
	private static final int DEFAULT_BORDER = 0xFF252936;
	private static final int DEFAULT_BACKGROUND = 0xF008090D;
	private static final int DEFAULT_PRIMARY = 0xFFF2F4F7;
	private static final int DEFAULT_SECONDARY = 0xFFA7ADB8;
	private static final int DEFAULT_DIVIDER = 0xFF303541;
	private static final int SHADOW = 0x33000000;
	private static final String CONTROLS_TEXT = "Shift: switch  •  Mouse wheel: scroll";

	private static final int FERRYMAN_BACKGROUND = 0xF004323F;
	private static final int FERRYMAN_BORDER = 0xFF077E7A;
	private static final int FERRYMAN_PRIMARY = 0xFFD9FCCB;
	private static final int FERRYMAN_SECONDARY = 0xFFBFEFD1;
	private static final int FERRYMAN_DIVIDER = 0xCC077E7A;
	private static final int FERRYMAN_ACCENT = 0xFF0BBD8B;

	private WeaponTooltipRenderer() {
	}

	public static void render(GuiGraphics graphics, Font font, ItemStack stack, WeaponDefinition weapon, WeaponTooltipPage page, int mouseX, int mouseY) {
		TooltipTheme theme = resolveTheme(weapon);
		int accentColor = theme.accentColor();
		String weaponName = stack.getHoverName().getString();
		String rarityLabel = weapon.rarity().name() + " WEAPON";
		List<String> rawStats = List.of(
				"⚔  " + weapon.damage() + " Attack Damage",
				"✦  " + weapon.rarity().name() + " Weapon",
				"◆  " + weapon.description());
		Layout layout = buildLayout(font, weaponName, rarityLabel, rawStats, "ABILITIES", page.title(), page.lines(), page.iconTextures().size());

		int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int x = Math.min(mouseX + 12, screenWidth - layout.panelWidth() - 6);
		int y = Math.min(mouseY - 10, screenHeight - layout.panelHeight() - 6);
		x = Math.max(4, x);
		y = Math.max(4, y);
		float time = net.minecraft.Util.getMillis() / 1000.0F;
		float sway = (float) Math.sin(time * 1.2F) * 0.7F;
		float hoverScale = 1.0F + 0.008F * (float) Math.sin(time * 1.6F);

		graphics.pose().pushPose();
		graphics.pose().translate(0.0F, 0.0F, 450.0F);
		graphics.pose().translate(x + layout.panelWidth() / 2.0F, y + layout.panelHeight() / 2.0F, 0.0F);
		graphics.pose().scale(hoverScale, hoverScale, 1.0F);
		graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(sway));
		graphics.pose().translate(-x - layout.panelWidth() / 2.0F, -y - layout.panelHeight() / 2.0F, 0.0F);

		graphics.fill(x + 2, y + 2, x + layout.panelWidth() - 2, y + layout.panelHeight() - 2, withAlpha(SHADOW, 120));
		graphics.fill(x, y, x + layout.panelWidth(), y + layout.panelHeight(), withAlpha(theme.backgroundColor(), 215));
		graphics.fill(x - 1, y - 1, x + layout.panelWidth() + 1, y, withAlpha(accentColor, 165));
		graphics.fill(x - 1, y + layout.panelHeight(), x + layout.panelWidth() + 1, y + layout.panelHeight() + 1, withAlpha(accentColor, 165));
		graphics.fill(x - 1, y, x, y + layout.panelHeight(), withAlpha(accentColor, 165));
		graphics.fill(x + layout.panelWidth(), y, x + layout.panelWidth() + 1, y + layout.panelHeight(), withAlpha(accentColor, 165));
		graphics.fill(x + 1, y + 1, x + layout.panelWidth() - 1, y + layout.panelHeight() - 1, withAlpha(theme.borderColor(), 170));

		int currentY = y + OUTER_PADDING;
		int contentX = x + OUTER_PADDING;
		for (String line : layout.nameLines()) {
			graphics.drawString(font, line, contentX, currentY, theme.primaryTextColor(), false);
			currentY += font.lineHeight;
		}
		currentY += 2;
		for (String line : layout.rarityLines()) {
			graphics.drawString(font, line, contentX, currentY, withAlpha(accentColor, 230), false);
			currentY += font.lineHeight;
		}
		currentY += 8;
		for (String statLine : layout.statLines()) {
			graphics.drawString(font, statLine, contentX, currentY, theme.secondaryTextColor(), false);
			currentY += font.lineHeight + 2;
		}
		if (!layout.statLines().isEmpty()) {
			currentY += 2;
		}
		graphics.fill(contentX, currentY, contentX + layout.contentWidth(), currentY + 1, withAlpha(theme.dividerColor(), 220));
		currentY += 9;
		for (String line : layout.sectionHeaderLines()) {
			graphics.drawString(font, line, contentX, currentY, withAlpha(theme.primaryTextColor(), 220), false);
			currentY += font.lineHeight;
		}
		currentY += 4;
		currentY = drawAbilityIcons(graphics, page, accentColor, contentX, currentY, layout.contentWidth(), layout.iconsPerRow());
		float focusPulse = 0.5F + 0.5F * (float) Math.sin(time * 3.2F);
		int abilityTitleStartY = currentY;
		int abilityTitleHeight = layout.abilityTitleLines().size() * font.lineHeight;
		if (abilityTitleHeight > 0) {
			graphics.fill(contentX - 2, abilityTitleStartY - 1, contentX + layout.contentWidth(), abilityTitleStartY + abilityTitleHeight + 1,
					withAlpha(accentColor, 22 + (int) (20 * focusPulse)));
		}
		for (String line : layout.abilityTitleLines()) {
			graphics.drawString(font, line, contentX, currentY, withAlpha(accentColor, 230 + (int) (25 * focusPulse)), false);
			currentY += font.lineHeight;
		}
		if (abilityTitleHeight > 0) {
			int underlineWidth = Math.min(layout.contentWidth(), 42 + (int) (64 * focusPulse));
			graphics.fill(contentX, currentY + 1, contentX + underlineWidth, currentY + 2, withAlpha(accentColor, 175));
		}
		if (!layout.bodyLines().isEmpty()) {
			currentY += 2;
		}
		for (int i = 0; i < layout.bodyLines().size(); i++) {
			String line = layout.bodyLines().get(i);
			int bodyColor = i == 0 ? withAlpha(theme.secondaryTextColor(), 215 + (int) (25 * focusPulse)) : theme.secondaryTextColor();
			graphics.drawString(font, line, contentX, currentY, bodyColor, false);
			currentY += font.lineHeight;
		}
		currentY += 5;
		graphics.fill(contentX, currentY, contentX + layout.contentWidth(), currentY + 1, withAlpha(theme.dividerColor(), 160));
		currentY += 5;
		for (String line : layout.controlsLines()) {
			graphics.drawString(font, line, contentX, currentY, withAlpha(theme.secondaryTextColor(), 220), false);
			currentY += font.lineHeight;
		}

		graphics.flush();
		graphics.pose().popPose();
	}

	private static Layout buildLayout(
			Font font,
			String weaponName,
			String rarityLabel,
			List<String> rawStats,
			String sectionHeader,
			String abilityTitle,
			List<String> bodySource,
			int iconCount) {
		int minContentWidth = MIN_WIDTH - OUTER_PADDING * 2;
		int maxContentWidth = MAX_WIDTH - OUTER_PADDING * 2;
		int contentWidth = determineInitialContentWidth(font, minContentWidth, maxContentWidth, weaponName, rarityLabel, rawStats, sectionHeader, abilityTitle, bodySource);

		for (int i = 0; i < 3; i++) {
			List<String> nameLines = wrapText(font, weaponName, contentWidth);
			List<String> rarityLines = wrapText(font, rarityLabel, contentWidth);
			List<String> statLines = flattenWrappedLines(font, rawStats, contentWidth);
			List<String> sectionHeaderLines = wrapText(font, sectionHeader, contentWidth);
			List<String> abilityTitleLines = wrapText(font, abilityTitle, contentWidth);
			List<String> bodyLines = flattenWrappedLines(font, bodySource, contentWidth);
			List<String> controlsLines = wrapText(font, CONTROLS_TEXT, contentWidth);

			int requiredWidth = measureMaxWidth(font, nameLines, rarityLines, statLines, sectionHeaderLines, abilityTitleLines, bodyLines, controlsLines);
			int adjustedWidth = Math.clamp(Math.max(requiredWidth, minContentWidth), minContentWidth, maxContentWidth);
			if (adjustedWidth == contentWidth) {
				break;
			}
			contentWidth = adjustedWidth;
		}

		List<String> nameLines = wrapText(font, weaponName, contentWidth);
		List<String> rarityLines = wrapText(font, rarityLabel, contentWidth);
		List<String> statLines = flattenWrappedLines(font, rawStats, contentWidth);
		List<String> sectionHeaderLines = wrapText(font, sectionHeader, contentWidth);
		List<String> abilityTitleLines = wrapText(font, abilityTitle, contentWidth);
		List<String> bodyLines = flattenWrappedLines(font, bodySource, contentWidth);
		List<String> controlsLines = wrapText(font, CONTROLS_TEXT, contentWidth);
		int iconsPerRow = computeIconsPerRow(iconCount, contentWidth);
		int iconRows = iconCount == 0 ? 0 : (iconCount + iconsPerRow - 1) / iconsPerRow;

		int totalContentHeight = 0;
		totalContentHeight += nameLines.size() * font.lineHeight;
		totalContentHeight += 2;
		totalContentHeight += rarityLines.size() * font.lineHeight;
		totalContentHeight += 8;
		for (int i = 0; i < statLines.size(); i++) {
			totalContentHeight += font.lineHeight + 2;
		}
		if (!statLines.isEmpty()) {
			totalContentHeight += 2;
		}
		totalContentHeight += 1;
		totalContentHeight += 9;
		totalContentHeight += sectionHeaderLines.size() * font.lineHeight;
		totalContentHeight += 4;
		if (iconRows > 0) {
			totalContentHeight += iconRows * ICON_SIZE + Math.max(0, iconRows - 1) * ICON_SPACING;
			totalContentHeight += 4;
		}
		totalContentHeight += abilityTitleLines.size() * font.lineHeight;
		if (!bodyLines.isEmpty()) {
			totalContentHeight += 2;
			totalContentHeight += bodyLines.size() * font.lineHeight;
		}
		totalContentHeight += 5;
		totalContentHeight += 1;
		totalContentHeight += 5;
		totalContentHeight += controlsLines.size() * font.lineHeight;

		int panelWidth = contentWidth + OUTER_PADDING * 2;
		int panelHeight = totalContentHeight + OUTER_PADDING * 2;
		return new Layout(panelWidth, panelHeight, contentWidth, iconsPerRow, nameLines, rarityLines, statLines, sectionHeaderLines, abilityTitleLines, bodyLines, controlsLines);
	}

	private static int determineInitialContentWidth(
			Font font,
			int minContentWidth,
			int maxContentWidth,
			String weaponName,
			String rarityLabel,
			List<String> rawStats,
			String sectionHeader,
			String abilityTitle,
			List<String> bodySource) {
		int width = Math.max(font.width(weaponName), font.width(rarityLabel));
		width = Math.max(width, font.width(sectionHeader));
		width = Math.max(width, font.width(abilityTitle));
		for (String line : rawStats) {
			width = Math.max(width, font.width(line));
		}
		for (String line : bodySource) {
			width = Math.max(width, font.width(line));
		}
		return Math.clamp(width, minContentWidth, maxContentWidth);
	}

	private static int computeIconsPerRow(int iconCount, int contentWidth) {
		if (iconCount <= 0) {
			return 0;
		}
		int stride = ICON_SIZE + ICON_SPACING;
		int perRow = Math.max(1, (contentWidth + ICON_SPACING) / stride);
		return Math.min(iconCount, perRow);
	}

	private static int drawAbilityIcons(GuiGraphics graphics, WeaponTooltipPage page, int rarityColor, int x, int y, int contentWidth, int iconsPerRow) {
		if (page.iconTextures().isEmpty() || iconsPerRow <= 0) {
			return y;
		}
		int iconX = x;
		int iconY = y;
		int lastRowIndex = 0;
		for (int index = 0; index < page.iconTextures().size(); index++) {
			boolean selected = index == page.selectedIconIndex();
			float pulse = selected ? 0.5F + 0.5F * (float) Math.sin(net.minecraft.Util.getMillis() / 160.0D) : 0.0F;
			int borderColor = selected ? withAlpha(rarityColor, 180 + (int) (40 * pulse)) : 0xFF2F3644;
			float hoverOffset = selected ? (float) Math.sin(net.minecraft.Util.getMillis() / 140.0D + index * 0.35D) * 1.2F : 0.0F;
			float hoverScale = selected ? 1.0F + 0.04F * pulse : 1.0F;
			int drawY = iconY + Math.round(hoverOffset);
			if (selected) {
				graphics.fill(iconX - 3, drawY - 3, iconX + ICON_SIZE + 3, drawY + ICON_SIZE + 3, withAlpha(rarityColor, 30 + (int) (35 * pulse)));
			}
			graphics.fill(iconX - 1, drawY - 1, iconX + ICON_SIZE + 1, drawY + ICON_SIZE + 1, withAlpha(0xFF161A22, 150));
			graphics.fill(iconX, drawY, iconX + ICON_SIZE, drawY + ICON_SIZE, borderColor);
			if (selected) {
				graphics.pose().pushPose();
				graphics.pose().translate(iconX + ICON_SIZE / 2.0F, drawY + ICON_SIZE / 2.0F, 0.0F);
				graphics.pose().scale(hoverScale, hoverScale, 1.0F);
				graphics.pose().translate(-iconX - ICON_SIZE / 2.0F, -drawY - ICON_SIZE / 2.0F, 0.0F);
				graphics.blit(page.iconTextures().get(index), iconX, drawY, 0, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
				graphics.pose().popPose();
			} else {
				graphics.blit(page.iconTextures().get(index), iconX, drawY, 0, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
			}

			lastRowIndex = index / iconsPerRow;
			if ((index + 1) % iconsPerRow == 0) {
				iconX = x;
				iconY += ICON_SIZE + ICON_SPACING;
				continue;
			}
			int nextX = iconX + ICON_SIZE + ICON_SPACING;
			if (nextX + ICON_SIZE <= x + contentWidth) {
				iconX = nextX;
			} else {
				iconX = x;
				iconY += ICON_SIZE + ICON_SPACING;
			}
		}
		return y + (lastRowIndex + 1) * ICON_SIZE + lastRowIndex * ICON_SPACING + 4;
	}

	private static List<String> flattenWrappedLines(Font font, List<String> sourceLines, int maxAllowedWidth) {
		List<String> wrapped = new ArrayList<>();
		for (String sourceLine : sourceLines) {
			wrapped.addAll(wrapText(font, sourceLine, maxAllowedWidth));
		}
		return wrapped;
	}

	private static List<String> wrapText(Font font, String text, int maxAllowedWidth) {
		if (text == null || text.isBlank()) {
			return List.of("");
		}
		List<String> wrapped = new ArrayList<>();
		int maxWidth = Math.max(40, maxAllowedWidth);
		String remainder = text.strip();
		while (!remainder.isEmpty()) {
			String candidate = font.plainSubstrByWidth(remainder, maxWidth);
			if (candidate.isEmpty()) {
				break;
			}
			if (candidate.length() < remainder.length()) {
				int splitIndex = candidate.lastIndexOf(' ');
				if (splitIndex > 0) {
					candidate = candidate.substring(0, splitIndex);
				}
			}
			candidate = candidate.stripTrailing();
			if (candidate.isEmpty()) {
				candidate = font.plainSubstrByWidth(remainder, maxWidth);
			}
			wrapped.add(candidate);
			remainder = remainder.substring(candidate.length()).stripLeading();
		}
		return wrapped.isEmpty() ? List.of(text.strip()) : wrapped;
	}

	@SafeVarargs
	private static int measureMaxWidth(Font font, List<String>... groups) {
		int max = 0;
		for (List<String> group : groups) {
			for (String line : group) {
				max = Math.max(max, font.width(line));
			}
		}
		return max;
	}

	private static int withAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | (Math.clamp(alpha, 0, 255) << 24);
	}

	private static TooltipTheme resolveTheme(WeaponDefinition weapon) {
		if ("last_ferryman".equals(weapon.itemId().getPath())) {
			return new TooltipTheme(FERRYMAN_BACKGROUND, FERRYMAN_BORDER, FERRYMAN_PRIMARY, FERRYMAN_SECONDARY, FERRYMAN_DIVIDER, FERRYMAN_ACCENT);
		}
		return new TooltipTheme(DEFAULT_BACKGROUND, DEFAULT_BORDER, DEFAULT_PRIMARY, DEFAULT_SECONDARY, DEFAULT_DIVIDER, weapon.rarity().color());
	}

	private record TooltipTheme(
			int backgroundColor,
			int borderColor,
			int primaryTextColor,
			int secondaryTextColor,
			int dividerColor,
			int accentColor) {
	}

	private record Layout(
			int panelWidth,
			int panelHeight,
			int contentWidth,
			int iconsPerRow,
			List<String> nameLines,
			List<String> rarityLines,
			List<String> statLines,
			List<String> sectionHeaderLines,
			List<String> abilityTitleLines,
			List<String> bodyLines,
			List<String> controlsLines) {
	}
}
