package com.rekindled.embers.datagen;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.Registry;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class EmbersBlockLootTables extends BlockLoot {

	@Nonnull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ForgeRegistries.BLOCKS.getValues().stream()
				.filter((block) -> Embers.MODID.equals(Objects.requireNonNull(Registry.BLOCK.getKey(block)).getNamespace()))
				.collect(Collectors.toList());
	}

	@Override
	protected void addTables() {
		dropSelf(RegistryManager.COPPER_CELL.get());
		dropSelf(RegistryManager.CREATIVE_EMBER.get());
		dropSelf(RegistryManager.EMBER_DIAL.get());
		dropSelf(RegistryManager.ITEM_DIAL.get());
		dropSelf(RegistryManager.EMBER_EMITTER.get());
		dropSelf(RegistryManager.EMBER_RECEIVER.get());
		dropSelf(RegistryManager.CAMINITE_LEVER.get());
		dropSelf(RegistryManager.ITEM_PIPE.get());
		dropSelf(RegistryManager.ITEM_EXTRACTOR.get());
		dropSelf(RegistryManager.EMBER_BORE.get());
		add(RegistryManager.EMBER_BORE_EDGE.get(), noDrop());
	}
}
