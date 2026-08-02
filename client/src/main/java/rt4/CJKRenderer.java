package rt4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * CJK 字元渲染器：用 AWT 把中文字元畫成像素，再寫入 SoftwareRaster。
 * 供 Font.render 在遇到 >255 的字元時呼叫。
 *
 * 字型大小由呼叫端（Font）依 lineHeight 傳入，使中文字與遊戲點陣字型大小一致。
 */
public final class CJKRenderer {

	private static final int GLYPH_PADDING = 2;

	/** 依字型大小快取的 Font 與 GlyphImage。 */
	private static final Map<Integer, java.awt.Font> FONTS_BY_SIZE = new HashMap<>();
	private static final Map<Integer, GlyphBuffer> BUFFERS_BY_SIZE = new HashMap<>();

	private static final class GlyphBuffer {
		final int size;
		final BufferedImage image;
		final Graphics2D g;
		GlyphBuffer(int size) {
			this.size = size;
			this.image = new BufferedImage(size + GLYPH_PADDING, size + GLYPH_PADDING, BufferedImage.TYPE_INT_ARGB);
			this.g = image.createGraphics();
			this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			this.g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		}
	}

	private static java.awt.Font fontFor(int size) {
		Integer key = Integer.valueOf(size);
		java.awt.Font f = FONTS_BY_SIZE.get(key);
		if (f != null) {
			return f;
		}
		f = findCjkFont(size);
		FONTS_BY_SIZE.put(key, f);
		return f;
	}

	private static java.awt.Font findCjkFont(int size) {
		String[] candidates = {"WenQuanYi Micro Hei", "Noto Sans CJK TC", "Noto Sans TC",
				"Microsoft JhengHei", "PingFang TC", "Heiti TC", "PMingLiU", "AR PL UMing TW"};
		for (String name : candidates) {
			java.awt.Font f = new java.awt.Font(name, java.awt.Font.PLAIN, size);
			if (f.canDisplay('\u4E2D')) {
				return f;
			}
		}
		return new java.awt.Font("Dialog", java.awt.Font.PLAIN, size);
	}

	private CJKRenderer() {
	}

	/**
	 * 在 (x, y) 繪製單一 Unicode 字元到 SoftwareRaster，回傳字元寬度（像素）。
	 *
	 * @param codepoint Unicode code point
	 * @param x 左邊界
	 * @param baselineY 基線
	 * @param fontSize 字型大小（像素），通常傳入 Font.lineHeight
	 */
	public static int drawGlyph(int codepoint, int x, int baselineY, int fontSize) {
		if (fontSize <= 0) {
			fontSize = 12;
		}
		int bufferSize = fontSize + GLYPH_PADDING;
		GlyphBuffer buf = BUFFERS_BY_SIZE.get(Integer.valueOf(fontSize));
		if (buf == null) {
			buf = new GlyphBuffer(fontSize);
			BUFFERS_BY_SIZE.put(Integer.valueOf(fontSize), buf);
		}
		Graphics2D g = buf.g;
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, bufferSize, bufferSize);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.WHITE);
		g.setFont(fontFor(fontSize));
		String ch = new String(Character.toChars(codepoint));
		FontMetrics fm = g.getFontMetrics();
		int charWidth = fm.charWidth(codepoint);
		if (charWidth <= 0) {
			charWidth = fontSize;
		}
		g.drawString(ch, GLYPH_PADDING, fontSize);

		int[] src = new int[bufferSize * bufferSize];
		buf.image.getRGB(0, 0, bufferSize, bufferSize, src, 0, bufferSize);

		int rasterW = SoftwareRaster.width;
		int rasterH = SoftwareRaster.height;
		int[] pixels = SoftwareRaster.pixels;
		int clipLeft = SoftwareRaster.clipLeft;
		int clipTop = SoftwareRaster.clipTop;
		int clipRight = SoftwareRaster.clipRight;
		int clipBottom = SoftwareRaster.clipBottom;

		int ascender = fm.getAscent();
		int top = baselineY - ascender;
		for (int dy = 0; dy < bufferSize; dy++) {
			int sy = top + dy;
			if (sy < clipTop || sy >= clipBottom) {
				continue;
			}
			int dstIndex = x + sy * rasterW;
			int srcIndex = dy * bufferSize;
			for (int dx = 0; dx < bufferSize; dx++) {
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
	public static int charWidth(int codepoint, int fontSize) {
		if (fontSize <= 0) {
			fontSize = 12;
		}
		Graphics2D g = BUFFERS_BY_SIZE.computeIfAbsent(Integer.valueOf(fontSize), GlyphBuffer::new).g;
		g.setFont(fontFor(fontSize));
		FontMetrics fm = g.getFontMetrics();
		int w = fm.charWidth(codepoint);
		return w <= 0 ? fontSize : w;
	}
}
