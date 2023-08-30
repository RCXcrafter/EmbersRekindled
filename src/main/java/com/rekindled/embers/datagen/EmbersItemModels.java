package com.rekindled.embers.datagen;

import com.google.gson.JsonObject;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;
import com.rekindled.embers.RegistryManager.ToolSet;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class EmbersItemModels extends ItemModelProvider {

	public EmbersItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, Embers.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (FluidStuff fluid : RegistryManager.fluidList) {
			bucketModel(fluid.FLUID_BUCKET, fluid.name);
			//itemWithModel(fluid.FLUID_BUCKET, "item/generated");
		}

		itemWithModel(RegistryManager.TINKER_HAMMER, "item/handheld");
		basicItem(RegistryManager.TINKER_LENS.get());
		basicItem(RegistryManager.ANCIENT_CODEX.get());
		basicItem(RegistryManager.ATMOSPHERIC_GAUGE.get());
		layeredItem(RegistryManager.EMBER_JAR, "ember_jar_glass", "ember_jar_glass_shine", "ember_jar");
		layeredItem(RegistryManager.EMBER_CARTRIDGE, "ember_cartridge_glass", "ember_cartridge_glass_shine", "ember_cartridge");
		basicItem(RegistryManager.ALCHEMICAL_WASTE.get());
		basicItem(RegistryManager.CODEBREAKING_SLATE.get());

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
		ResourceLocation id = registryObject.getId();
		ResourceLocation textureLocation = new ResourceLocation(id.getNamespace(), "item/" + texture);
		singleTexture(id.getPath(), new ResourceLocation("item/generated"), "layer0", textureLocation);
	}

	public void layeredItem(RegistryObject<? extends Item> registryObject, String... textures) {
		ResourceLocation id = registryObject.getId();

		ModelBuilder<?> builder = withExistingParent(id.getPath(), new ResourceLocation("item/generated"));
		for (int i = 0; i < textures.length; i ++) {
			builder.texture("layer" + i, new ResourceLocation(id.getNamespace(), "item/" + textures[i]));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void bucketModel(RegistryObject<? extends BucketItem> registryObject, String name) {
		ModelBuilder builder = getBuilder(registryObject.getId().getPath()).parent(getExistingFile(new ResourceLocation(Embers.MODID, "item/bucket_fluid")));

		//I'm not sure how this works but it works
		builder.customLoader((t, u) ->  new CustomLoaderBuilder(((ModelBuilder) t).getLocation(), (ModelBuilder) t, (ExistingFileHelper) u) {
			public JsonObject toJson(JsonObject json) {
				json.addProperty("loader", "forge:fluid_container");
				json.addProperty("cover_is_mask", false);
				json.addProperty("fluid", Embers.MODID + ":" + name);
				return json;
			}
		});
	}

	public void toolModels(ToolSet set) {
		itemWithTexture(set.SWORD, "sword_" + set.name);
		itemWithTexture(set.SHOVEL, "shovel_" + set.name);
		itemWithTexture(set.PICKAXE, "pickaxe_" + set.name);
		itemWithTexture(set.AXE, "axe_" + set.name);
		itemWithTexture(set.HOE, "hoe_" + set.name);
	}
}
