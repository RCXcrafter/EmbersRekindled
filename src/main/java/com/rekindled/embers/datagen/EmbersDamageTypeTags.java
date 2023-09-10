package com.rekindled.embers.datagen;

import java.util.concurrent.CompletableFuture;

import com.rekindled.embers.Embers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EmbersDamageTypeTags extends TagsProvider<DamageType> {

	public EmbersDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, Registries.DAMAGE_TYPE, lookupProvider, Embers.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(EmbersDamageTypes.EMBER_KEY.location());
		//tag(DamageTypeTags.IS_FIRE).addOptional(EmbersDamageTypes.EMBER_KEY.location());
		tag(DamageTypeTags.WITCH_RESISTANT_TO).addOptional(EmbersDamageTypes.EMBER_KEY.location());
	}
}
