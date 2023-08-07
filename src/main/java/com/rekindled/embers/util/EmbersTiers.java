package com.rekindled.embers.util;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersItemTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

public class EmbersTiers {

	public static final Tier LEAD = new ForgeTier(2, 168, 6.0f, 2.0f, 4, EmbersBlockTags.NEEDS_LEAD_TOOL, () -> Ingredient.of(EmbersItemTags.LEAD_INGOT));
	public static final Tier TYRFING = new ForgeTier(2, 512, 7.5f, 0.0f, 24, EmbersBlockTags.NEEDS_TYRFING, () -> Ingredient.of(EmbersItemTags.LEAD_INGOT));
	public static final Tier SILVER = new ForgeTier(2, 202, 7.6f, 2.0f, 20, EmbersBlockTags.NEEDS_SILVER_TOOL, () -> Ingredient.of(EmbersItemTags.SILVER_INGOT));
	public static final Tier DAWNSTONE = new ForgeTier(2, 644, 7.5f, 2.5f, 18, EmbersBlockTags.NEEDS_DAWNSTONE_TOOL, () -> Ingredient.of(EmbersItemTags.DAWNSTONE_INGOT));
	public static final Tier CLOCKWORK_PICK = new ForgeTier(3, -1, 16.0F, 4.0F, 18, EmbersBlockTags.NEEDS_CLOCKWORK_TOOL, () -> Ingredient.EMPTY);
	public static final Tier CLOCKWORK_AXE = new ForgeTier(3, -1, 16.0F, 5.0F, 18, EmbersBlockTags.NEEDS_CLOCKWORK_TOOL, () -> Ingredient.EMPTY);
	public static final Tier CLOCKWORK_HAMMER = new ForgeTier(1, -1, 6.0F, 6.0F, 18, EmbersBlockTags.NEEDS_CLOCKWORK_HAMMER, () -> Ingredient.EMPTY);

	static {
		TierSortingRegistry.registerTier(LEAD, new ResourceLocation(Embers.MODID, "lead"), List.of(Tiers.IRON), List.of(Tiers.DIAMOND, Embers.MODID + ":silver"));
		TierSortingRegistry.registerTier(TYRFING, new ResourceLocation(Embers.MODID, "tyrfing"), List.of(Tiers.IRON, LEAD), List.of(Tiers.DIAMOND, Embers.MODID + ":silver"));
		TierSortingRegistry.registerTier(SILVER, new ResourceLocation(Embers.MODID, "silver"), List.of(Tiers.IRON, LEAD), List.of(Tiers.DIAMOND, Embers.MODID + ":dawnstone"));
		TierSortingRegistry.registerTier(DAWNSTONE, new ResourceLocation(Embers.MODID, "dawnstone"), List.of(Tiers.IRON, SILVER), List.of(Tiers.DIAMOND, Embers.MODID + ":clockwork"));
		TierSortingRegistry.registerTier(CLOCKWORK_PICK, new ResourceLocation(Embers.MODID, "clockwork_pickaxe"), List.of(Tiers.DIAMOND, DAWNSTONE), List.of(Tiers.NETHERITE));
		TierSortingRegistry.registerTier(CLOCKWORK_AXE, new ResourceLocation(Embers.MODID, "clockwork_axe"), List.of(Tiers.DIAMOND, DAWNSTONE), List.of(Tiers.NETHERITE));
		TierSortingRegistry.registerTier(CLOCKWORK_HAMMER, new ResourceLocation(Embers.MODID, "clockwork_hammer"), List.of(Tiers.STONE), List.of(Tiers.IRON));
	}
}
