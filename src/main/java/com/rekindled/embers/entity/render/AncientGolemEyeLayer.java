package com.rekindled.embers.entity.render;

import com.rekindled.embers.Embers;
import com.rekindled.embers.model.AncientGolemModel;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AncientGolemEyeLayer<T extends Entity, M extends AncientGolemModel<T>> extends EyesLayer<T, M> {
	private static final RenderType GOLEM_EYE = RenderType.eyes(new ResourceLocation(Embers.MODID, "textures/entity/golem_overlay.png"));

	public AncientGolemEyeLayer(RenderLayerParent<T, M> p_117507_) {
		super(p_117507_);
	}

	public RenderType renderType() {
		return GOLEM_EYE;
	}
}
