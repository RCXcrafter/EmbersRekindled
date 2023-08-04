package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.GrandhammerLootModifier;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class EmbersLootModifiers extends GlobalLootModifierProvider {

	public EmbersLootModifiers(PackOutput output) {
		super(output, Embers.MODID);
	}

	@Override
	protected void start() {
		add("grandhammer", new GrandhammerLootModifier(new LootItemCondition[]{MatchTool.toolMatches(ItemPredicate.Builder.item().of(RegistryManager.GRANDHAMMER.get())).build()}));
	}
}
