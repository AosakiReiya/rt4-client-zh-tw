package rt4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * CJK 字元渲染器：用 AWT 把中文字元畫成像素，再寫入 SoftwareRaster。
 * 供 Font.render 在遇到 >255 的字元時呼叫。
 *
 * 字型優先載入內嵌的 Chiron 港體字型（/fonts/），失敗時 fallback 系統字型。
 * 字型大小由呼叫端（Font）依 lineHeight 傳入，使中文字與遊戲點陣字型大小一致。
 */
public final class CJKRenderer {

	private static final int GLYPH_PADDING = 2;

	/** CJK 字元縮放係數（調整中文字與點陣字型的視覺比例，1.0 = 與點陣行高一致）。 */
	private static final double CJK_SCALE = 1.0;

	/** 依字型大小快取的 Font 與 GlyphImage。 */
	private static final Map<Integer, java.awt.Font> FONTS_BY_SIZE = new HashMap<>();
	private static final Map<Integer, GlyphBuffer> BUFFERS_BY_SIZE = new HashMap<>();

	/** 目前渲染是否為粗體（標題）。由 Font 透過 setBold 設定。 */
	private static boolean bold = false;

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

	/** 設定目前渲染是否為粗體（標題字型）。 */
	public static void setBold(boolean b) {
		bold = b;
	}

	/** 已確認支援中文的系統字型族名稱（快取，避免重複枚舉）。 */
	private static String CJK_FAMILY = null;

	/** 內嵌 Chiron 港體字型（延遲載入，解決繁體字形缺失問題）。 */
	private static java.awt.Font CHIRON_BASE = null;
	private static boolean chironTried = false;

	/** 從 jar 資源載入 Chiron 字型（延遲 + 完整 try-catch，失敗回傳 null 不崩潰）。 */
	private static java.awt.Font loadChiron() {
		if (chironTried) {
			return CHIRON_BASE;
		}
		chironTried = true;
		java.io.InputStream in = null;
		try {
			in = CJKRenderer.class.getResourceAsStream("/fonts/ChironHeiHK-R.ttf");
			if (in == null) {
				return null;
			}
			java.awt.Font base = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, in);
			// 驗證支援繁體中文（「啟」U+555F）
			if (!base.canDisplay('\u555F')) {
				return null;
			}
			CHIRON_BASE = base;
			return base;
		} catch (Throwable t) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Throwable t) {
			}
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
		int style = bold ? java.awt.Font.BOLD : java.awt.Font.PLAIN;
		// 1. Chiron 港體優先（完整繁體字形，解決「啟」等系統字型缺失）
		java.awt.Font chiron = loadChiron();
		if (chiron != null) {
			try {
				return chiron.deriveFont(style, size);
			} catch (Throwable t) {
			}
		}
		// 2. 已快取的支援中文字型族
		if (CJK_FAMILY != null) {
			try {
				java.awt.Font f = new java.awt.Font(CJK_FAMILY, style, size);
				if (f.canDisplay('\u4E2D')) {
					return f;
				}
			} catch (Throwable t) {
			}
		}
		// 3. 枚舉系統字型，找第一個支援中文的（跨平台，Windows/Linux/macOS）
		try {
			String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			for (String n : names) {
				try {
					java.awt.Font f = new java.awt.Font(n, style, size);
					if (f.canDisplay('\u4E2D')) {
						CJK_FAMILY = n;
						return f;
					}
				} catch (Throwable t) {
				}
			}
		} catch (Throwable t) {
		}
		// 4. 邏輯字型（Java fontconfig 會 fallback 到系統中文字型）
		String[] logical = {"SansSerif", "Serif", "Monospaced", "Dialog"};
		for (String n : logical) {
			try {
				java.awt.Font f = new java.awt.Font(n, style, size);
				if (f.canDisplay('\u4E2D')) {
					return f;
				}
			} catch (Throwable t) {
			}
		}
		// 5. 最後保證：回傳 Dialog（即使無法顯示中文，也絕不崩潰）
		return new java.awt.Font("Dialog", style, size);
	}

	private CJKRenderer() {
	}

	/** GL 字元 sprite 快取：codepoint*65536+scaledSize -> GlAlphaSprite（離屏 sprite 上傳）。 */
	private static final java.util.Map<Integer, GlAlphaSprite> GL_SPRITES = new java.util.HashMap<>();
	private static int glContextId = -1;

	/**
	 * 在 (x, y) 用離屏 sprite 繪製單一中文字元（GL 模式）。
	 * 整個 bufferSize×bufferSize 裝進 SoftwareAlphaSprite（含 GLYPH_PADDING），再用 GlAlphaSprite 上傳 GL，
	 * 位置與 Software 分支完全一致；全程透過 GlRenderer.setTextureId 追蹤 state（不手動 glDisable）。
	 */
	private static void drawGlSprite(int codepoint, int scaledSize, int[] argb, int bufferSize,
			int x, int y) {
		if (GlRenderer.gl == null || bufferSize <= 0) {
			return;
		}
		if (glContextId != GlCleaner.contextId) {
			glContextId = GlCleaner.contextId;
			GL_SPRITES.clear();
		}
		int key = codepoint * 65536 + scaledSize;
		GlAlphaSprite cached = GL_SPRITES.get(Integer.valueOf(key));
		if (cached == null) {
			SoftwareAlphaSprite ss = new SoftwareAlphaSprite(bufferSize, bufferSize, 0, 0, bufferSize, bufferSize, argb);
			cached = new GlAlphaSprite(ss);
			GL_SPRITES.put(Integer.valueOf(key), cached);
		}
		cached.render(x, y);
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
		int scaledSize = Math.max(6, (int) (fontSize * CJK_SCALE));
		int bufferSize = scaledSize + GLYPH_PADDING;
		GlyphBuffer buf = BUFFERS_BY_SIZE.get(Integer.valueOf(scaledSize));
		if (buf == null) {
			buf = new GlyphBuffer(scaledSize);
			BUFFERS_BY_SIZE.put(Integer.valueOf(scaledSize), buf);
		}
		Graphics2D g = buf.g;
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, bufferSize, bufferSize);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.WHITE);
		g.setFont(fontFor(scaledSize));
		String ch = new String(Character.toChars(codepoint));
		FontMetrics fm = g.getFontMetrics();
		int charWidth = fm.charWidth(codepoint);
		if (charWidth <= 0) {
			charWidth = scaledSize;
		}
		g.drawString(ch, GLYPH_PADDING, scaledSize);

		int[] src = new int[bufferSize * bufferSize];
		buf.image.getRGB(0, 0, bufferSize, bufferSize, src, 0, bufferSize);

		// GL（HD）模式：用離屏 sprite（SoftwareAlphaSprite → GlAlphaSprite）渲染，alpha 混合正確
		if (GlRenderer.enabled) {
			try {
				int asc = fm.getAscent();
				int glyphH = asc + fm.getDescent();
				int vertOff = (fontSize - glyphH) / 2;
				if (vertOff < 0) {
					vertOff = 0;
				}
				drawGlSprite(codepoint, scaledSize, src, bufferSize,
						x, baselineY - asc + vertOff);
				return charWidth;
			} catch (Throwable t) {
				// GL 渲染失敗 → 退回只算寬度，不崩潰
				return charWidth;
			}
		}
		return drawGlyphSoftwareToRaster(codepoint, x, baselineY, fontSize, fm, src, bufferSize, charWidth);
	}

	/**
	 * 強制把字元畫進 SoftwareRaster（無論 GL/SD 模式）。供 WorldMapFont 等地圖離屏 sprite 使用：
	 * 地圖在 GL 模式是 SoftwareSprite 離屏渲染，字元必須寫進 SoftwareRaster（再由 GlRaster.drawPixels 上傳），
	 * 不能走 GL sprite 分支（那會直接畫到 framebuffer）。
	 */
	public static int drawGlyphSoftware(int codepoint, int x, int baselineY, int fontSize) {
		if (fontSize <= 0) {
			fontSize = 12;
		}
		int scaledSize = Math.max(6, (int) (fontSize * CJK_SCALE));
		int bufferSize = scaledSize + GLYPH_PADDING;
		GlyphBuffer buf = BUFFERS_BY_SIZE.get(Integer.valueOf(scaledSize));
		if (buf == null) {
			buf = new GlyphBuffer(scaledSize);
			BUFFERS_BY_SIZE.put(Integer.valueOf(scaledSize), buf);
		}
		Graphics2D g = buf.g;
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, bufferSize, bufferSize);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.WHITE);
		g.setFont(fontFor(scaledSize));
		FontMetrics fm = g.getFontMetrics();
		int charWidth = fm.charWidth(codepoint);
		if (charWidth <= 0) {
			charWidth = scaledSize;
		}
		g.drawString(new String(Character.toChars(codepoint)), GLYPH_PADDING, scaledSize);
		int[] src = new int[bufferSize * bufferSize];
		buf.image.getRGB(0, 0, bufferSize, bufferSize, src, 0, bufferSize);
		return drawGlyphSoftwareToRaster(codepoint, x, baselineY, fontSize, fm, src, bufferSize, charWidth);
	}

	/** SoftwareRaster 渲染共用邏輯（drawGlyph 與 drawGlyphSoftware 共用）。 */
	private static int drawGlyphSoftwareToRaster(int codepoint, int x, int baselineY, int fontSize,
			FontMetrics fm, int[] src, int bufferSize, int charWidth) {
		// 安全保護：SoftwareRaster 未初始化（如右鍵選單渲染時）→ 只算寬度不繪製，避免 NPE
		if (SoftwareRaster.pixels == null || SoftwareRaster.width <= 0 || SoftwareRaster.height <= 0) {
			return charWidth;
		}

		int rasterW = SoftwareRaster.width;
		int rasterH = SoftwareRaster.height;
		int[] pixels = SoftwareRaster.pixels;
		int clipLeft = SoftwareRaster.clipLeft;
		int clipTop = SoftwareRaster.clipTop;
		int clipRight = SoftwareRaster.clipRight;
		int clipBottom = SoftwareRaster.clipBottom;

		int ascender = fm.getAscent();
		// 縮小後字元在 lineHeight 內垂直置中，避免文字上移被框線擋住
		int glyphH = ascender + fm.getDescent();
		int vertOffset = (fontSize - glyphH) / 2;
		if (vertOffset < 0) {
			vertOffset = 0;
		}
		int top = baselineY - ascender + vertOffset;
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
		int scaledSize = Math.max(6, (int) (fontSize * CJK_SCALE));
		Graphics2D g = BUFFERS_BY_SIZE.computeIfAbsent(Integer.valueOf(scaledSize), GlyphBuffer::new).g;
		g.setFont(fontFor(scaledSize));
		FontMetrics fm = g.getFontMetrics();
		int w = fm.charWidth(codepoint);
		return w <= 0 ? scaledSize : w;
	}
}
