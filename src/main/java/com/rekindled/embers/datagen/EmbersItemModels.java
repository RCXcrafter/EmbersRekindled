package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;
import com.rekindled.embers.RegistryManager.ToolSet;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class EmbersItemModels extends ItemModelProvider {

	public EmbersItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, Embers.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (FluidStuff fluid : RegistryManager.fluidList) {
			bucketModel(fluid.FLUID_BUCKET, fluid.FLUID.get());
			//itemWithModel(fluid.FLUID_BUCKET, "item/generated");
		}

		itemWithModel(RegistryManager.TINKER_HAMMER, "item/handheld");
		basicItem(RegistryManager.TINKER_LENS.get());
		basicItem(RegistryManager.SMOKY_TINKER_LENS.get());
		basicItem(RegistryManager.ANCIENT_CODEX.get());
		layeredItem(RegistryManager.EMBER_JAR, "item/generated", "ember_jar_glass", "ember_jar_glass_shine", "ember_jar");
		layeredItem(RegistryManager.EMBER_CARTRIDGE, "item/generated", "ember_cartridge_glass", "ember_cartridge_glass_shine", "ember_cartridge");
		itemWithTexture(RegistryManager.MUSIC_DISC_7F_PATTERNS, "music_disc_ember");
		basicItem(RegistryManager.ALCHEMICAL_WASTE.get());
		withExistingParent(RegistryManager.ALCHEMICAL_NOTE.getId().getPath(), new ResourceLocation("forge", "item/default")).customLoader(SeparateTransformsModelBuilder::begin)
		.base(basicItem(new ResourceLocation(Embers.MODID, "alchemical_note_item")))
		.perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, nested().parent(new UncheckedModelFile(new ResourceLocation("builtin/entity"))))
		.perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, nested().parent(new UncheckedModelFile(new ResourceLocation("builtin/entity"))))
		.perspective(ItemDisplayContext.FIXED, nested().parent(new UncheckedModelFile(new ResourceLocation("builtin/entity"))));
		basicItem(RegistryManager.CODEBREAKING_SLATE.get());
		layeredItem(RegistryManager.TYRFING, "item/handheld", "tyrfing", "tyrfing_gem");
		withExistingParent(RegistryManager.INFLICTOR_GEM.getId().getPath(), new ResourceLocation("item/generated"))
		.texture("layer0", new ResourceLocation(Embers.MODID, "item/inflictor_gem"))
		.override().predicate(new ResourceLocation(Embers.MODID, "charged"), 1)
		.model(singleTexture("inflictor_gem_charged", new ResourceLocation("item/generated"), "layer0", new ResourceLocation(Embers.MODID, "item/inflictor_gem_charged")));
		basicItem(RegistryManager.ASHEN_GOGGLES.get());
		basicItem(RegistryManager.ASHEN_CLOAK.get());
		basicItem(RegistryManager.ASHEN_LEGGINGS.get());
		basicItem(RegistryManager.ASHEN_BOOTS.get());
		basicItem(RegistryManager.GLIMMER_CRYSTAL.get());
		basicItem(RegistryManager.GLIMMER_LAMP.get());

		basicItem(RegistryManager.EMBER_CRYSTAL.get());
		basicItem(RegistryManager.EMBER_SHARD.get());
		basicItem(RegistryManager.EMBER_GRIT.get());
		basicItem(RegistryManager.CAMINITE_BLEND.get());
		basicItem(RegistryManager.CAMINITE_BRICK.get());
		basicItem(RegistryManager.ARCHAIC_BRICK.get());
		basicItem(RegistryManager.ANCIENT_MOTIVE_CORE.get());
		basicItem(RegistryManager.ASH.get());
		basicItem(RegistryManager.ASHEN_FABRIC.get());
		basicItem(RegistryManager.EMBER_CRYSTAL_CLUSTER.get());
		basicItem(RegistryManager.WILDFIRE_CORE.get());
		basicItem(RegistryManager.ISOLATED_MATERIA.get());
		basicItem(RegistryManager.ADHESIVE.get());
		basicItem(RegistryManager.ARCHAIC_CIRCUIT.get());

		basicItem(RegistryManager.SUPERHEATER.get());
		basicItem(RegistryManager.CINDER_JET.get());
		basicItem(RegistryManager.BLASTING_CORE.get());
		basicItem(RegistryManager.CASTER_ORB.get());
		basicItem(RegistryManager.RESONATING_BELL.get());
		basicItem(RegistryManager.FLAME_BARRIER.get());
		basicItem(RegistryManager.ELDRITCH_INSIGNIA.get());
		basicItem(RegistryManager.INTELLIGENT_APPARATUS.get());
		basicItem(RegistryManager.DIFFRACTION_BARREL.get());
		basicItem(RegistryManager.FOCAL_LENS.get());
		basicItem(RegistryManager.SHIFTING_SCALES.get());
		basicItem(RegistryManager.WINDING_GEARS.get());

		itemWithTexture(RegistryManager.RAW_CAMINITE_PLATE, "plate_caminite_raw");
		itemWithTexture(RegistryManager.RAW_FLAT_STAMP, "flat_stamp_raw");
		itemWithTexture(RegistryManager.RAW_INGOT_STAMP, "ingot_stamp_raw");
		itemWithTexture(RegistryManager.RAW_NUGGET_STAMP, "nugget_stamp_raw");
		itemWithTexture(RegistryManager.RAW_PLATE_STAMP, "plate_stamp_raw");

		itemWithTexture(RegistryManager.CAMINITE_PLATE, "plate_caminite");
		basicItem(RegistryManager.FLAT_STAMP.get());
		basicItem(RegistryManager.INGOT_STAMP.get());
		basicItem(RegistryManager.NUGGET_STAMP.get());
		basicItem(RegistryManager.PLATE_STAMP.get());

		itemWithTexture(RegistryManager.IRON_ASPECTUS, "aspectus_iron");
		itemWithTexture(RegistryManager.COPPER_ASPECTUS, "aspectus_copper");
		itemWithTexture(RegistryManager.LEAD_ASPECTUS, "aspectus_lead");
		itemWithTexture(RegistryManager.SILVER_ASPECTUS, "aspectus_silver");
		itemWithTexture(RegistryManager.DAWNSTONE_ASPECTUS, "aspectus_dawnstone");

		itemWithTexture(RegistryManager.IRON_PLATE, "plate_iron");
		//itemWithTexture(RegistryManager.GOLD_PLATE, "plate_gold");
		itemWithTexture(RegistryManager.COPPER_PLATE, "plate_copper");
		itemWithTexture(RegistryManager.COPPER_NUGGET, "nugget_copper");

		basicItem(RegistryManager.RAW_LEAD.get());
		itemWithTexture(RegistryManager.LEAD_INGOT, "ingot_lead");
		itemWithTexture(RegistryManager.LEAD_NUGGET, "nugget_lead");
		itemWithTexture(RegistryManager.LEAD_PLATE, "plate_lead");

		basicItem(RegistryManager.RAW_SILVER.get());
		itemWithTexture(RegistryManager.SILVER_INGOT, "ingot_silver");
		itemWithTexture(RegistryManager.SILVER_NUGGET, "nugget_silver");
		itemWithTexture(RegistryManager.SILVER_PLATE, "plate_silver");

		itemWithTexture(RegistryManager.DAWNSTONE_INGOT, "ingot_dawnstone");
		itemWithTexture(RegistryManager.DAWNSTONE_NUGGET, "nugget_dawnstone");
		itemWithTexture(RegistryManager.DAWNSTONE_PLATE, "plate_dawnstone");

		toolModels(RegistryManager.LEAD_TOOLS);
		toolModels(RegistryManager.SILVER_TOOLS);
		toolModels(RegistryManager.DAWNSTONE_TOOLS);

		spawnEgg(RegistryManager.ANCIENT_GOLEM_SPAWN_EGG);
		buttonInventory(RegistryManager.CAMINITE_BUTTON_ITEM.getId().getPath(), new ResourceLocation(Embers.MODID, "block/caminite_button"));
	}

	public void itemWithModel(RegistryObject<? extends Item> registryObject, String model) {
		ResourceLocation id = registryObject.getId();
		ResourceLocation textureLocation = new ResourceLocation(id.getNamespace(), "item/" + id.getPath());
		singleTexture(id.getPath(), new ResourceLocation(model), "layer0", textureLocation);
	}

	public void spawnEgg(RegistryObject<? extends Item> registryObject) {
		withExistingParent(registryObject.getId().getPath(), "item/template_spawn_egg");
	}

	public void itemWithTexture(RegistryObject<? extends Item> registryObject, String texture) {
		itemWithTexture(registryObject, "item/generated", texture);
	}

	public void itemWithTexture(RegistryObject<? extends Item> registryObject, String model, String texture) {
		ResourceLocation id = registryObject.getId();
		ResourceLocation textureLocation = new ResourceLocation(id.getNamespace(), "item/" + texture);
		singleTexture(id.getPath(), new ResourceLocation(model), "layer0", textureLocation);
	}

	public void layeredItem(RegistryObject<? extends Item> registryObject, String model, String... textures) {
		ResourceLocation id = registryObject.getId();

		ModelBuilder<?> builder = withExistingParent(id.getPath(), new ResourceLocation(model));
		for (int i = 0; i < textures.length; i ++) {
			builder.texture("layer" + i, new ResourceLocation(id.getNamespace(), "item/" + textures[i]));
		}
	}

	public void bucketModel(RegistryObject<? extends BucketItem> registryObject, Fluid fluid) {
		ModelBuilder<ItemModelBuilder> builder = withExistingParent(registryObject.getId().getPath(), new ResourceLocation(Embers.MODID, "item/bucket_fluid"));
		builder.customLoader(DynamicFluidContainerModelBuilder::begin).fluid(fluid).coverIsMask(false).flipGas(true).end();
	}

	public void toolModels(ToolSet set) {
		itemWithTexture(set.SWORD, "item/handheld", "sword_" + set.name);
		itemWithTexture(set.SHOVEL, "item/handheld", "shovel_" + set.name);
		itemWithTexture(set.PICKAXE, "item/handheld", "pickaxe_" + set.name);
		itemWithTexture(set.AXE, "item/handheld", "axe_" + set.name);
		itemWithTexture(set.HOE, "item/handheld", "hoe_" + set.name);
	}
}
