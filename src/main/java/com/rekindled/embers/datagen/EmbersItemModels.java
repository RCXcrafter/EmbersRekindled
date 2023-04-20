package com.rekindled.embers.datagen;

import com.google.gson.JsonObject;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class EmbersItemModels extends ItemModelProvider {

	public EmbersItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
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
		basicItem(RegistryManager.EMBER_CRYSTAL.get());
		basicItem(RegistryManager.EMBER_SHARD.get());
		basicItem(RegistryManager.EMBER_GRIT.get());
		basicItem(RegistryManager.CAMINITE_BLEND.get());
		basicItem(RegistryManager.CAMINITE_BRICK.get());
		basicItem(RegistryManager.ARCHAIC_BRICK.get());
		basicItem(RegistryManager.ANCIENT_MOTIVE_CORE.get());

		itemWithTexture(RegistryManager.RAW_CAMINITE_PLATE, "plate_caminite_raw");
		itemWithTexture(RegistryManager.RAW_INGOT_STAMP, "ingot_stamp_raw");
		itemWithTexture(RegistryManager.RAW_NUGGET_STAMP, "nugget_stamp_raw");
		itemWithTexture(RegistryManager.RAW_PLATE_STAMP, "plate_stamp_raw");

		itemWithTexture(RegistryManager.CAMINITE_PLATE, "plate_caminite");
		basicItem(RegistryManager.INGOT_STAMP.get());
		basicItem(RegistryManager.NUGGET_STAMP.get());
		basicItem(RegistryManager.PLATE_STAMP.get());

		itemWithTexture(RegistryManager.IRON_PLATE, "plate_iron");
		//itemWithTexture(RegistryManager.GOLD_PLATE, "plate_gold");
		itemWithTexture(RegistryManager.COPPER_PLATE, "plate_copper");
		itemWithTexture(RegistryManager.COPPER_NUGGET, "nugget_copper");

		basicItem(RegistryManager.RAW_LEAD.get());
		itemWithTexture(RegistryManager.LEAD_INGOT, "ingot_lead");
		itemWithTexture(RegistryManager.LEAD_NUGGET, "nugget_lead");
		itemWithTexture(RegistryManager.LEAD_PLATE, "plate_lead");

		itemWithTexture(RegistryManager.DAWNSTONE_INGOT, "ingot_dawnstone");
		itemWithTexture(RegistryManager.DAWNSTONE_NUGGET, "nugget_dawnstone");
		itemWithTexture(RegistryManager.DAWNSTONE_PLATE, "plate_dawnstone");

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
}
