package rt4;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

import java.nio.ByteBuffer;

@OriginalClass("client!pd")
public final class WaterMaterialRenderer implements MaterialRenderer {

	@OriginalMember(owner = "client!v", name = "c", descriptor = "[F")
	public static final float[] aFloatArray2 = new float[]{0.073F, 0.169F, 0.24F, 1.0F};
	@OriginalMember(owner = "client!pd", name = "b", descriptor = "[F")
	public static final float[] aFloatArray22 = new float[]{0.1F, 0.1F, 0.15F, 0.1F};
	@OriginalMember(owner = "client!pd", name = "a", descriptor = "I")
	private int anInt4440 = -1;

	@OriginalMember(owner = "client!pd", name = "c", descriptor = "[F")
	private final float[] aFloatArray23 = new float[4];

	@OriginalMember(owner = "client!pd", name = "d", descriptor = "I")
	private int anInt4441 = -1;

	@OriginalMember(owner = "client!pd", name = "e", descriptor = "I")
	private int anInt4442 = -1;

	@OriginalMember(owner = "client!pd", name = "<init>", descriptor = "()V")
	public WaterMaterialRenderer() {
		this.method3435();
		this.method3437();
	}

	@OriginalMember(owner = "client!jj", name = "a", descriptor = "(B)[F")
	public static float[] method2422() {
		@Pc(3) float local3 = FogManager.getLightingModelAmbient() + FogManager.getLight0Diffuse();
		@Pc(9) int local9 = FogManager.getLightColor();
		@Pc(18) float local18 = (float) (local9 >> 16 & 0xFF) / 255.0F;
		ColorUtils.aFloatArray28[3] = 1.0F;
		@Pc(37) float local37 = (float) (local9 >> 8 & 0xFF) / 255.0F;
		@Pc(39) float local39 = 0.58823526F;
		@Pc(46) float local46 = (float) (local9 & 0xFF) / 255.0F;
		ColorUtils.aFloatArray28[2] = aFloatArray2[2] * local46 * local39 * local3;
		ColorUtils.aFloatArray28[0] = aFloatArray2[0] * local18 * local39 * local3;
		ColorUtils.aFloatArray28[1] = local3 * local39 * local37 * aFloatArray2[1];
		return ColorUtils.aFloatArray28;
	}

	@OriginalMember(owner = "client!bk", name = "a", descriptor = "(BI)V")
	public static void method619(@OriginalArg(1) int color) {
		aFloatArray2[0] = (float) (color >> 16 & 0xFF) / 255.0F;
		aFloatArray2[1] = (float) (color >> 8 & 0xFF) / 255.0F;
		aFloatArray2[2] = (float) (color & 0xFF) / 255.0F;
		MaterialManager.resetArgument(3);
		MaterialManager.resetArgument(4);
	}

	@OriginalMember(owner = "client!pd", name = "d", descriptor = "()V")
	private void method3435() {
		@Pc(2) byte[] local2 = new byte[]{0, -1};
		@Pc(12) GL2 gl = GlRenderer.gl;
		@Pc(15) int[] local15 = new int[1];
		gl.glGenTextures(1, local15, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_1D, local15[0]);
		gl.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_ALPHA, 2, 0, GL2.GL_ALPHA, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(local2));
		gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		this.anInt4441 = local15[0];
	}

	@OriginalMember(owner = "client!pd", name = "f", descriptor = "()V")
	private void method3437() {
		@Pc(1) GL2 gl = GlRenderer.gl;
		this.anInt4440 = gl.glGenLists(2);
		gl.glNewList(this.anInt4440, GL2.GL_COMPILE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC1_RGB, GL2.GL_CONSTANT);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_RGB_SCALE, 2.0F);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2GL3.GL_SRC1_ALPHA, GL2.GL_CONSTANT);
		gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
		gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
		gl.glTexGenfv(GL2.GL_S, GL2.GL_OBJECT_PLANE, new float[]{9.765625E-4F, 0.0F, 0.0F, 0.0F}, 0);
		gl.glTexGenfv(GL2.GL_T, GL2.GL_OBJECT_PLANE, new float[]{0.0F, 0.0F, 9.765625E-4F, 0.0F}, 0);
		gl.glEnable(GL2.GL_TEXTURE_GEN_S);
		gl.glEnable(GL2.GL_TEXTURE_GEN_T);
		if (MaterialManager.allows3DTextureMapping) {
			gl.glBindTexture(GL2.GL_TEXTURE_3D, MaterialManager.texture3D);
			gl.glTexGeni(GL2.GL_R, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
			gl.glTexGeni(GL2.GL_Q, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
			gl.glTexGenfv(GL2.GL_Q, GL2.GL_OBJECT_PLANE, new float[]{0.0F, 0.0F, 0.0F, 1.0F}, 0);
			gl.glEnable(GL2.GL_TEXTURE_GEN_R);
			gl.glEnable(GL2.GL_TEXTURE_GEN_Q);
			gl.glEnable(GL2.GL_TEXTURE_3D);
		}
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		gl.glEnable(GL2.GL_TEXTURE_1D);
		gl.glBindTexture(GL2.GL_TEXTURE_1D, this.anInt4441);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_INTERPOLATE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC0_RGB, GL2.GL_CONSTANT);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC2_RGB, GL2.GL_TEXTURE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_ALPHA, GL2.GL_INTERPOLATE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC0_ALPHA, GL2.GL_CONSTANT);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC2_ALPHA, GL2.GL_TEXTURE);
		gl.glEnable(GL2.GL_TEXTURE_GEN_S);
		gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glEndList();
		gl.glNewList(this.anInt4440 + 1, GL2.GL_COMPILE);
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		gl.glDisable(GL2.GL_TEXTURE_1D);
		gl.glDisable(GL2.GL_TEXTURE_GEN_S);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_MODULATE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC0_RGB, GL2.GL_TEXTURE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC2_RGB, GL2.GL_CONSTANT);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_ALPHA, GL2.GL_MODULATE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC0_ALPHA, GL2.GL_TEXTURE);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC2_ALPHA, GL2.GL_CONSTANT);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SRC1_RGB, GL2.GL_PREVIOUS);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_RGB_SCALE, 1.0F);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2GL3.GL_SRC1_ALPHA, GL2.GL_PREVIOUS);
		gl.glDisable(GL2.GL_TEXTURE_GEN_S);
		gl.glDisable(GL2.GL_TEXTURE_GEN_T);
		if (MaterialManager.allows3DTextureMapping) {
			gl.glDisable(GL2.GL_TEXTURE_GEN_R);
			gl.glDisable(GL2.GL_TEXTURE_GEN_Q);
			gl.glDisable(GL2.GL_TEXTURE_3D);
		}
		gl.glEndList();
	}

	@OriginalMember(owner = "client!pd", name = "a", descriptor = "()V")
	@Override
	public final void unbind() {
		GlRenderer.gl.glCallList(this.anInt4440 + 1);
	}

	@OriginalMember(owner = "client!pd", name = "a", descriptor = "(I)V")
	@Override
	public final void setArgument(@OriginalArg(0) int arg0) {
		@Pc(1) GL2 gl = GlRenderer.gl;
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR, aFloatArray2, 0);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		if ((arg0 & 0x1) == 1) {
			if (!MaterialManager.allows3DTextureMapping) {
				GlRenderer.setTextureId(MaterialManager.anIntArray341[GlRenderer.anInt5323 * 64 / 100 % 64]);
			} else if (this.anInt4442 != GlRenderer.anInt5323) {
				this.aFloatArray23[0] = 0.0F;
				this.aFloatArray23[1] = 0.0F;
				this.aFloatArray23[2] = 0.0F;
				this.aFloatArray23[3] = (float) GlRenderer.anInt5323 * 0.005F;
				gl.glTexGenfv(GL2.GL_R, GL2.GL_OBJECT_PLANE, this.aFloatArray23, 0);
				this.anInt4442 = GlRenderer.anInt5323;
			}
		} else if (MaterialManager.allows3DTextureMapping) {
			this.aFloatArray23[0] = 0.0F;
			this.aFloatArray23[1] = 0.0F;
			this.aFloatArray23[2] = 0.0F;
			this.aFloatArray23[3] = 0.0F;
			gl.glTexGenfv(GL2.GL_R, GL2.GL_OBJECT_PLANE, this.aFloatArray23, 0);
		} else {
			GlRenderer.setTextureId(MaterialManager.anIntArray341[0]);
		}
	}

	@OriginalMember(owner = "client!pd", name = "b", descriptor = "()V")
	@Override
	public final void bind() {
		@Pc(1) GL2 gl = GlRenderer.gl;
		GlRenderer.setTextureCombineRgbMode(2);
		GlRenderer.setTextureCombineAlphaMode(2);
		GlRenderer.resetTextureMatrix();
		gl.glCallList(this.anInt4440);
		@Pc(12) float local12 = 2662.4001F;
		local12 += (float) (MaterialManager.anInt5559 - 128) * 0.5F;
		float max = (float) GlobalConfig.VIEW_DISTANCE - GlobalConfig.VIEW_FADE_DISTANCE;
		if (local12 >= max) {
			local12 = max - 1.0f;
		}
		this.aFloatArray23[0] = 0.0F;
		this.aFloatArray23[1] = 0.0F;
		this.aFloatArray23[2] = 1.0F / (local12 - max);
		this.aFloatArray23[3] = local12 / (local12 - max);
		gl.glTexGenfv(GL2.GL_S, GL2.GL_EYE_PLANE, this.aFloatArray23, 0);
		gl.glPopMatrix();
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR, aFloatArray22, 0);
	}

	@OriginalMember(owner = "client!pd", name = "c", descriptor = "()I")
	@Override
	public final int getFlags() {
		return 15;
	}
}
