package com.rekindled.embers.blockentity.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rekindled.embers.blockentity.ReservoirBlockEntity;
import com.rekindled.embers.render.EmbersRenderTypes;
import com.rekindled.embers.render.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public class ReservoirBlockEntityRenderer implements BlockEntityRenderer<ReservoirBlockEntity> {

	float[] bounds = getBlockBounds(2, 0, 1);

	public ReservoirBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(ReservoirBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render fluid
			FluidStack fluidStack = blockEntity.getFluidStack();
			int capacity = blockEntity.getCapacity();
			if (!fluidStack.isEmpty() && capacity > 0) {
				poseStack.pushPose();
				poseStack.translate(-0.5, 0, -0.5);
				float offset = blockEntity.renderOffset;
				if (offset > 1.2f || offset < -1.2f) {
					offset = offset - ((offset / 12f + 0.1f) * partialTick);
					blockEntity.renderOffset = offset;
				} else {
					blockEntity.renderOffset = 0;
				}
				renderLargeFluidCuboid(poseStack, bufferSource.getBuffer(EmbersRenderTypes.FLUID), fluidStack, packedLight, 1, bounds, 1, bounds, 1, 1 + blockEntity.height * (fluidStack.getAmount() - offset) / capacity - 0.0625f);
				poseStack.popPose();
			} else {
				blockEntity.renderOffset = 0;
			}
		}
	}

	/**
	 * Renders a large fluid cuboid
	 * @param matrices   Matrix stack intance
	 * @param builder    Builder instance
	 * @param fluid      Fluid to render
	 * @param brightness Packed lighting values
	 * @param xd         X size for renderer
	 * @param xBounds    X positions to render
	 * @param zd         Z size for renderer
	 * @param zBounds    Z positions to render
	 * @param yMin       Min y position
	 * @param yMax       Max y position
	 */
	private static void renderLargeFluidCuboid(PoseStack matrices, VertexConsumer builder, FluidStack fluid, int brightness,
			int xd, float[] xBounds, int zd, float[] zBounds, float yMin, float yMax) {
		if (yMin >= yMax || fluid.isEmpty()) {
			return;
		}
		// fluid attributes
		FluidType type = fluid.getFluid().getFluidType();
		IClientFluidTypeExtensions clientType = IClientFluidTypeExtensions.of(type);
		TextureAtlasSprite still = FluidRenderer.getBlockSprite(clientType.getStillTexture(fluid));
		int color = clientType.getTintColor(fluid);
		brightness = FluidRenderer.withBlockLight(brightness, type.getLightLevel(fluid));

		// the liquid can stretch over more blocks than the subtracted height is if yMin's decimal is bigger than yMax's decimal (causing UV over 1)
		// ignoring the decimals prevents this, as yd then equals exactly how many ints are between the two
		// for example, if yMax = 5.1 and yMin = 2.3, 2.8 (which rounds to 2), with the face array becoming 2.3, 3, 4, 5.1
		int yd = (int) (yMax - (int) yMin);
		// except in the rare case of yMax perfectly aligned with the block, causing the top face to render multiple times
		// for example, if yMax = 3 and yMin = 1, the values of the face array become 1, 2, 3, 3 as we then have middle ints
		if (yMax % 1d == 0) yd--;
		float[] yBounds = getBlockBounds(yd, yMin, yMax);

		// render each side
		Matrix4f matrix = matrices.last().pose();
		Vector3f from = new Vector3f();
		Vector3f to = new Vector3f();
		for (int y = 0; y <= yd; y++) {
			for (int z = 0; z <= zd; z++) {
				for (int x = 0; x <= xd; x++) {
					from.set(xBounds[x], yBounds[y], zBounds[z]);
					to.set(xBounds[x + 1], yBounds[y + 1], zBounds[z + 1]);
					if (x == 0)  FluidRenderer.putTexturedQuad(builder, matrix, still, from, to, Direction.WEST,  color, brightness, 0, false);
					if (x == xd) FluidRenderer.putTexturedQuad(builder, matrix, still, from, to, Direction.EAST,  color, brightness, 0, false);
					if (z == 0)  FluidRenderer.putTexturedQuad(builder, matrix, still, from, to, Direction.NORTH, color, brightness, 0, false);
					if (z == zd) FluidRenderer.putTexturedQuad(builder, matrix, still, from, to, Direction.SOUTH, color, brightness, 0, false);
					if (y == yd) FluidRenderer.putTexturedQuad(builder, matrix, still, from, to, Direction.UP,    color, brightness, 0, false);
					if (y == 0) {
						// increase Y position slightly to prevent z fighting on neighboring fluids
						from.set(from.x(), from.y() + 0.001f, from.z());
						FluidRenderer.putTexturedQuad(builder, matrix, still,   from, to, Direction.DOWN,  color, brightness, 0, false);
					}
				}
			}
		}
	}

	/**
	 * Gets the integer bounds for rendering a fluid with the given delta
	 * @param delta  Delta
	 * @param start  Start position
	 * @param end    End position
	 * @return  Position array
	 */
	private static float[] getBlockBounds(int delta, float start, float end) {
		float[] bounds = new float[2 + delta];
		bounds[0] = start;
		int offset = (int) start;
		for(int i = 1; i <= delta; i++) bounds[i] = i + offset;
		bounds[delta+1] = end;
		return bounds;
	}
}
