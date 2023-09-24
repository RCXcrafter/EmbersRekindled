package com.rekindled.embers.datagen;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.StoneDecoBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class EmbersBlockLootTables extends BlockLootSubProvider {

	public EmbersBlockLootTables() {
		super(Set.of(), FeatureFlags.VANILLA_SET);
	}

	@Nonnull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ForgeRegistries.BLOCKS.getValues().stream()
				.filter((block) -> Embers.MODID.equals(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getNamespace()))
				.collect(Collectors.toList());
	}

	@Override
	protected void generate() {
		add(RegistryManager.LEAD_ORE.get(), (block) -> {
			return createOreDrop(block, RegistryManager.RAW_LEAD.get());
		});
		add(RegistryManager.DEEPSLATE_LEAD_ORE.get(), (block) -> {
			return createOreDrop(block, RegistryManager.RAW_LEAD.get());
		});
		dropSelf(RegistryManager.RAW_LEAD_BLOCK.get());
		dropSelf(RegistryManager.LEAD_BLOCK.get());

		add(RegistryManager.SILVER_ORE.get(), (block) -> {
			return createOreDrop(block, RegistryManager.RAW_SILVER.get());
		});
		add(RegistryManager.DEEPSLATE_SILVER_ORE.get(), (block) -> {
			return createOreDrop(block, RegistryManager.RAW_SILVER.get());
		});
		dropSelf(RegistryManager.RAW_SILVER_BLOCK.get());
		dropSelf(RegistryManager.SILVER_BLOCK.get());

		dropSelf(RegistryManager.DAWNSTONE_BLOCK.get());

		dropSelf(RegistryManager.CAMINITE_BRICKS.get());
		decoDrops(RegistryManager.CAMINITE_BRICKS_DECO);
		dropSelf(RegistryManager.ARCHAIC_BRICKS.get());
		decoDrops(RegistryManager.ARCHAIC_BRICKS_DECO);
		dropSelf(RegistryManager.ARCHAIC_EDGE.get());
		dropSelf(RegistryManager.ARCHAIC_TILE.get());
		decoDrops(RegistryManager.ARCHAIC_TILE_DECO);
		dropSelf(RegistryManager.ARCHAIC_LIGHT.get());
		dropSelf(RegistryManager.ASHEN_STONE.get());
		decoDrops(RegistryManager.ASHEN_STONE_DECO);
		dropSelf(RegistryManager.ASHEN_BRICK.get());
		decoDrops(RegistryManager.ASHEN_BRICK_DECO);
		dropSelf(RegistryManager.ASHEN_TILE.get());
		decoDrops(RegistryManager.ASHEN_TILE_DECO);
		dropSelf(RegistryManager.EMBER_LANTERN.get());

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
		dropSelf(RegistryManager.MIRROR_RELAY.get());
		dropSelf(RegistryManager.BEAM_SPLITTER.get());
		dropSelf(RegistryManager.ITEM_VACUUM.get());
		dropSelf(RegistryManager.HEARTH_COIL.get());
		add(RegistryManager.HEARTH_COIL_EDGE.get(), noDrop());
		dropSelf(RegistryManager.RESERVOIR.get());
		add(RegistryManager.RESERVOIR_EDGE.get(), noDrop());
		dropSelf(RegistryManager.CAMINITE_RING.get());
		add(RegistryManager.CAMINITE_RING_EDGE.get(), noDrop());
		dropSelf(RegistryManager.CAMINITE_GAUGE.get());
		add(RegistryManager.CAMINITE_GAUGE_EDGE.get(), noDrop());
		dropSelf(RegistryManager.CAMINITE_VALVE.get());
		add(RegistryManager.CAMINITE_VALVE_EDGE.get(), noDrop());
		dropSelf(RegistryManager.CRYSTAL_CELL.get());
		add(RegistryManager.CRYSTAL_CELL_EDGE.get(), noDrop());
		dropSelf(RegistryManager.CLOCKWORK_ATTENUATOR.get());
		dropSelf(RegistryManager.GEOLOGIC_SEPARATOR.get());
		dropSelf(RegistryManager.COPPER_CHARGER.get());
		dropSelf(RegistryManager.EMBER_SIPHON.get());
		dropSelf(RegistryManager.ITEM_TRANSFER.get());
		dropSelf(RegistryManager.FLUID_TRANSFER.get());
		dropSelf(RegistryManager.ALCHEMY_PEDESTAL.get());
		dropSelf(RegistryManager.ALCHEMY_TABLET.get());
		dropSelf(RegistryManager.BEAM_CANNON.get());
		dropSelf(RegistryManager.MECHANICAL_PUMP.get());
		dropSelf(RegistryManager.MINI_BOILER.get());
		dropSelf(RegistryManager.CATALYTIC_PLUG.get());
		dropSelf(RegistryManager.WILDFIRE_STIRLING.get());
	}

	public void decoDrops(StoneDecoBlocks deco) {
		if (deco.stairs != null)
			dropSelf(deco.stairs.get());
		if (deco.slab != null)
			add(deco.slab.get(), this::createSlabItemTable);
		if (deco.wall != null)
			dropSelf(deco.wall.get());
	}
}
