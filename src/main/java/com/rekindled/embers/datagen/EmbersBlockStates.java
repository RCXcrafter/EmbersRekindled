package com.rekindled.embers.datagen;

import java.util.function.Function;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;
import com.rekindled.embers.RegistryManager.MetalCrystalSeed;
import com.rekindled.embers.RegistryManager.StoneDecoBlocks;
import com.rekindled.embers.block.EmberEmitterBlock;
import com.rekindled.embers.block.ItemTransferBlock;
import com.rekindled.embers.block.MechEdgeBlockBase;
import com.rekindled.embers.block.MechEdgeBlockBase.MechEdge;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ConfiguredModel.Builder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EmbersBlockStates extends BlockStateProvider {

	public EmbersBlockStates(PackOutput gen, ExistingFileHelper exFileHelper) {
		super(gen, Embers.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		//this is just to give them proper particles
		for (FluidStuff fluid : RegistryManager.fluidList) {
			fluid(fluid.FLUID_BLOCK, fluid.name);
		}

		blockWithItemTexture(RegistryManager.LEAD_ORE, "ore_lead");
		blockWithItemTexture(RegistryManager.DEEPSLATE_LEAD_ORE, "deepslate_ore_lead");
		blockWithItemTexture(RegistryManager.RAW_LEAD_BLOCK, "material_lead");
		blockWithItemTexture(RegistryManager.LEAD_BLOCK, "block_lead");

		blockWithItemTexture(RegistryManager.SILVER_ORE, "ore_silver");
		blockWithItemTexture(RegistryManager.DEEPSLATE_SILVER_ORE, "deepslate_ore_silver");
		blockWithItemTexture(RegistryManager.RAW_SILVER_BLOCK, "material_silver");
		blockWithItemTexture(RegistryManager.SILVER_BLOCK, "block_silver");

		blockWithItemTexture(RegistryManager.DAWNSTONE_BLOCK, "block_dawnstone");

		blockWithItem(RegistryManager.CAMINITE_BRICKS);
		decoBlocks(RegistryManager.CAMINITE_BRICKS_DECO);
		blockWithItem(RegistryManager.ARCHAIC_BRICKS);
		decoBlocks(RegistryManager.ARCHAIC_BRICKS_DECO);
		blockWithItem(RegistryManager.ARCHAIC_EDGE, "archaic_edge");
		blockWithItem(RegistryManager.ARCHAIC_TILE);
		decoBlocks(RegistryManager.ARCHAIC_TILE_DECO);
		blockWithItem(RegistryManager.ARCHAIC_LIGHT, "archaic_light");
		blockWithItem(RegistryManager.ASHEN_STONE);
		decoBlocks(RegistryManager.ASHEN_STONE_DECO);
		blockWithItem(RegistryManager.ASHEN_BRICK);
		decoBlocks(RegistryManager.ASHEN_BRICK_DECO);
		blockWithItem(RegistryManager.ASHEN_TILE);
		decoBlocks(RegistryManager.ASHEN_TILE_DECO);
		blockWithItem(RegistryManager.EMBER_LANTERN, "ember_lantern");

		blockWithItem(RegistryManager.COPPER_CELL, "copper_cell");
		blockWithItem(RegistryManager.CREATIVE_EMBER);
		dial(RegistryManager.EMBER_DIAL, "ember_dial");
		dial(RegistryManager.ITEM_DIAL, "item_dial");
		dial(RegistryManager.FLUID_DIAL, "fluid_dial");
		dial(RegistryManager.CLOCKWORK_ATTENUATOR, "clockwork_attenuator");

		ModelFile fluidPipeCenterModel = models().withExistingParent("fluid_pipe_center", new ResourceLocation(Embers.MODID, "pipe_center"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"));
		ModelFile fluidPipeEndModel = models().withExistingParent("fluid_pipe_end", new ResourceLocation(Embers.MODID, "pipe_end"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"));
		models().withExistingParent("fluid_pipe_connection", new ResourceLocation(Embers.MODID, "pipe_connection"))
		.texture("pipe", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"))
		.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"));
		ModelFile fluidPipeEndModel2 = models().withExistingParent("fluid_pipe_end_2", new ResourceLocation(Embers.MODID, "pipe_end_2"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"));
		models().withExistingParent("fluid_pipe_connection_2", new ResourceLocation(Embers.MODID, "pipe_connection_2"))
		.texture("pipe", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"))
		.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"));

		ExistingModelFile emitterModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_emitter"));
		simpleBlockItem(RegistryManager.EMBER_EMITTER.get(), emitterModel);

		MultiPartBlockStateBuilder emitterBuilder = getMultipartBuilder(RegistryManager.EMBER_EMITTER.get())
				.part().modelFile(emitterModel).addModel()
				.condition(BlockStateProperties.FACING, Direction.UP).end()
				.part().modelFile(emitterModel).rotationX(180).addModel()
				.condition(BlockStateProperties.FACING, Direction.DOWN).end()
				.part().modelFile(emitterModel).rotationX(90).addModel()
				.condition(BlockStateProperties.FACING, Direction.NORTH).end()
				.part().modelFile(emitterModel).rotationX(90).rotationY(180).addModel()
				.condition(BlockStateProperties.FACING, Direction.SOUTH).end()
				.part().modelFile(emitterModel).rotationX(90).rotationY(90).addModel()
				.condition(BlockStateProperties.FACING, Direction.EAST).end()
				.part().modelFile(emitterModel).rotationX(90).rotationY(270).addModel()
				.condition(BlockStateProperties.FACING, Direction.WEST).end();
		addEmitterConnections(emitterBuilder, fluidPipeEndModel, fluidPipeEndModel2);

		ExistingModelFile receiverModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_receiver"));
		directionalBlock(RegistryManager.EMBER_RECEIVER.get(), receiverModel);
		simpleBlockItem(RegistryManager.EMBER_RECEIVER.get(), receiverModel);

		ExistingModelFile leverModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_lever"));
		leverBlock(RegistryManager.CAMINITE_LEVER.get(), leverModel, models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_lever_on")));
		simpleBlockItem(RegistryManager.CAMINITE_LEVER.get(), leverModel);

		ModelFile itemPipeCenterModel = models().withExistingParent("item_pipe_center", new ResourceLocation(Embers.MODID, "pipe_center"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		models().withExistingParent("item_pipe_end", new ResourceLocation(Embers.MODID, "pipe_end"))
		.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
		.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		models().withExistingParent("item_pipe_connection", new ResourceLocation(Embers.MODID, "pipe_connection"))
		.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
		.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		models().withExistingParent("item_pipe_end_2", new ResourceLocation(Embers.MODID, "pipe_end_2"))
		.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
		.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		models().withExistingParent("item_pipe_connection_2", new ResourceLocation(Embers.MODID, "pipe_connection_2"))
		.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
		.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));

		simpleBlockItem(RegistryManager.ITEM_PIPE.get(), models().withExistingParent("item_pipe_inventory", new ResourceLocation(Embers.MODID, "pipe_inventory"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex")));

		simpleBlock(RegistryManager.ITEM_PIPE.get(), itemPipeCenterModel);
		/*getMultipartBuilder(RegistryManager.ITEM_PIPE.get())
		.part().modelFile(itemPipeCenterModel).addModel().end()
		//pipe ends
		.part().modelFile(itemPipeEndModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.END).end()
		//pipe connections
		.part().modelFile(itemPipeConnectionModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.PIPE).end();*/

		ModelFile itemExtractorCenterModel = models().withExistingParent("item_extractor_center", new ResourceLocation(Embers.MODID, "extractor_center"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));

		simpleBlockItem(RegistryManager.ITEM_EXTRACTOR.get(), itemExtractorCenterModel);
		simpleBlock(RegistryManager.ITEM_EXTRACTOR.get(), itemExtractorCenterModel);
		/*getMultipartBuilder(RegistryManager.ITEM_EXTRACTOR.get())
		.part().modelFile(itemExtractorCenterModel).addModel().end()
		//pipe ends
		.part().modelFile(itemPipeEndModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.END).end()
		.part().modelFile(itemPipeEndModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.END).end()
		//pipe connections
		.part().modelFile(itemPipeConnectionModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.PIPE).end()
		.part().modelFile(itemPipeConnectionModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.PIPE).end();*/

		ExistingModelFile emberBoreModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_bore_center"));
		getVariantBuilder(RegistryManager.EMBER_BORE.get()).forAllStates(state -> {
			Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);

			return ConfiguredModel.builder()
					.modelFile(emberBoreModel)
					.rotationY(axis == Axis.Z ? 90 : 0)
					.uvLock(false)
					.build();
		});
		simpleBlockItem(RegistryManager.EMBER_BORE.get(), models().cubeAll("ember_bore", new ResourceLocation(Embers.MODID, "block/crate_bore")));

		ExistingModelFile mechEdgeModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mech_edge_straight"));
		ExistingModelFile boreEdgeModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_bore_edge"));
		ExistingModelFile mechCornerModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mech_edge_corner"));
		getVariantBuilder(RegistryManager.EMBER_BORE_EDGE.get()).forAllStates(state -> {
			MechEdge edge = state.getValue(MechEdgeBlockBase.EDGE);
			Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);

			return ConfiguredModel.builder()
					.modelFile(edge.corner ? mechCornerModel : (edge == MechEdge.NORTH || edge == MechEdge.SOUTH) && axis == Axis.Z || (edge == MechEdge.EAST || edge == MechEdge.WEST) && axis == Axis.X ? mechEdgeModel : boreEdgeModel)
					.rotationY(edge.rotation)
					.uvLock(true)
					.build();
		});

		ExistingModelFile mechCoreModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mech_core"));
		betterDirectionalBlock(RegistryManager.MECHANICAL_CORE.get(), $ -> mechCoreModel, 0);
		simpleBlockItem(RegistryManager.MECHANICAL_CORE.get(), mechCoreModel);

		ExistingModelFile activatorTopModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "activator_top"));
		simpleBlockItem(RegistryManager.EMBER_ACTIVATOR.get(), activatorTopModel);
		getVariantBuilder(RegistryManager.EMBER_ACTIVATOR.get()).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? models().getExistingFile(new ResourceLocation(Embers.MODID, "activator_bottom")) : activatorTopModel)
					.uvLock(false)
					.build();
		});

		ExistingModelFile melterBottomModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "melter_bottom"));
		simpleBlockItem(RegistryManager.MELTER.get(), melterBottomModel);
		getVariantBuilder(RegistryManager.MELTER.get()).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? melterBottomModel : models().getExistingFile(new ResourceLocation(Embers.MODID, "melter_top")))
					.uvLock(false)
					.build();
		});

		simpleBlockItem(RegistryManager.FLUID_PIPE.get(), models().withExistingParent("fluid_pipe_inventory", new ResourceLocation(Embers.MODID, "pipe_inventory"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex")));

		simpleBlock(RegistryManager.FLUID_PIPE.get(), fluidPipeCenterModel);
		/*getMultipartBuilder(RegistryManager.FLUID_PIPE.get())
		.part().modelFile(fluidPipeCenterModel).addModel().end()
		//pipe ends
		.part().modelFile(fluidPipeEndModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.END).end()
		//pipe connections
		.part().modelFile(fluidPipeConnectionModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.PIPE).end();*/

		ModelFile fluidExtractorCenterModel = models().withExistingParent("fluid_extractor_center", new ResourceLocation(Embers.MODID, "extractor_center"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_pipe_tex"));

		simpleBlockItem(RegistryManager.FLUID_EXTRACTOR.get(), fluidExtractorCenterModel);
		simpleBlock(RegistryManager.FLUID_EXTRACTOR.get(), fluidExtractorCenterModel);
		/*getMultipartBuilder(RegistryManager.FLUID_EXTRACTOR.get())
		.part().modelFile(fluidExtractorCenterModel).addModel().end()
		//pipe ends
		.part().modelFile(fluidPipeEndModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.END).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.END).end()
		//pipe connections
		.part().modelFile(fluidPipeConnectionModel).addModel()
		.condition(PipeBlockBase.DOWN, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel2).rotationX(180).addModel()
		.condition(PipeBlockBase.UP, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel).rotationX(90).addModel()
		.condition(PipeBlockBase.SOUTH, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel2).rotationX(90).rotationY(180).addModel()
		.condition(PipeBlockBase.NORTH, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel).rotationX(90).rotationY(90).addModel()
		.condition(PipeBlockBase.WEST, PipeConnection.PIPE).end()
		.part().modelFile(fluidPipeConnectionModel2).rotationX(90).rotationY(270).addModel()
		.condition(PipeBlockBase.EAST, PipeConnection.PIPE).end();*/

		blockWithItem(RegistryManager.FLUID_VESSEL, "fluid_vessel");

		ExistingModelFile stamperModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "stamper"));
		simpleBlock(RegistryManager.STAMPER.get(), stamperModel);
		simpleBlockItem(RegistryManager.STAMPER.get(), stamperModel);

		blockWithItem(RegistryManager.STAMP_BASE, "stamp_base");

		blockWithItem(RegistryManager.BIN, "bin");

		ExistingModelFile mixerBottomModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mixer_bottom"));
		simpleBlockItem(RegistryManager.MIXER_CENTRIFUGE.get(), mixerBottomModel);
		getVariantBuilder(RegistryManager.MIXER_CENTRIFUGE.get()).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? mixerBottomModel : models().getExistingFile(new ResourceLocation(Embers.MODID, "mixer_top")))
					.uvLock(false)
					.build();
		});

		ModelFile dropperModel = models().withExistingParent(RegistryManager.ITEM_DROPPER.getId().toString(), new ResourceLocation(Embers.MODID, "dropper"))
				.texture("dropper", new ResourceLocation(Embers.MODID, "block/plates_lead"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/plates_lead"));
		simpleBlock(RegistryManager.ITEM_DROPPER.get(), dropperModel);
		simpleBlockItem(RegistryManager.ITEM_DROPPER.get(), dropperModel);

		ExistingModelFile refineryBottomModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "refinery_bottom"));
		simpleBlockItem(RegistryManager.PRESSURE_REFINERY.get(), refineryBottomModel);
		getVariantBuilder(RegistryManager.PRESSURE_REFINERY.get()).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? refineryBottomModel : models().getExistingFile(new ResourceLocation(Embers.MODID, "refinery_top")))
					.uvLock(false)
					.build();
		});

		ExistingModelFile ejectorModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_ejector"));
		simpleBlockItem(RegistryManager.EMBER_EJECTOR.get(), ejectorModel);

		MultiPartBlockStateBuilder ejectorBuilder = getMultipartBuilder(RegistryManager.EMBER_EJECTOR.get())
				.part().modelFile(ejectorModel).addModel()
				.condition(BlockStateProperties.FACING, Direction.UP).end()
				.part().modelFile(ejectorModel).rotationX(180).addModel()
				.condition(BlockStateProperties.FACING, Direction.DOWN).end()
				.part().modelFile(ejectorModel).rotationX(90).addModel()
				.condition(BlockStateProperties.FACING, Direction.NORTH).end()
				.part().modelFile(ejectorModel).rotationX(90).rotationY(180).addModel()
				.condition(BlockStateProperties.FACING, Direction.SOUTH).end()
				.part().modelFile(ejectorModel).rotationX(90).rotationY(90).addModel()
				.condition(BlockStateProperties.FACING, Direction.EAST).end()
				.part().modelFile(ejectorModel).rotationX(90).rotationY(270).addModel()
				.condition(BlockStateProperties.FACING, Direction.WEST).end();
		addEmitterConnections(ejectorBuilder, fluidPipeEndModel, fluidPipeEndModel2);

		ExistingModelFile funnelModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_funnel"));
		directionalBlock(RegistryManager.EMBER_FUNNEL.get(), funnelModel);
		simpleBlockItem(RegistryManager.EMBER_FUNNEL.get(), funnelModel);

		ExistingModelFile relayModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_relay"));
		directionalBlock(RegistryManager.EMBER_RELAY.get(), relayModel);
		simpleBlockItem(RegistryManager.EMBER_RELAY.get(), relayModel);

		ExistingModelFile mirrorModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mirror_relay"));
		directionalBlock(RegistryManager.MIRROR_RELAY.get(), mirrorModel);
		simpleBlockItem(RegistryManager.MIRROR_RELAY.get(), mirrorModel);

		ExistingModelFile splitterModelX = models().getExistingFile(new ResourceLocation(Embers.MODID, "beam_splitter_x"));
		ExistingModelFile splitterModelZ = models().getExistingFile(new ResourceLocation(Embers.MODID, "beam_splitter_z"));
		simpleBlockItem(RegistryManager.BEAM_SPLITTER.get(), splitterModelZ);
		getVariantBuilder(RegistryManager.BEAM_SPLITTER.get()).forAllStates(state -> {
			Direction face = state.getValue(BlockStateProperties.FACING);
			Axis axis = state.getValue(BlockStateProperties.AXIS);
			return ConfiguredModel.builder()
					.modelFile((axis == Axis.X && face.getAxis() == Axis.Y) || (axis != Axis.Y && face.getAxis() != Axis.Y) ? splitterModelX : splitterModelZ)
					.rotationX(face == Direction.DOWN ? 180 : face == Direction.UP ? 0 : 90)
					.rotationY(face == Direction.SOUTH ? 180 : face == Direction.WEST ? 270 : face == Direction.EAST ? 90 : 0)
					.uvLock(false)
					.build();
		});

		ExistingModelFile vacuumModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "item_vacuum"));
		directionalBlock(RegistryManager.ITEM_VACUUM.get(), vacuumModel);
		simpleBlockItem(RegistryManager.ITEM_VACUUM.get(), vacuumModel);

		simpleBlock(RegistryManager.HEARTH_COIL.get(), models().getExistingFile(new ResourceLocation(Embers.MODID, "hearth_coil_center")));
		simpleBlockItem(RegistryManager.HEARTH_COIL.get(), models().cubeAll("hearth_coil", new ResourceLocation(Embers.MODID, "block/crate_coil")));

		ExistingModelFile coilEdgeModelX = models().getExistingFile(new ResourceLocation(Embers.MODID, "hearth_coil_edge_x"));
		ExistingModelFile coilEdgeModelZ = models().getExistingFile(new ResourceLocation(Embers.MODID, "hearth_coil_edge_z"));
		ExistingModelFile coilCornerModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "hearth_coil_corner"));
		getVariantBuilder(RegistryManager.HEARTH_COIL_EDGE.get()).forAllStates(state -> {
			MechEdge edge = state.getValue(MechEdgeBlockBase.EDGE);

			return ConfiguredModel.builder()
					.modelFile(edge.corner ? coilCornerModel : edge == MechEdge.EAST || edge == MechEdge.WEST ? coilEdgeModelZ : coilEdgeModelX)
					.rotationY(edge.rotation)
					.uvLock(true)
					.build();
		});

		simpleBlock(RegistryManager.RESERVOIR.get(), models().getExistingFile(new ResourceLocation(Embers.MODID, "reservoir_center")));
		simpleBlockItem(RegistryManager.RESERVOIR.get(), models().cubeAll("reservoir", new ResourceLocation(Embers.MODID, "block/crate_tank")));

		getVariantBuilder(RegistryManager.RESERVOIR_EDGE.get()).forAllStates(state -> {
			MechEdge edge = state.getValue(MechEdgeBlockBase.EDGE);

			return ConfiguredModel.builder()
					.modelFile(edge.corner ? mechCornerModel : mechEdgeModel)
					.rotationY(edge.rotation)
					.uvLock(true)
					.build();
		});

		simpleBlock(RegistryManager.CAMINITE_RING.get(), models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_ring_center")));
		flatItem(RegistryManager.CAMINITE_RING, "caminite_ring");

		ExistingModelFile ringEdgeModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_ring_edge"));
		ExistingModelFile ringCornerModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_ring_corner"));
		getVariantBuilder(RegistryManager.CAMINITE_RING_EDGE.get()).forAllStates(state -> {
			MechEdge edge = state.getValue(MechEdgeBlockBase.EDGE);

			return ConfiguredModel.builder()
					.modelFile(edge.corner ? ringCornerModel : ringEdgeModel)
					.rotationY(edge.rotation)
					.uvLock(false)
					.build();
		});

		simpleBlock(RegistryManager.CAMINITE_GAUGE.get(), models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_ring_center")));
		flatItem(RegistryManager.CAMINITE_GAUGE, "caminite_gauge");

		ModelFile gaugeEdgeModel = models().withExistingParent("caminite_gauge_edge", new ResourceLocation(Embers.MODID, "caminite_gauge_edge_base")).customLoader(CompositeModelBuilder::begin)
				.child("base", models().nested().parent(models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_gauge_edge_base"))))
				.child("glass", models().nested().parent(models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_gauge_edge_glass")))).end();
		getVariantBuilder(RegistryManager.CAMINITE_GAUGE_EDGE.get()).forAllStates(state -> {
			MechEdge edge = state.getValue(MechEdgeBlockBase.EDGE);

			return ConfiguredModel.builder()
					.modelFile(edge.corner ? ringCornerModel : gaugeEdgeModel)
					.rotationY(edge.rotation)
					.uvLock(false)
					.build();
		});

		simpleBlock(RegistryManager.CAMINITE_VALVE.get(), models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_ring_center")));
		flatItem(RegistryManager.CAMINITE_VALVE, "caminite_valve");

		ExistingModelFile valveEdgeModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_valve_edge"));
		getVariantBuilder(RegistryManager.CAMINITE_VALVE_EDGE.get()).forAllStates(state -> {
			MechEdge edge = state.getValue(MechEdgeBlockBase.EDGE);

			return ConfiguredModel.builder()
					.modelFile(edge.corner ? ringCornerModel : valveEdgeModel)
					.rotationY(edge.rotation)
					.uvLock(false)
					.build();
		});

		simpleBlock(RegistryManager.CRYSTAL_CELL.get(), models().getExistingFile(new ResourceLocation(Embers.MODID, "crystal_cell_center")));
		simpleBlockItem(RegistryManager.CRYSTAL_CELL.get(), models().cubeAll("crystal_cell", new ResourceLocation(Embers.MODID, "block/crate_crystal")));

		ExistingModelFile cellEdgeModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "crystal_cell_edge"));
		ExistingModelFile cellCornerModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "crystal_cell_corner"));
		getVariantBuilder(RegistryManager.CRYSTAL_CELL_EDGE.get()).forAllStates(state -> {
			MechEdge edge = state.getValue(MechEdgeBlockBase.EDGE);

			return ConfiguredModel.builder()
					.modelFile(edge.corner ? cellCornerModel : cellEdgeModel)
					.rotationY(edge.rotation)
					.uvLock(false)
					.build();
		});

		ExistingModelFile separatorModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "geologic_separator"));
		horizontalBlock(RegistryManager.GEOLOGIC_SEPARATOR.get(), separatorModel);
		simpleBlockItem(RegistryManager.GEOLOGIC_SEPARATOR.get(), separatorModel);

		ExistingModelFile chargerModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "copper_charger"));
		horizontalBlock(RegistryManager.COPPER_CHARGER.get(), chargerModel);
		simpleBlockItem(RegistryManager.COPPER_CHARGER.get(), chargerModel);

		ExistingModelFile siphonModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_siphon"));
		simpleBlock(RegistryManager.EMBER_SIPHON.get(), siphonModel);
		simpleBlockItem(RegistryManager.EMBER_SIPHON.get(), siphonModel);

		ExistingModelFile itemTransferModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "item_transfer"));
		ExistingModelFile itemTransferFilterModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "item_transfer_filtered"));
		directionalBlock(RegistryManager.ITEM_TRANSFER.get(), state -> state.getValue(ItemTransferBlock.FILTER) ? itemTransferFilterModel : itemTransferModel, 180);
		simpleBlockItem(RegistryManager.ITEM_TRANSFER.get(), itemTransferModel);

		ModelFile fluidTransferModel = models().withExistingParent("fluid_transfer", new ResourceLocation(Embers.MODID, "item_transfer"))
				.texture("top", new ResourceLocation(Embers.MODID, "block/fluid_transfer_top"))
				.texture("side", new ResourceLocation(Embers.MODID, "block/fluid_transfer_side"))
				.texture("bottom", new ResourceLocation(Embers.MODID, "block/fluid_transfer_bottom"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_transfer_side"));
		ModelFile fluidTransferFilterModel = models().withExistingParent("fluid_transfer_filtered", new ResourceLocation(Embers.MODID, "item_transfer_filtered"))
				.texture("top", new ResourceLocation(Embers.MODID, "block/fluid_transfer_top"))
				.texture("side", new ResourceLocation(Embers.MODID, "block/fluid_transfer_side"))
				.texture("bottom", new ResourceLocation(Embers.MODID, "block/fluid_transfer_bottom"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/fluid_transfer_side"));
		directionalBlock(RegistryManager.FLUID_TRANSFER.get(), state -> state.getValue(ItemTransferBlock.FILTER) ? fluidTransferFilterModel : fluidTransferModel, 180);
		simpleBlockItem(RegistryManager.FLUID_TRANSFER.get(), fluidTransferModel);

		getVariantBuilder(RegistryManager.ALCHEMY_PEDESTAL.get()).forAllStates(state -> {
			return ConfiguredModel.builder()
					.modelFile(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? models().getExistingFile(new ResourceLocation(Embers.MODID, "alchemy_pedestal_bottom")) : models().getExistingFile(new ResourceLocation(Embers.MODID, "alchemy_pedestal_top")))
					.uvLock(false)
					.build();
		});

		blockWithItem(RegistryManager.ALCHEMY_TABLET, "alchemy_tablet");

		ExistingModelFile cannonModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "beam_cannon"));
		simpleBlockItem(RegistryManager.BEAM_CANNON.get(), cannonModel);

		MultiPartBlockStateBuilder cannonBuilder = getMultipartBuilder(RegistryManager.BEAM_CANNON.get())
				.part().modelFile(cannonModel).addModel()
				.condition(BlockStateProperties.FACING, Direction.UP).end()
				.part().modelFile(cannonModel).rotationX(180).addModel()
				.condition(BlockStateProperties.FACING, Direction.DOWN).end()
				.part().modelFile(cannonModel).rotationX(90).addModel()
				.condition(BlockStateProperties.FACING, Direction.NORTH).end()
				.part().modelFile(cannonModel).rotationX(90).rotationY(180).addModel()
				.condition(BlockStateProperties.FACING, Direction.SOUTH).end()
				.part().modelFile(cannonModel).rotationX(90).rotationY(90).addModel()
				.condition(BlockStateProperties.FACING, Direction.EAST).end()
				.part().modelFile(cannonModel).rotationX(90).rotationY(270).addModel()
				.condition(BlockStateProperties.FACING, Direction.WEST).end();
		addEmitterConnections(cannonBuilder, fluidPipeEndModel, fluidPipeEndModel2);

		ExistingModelFile pumpBottomModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mechanical_pump_bottom"));
		ExistingModelFile pumpTopModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mechanical_pump_top"));
		getVariantBuilder(RegistryManager.MECHANICAL_PUMP.get()).forAllStates(state -> {
			Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);

			return ConfiguredModel.builder()
					.modelFile(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pumpBottomModel : pumpTopModel)
					.rotationY(axis == Axis.Z ? 0 : 90)
					.uvLock(false)
					.build();
		});
		simpleBlockItem(RegistryManager.MECHANICAL_PUMP.get(), pumpBottomModel);

		ExistingModelFile boilerModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "mini_boiler"));
		horizontalBlock(RegistryManager.MINI_BOILER.get(), boilerModel);
		simpleBlockItem(RegistryManager.MINI_BOILER.get(), boilerModel);

		ModelFile catalyticPlugModel = models().withExistingParent("catalytic_plug", new ResourceLocation(Embers.MODID, "catalytic_plug_base")).customLoader(CompositeModelBuilder::begin)
				.child("base", models().nested().parent(models().getExistingFile(new ResourceLocation(Embers.MODID, "catalytic_plug_base"))))
				.child("glass", models().nested().parent(models().getExistingFile(new ResourceLocation(Embers.MODID, "catalytic_plug_glass")))).end();

		directionalBlock(RegistryManager.CATALYTIC_PLUG.get(), catalyticPlugModel);
		simpleBlockItem(RegistryManager.CATALYTIC_PLUG.get(), catalyticPlugModel);

		ExistingModelFile stirlingModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "wildfire_stirling"));
		directionalBlock(RegistryManager.WILDFIRE_STIRLING.get(), stirlingModel);
		simpleBlockItem(RegistryManager.WILDFIRE_STIRLING.get(), stirlingModel);

		ExistingModelFile injectorModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_injector"));
		directionalBlock(RegistryManager.EMBER_INJECTOR.get(), injectorModel);
		simpleBlockItem(RegistryManager.EMBER_INJECTOR.get(), injectorModel);

		metalSeed(RegistryManager.COPPER_CRYSTAL_SEED);
		metalSeed(RegistryManager.IRON_CRYSTAL_SEED);
		metalSeed(RegistryManager.GOLD_CRYSTAL_SEED);
		metalSeed(RegistryManager.LEAD_CRYSTAL_SEED);
		metalSeed(RegistryManager.SILVER_CRYSTAL_SEED);
		metalSeed(RegistryManager.ALUMINUM_CRYSTAL_SEED);
		metalSeed(RegistryManager.NICKEL_CRYSTAL_SEED);
		metalSeed(RegistryManager.TIN_CRYSTAL_SEED);
		metalSeed(RegistryManager.DAWNSTONE_CRYSTAL_SEED);
	}

	public void blockWithItem(RegistryObject<? extends Block> registryObject) {
		//block model
		simpleBlock(registryObject.get());
		//itemblock model
		simpleBlockItem(registryObject.get(), cubeAll(registryObject.get()));
	}

	public void columnBlockWithItem(RegistryObject<? extends Block> registryObject, String sideTex, String topTex) {
		ResourceLocation side = new ResourceLocation(Embers.MODID, "block/" + sideTex);
		//ResourceLocation side_overlay = new ResourceLocation(Embers.MODID, "block/" + sideTex + "_overlay");
		ResourceLocation end = new ResourceLocation(Embers.MODID, "block/" + topTex);

		ModelFile model = models().cubeColumn(registryObject.getId().getPath(), side, end)/*.texture("overlay", side_overlay).renderType("cutout").element()
				.from(0, 0, 0)
				.to(16, 16, 16)
				.cube(topTex)
				.face(Direction.NORTH).texture("#overlay").cullface(Direction.NORTH).emissivity(8).end()
				.allFaces(
						(dir, f) -> {
							if (dir.getAxis() != Axis.Y) {
								f.texture("#overlay");
								f.cullface(dir);
								f.emissivity(8);
							}
						}).end()*/;
		//block model
		simpleBlock(registryObject.get(), model);
		//itemblock model
		simpleBlockItem(registryObject.get(), model);
	}

	public void blockWithItem(RegistryObject<? extends Block> registryObject, String model) {
		ExistingModelFile modelFile = models().getExistingFile(new ResourceLocation(Embers.MODID, model));
		//block model
		simpleBlock(registryObject.get(), modelFile);
		//itemblock model
		simpleBlockItem(registryObject.get(), modelFile);
	}

	public void blockWithItemTexture(RegistryObject<? extends Block> registryObject, String texture) {
		ModelFile modelFile = models().cubeAll(ForgeRegistries.BLOCKS.getKey(registryObject.get()).getPath(), new ResourceLocation(Embers.MODID, "block/" + texture));
		//block model
		simpleBlock(registryObject.get(), modelFile);
		//itemblock model
		simpleBlockItem(registryObject.get(), modelFile);
	}

	public void dial(RegistryObject<? extends Block> registryObject, String texture) {
		//block model
		ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(registryObject.get());
		ModelFile model = models().withExistingParent(loc.toString(), new ResourceLocation(Embers.MODID, "dial"))
				.texture("dial", new ResourceLocation(Embers.MODID, "block/" + texture))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/" + texture));
		directionalBlock(registryObject.get(), model);

		//item model
		flatItem(registryObject, texture);
	}

	public void flatItem(RegistryObject<? extends Block> registryObject, String texture) {
		ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(registryObject.get());
		itemModels().getBuilder(loc.toString())
		.parent(new ModelFile.UncheckedModelFile("item/generated"))
		.texture("layer0", new ResourceLocation(loc.getNamespace(), "item/" + texture));
	}

	public void betterDirectionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
		getVariantBuilder(block)
		.forAllStates(state -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			return ConfiguredModel.builder()
					.modelFile(modelFunc.apply(state))
					.rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 270 : 0)
					.rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
					.build();
		});
	}

	public void leverBlock(Block block, ModelFile lever, ModelFile leverFlipped) {
		getVariantBuilder(block).forAllStates(state -> {
			Direction facing = state.getValue(ButtonBlock.FACING);
			AttachFace face = state.getValue(ButtonBlock.FACE);
			boolean powered = state.getValue(ButtonBlock.POWERED);

			return ConfiguredModel.builder()
					.modelFile(powered ? leverFlipped : lever)
					.rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
					.rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
					.uvLock(false)
					.build();
		});
	}

	public void fluid(RegistryObject<? extends Block> fluid, String name) {
		simpleBlock(fluid.get(), models().cubeAll(name, new ResourceLocation(Embers.MODID, ModelProvider.BLOCK_FOLDER + "/fluid/" + name + "_still")));
	}

	public void decoBlocks(StoneDecoBlocks deco) {
		ResourceLocation resourceLocation = this.blockTexture(deco.block.get());

		if (deco.stairs != null) {
			this.stairsBlock(deco.stairs.get(), resourceLocation);
			this.itemModels().stairs(deco.stairs.getId().getPath(), resourceLocation, resourceLocation, resourceLocation);
		}
		if (deco.slab != null) {
			this.slabBlock(deco.slab.get(), deco.block.getId(), resourceLocation);
			this.itemModels().slab(deco.slab.getId().getPath(), resourceLocation, resourceLocation, resourceLocation);
		}
		if (deco.wall != null) {
			this.wallBlock(deco.wall.get(), resourceLocation);
			this.itemModels().wallInventory(deco.wall.getId().getPath(), resourceLocation);
		}
	}

	public void metalSeed(MetalCrystalSeed seed) {
		simpleBlock(seed.BLOCK.get(), models().cubeAll(seed.name, new ResourceLocation(Embers.MODID, ModelProvider.BLOCK_FOLDER + "/material_" + seed.name)));
		flatItem(seed.BLOCK, "seed_" + seed.name);
	}

	public static void addEmitterConnections(MultiPartBlockStateBuilder builder, ModelFile pipe1, ModelFile pipe2) {
		for (Direction direction : Direction.values()) {
			Axis axis1;
			Axis axis2;

			switch (direction.getAxis()) {
			case X:
				axis1 = Direction.Axis.Y;
				axis2 = Direction.Axis.Z;
				break;
			case Y:
				axis1 = Direction.Axis.X;
				axis2 = Direction.Axis.Z;
				break;
			case Z:
			default:
				axis1 = Direction.Axis.X;
				axis2 = Direction.Axis.Y;
				break;
			}

			rotationsForDirection(builder.part().modelFile(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? pipe2 : pipe1), direction).addModel()

			.nestedGroup().useOr()

			.nestedGroup()
			.condition(BlockStateProperties.FACING, Direction.fromAxisAndDirection(axis1, Direction.AxisDirection.POSITIVE), Direction.fromAxisAndDirection(axis1, Direction.AxisDirection.NEGATIVE))
			//AND
			.condition(EmberEmitterBlock.DIRECTIONS[EmberEmitterBlock.getIndexForDirection(axis1, direction)], true)
			.endNestedGroup()

			.nestedGroup()
			.condition(BlockStateProperties.FACING, Direction.fromAxisAndDirection(axis2, Direction.AxisDirection.POSITIVE), Direction.fromAxisAndDirection(axis2, Direction.AxisDirection.NEGATIVE))
			//AND
			.condition(EmberEmitterBlock.DIRECTIONS[EmberEmitterBlock.getIndexForDirection(axis2, direction)], true)
			.endNestedGroup()

			.end();
		}
	}

	public static <T> Builder<T> rotationsForDirection(Builder<T> builder, Direction direction) {
		switch (direction) {
		case DOWN:
		default:
			return builder;
		case UP:
			return builder.rotationX(180);
		case EAST:
			return builder.rotationX(90).rotationY(270);
		case WEST:
			return builder.rotationX(90).rotationY(90);
		case SOUTH:
			return builder.rotationX(90);
		case NORTH:
			return builder.rotationX(90).rotationY(180);
		}
	}
}
