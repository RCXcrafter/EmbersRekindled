package com.rekindled.embers.blockentity.render;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.CrystalSeedBlockEntity;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class CrystalSeedBlockEntityRenderer implements BlockEntityRenderer<CrystalSeedBlockEntity> {

	public CrystalSeedBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(CrystalSeedBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//VertexConsumer buffer = bufferSource.getBuffer(EmbersRenderTypes.CRYSTAL_SEED);
			RenderSystem.setShaderTexture(0, blockEntity.texture);
			RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
			RenderSystem.enableDepthTest();
			BufferBuilder buffer = Tesselator.getInstance().getBuilder();
			RenderSystem.disableCull();

			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.5);
			poseStack.mulPose(Axis.XP.rotationDegrees(15.0f*(float)Math.sin(Math.toRadians(blockEntity.ticksExisted+partialTick))));

			buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
			this.drawCrystal(buffer, poseStack.last().pose(), 0, 0, 0, ((float) blockEntity.ticksExisted+partialTick)*6.0f, 1.0f, 0.25f, 0.0f, 0.75f, 1.0f);
			BufferUploader.drawWithShader(buffer.end());

			poseStack.mulPose(Axis.XP.rotationDegrees(-15.0f*(float)Math.sin(Math.toRadians(blockEntity.ticksExisted+partialTick))));
			poseStack.mulPose(Axis.XP.rotationDegrees(-15.0f*(float)Math.sin(Math.toRadians(2.5f*(blockEntity.ticksExisted+partialTick)))));

			buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
			float oneAng = 360f/blockEntity.willSpawn.length;
			Random crystalRandom = new Random(blockEntity.getBlockPos().asLong());
			for (int i = 0; i < blockEntity.willSpawn.length; i += 1) {
				if (blockEntity.willSpawn[i]) {
					float distVariation = 1.0f;
					if(blockEntity.willSpawn.length > 12)
						distVariation += crystalRandom.nextFloat() * 0.5f;
					float offX = distVariation * 0.4f * (float)Math.sin(Math.toRadians(oneAng*i+((float) blockEntity.ticksExisted+partialTick)*2.0f));
					float offZ = distVariation * 0.4f * (float)Math.cos(Math.toRadians(oneAng*i+((float) blockEntity.ticksExisted+partialTick)*2.0f));
					float texWidth = 0.125f*2*blockEntity.size/1000.0f;
					float texHeight = 0.25f*2*blockEntity.size/1000.0f;
					float texX = crystalRandom.nextFloat() * (1 - texWidth);
					float texY = crystalRandom.nextFloat() * (1 - texHeight);
					float sizeVariation = 0.5f + crystalRandom.nextFloat() * 0.5f;
					this.drawCrystal(buffer, poseStack.last().pose(), offX, 0, offZ, (blockEntity.ticksExisted+partialTick)*2.0f, sizeVariation*0.4f*(blockEntity.size/1000.0f), texX, texY, texX+texWidth, texY+texHeight);
				}
			}
			BufferUploader.drawWithShader(buffer.end());

			poseStack.popPose();
			RenderSystem.enableCull();
		}
	}

	public void drawCrystal(VertexConsumer b, Matrix4f matrix4f, float x, float y, float z, float rotation, float size, float minU, float minV, float maxU, float maxV) {
		float offX1 = size * 0.25f * (float)Math.sin(Math.toRadians(rotation));
		float offZ1 = size * 0.25f * (float)Math.cos(Math.toRadians(rotation));
		float offX2 = size * 0.25f * (float)Math.sin(Math.toRadians(rotation+90.0f));
		float offZ2 = size * 0.25f * (float)Math.cos(Math.toRadians(rotation+90.0f));
		float pos1X = x;
		float pos1Y = y-size*0.5f;
		float pos1Z = z;
		float pos2X = x+offX1;
		float pos2Y = y;
		float pos2Z = z+offZ1;
		float pos3X = x+offX2;
		float pos3Y = y;
		float pos3Z = z+offZ2;
		float pos4X = x-offX1;
		float pos4Y = y;
		float pos4Z = z-offZ1;
		float pos5X = x-offX2;
		float pos5Y = y;
		float pos5Z = z-offZ2;
		float pos6X = x;
		float pos6Y = y+size*0.5f;
		float pos6Z = z;
		Vector3f diff1 = new Vector3f(pos3X-pos1X,pos3Y-pos1Y,pos3Z-pos1Z);
		Vector3f diff2 = new Vector3f(pos2X-pos1X,pos2Y-pos1Y,pos2Z-pos1Z);
		Vector3f normal1 = new Vector3f(diff1.y*diff2.z-diff1.z*diff2.y,diff1.x*diff2.z-diff1.z*diff2.x,diff1.x*diff2.y-diff1.y*diff2.x).normalize().mul(-1.0f);
		Vector3f normal2 = new Vector3f(normal1.z,-normal1.y,normal1.x).mul(-1.0f);
		Vector3f normal3 = new Vector3f(-normal1.x,-normal1.y,-normal1.z).mul(-1.0f);
		Vector3f normal4 = new Vector3f(-normal1.z,-normal1.y,-normal1.x).mul(-1.0f);
		Vector3f normal5 = new Vector3f(normal1.x,normal1.y,normal1.z).mul(-1.0f);
		Vector3f normal6 = new Vector3f(normal1.z,normal1.y,normal1.x).mul(-1.0f);
		Vector3f normal7 = new Vector3f(-normal1.x,normal1.y,-normal1.z).mul(-1.0f);
		Vector3f normal8 = new Vector3f(-normal1.z,normal1.y,-normal1.x).mul(-1.0f);
		normal1 = new Vector3f(normal1.x,-normal1.y,normal1.z).mul(-1.0f);
		b.vertex(matrix4f, pos1X, pos1Y, pos1Z).uv((minU+maxU)/2.0f, maxV).color(255, 255, 255, 255).normal((float)normal1.x,(float)normal1.y,(float)normal1.z).endVertex();
		b.vertex(matrix4f, pos2X, pos2Y, pos2Z).uv(minU, minV).color(255, 255, 255, 255).normal((float)normal1.x,(float)normal1.y,(float)normal1.z).endVertex();
		b.vertex(matrix4f, pos3X, pos3Y, pos3Z).uv(maxU, minV).color(255, 255, 255, 255).normal((float)normal1.x,(float)normal1.y,(float)normal1.z).endVertex();

		b.vertex(matrix4f, pos1X, pos1Y, pos1Z).uv((minU+maxU)/2.0f, maxV).color(255, 255, 255, 255).normal((float)normal2.x,(float)normal2.y,(float)normal2.z).endVertex();
		b.vertex(matrix4f, pos3X, pos3Y, pos3Z).uv(minU, minV).color(255, 255, 255, 255).normal((float)normal2.x,(float)normal2.y,(float)normal2.z).endVertex();
		b.vertex(matrix4f, pos4X, pos4Y, pos4Z).uv(maxU, minV).color(255, 255, 255, 255).normal((float)normal2.x,(float)normal2.y,(float)normal2.z).endVertex();

		b.vertex(matrix4f, pos1X, pos1Y, pos1Z).uv((minU+maxU)/2.0f, maxV).color(255, 255, 255, 255).normal((float)normal3.x,(float)normal3.y,(float)normal3.z).endVertex();
		b.vertex(matrix4f, pos4X, pos4Y, pos4Z).uv(minU, minV).color(255, 255, 255, 255).normal((float)normal3.x,(float)normal3.y,(float)normal3.z).endVertex();
		b.vertex(matrix4f, pos5X, pos5Y, pos5Z).uv(maxU, minV).color(255, 255, 255, 255).normal((float)normal3.x,(float)normal3.y,(float)normal3.z).endVertex();

		b.vertex(matrix4f, pos1X, pos1Y, pos1Z).uv((minU+maxU)/2.0f, maxV).color(255, 255, 255, 255).normal((float)normal4.x,(float)normal4.y,(float)normal4.z).endVertex();
		b.vertex(matrix4f, pos5X, pos5Y, pos5Z).uv(minU, minV).color(255, 255, 255, 255).normal((float)normal4.x,(float)normal4.y,(float)normal4.z).endVertex();
		b.vertex(matrix4f, pos2X, pos2Y, pos2Z).uv(maxU, minV).color(255, 255, 255, 255).normal((float)normal4.x,(float)normal4.y,(float)normal4.z).endVertex();

		b.vertex(matrix4f, pos6X, pos6Y, pos6Z).uv((minU+maxU)/2.0f, minV).color(255, 255, 255, 255).normal((float)normal5.x,(float)normal5.y,(float)normal5.z).endVertex();
		b.vertex(matrix4f, pos2X, pos2Y, pos2Z).uv(minU, maxV).color(255, 255, 255, 255).normal((float)normal5.x,(float)normal5.y,(float)normal5.z).endVertex();
		b.vertex(matrix4f, pos3X, pos3Y, pos3Z).uv(maxU, maxV).color(255, 255, 255, 255).normal((float)normal5.x,(float)normal5.y,(float)normal5.z).endVertex();

		b.vertex(matrix4f, pos6X, pos6Y, pos6Z).uv((minU+maxU)/2.0f, minV).color(255, 255, 255, 255).normal((float)normal6.x,(float)normal6.y,(float)normal6.z).endVertex();
		b.vertex(matrix4f, pos3X, pos3Y, pos3Z).uv(minU, maxV).color(255, 255, 255, 255).normal((float)normal6.x,(float)normal6.y,(float)normal6.z).endVertex();
		b.vertex(matrix4f, pos4X, pos4Y, pos4Z).uv(maxU, maxV).color(255, 255, 255, 255).normal((float)normal6.x,(float)normal6.y,(float)normal6.z).endVertex();

		b.vertex(matrix4f, pos6X, pos6Y, pos6Z).uv((minU+maxU)/2.0f, minV).color(255, 255, 255, 255).normal((float)normal7.x,(float)normal7.y,(float)normal7.z).endVertex();
		b.vertex(matrix4f, pos4X, pos4Y, pos4Z).uv(minU, maxV).color(255, 255, 255, 255).normal((float)normal7.x,(float)normal7.y,(float)normal7.z).endVertex();
		b.vertex(matrix4f, pos5X, pos5Y, pos5Z).uv(maxU, maxV).color(255, 255, 255, 255).normal((float)normal7.x,(float)normal7.y,(float)normal7.z).endVertex();

		b.vertex(matrix4f, pos6X, pos6Y, pos6Z).uv((minU+maxU)/2.0f, minV).color(255, 255, 255, 255).normal((float)normal8.x,(float)normal8.y,(float)normal8.z).endVertex();
		b.vertex(matrix4f, pos5X, pos5Y, pos5Z).uv(minU, maxV).color(255, 255, 255, 255).normal((float)normal8.x,(float)normal8.y,(float)normal8.z).endVertex();
		b.vertex(matrix4f, pos2X, pos2Y, pos2Z).uv(maxU, maxV).color(255, 255, 255, 255).normal((float)normal8.x,(float)normal8.y,(float)normal8.z).endVertex();
	}
}
