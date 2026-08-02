package rt4;

import com.jogamp.opengl.GL2;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ob")
public final class WaterfallMaterialRenderer implements MaterialRenderer {

	@OriginalMember(owner = "client!ob", name = "g", descriptor = "I")
	private int anInt4236;

	@OriginalMember(owner = "client!ob", name = "n", descriptor = "[F")
	private final float[] aFloatArray21 = new float[4];

	@OriginalMember(owner = "client!ob", name = "<init>", descriptor = "()V")
	public WaterfallMaterialRenderer() {
		this.method3307();
	}

	@OriginalMember(owner = "client!ob", name = "c", descriptor = "()I")
	@Override
	public final int getFlags() {
		return 0;
	}

	@OriginalMember(owner = "client!ob", name = "a", descriptor = "(I)V")
	@Override
	public final void setArgument(@OriginalArg(0) int arg0) {
		@Pc(7) GL2 gl = GlRenderer.gl;
		@Pc(18) float local18 = (float) ((arg0 >> 3 & 0x3) + 1) * 0.01F;
		@Pc(27) float local27 = -0.01F * (float) ((arg0 & 0x3) + 1);
		@Pc(36) float local36 = (arg0 & 0x40) == 0 ? 4.8828125E-4F : 9.765625E-4F;
		@Pc(47) boolean local47 = (arg0 & 0x80) != 0;
		if (local47) {
			this.aFloatArray21[0] = local36;
			this.aFloatArray21[1] = 0.0F;
			this.aFloatArray21[2] = 0.0F;
			this.aFloatArray21[3] = 0.0F;
		} else {
			this.aFloatArray21[2] = local36;
			this.aFloatArray21[1] = 0.0F;
			this.aFloatArray21[3] = 0.0F;
			this.aFloatArray21[0] = 0.0F;
		}
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		gl.glRotatef((float) MaterialManager.anInt5559 * 360.0F / 2048.0F, 1.0F, 0.0F, 0.0F);
		gl.glRotatef((float) MaterialManager.anInt1815 * 360.0F / 2048.0F, 0.0F, 1.0F, 0.0F);
		gl.glTranslatef((float) -MaterialManager.anInt406, (float) -MaterialManager.anInt4675, (float) -MaterialManager.anInt5158);
		gl.glTexGenfv(GL2.GL_S, GL2.GL_EYE_PLANE, this.aFloatArray21, 0);
		this.aFloatArray21[3] = local27 * (float) GlRenderer.anInt5323;
		this.aFloatArray21[0] = 0.0F;
		this.aFloatArray21[2] = 0.0F;
		this.aFloatArray21[1] = local36;
		gl.glTexGenfv(GL2.GL_T, GL2.GL_EYE_PLANE, this.aFloatArray21, 0);
		gl.glPopMatrix();
		if (MaterialManager.allows3DTextureMapping) {
			this.aFloatArray21[3] = (float) GlRenderer.anInt5323 * local18;
			this.aFloatArray21[1] = 0.0F;
			this.aFloatArray21[0] = 0.0F;
			this.aFloatArray21[2] = 0.0F;
			gl.glTexGenfv(GL2.GL_R, GL2.GL_OBJECT_PLANE, this.aFloatArray21, 0);
		} else {
			@Pc(189) int local189 = (int) ((float) GlRenderer.anInt5323 * local18 * 64.0F);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, MaterialManager.waterfallTextures[local189 % 64]);
		}
		gl.glActiveTexture(GL2.GL_TEXTURE0);
	}

	@OriginalMember(owner = "client!ob", name = "a", descriptor = "()V")
	@Override
	public final void unbind() {
		@Pc(1) GL2 gl = GlRenderer.gl;
		gl.glCallList(this.anInt4236 + 1);
	}

	@OriginalMember(owner = "client!ob", name = "b", descriptor = "()V")
	@Override
	public final void bind() {
		@Pc(5) GL2 gl = GlRenderer.gl;
		gl.glCallList(this.anInt4236);
	}

	@OriginalMember(owner = "client!ob", name = "b", descriptor = "(I)V")
	private void method3307() {
		@Pc(3) GL2 gl = GlRenderer.gl;
		this.anInt4236 = gl.glGenLists(2);
		gl.glNewList(this.anInt4236, GL2.GL_COMPILE);
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		if (MaterialManager.allows3DTextureMapping) {
			gl.glBindTexture(GL2.GL_TEXTURE_3D, MaterialManager.waterfallTextureId);
			gl.glTexGeni(GL2.GL_R, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
			gl.glEnable(GL2.GL_TEXTURE_GEN_R);
			gl.glEnable(GL2.GL_TEXTURE_3D);
		} else {
			gl.glEnable(GL2.GL_TEXTURE_2D);
		}
		gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
		gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_EYE_LINEAR);
		gl.glEnable(GL2.GL_TEXTURE_GEN_S);
		gl.glEnable(GL2.GL_TEXTURE_GEN_T);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glEndList();
		gl.glNewList(this.anInt4236 + 1, GL2.GL_COMPILE);
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		if (MaterialManager.allows3DTextureMapping) {
			gl.glDisable(GL2.GL_TEXTURE_3D);
			gl.glDisable(GL2.GL_TEXTURE_GEN_R);
		} else {
			gl.glDisable(GL2.GL_TEXTURE_2D);
		}
		gl.glDisable(GL2.GL_TEXTURE_GEN_S);
		gl.glDisable(GL2.GL_TEXTURE_GEN_T);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glEndList();
	}
}
