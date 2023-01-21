package com.rekindled.embers.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class AncientGolemModel<T extends Entity> extends HierarchicalModel<T> {

	ModelPart root;
	ModelPart legL;
	ModelPart legR;
	ModelPart body1;
	ModelPart body2;
	ModelPart armR;
	ModelPart fistR;
	ModelPart armL;
	ModelPart fistL;
	ModelPart head;

	public AncientGolemModel(ModelPart root) {
		this.root = root;
		this.legL = root.getChild("legL");
		this.legR = root.getChild("legR");
		this.body1 = root.getChild("body1");
		this.body2 = root.getChild("body2");
		this.armR = root.getChild("armR");
		this.fistR = root.getChild("fistR");
		this.armL = root.getChild("armL");
		this.fistL = root.getChild("fistL");
		this.head = root.getChild("head");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("legL", CubeListBuilder.create().texOffs(0, 0).addBox(-2F, 0F, -2F, 4, 10, 4), PartPose.offset(2F, 14F, 0F));
		partdefinition.addOrReplaceChild("legR", CubeListBuilder.create().texOffs(0, 0).addBox(-2F, 0F, -2F, 4, 10, 4), PartPose.offset(-2F, 14F, 0F));
		partdefinition.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8, 4, 4), PartPose.offset(0F, 10F, 0F));
		partdefinition.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(16, 0).addBox(-4.5F, 0F, -3F, 9, 8, 6), PartPose.offset(0F, 2F, 0F));
		partdefinition.addOrReplaceChild("armR", CubeListBuilder.create().texOffs(48, 0).addBox(-2F, 0F, -2F, 4, 12, 4), PartPose.offsetAndRotation(-4.5F, 2.013333F, 0F, 0F, 0F, 0.3926991F));
		partdefinition.addOrReplaceChild("fistR", CubeListBuilder.create().texOffs(0, 32).addBox(-2F, 12F, -2.5F, 5, 5, 5), PartPose.offsetAndRotation(-4.5F, 2.013333F, 0F, 0F, 0F, 0.3926991F));
		partdefinition.addOrReplaceChild("armL", CubeListBuilder.create().texOffs(48, 0).addBox(-2F, 0F, -2F, 4, 12, 4), PartPose.offsetAndRotation(4.5F, 2.013333F, 0F, 0F, 0F, -0.3926991F));
		partdefinition.addOrReplaceChild("fistL", CubeListBuilder.create().texOffs(0, 32).addBox(-3F, 12F, -2.5F, 5, 5, 5), PartPose.offsetAndRotation(4.5F, 2.013333F, 0F, 0F, 0F, -0.3926991F));
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 32).addBox(-4F, -8F, -4F, 8, 8, 8), PartPose.offset(0F, 2F, 0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.xRot = headPitch * ((float)Math.PI / 180F);
		head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		legR.xRot = (float)Math.toRadians(180f*(float) Math.sin(limbSwing*0.5)*limbSwingAmount*0.5);
		legL.xRot = -(float)Math.toRadians(180f*(float) Math.sin(limbSwing*0.5)*limbSwingAmount*0.5);
		armL.xRot = (float)Math.toRadians(180f*(float) Math.sin(limbSwing*0.5)*limbSwingAmount*0.5);
		armR.xRot = -(float)Math.toRadians(180f*(float) Math.sin(limbSwing*0.5)*limbSwingAmount*0.5);
		fistL.xRot = (float)Math.toRadians(180f*(float) Math.sin(limbSwing*0.5)*limbSwingAmount*0.5);
		fistR.xRot = -(float)Math.toRadians(180f*(float) Math.sin(limbSwing*0.5)*limbSwingAmount*0.5);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}
