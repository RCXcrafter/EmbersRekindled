package com.rekindled.embers.entity.render;

import com.rekindled.embers.Embers;
import com.rekindled.embers.entity.AncientGolemEntity;
import com.rekindled.embers.model.AncientGolemModel;
import com.rekindled.embers.model.AshenArmorModel;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AncientGolemRenderer extends MobRenderer<AncientGolemEntity, AncientGolemModel<AncientGolemEntity>> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Embers.MODID, "ancient_golem"), "main");
	public static final ResourceLocation TEXTURE = new ResourceLocation(Embers.MODID, "textures/entity/golem.png");

	public AncientGolemRenderer(EntityRendererProvider.Context context) {
		super(context, new AncientGolemModel<AncientGolemEntity>(context.bakeLayer(LAYER_LOCATION)), 0.5f);
		this.addLayer(new AncientGolemEyeLayer<>(this));
		//I just need to get this context from somewhere
		AshenArmorModel.init(context);
	}

	@Override
	public ResourceLocation getTextureLocation(AncientGolemEntity pEntity) {
		return TEXTURE;
	}
}
