package com.rekindled.embers.render;

import java.util.OptionalDouble;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.rekindled.embers.Embers;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class EmbersRenderTypes extends RenderType {

	public EmbersRenderTypes(String pName, VertexFormat pFormat, Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
		super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
	}

	//render type used for ember particles
	public static ParticleRenderType PARTICLE_SHEET_ADDITIVE = new ParticleRenderType() {
		public void begin(BufferBuilder p_107455_, TextureManager p_107456_) {
			RenderSystem.depthMask(false);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			p_107455_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		public void end(Tesselator p_107458_) {
			p_107458_.end();
		}

		public String toString() {
			return "PARTICLE_SHEET_ADDITIVE";
		}
	};

	//render type used for smoke particles
	public static ParticleRenderType PARTICLE_SHEET_TRANSLUCENT_NODEPTH = new ParticleRenderType() {
		public void begin(BufferBuilder p_107455_, TextureManager p_107456_) {
			RenderSystem.depthMask(false);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			p_107455_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		public void end(Tesselator p_107458_) {
			p_107458_.end();
		}

		public String toString() {
			return "PARTICLE_SHEET_TRANSLUCENT_NODEPTH";
		}
	};

	//render type used for the fluid renderer
	public static final RenderType FLUID = create(
			Embers.MODID + ":block_render_type",
			DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder()
			.setLightmapState(LIGHTMAP)
			.setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
			.setTextureState(BLOCK_SHEET_MIPPED)
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setCullState(CULL)
			.setOutputState(TRANSLUCENT_TARGET)
			.createCompositeState(false));

	public static final RenderStateShard.ShaderStateShard PTLC_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexLightmapColorShader);

	//render type used for the crystal cell
	public static final RenderType CRYSTAL = create(
			Embers.MODID + ":crystal_render_type",
			DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder()
			.setShaderState(PTLC_SHADER)
			.setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(Embers.MODID + ":textures/block/crystal_material.png"), false, false))
			.setTransparencyState(LIGHTNING_TRANSPARENCY)
			.setCullState(NO_CULL)
			.setOutputState(TRANSLUCENT_TARGET)
			.createCompositeState(false));

	//render type used for the alchemy circle
	public static final RenderType BEAM = create(
			Embers.MODID + ":beam_render_type",
			DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
			RenderType.CompositeState.builder()
			.setShaderState(PTLC_SHADER)
			.setTextureState(new RenderStateShard.TextureStateShard(new ResourceLocation(Embers.MODID + ":textures/entity/alchemy_circle.png"), false, false))
			.setTransparencyState(LIGHTNING_TRANSPARENCY)
			.setCullState(NO_CULL)
			//.setDepthTestState(NO_DEPTH_TEST)
			.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.setWriteMaskState(COLOR_DEPTH_WRITE)
			
			.setOutputState(TRANSLUCENT_TARGET)
			.createCompositeState(false));

	//render type used for highlighting the emitter being aimed
	public static final RenderType GLOW_LINES = create(
			Embers.MODID + ":glow_lines",
			DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false,
			RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_LINES_SHADER)
			.setLightmapState(NO_LIGHTMAP)
			.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(6.0D)))
			.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setOutputState(ITEM_ENTITY_TARGET)
			.setWriteMaskState(COLOR_DEPTH_WRITE)
			.setCullState(NO_CULL)
			.createCompositeState(false));
}
