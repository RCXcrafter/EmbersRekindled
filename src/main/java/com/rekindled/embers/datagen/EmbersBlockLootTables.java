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
		add(RegistryManager.LEAD_ORE.get(), (block) -> {
			return createOreDrop(block, RegistryManager.RAW_LEAD.get());
		});
		add(RegistryManager.DEEPSLATE_LEAD_ORE.get(), (block) -> {
			return createOreDrop(block, RegistryManager.RAW_LEAD.get());
		});
		dropSelf(RegistryManager.RAW_LEAD_BLOCK.get());
		dropSelf(RegistryManager.LEAD_BLOCK.get());
		dropSelf(RegistryManager.DAWNSTONE_BLOCK.get());

		dropSelf(RegistryManager.CAMINITE_BRICKS.get());
		dropSelf(RegistryManager.ARCHAIC_BRICKS.get());
		dropSelf(RegistryManager.ARCHAIC_EDGE.get());
		dropSelf(RegistryManager.ARCHAIC_TILE.get());
		dropSelf(RegistryManager.ARCHAIC_LIGHT.get());

		dropSelf(RegistryManager.COPPER_CELL.get());
		dropSelf(RegistryManager.CREATIVE_EMBER.get());
		dropSelf(RegistryManager.EMBER_DIAL.get());
		dropSelf(RegistryManager.ITEM_DIAL.get());
		dropSelf(RegistryManager.FLUID_DIAL.get());
		dropSelf(RegistryManager.EMBER_EMITTER.get());
		dropSelf(RegistryManager.EMBER_RECEIVER.get());
		dropSelf(RegistryManager.CAMINITE_LEVER.get());
		dropSelf(RegistryManager.ITEM_PIPE.get());
		dropSelf(RegistryManager.ITEM_EXTRACTOR.get());
		dropSelf(RegistryManager.EMBER_BORE.get());
		add(RegistryManager.EMBER_BORE_EDGE.get(), noDrop());
		dropSelf(RegistryManager.MECHANICAL_CORE.get());
		dropSelf(RegistryManager.EMBER_ACTIVATOR.get());
		dropSelf(RegistryManager.MELTER.get());
		dropSelf(RegistryManager.FLUID_PIPE.get());
		dropSelf(RegistryManager.FLUID_EXTRACTOR.get());
		dropSelf(RegistryManager.FLUID_VESSEL.get());
		dropSelf(RegistryManager.STAMPER.get());
		dropSelf(RegistryManager.STAMP_BASE.get());
		dropSelf(RegistryManager.BIN.get());
		dropSelf(RegistryManager.MIXER_CENTRIFUGE.get());
		dropSelf(RegistryManager.ITEM_DROPPER.get());
		dropSelf(RegistryManager.PRESSURE_REFINERY.get());
		dropSelf(RegistryManager.EMBER_EJECTOR.get());
		dropSelf(RegistryManager.EMBER_FUNNEL.get());
		dropSelf(RegistryManager.EMBER_RELAY.get());
		dropSelf(RegistryManager.BEAM_SPLITTER.get());
	}
}
