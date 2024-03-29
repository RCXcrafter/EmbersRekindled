package com.rekindled.embers.model;

import java.util.EnumMap;
import java.util.Map;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class AshenArmorModel extends HumanoidModel<LivingEntity> {

	public static final ModelLayerLocation ASHEN_ARMOR_HEAD = new ModelLayerLocation(new ResourceLocation(Embers.MODID, "ashen_armor"), "head");
	public static final ModelLayerLocation ASHEN_ARMOR_CHEST = new ModelLayerLocation(new ResourceLocation(Embers.MODID, "ashen_armor"), "chest");
	public static final ModelLayerLocation ASHEN_ARMOR_LEGS = new ModelLayerLocation(new ResourceLocation(Embers.MODID, "ashen_armor"), "legs");
	public static final ModelLayerLocation ASHEN_ARMOR_FEET = new ModelLayerLocation(new ResourceLocation(Embers.MODID, "ashen_armor"), "feet");

	public static final Map<EquipmentSlot, AshenArmorModel> ARMOR_MODELS = new EnumMap<>(EquipmentSlot.class);
	public static final IClientItemExtensions ARMOR_MODEL_GETTER = new IClientItemExtensions() {
		@Override
		public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
			AshenArmorModel model = AshenArmorModel.ARMOR_MODELS.get(equipmentSlot);
			model.setup(livingEntity, itemStack);
			return model;
		}
	};
	public static CubeDeformation scale = new CubeDeformation(0.1F);

	public double velocity = 0;
	public ModelPart cape = null;
	public ModelPart[] gems = new ModelPart[7];

	public AshenArmorModel(ModelPart root, EquipmentSlot slot) {
		super(root);
		if (this.body.hasChild("cape")) {
			cape = this.body.getChild("cape");
			for (int i = 0; i < gems.length; i++) {
				gems[i] = cape.getChild("cape_gem_" + i);
			}
		}
	}

	public void setup(LivingEntity entity, ItemStack itemStack) {
		if (cape != null) {
			if (entity instanceof AbstractClientPlayer player) {
				float partialTicks = Minecraft.getInstance().getPartialTick();
				double d0 = Mth.lerp((double)partialTicks, player.xCloakO, player.xCloak) - Mth.lerp((double)partialTicks, entity.xo, entity.getX());
				double d1 = Mth.lerp((double)partialTicks, player.yCloakO, player.yCloak) - Mth.lerp((double)partialTicks, entity.yo, entity.getY());
				double d2 = Mth.lerp((double)partialTicks, player.zCloakO, player.zCloak) - Mth.lerp((double)partialTicks, entity.zo, entity.getZ());
				float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
				double d3 = (double)Mth.sin(f * ((float)Math.PI / 180F));
				double d4 = (double)(-Mth.cos(f * ((float)Math.PI / 180F)));
				float f1 = (float)d1 * 10.0F;
				f1 = Mth.clamp(f1, -6.0F, 32.0F);
				float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
				f2 = Mth.clamp(f2, 0.0F, 150.0F);
				if (f2 < 0.0F) {
					f2 = 0.0F;
				}
				float f4 = Mth.lerp(partialTicks, player.oBob, player.bob);
				f1 += Mth.sin(Mth.lerp(partialTicks, entity.walkDistO, entity.walkDist) * 6.0F) * 32.0F * f4;

				this.cape.xRot = (6.0F + f2 / 2.0F + f1) * ((float)Math.PI / 180F);
			} else {
				this.cape.xRot = 0;
			}
			if (itemStack.getItem() == RegistryManager.ASHEN_CLOAK.get()) {
				for (int i = 0; i < gems.length; i++) {
					gems[i].visible = itemStack.getOrCreateTag().contains("gem" + i);;
				}
			} else {
				for (int i = 0; i < gems.length; i++) {
					gems[i].visible = false;
				}
			}
		}
	}

	public static void init(EntityRendererProvider.Context context) {
		for (var slot : EquipmentSlot.values()) {
			ModelPart mesh;
			switch (slot) {
			case HEAD:
				mesh = context.bakeLayer(ASHEN_ARMOR_HEAD);
				break;
			case CHEST:
				mesh = context.bakeLayer(ASHEN_ARMOR_CHEST);
				break;
			case LEGS:
				mesh = context.bakeLayer(ASHEN_ARMOR_LEGS);
				break;
			default:
			case FEET:
				mesh = context.bakeLayer(ASHEN_ARMOR_FEET);
				break;
			}
			ARMOR_MODELS.put(slot, new AshenArmorModel(mesh, slot));
		}
	}

	public static MeshDefinition createHeadMesh() {
		CubeDeformation scale = new CubeDeformation(0.01F);
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

		head.addOrReplaceChild("head_1", CubeListBuilder.create().texOffs(20, 35)
				.addBox(-2.0F, -6.0F, 3.0F, 4, 4, 2), PartPose.ZERO);

		head.addOrReplaceChild("strap_back", CubeListBuilder.create().texOffs(36, 32)
				.addBox(-4.5F, -1.0F, -0.5F, 9, 2, 1, scale), PartPose.offset(0.0F, -4.0F, 4.0F));

		head.addOrReplaceChild("strap_side_1", CubeListBuilder.create().texOffs(36, 22)
				.addBox(-1.0F, -1.0F, -4.5F, 1, 2, 8, scale), PartPose.offset(-3.52F, -4.0F, 0.0F));

		head.addOrReplaceChild("strap_side_2", CubeListBuilder.create().texOffs(36, 22)
				.addBox(-1.0F, -1.0F, -4.5F, 1, 2, 8, scale), PartPose.offset(4.52F, -4.0F, 0.0F));

		head.addOrReplaceChild("strap_back_top", CubeListBuilder.create().texOffs(32, 36)
				.addBox(-1.0F, -4.5F, -0.5F, 2, 5, 1, scale), PartPose.offset(0.0F, -4.0F, 4.0F));

		head.addOrReplaceChild("strap_top", CubeListBuilder.create().texOffs(32, 48)
				.addBox(-1.0F, -4.5F, -3.5F, 2, 1, 7, scale), PartPose.offset(0.0F, -4.0F, 0.0F));

		head.addOrReplaceChild("strap_front", CubeListBuilder.create().texOffs(32, 36)
				.addBox(-1.0F, -5.5F, -1.5F, 2, 5, 1, scale), PartPose.offset(0.0F, -3.0F, -3.0F));

		head.addOrReplaceChild("head_7", CubeListBuilder.create().texOffs(22, 50)
				.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1), PartPose.offset(1.0F, -5.0F, -4.0F));

		head.addOrReplaceChild("head_8", CubeListBuilder.create().texOffs(22, 50)
				.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1), PartPose.offset(4.0F, -5.0F, -4.0F));

		head.addOrReplaceChild("head_9", CubeListBuilder.create().texOffs(22, 48)
				.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 1), PartPose.offset(2.5F, -6.0F, -4.0F));

		head.addOrReplaceChild("head_10", CubeListBuilder.create().texOffs(22, 48)
				.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 1), PartPose.offset(2.5F, -3.0F, -4.0F));

		head.addOrReplaceChild("head_11", CubeListBuilder.create().texOffs(22, 50)
				.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1), PartPose.offset(-4.0F, -5.0F, -4.0F));

		head.addOrReplaceChild("head_12", CubeListBuilder.create().texOffs(22, 48)
				.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 1), PartPose.offset(-2.5F, -6.0F, -4.0F));

		head.addOrReplaceChild("head_13", CubeListBuilder.create().texOffs(22, 50)
				.addBox(-0.5F, 0.0F, -1.0F, 1, 2, 1), PartPose.offset(-1.0F, -5.0F, -4.0F));

		head.addOrReplaceChild("head_14", CubeListBuilder.create().texOffs(22, 48)
				.addBox(-2.0F, 0.0F, -1.0F, 4, 1, 1), PartPose.offset(-2.5F, -3.0F, -4.0F));

		return mesh;
	}

	public static MeshDefinition createChestMesh() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition armL = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition armR = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

		body.addOrReplaceChild("front_1", CubeListBuilder.create().texOffs(16, 0)
				.addBox(-2.0F, 0.0F, -1.0F, 4, 12, 2), PartPose.offsetAndRotation(-3.0F, 0.0F, -1.5F, 0.0873F, -2.8798F, 0.0F));

		body.addOrReplaceChild("front_2", CubeListBuilder.create().texOffs(16, 0)
				.addBox(-2.0F, 0.0F, -1.0F, 4, 12, 2), PartPose.offsetAndRotation(3.0F, 0.0F, -1.5F, 0.0873F, 2.8798F, 0.0F));

		body.addOrReplaceChild("front_3", CubeListBuilder.create().texOffs(28, 0)
				.addBox(-2.5F, 0.0F, -1.0F, 5, 10, 2), PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, 0.0873F, 3.1416F, 0.0F));

		body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 25)
				.addBox(-4.5F, 0.0F, -4.0F, 9, 2, 8), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.3F, 0.0F, 0.0F));

		armR.addOrReplaceChild("shoulder_right", CubeListBuilder.create().texOffs(0, 35)
				.addBox(-3.5F, -4.0F, -2.5F, 5, 5, 5), PartPose.ZERO);

		armR.addOrReplaceChild("glove_right", CubeListBuilder.create().texOffs(36, 12)
				.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.offset(-1.0F, 8.0F, 0.0F));

		armL.addOrReplaceChild("shoulder_left", CubeListBuilder.create().texOffs(16, 54)
				.addBox(-1.5F, -4.0F, -2.5F, 5, 5, 5), PartPose.ZERO);

		armL.addOrReplaceChild("glove_left", CubeListBuilder.create().texOffs(36, 12)
				.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.offset(1.0F, 8.0F, 0.0F));

		cape.addOrReplaceChild("cape_1", CubeListBuilder.create().texOffs(0, 0)
				.addBox(-3.0F, 0.0F, -1.0F, 6, 22, 2), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

		cape.addOrReplaceChild("cape_2", CubeListBuilder.create().texOffs(48, 40)
				.addBox(-3.0F, 0.0F, -1.0F, 6, 22, 2), PartPose.offsetAndRotation(3.0F, 0.0F, 1.5F, 0.2618F, 0.5236F, 0.0F));

		cape.addOrReplaceChild("cape_3", CubeListBuilder.create().texOffs(48, 40)
				.addBox(-3.0F, 0.0F, -1.0F, 6, 22, 2), PartPose.offsetAndRotation(-3.0F, 0.0F, 1.5F, 0.2618F, -0.5236F, 0.0F));

		cape.addOrReplaceChild("cape_gem_0", CubeListBuilder.create().texOffs(0, 60)
				.addBox(-1.0F, 16.0F, 0.0F, 2, 2, 2), PartPose.offsetAndRotation(3.0F, 0.0F, 1.5F, 0.2618F, 0.5236F, 0.0F));

		cape.addOrReplaceChild("cape_gem_1", CubeListBuilder.create().texOffs(0, 60)
				.addBox(-1.0F, 10.0F, 0.0F, 2, 2, 2), PartPose.offsetAndRotation(3.0F, 0.0F, 1.5F, 0.2618F, 0.5236F, 0.0F));

		cape.addOrReplaceChild("cape_gem_2", CubeListBuilder.create().texOffs(0, 60)
				.addBox(-1.0F, 4.0F, 0.0F, 2, 2, 2), PartPose.offsetAndRotation(3.0F, 0.0F, 1.5F, 0.2618F, 0.5236F, 0.0F));

		cape.addOrReplaceChild("cape_gem_3", CubeListBuilder.create().texOffs(0, 60)
				.addBox(-1.0F, 2.0F, 0.0F, 2, 2, 2), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

		cape.addOrReplaceChild("cape_gem_4", CubeListBuilder.create().texOffs(0, 60)
				.addBox(-1.0F, 4.0F, 0.0F, 2, 2, 2), PartPose.offsetAndRotation(-3.0F, 0.0F, 1.5F, 0.2618F, -0.5236F, 0.0F));

		cape.addOrReplaceChild("cape_gem_5", CubeListBuilder.create().texOffs(0, 60)
				.addBox(-1.0F, 10.0F, 0.0F, 2, 2, 2), PartPose.offsetAndRotation(-3.0F, 0.0F, 1.5F, 0.2618F, -0.5236F, 0.0F));

		cape.addOrReplaceChild("cape_gem_6", CubeListBuilder.create().texOffs(0, 60)
				.addBox(-1.0F, 16.0F, 0.0F, 2, 2, 2), PartPose.offsetAndRotation(-3.0F, 0.0F, 1.5F, 0.2618F, -0.5236F, 0.0F));

		return mesh;
	}

	public static MeshDefinition createLegsMesh() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition legL = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition legR = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

		legL.addOrReplaceChild("left_knee", CubeListBuilder.create().texOffs(0, 45)
				.addBox(-2.5F, 1.0F, -2.5F, 5, 5, 5, scale), PartPose.offsetAndRotation(0.6F, 0.0F, -0.5F, 0.0873F, -0.0873F, 0.0F));

		legR.addOrReplaceChild("right_knee", CubeListBuilder.create().texOffs(0, 45)
				.addBox(-2.5F, 1.0F, -2.5F, 5, 5, 5, scale), PartPose.offsetAndRotation(-0.6F, 0.0F, -0.5F, 0.0873F, 0.0873F, 0.0F));

		body.addOrReplaceChild("left_leg_1", CubeListBuilder.create().texOffs(44, 0)
				.addBox(-2.5F, -5.1867F, -2.0F, 5, 4, 5), PartPose.offsetAndRotation(2.1F, 13.0F, -0.5F, 0.0F, -0.1745F, 0.0F));

		body.addOrReplaceChild("right_leg_1", CubeListBuilder.create().texOffs(44, 0)
				.addBox(-2.5F, -5.1867F, -2.0F, 5, 4, 5), PartPose.offsetAndRotation(-2.1F, 13.0F, -0.5F, 0.0F, 0.1745F, 0.0F));

		return mesh;
	}

	public static MeshDefinition createFeetMesh() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
		root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition legL = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition legR = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

		legR.addOrReplaceChild("boot_right", CubeListBuilder.create().texOffs(16, 14)
				.addBox(-2.5F, -6.5F, -2.5F, 5, 6, 5), PartPose.offset(-0.1F, 13.0F, 0.0F));

		legR.addOrReplaceChild("toe_right", CubeListBuilder.create().texOffs(51, 12)
				.addBox(-2.5F, -2.5F, -3.5F, 5, 2, 1), PartPose.offset(-0.1F, 13.0F, 0.0F));

		legL.addOrReplaceChild("boot_left", CubeListBuilder.create().texOffs(16, 14)
				.addBox(-2.5F, -6.5F, -2.5F, 5, 6, 5), PartPose.offset(0.1F, 13.0F, 0.0F));

		legL.addOrReplaceChild("toe_left", CubeListBuilder.create().texOffs(51, 12)
				.addBox(-2.5F, -2.5F, -3.5F, 5, 2, 1), PartPose.offset(0.1F, 13.0F, 0.0F));

		return mesh;
	}
}
