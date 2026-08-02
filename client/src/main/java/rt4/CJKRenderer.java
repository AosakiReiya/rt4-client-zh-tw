package rt4;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * CJK 字元渲染器：用 AWT 把中文字元畫成像素，再寫入 SoftwareRaster。
 * 供 Font.render 在遇到 >255 的字元時呼叫。
 */
public final class CJKRenderer {

	private static java.awt.Font awtFont;
	private static BufferedImage glyphImage;
	private static Graphics2D glyphGraphics;
	private static final int GLYPH_SIZE = 36;
	private static final int GLYPH_PADDING = 2;

	static {
		// 使用系統 CJK 字型；找不到時用 Dialog fallback
		String[] candidates = {"WenQuanYi Micro Hei", "Noto Sans CJK TC", "Noto Sans TC",
				"Microsoft JhengHei", "PingFang TC", "Heiti TC", "PMingLiU", "AR PL UMing TW"};
		awtFont = null;
		for (String name : candidates) {
			java.awt.Font f = new java.awt.Font(name, java.awt.Font.PLAIN, GLYPH_SIZE);
			if (!name.equals("Dialog") && f.canDisplay('\u4E2D')) {
				awtFont = f;
				break;
			}
		}
		if (awtFont == null) {
			awtFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, GLYPH_SIZE);
		}
		glyphImage = new BufferedImage(GLYPH_SIZE, GLYPH_SIZE + GLYPH_PADDING, BufferedImage.TYPE_INT_ARGB);
		glyphGraphics = glyphImage.createGraphics();
		glyphGraphics.setFont(awtFont);
		glyphGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		glyphGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		glyphGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
	}

	private CJKRenderer() {
	}

	/**
	 * 在 (x, y) 繪製單一 Unicode 字元到 SoftwareRaster，回傳字元寬度（像素）。
	 * y 為基線。
	 */
	public static int drawGlyph(int codepoint, int x, int baselineY) {
		Graphics2D g = glyphGraphics;
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, GLYPH_SIZE, GLYPH_SIZE + GLYPH_PADDING);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.WHITE);
		String ch = new String(Character.toChars(codepoint));
		FontMetrics fm = g.getFontMetrics();
		int charWidth = fm.charWidth(codepoint);
		if (charWidth <= 0) {
			charWidth = GLYPH_SIZE;
		}
		g.drawString(ch, GLYPH_PADDING, GLYPH_SIZE - GLYPH_PADDING);

		int[] src = new int[GLYPH_SIZE * (GLYPH_SIZE + GLYPH_PADDING)];
		glyphImage.getRGB(0, 0, GLYPH_SIZE, GLYPH_SIZE + GLYPH_PADDING, src, 0, GLYPH_SIZE);

		int rasterW = SoftwareRaster.width;
		int rasterH = SoftwareRaster.height;
		int[] pixels = SoftwareRaster.pixels;
		int clipLeft = SoftwareRaster.clipLeft;
		int clipTop = SoftwareRaster.clipTop;
		int clipRight = SoftwareRaster.clipRight;
		int clipBottom = SoftwareRaster.clipBottom;

		int ascender = fm.getAscent();
		int top = baselineY - ascender;
		int height = GLYPH_SIZE + GLYPH_PADDING;
		for (int dy = 0; dy < height; dy++) {
			int sy = top + dy;
			if (sy < clipTop || sy >= clipBottom) {
				continue;
			}
			int dstIndex = x + sy * rasterW;
			int srcIndex = dy * GLYPH_SIZE;
			for (int dx = 0; dx < GLYPH_SIZE; dx++) {
				int sx = x + dx;
				if (sx < clipLeft || sx >= clipRight) {
					continue;
				}
				int argb = src[srcIndex + dx];
				int alpha = (argb >> 24) & 0xFF;
				if (alpha < 16) {
					continue;
				}
				int rgb = argb & 0xFFFFFF;
				if (alpha >= 248) {
					pixels[dstIndex + dx] = rgb;
				} else {
					// 半透明 alpha 混合
					int old = pixels[dstIndex + dx];
					int outA = alpha;
					int invA = 256 - outA;
					int r = ((old >> 16 & 0xFF) * invA + (rgb >> 16 & 0xFF) * outA) / 256;
					int gb = ((old >> 8 & 0xFF) * invA + (rgb >> 8 & 0xFF) * outA) / 256;
					int b = ((old & 0xFF) * invA + (rgb & 0xFF) * outA) / 256;
					pixels[dstIndex + dx] = (r << 16) | (gb << 8) | b;
				}
			}
		}
		return charWidth;
	}

	/** 取得某個 Unicode 字元的近似寬度（像素）。 */
	public static int charWidth(int codepoint) {
		FontMetrics fm = glyphGraphics.getFontMetrics();
		int w = fm.charWidth(codepoint);
		return w <= 0 ? GLYPH_SIZE : w;
	}
}
