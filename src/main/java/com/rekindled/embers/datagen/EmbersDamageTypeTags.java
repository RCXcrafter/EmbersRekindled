package com.rekindled.embers.datagen;

import java.util.concurrent.CompletableFuture;

import com.rekindled.embers.Embers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EmbersDamageTypeTags extends TagsProvider<DamageType> {

	public static final TagKey<DamageType> INFLICTOR_GEM_BLACKLIST = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Embers.MODID, "inflictor_gem_blacklist"));
	public static final TagKey<DamageType> HOLY_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("consecration", "holy"));

	public EmbersDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, Registries.DAMAGE_TYPE, lookupProvider, Embers.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(EmbersDamageTypes.EMBER_KEY.location());
		//tag(DamageTypeTags.IS_FIRE).addOptional(EmbersDamageTypes.EMBER_KEY.location());
		tag(INFLICTOR_GEM_BLACKLIST)
		.add(DamageTypes.MOB_ATTACK, DamageTypes.MOB_ATTACK_NO_AGGRO, DamageTypes.MOB_PROJECTILE)
		.add(DamageTypes.GENERIC, DamageTypes.GENERIC_KILL)
		.add(DamageTypes.PLAYER_ATTACK)
		.add(DamageTypes.ARROW)
		.addTag(DamageTypeTags.BYPASSES_INVULNERABILITY);

		tag(HOLY_DAMAGE).addOptional(EmbersDamageTypes.EMBER_KEY.location());
		tag(DamageTypeTags.WITCH_RESISTANT_TO).addOptional(EmbersDamageTypes.EMBER_KEY.location());
	}
}
