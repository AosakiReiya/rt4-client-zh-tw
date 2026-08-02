package rt4;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import plugin.PluginRepository;

import java.awt.*;

@OriginalClass("client!od")
public final class DisplayMode {

	@OriginalMember(owner = "client!ib", name = "i", descriptor = "[Lclient!od;")
	public static DisplayMode[] aClass114Array1;
	@OriginalMember(owner = "client!rc", name = "M", descriptor = "Z")
	public static boolean start_GLRenderer = false;
	@OriginalMember(owner = "client!jk", name = "y", descriptor = "Z")
	public static boolean resizable = false;
	@OriginalMember(owner = "client!hi", name = "f", descriptor = "J")
	public static long aLong89 = 0L;

	@OriginalMember(owner = "client!od", name = "j", descriptor = "I")
	public int width;

	@OriginalMember(owner = "client!od", name = "k", descriptor = "I")
	public int refreshRate;

	@OriginalMember(owner = "client!od", name = "l", descriptor = "I")
	public int height;

	@OriginalMember(owner = "client!od", name = "m", descriptor = "I")
	public int bitDepth;

	public static boolean resizableSD = false;

	@OriginalMember(owner = "client!c", name = "a", descriptor = "(Ljava/awt/Frame;ZLsignlink!ll;)V")
	public static void exitFullScreen(@OriginalArg(0) Frame frame, @OriginalArg(2) SignLink signLink) {
		while (true) {
			@Pc(16) PrivilegedRequest request = signLink.exitFullScreen(frame);
			while (request.status == 0) {
				ThreadUtils.sleep(10L);
			}
			if (request.status == 1) {
				frame.setVisible(false);
				frame.dispose();
				return;
			}
			ThreadUtils.sleep(100L);
		}
	}

	@OriginalMember(owner = "client!th", name = "a", descriptor = "(ZIIII)V")
	public static void setWindowMode(@OriginalArg(0) boolean replaceCanvas, @OriginalArg(1) int newMode, @OriginalArg(3) int width, @OriginalArg(4) int height) {
		aLong89 = 0L;
		@Pc(4) int currentMode = getWindowMode();
		if (newMode == 3 || currentMode == 3) {
			replaceCanvas = true;
		}
		@Pc(44) boolean useHD = currentMode > 0 != newMode > 0;
		if (replaceCanvas && newMode > 0 && !resizableSD) {
			useHD = true;
		}
		setWindowMode(replaceCanvas, newMode, useHD, currentMode, width, height);
	}

	@OriginalMember(owner = "client!le", name = "a", descriptor = "(I)I")
	public static int getWindowMode() {
		if (GameShell.fullScreenFrame != null) {
			return 3;
		} else if ((GlRenderer.enabled && resizable) || resizableSD) {
			return 2;
		} else if (GlRenderer.enabled && !resizable) {
			return 1;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!pm", name = "a", descriptor = "(ZIZIZII)V")
	public static void setWindowMode(@OriginalArg(0) boolean replaceCanvas, @OriginalArg(1) int newMode, @OriginalArg(2) boolean useHD, @OriginalArg(3) int currentMode, @OriginalArg(5) int width, @OriginalArg(6) int height) {
		if (useHD) {
			GlRenderer.quit();
		}
		if (GameShell.fullScreenFrame != null && (newMode != 3 || width != Preferences.fullScreenWidth || height != Preferences.fullScreenHeight)) {
			exitFullScreen(GameShell.fullScreenFrame, GameShell.signLink);
			GameShell.fullScreenFrame = null;
		}
		if (newMode == 3 && GameShell.fullScreenFrame == null) {
			GameShell.fullScreenFrame = createFullScreenFrame(0, height, width, GameShell.signLink);
			if (GameShell.fullScreenFrame != null) {
				Preferences.fullScreenHeight = height;
				Preferences.fullScreenWidth = width;
				Preferences.write(GameShell.signLink);
			}
		}
		/**
		 * If somehow the above code block fails to produce a frame for Fullscreen then this block is a safeguard
		 * to fall back to Standard Mode.
 		 */
		if (newMode == 3 && GameShell.fullScreenFrame == null) {
			setWindowMode(true, Preferences.favoriteWorlds, true, currentMode, -1, -1);
			return;
		}
		@Pc(85) Container container;
		if (GameShell.fullScreenFrame != null) {
			container = GameShell.fullScreenFrame;
		} else if (GameShell.frame == null) {
			container = GameShell.signLink.applet;
		} else {
			container = GameShell.frame;
		}
		GameShell.frameWidth = container.getSize().width;
		GameShell.frameHeight = container.getSize().height;
		@Pc(109) Insets insets;
		if (GameShell.frame == container) {
			insets = GameShell.frame.getInsets();
			GameShell.frameWidth -= insets.right + insets.left;
			GameShell.frameHeight -= insets.bottom + insets.top;
		}
		if (newMode >= 2 || resizableSD) {
			GameShell.canvasWidth = GameShell.frameWidth;
			GameShell.canvasHeight = GameShell.frameHeight;
			GameShell.leftMargin = 0;
			GameShell.topMargin = 0;
		} else {
			GameShell.topMargin = 0;
			GameShell.leftMargin = (GameShell.frameWidth - 765) / 2;
			GameShell.canvasWidth = 765;
			GameShell.canvasHeight = 503;
		}
		/**
		 * This code bock executes for Fullscreen mode and removes current canvas in main frame window
		 * with a new canvas for the fullscreen frame window. Else if no canvas is replaced then just makes
		 * size adjustments to current canvas.
		 */
		if (replaceCanvas) {
			Keyboard.stop(GameShell.canvas);
			Mouse.stop(GameShell.canvas);
			if (client.mouseWheel != null) {
				client.mouseWheel.stop(GameShell.canvas);
			}
			client.instance.addCanvas();
			Keyboard.start(GameShell.canvas);
			Mouse.start(GameShell.canvas);
			if (client.mouseWheel != null) {
				client.mouseWheel.start(GameShell.canvas);
			}
		} else {
			if (GlRenderer.enabled) {
				GlRenderer.setCanvasSize(GameShell.canvasWidth, GameShell.canvasHeight);
			}
			GameShell.canvas.setSize(GameShell.canvasWidth, GameShell.canvasHeight);
			if (GameShell.frame == container) {
				insets = GameShell.frame.getInsets();
				GameShell.canvas.setLocation(insets.left + GameShell.leftMargin, insets.top + GameShell.topMargin);
			} else {
				GameShell.canvas.setLocation(GameShell.leftMargin, GameShell.topMargin);
			}
		}
		/**
		 * newMode 0 is no mode, so this would be a reset of the canvas and glrenderer
		 */
		if (newMode == 0 && currentMode > 0) {
			GlRenderer.createAndDestroyContext(GameShell.canvas);
		}
		/**
		 *  This Code block starts on mode 1=HD,2=HDResize,3=Fullscreen. It also Starts the GLrenderer.
		 */
		if (useHD && newMode > 0) {
			GameShell.canvas.setIgnoreRepaint(true);
			if (!start_GLRenderer) {
				SceneGraph.clear();
				SoftwareRaster.frameBuffer = null;
				SoftwareRaster.frameBuffer = FrameBuffer.create(GameShell.canvasHeight, GameShell.canvasWidth, GameShell.canvas);
				SoftwareRaster.clear();
				if (client.gameState == 5) {
					LoadingBar.render(true, Fonts.b12Full);
				} else {
					Fonts.drawTextOnScreen(false, LocalizedText.LOADING);
				}
				try {
					@Pc(269) Graphics graphics = GameShell.canvas.getGraphics();
					SoftwareRaster.frameBuffer.draw(graphics);
				} catch (@Pc(277) Exception local277) {
				}
				GameShell.method2704(); // Creates a black background for SD Mode window, gameplay frame will render on top of.
				if (currentMode == 0) {
					if(resizableSD)
						SoftwareRaster.frameBuffer = FrameBuffer.create(GameShell.frameHeight, GameShell.frameWidth, GameShell.canvas);
					else
						SoftwareRaster.frameBuffer = FrameBuffer.create(503, 765, GameShell.canvas);
				} else {
					SoftwareRaster.frameBuffer = null;
				}

				/**
				 * Code below tries to trigger GLProfile.initSingleton() through a queue in PrivilegedRequest where it waits
				 * for its turn. This along with the while loop waits long enough to cause a de-sync in the start process which causes
				 * lag and delay when switching to HD Mode and when launching the client in HD Mode.
				 * GLProfile.initSingleton() is automatically run starting with jogl 2.0 and later. So no need to execute it.
				 * After it returns status code 1, it also sets an important variable aBoolean73(Start_Renderer) to true.
				 * This triggers the needed GLRenderer.init() function which we can control to be true manually as intended.
				 **/

				/**
				 @Pc(300) PrivilegedRequest local300 = GameShell.signLink.loadGlNatives(client.instance.getClass());
				 while (local300.status == 0) {
				 ThreadUtils.sleep(100L);
				 }
				 if (local300.status == 1) {
				 aBoolean73 = true;
				 }
				 */

				start_GLRenderer = true; // Manually set here to allow GLRenderer.init() to execute.
			}
			if (start_GLRenderer) {
				GlRenderer.init(GameShell.canvas, Preferences.antiAliasingMode * 2);
			}
		}
		// If HD Mode fails, this restarts the whole process to enter into SD Mode
		if (!GlRenderer.enabled && newMode > 0) {
			setWindowMode(true, 0, true, currentMode, -1, -1);
			return;
		}
		/**
		 * Executes once when the client is launched. Doesn't need to be executed more than that.
		 */
		if (newMode > 0 && currentMode == 0) {
			GameShell.thread.setPriority(5);
			SoftwareRaster.frameBuffer = null;
			SoftwareModel.method4580();
			((Js5GlTextureProvider) Rasteriser.textureProvider).method3248(200);
			if (Preferences.highDetailLighting) {
				Rasteriser.setBrightness(0.7F);
			}
			LoginManager.method4637();
		} else if (newMode == 0 && currentMode > 0) { // This is when client switches from any other mode to SD Mode
			GameShell.thread.setPriority(1);
			if(resizableSD)
				SoftwareRaster.frameBuffer = FrameBuffer.create(GameShell.frameHeight, GameShell.frameWidth, GameShell.canvas);
			else
				SoftwareRaster.frameBuffer = FrameBuffer.create(503, 765, GameShell.canvas);
			SoftwareModel.method4583();
			ParticleSystem.quit();
			((Js5GlTextureProvider) Rasteriser.textureProvider).method3248(20);
			if (Preferences.highDetailLighting) {
				if (Preferences.brightness == 1) {
					Rasteriser.setBrightness(0.9F);
				}
				if (Preferences.brightness == 2) {
					Rasteriser.setBrightness(0.8F);
				}
				if (Preferences.brightness == 3) {
					Rasteriser.setBrightness(0.7F);
				}
				if (Preferences.brightness == 4) {
					Rasteriser.setBrightness(0.6F);
				}
			}
			GlTile.method1939();
			LoginManager.method4637();
		}
		SceneGraph.aBoolean130 = !SceneGraph.allLevelsAreVisible();
		if (useHD) {
			client.method2721();
		}
		resizable = newMode == 2; // resizeable should only be done in HD-Resizeable mode and not full screen mode.
		if (InterfaceList.topLevelInterface != -1) {
			InterfaceList.method3712(true);
		}
		if (Protocol.socket != null && (client.gameState == 30 || client.gameState == 25)) {
			ClientProt.sendWindowDetails();
		}
		for (@Pc(466) int local466 = 0; local466 < 100; local466++) {
			InterfaceList.aBooleanArray100[local466] = true;
		}
		GameShell.fullRedraw = true;
		PluginRepository.reloadPlugins();
	}

	@OriginalMember(owner = "client!ab", name = "c", descriptor = "(B)[Lclient!od;")
	public static DisplayMode[] getDisplayModes() {
		if (aClass114Array1 == null) {
			@Pc(16) DisplayMode[] local16 = method3558(GameShell.signLink);
			@Pc(20) DisplayMode[] local20 = new DisplayMode[local16.length];
			@Pc(22) int local22 = 0;
			label52:
			for (@Pc(24) int local24 = 0; local24 < local16.length; local24++) {
				@Pc(32) DisplayMode local32 = local16[local24];
				if ((local32.bitDepth <= 0 || local32.bitDepth >= 24) && local32.width >= 800 && local32.height >= 600) {
					for (@Pc(52) int local52 = 0; local52 < local22; local52++) {
						@Pc(59) DisplayMode local59 = local20[local52];
						if (local32.width == local59.width && local59.height == local32.height) {
							if (local32.bitDepth > local59.bitDepth) {
								local20[local52] = local32;
							}
							continue label52;
						}
					}
					local20[local22] = local32;
					local22++;
				}
			}
			aClass114Array1 = new DisplayMode[local22];
			ArrayUtils.copy(local20, 0, aClass114Array1, 0, local22);
			@Pc(112) int[] local112 = new int[aClass114Array1.length];
			for (@Pc(114) int local114 = 0; local114 < aClass114Array1.length; local114++) {
				@Pc(122) DisplayMode local122 = aClass114Array1[local114];
				local112[local114] = local122.height * local122.width;
			}
			ArrayUtils.sort(local112, aClass114Array1);
		}
		return aClass114Array1;
	}

	@OriginalMember(owner = "client!pm", name = "a", descriptor = "(ILsignlink!ll;)[Lclient!od;")
	public static DisplayMode[] method3558(@OriginalArg(1) SignLink arg0) {
		if (!arg0.isFullScreenSupported()) {
			return new DisplayMode[0];
		}
		@Pc(17) PrivilegedRequest local17 = arg0.getDisplayModes();
		while (local17.status == 0) {
			ThreadUtils.sleep(10L);
		}
		if (local17.status == 2) {
			return new DisplayMode[0];
		}
		@Pc(39) int[] local39 = (int[]) local17.result;
		@Pc(45) DisplayMode[] local45 = new DisplayMode[local39.length >> 2];
		for (@Pc(47) int local47 = 0; local47 < local45.length; local47++) {
			@Pc(59) DisplayMode local59 = new DisplayMode();
			local45[local47] = local59;
			local59.width = local39[local47 << 2];
			local59.height = local39[(local47 << 2) + 1];
			local59.bitDepth = local39[(local47 << 2) + 2];
			local59.refreshRate = local39[(local47 << 2) + 3];
		}
		return local45;
	}

	@OriginalMember(owner = "client!nf", name = "a", descriptor = "(IIIIILsignlink!ll;)Ljava/awt/Frame;")
	public static Frame createFullScreenFrame(@OriginalArg(2) int bitDepth, @OriginalArg(3) int height, @OriginalArg(4) int width, @OriginalArg(5) SignLink signLink) {
		if (!signLink.isFullScreenSupported()) {
			return null;
		}
		@Pc(20) DisplayMode[] displayModes = method3558(signLink);
		if (displayModes == null) {
			return null;
		}
		@Pc(27) boolean local27 = false;
		for (@Pc(29) int local29 = 0; local29 < displayModes.length; local29++) {
			if (width == displayModes[local29].width && height == displayModes[local29].height && (!local27 || displayModes[local29].bitDepth > bitDepth)) {
				bitDepth = displayModes[local29].bitDepth;
				local27 = true;
			}
		}
		if (!local27) {
			return null;
		}
		@Pc(90) PrivilegedRequest request = signLink.enterFullScreen(bitDepth, height, width);
		while (request.status == 0) {
			ThreadUtils.sleep(10L);
		}
		@Pc(103) Frame frame = (Frame) request.result;
		if (frame == null) {
			return null;
		} else if (request.status == 2) {
			exitFullScreen(frame, signLink);
			return null;
		} else {
			return frame;
		}
	}

}
