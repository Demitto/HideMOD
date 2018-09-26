package types.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class DisplayPart extends ModelPart{
	private int displayList;
	private boolean compiled;

	public void render() {
		if (!compiled) {
			compileDisplayList();
		}
		GL11.glPushMatrix();
		GL11.glTranslatef(translateX, translateY, translateZ);

		GL11.glTranslatef(rotatepointX, rotatepointY, rotatepointZ);
		GL11.glRotatef(rotateYaw, 0, 1, 0);
		GL11.glRotatef(rotatePitch, 1, 0, 0);
		GL11.glTranslatef(-rotatepointX, -rotatepointY, -rotatepointZ);

		GL11.glCallList(displayList);
		GL11.glPopMatrix();
	}

	private void compileDisplayList() {
		displayList = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		// 面を全部呼ぶ
		for (Polygon surface : Polygon) {
			if (surface.Vertex.length == 3) {
				bb.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

			} else if (surface.Vertex.length == 4) {
				bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			} else {
				return;
			}
			//頂点を呼ぶ
			for(VertexUV vert:surface.Vertex){
				bb.pos(vert.X, vert.Y, vert.Z).tex(vert.U,vert.V).endVertex();
			}
			Tessellator.getInstance().draw();
		}
		GL11.glEndList();
		compiled = true;
	}

}