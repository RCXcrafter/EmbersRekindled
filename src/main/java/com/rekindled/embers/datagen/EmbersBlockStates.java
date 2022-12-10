package com.rekindled.embers.datagen;

import com.google.common.collect.ImmutableMap;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;
import com.rekindled.embers.block.PipeBlockBase;
import com.rekindled.embers.block.PipeBlockBase.PipeConnection;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.ExistingModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EmbersBlockStates extends BlockStateProvider {

	public static final ImmutableMap<Direction, BooleanProperty> PIPE_PROPS = ImmutableMap.<Direction, BooleanProperty>builder()
			.put(Direction.EAST,  BlockStateProperties.EAST)
			.put(Direction.NORTH, BlockStateProperties.NORTH)
			.put(Direction.SOUTH, BlockStateProperties.SOUTH)
			.put(Direction.WEST,  BlockStateProperties.WEST)
			.put(Direction.UP,  BlockStateProperties.UP)
			.put(Direction.DOWN,  BlockStateProperties.DOWN)
			.build();

	public EmbersBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, Embers.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		//this is just to give them proper particles
		for (FluidStuff fluid : RegistryManager.fluidList) {
			fluid(fluid.FLUID_BLOCK, fluid.name);
		}

		blockWithItem(RegistryManager.COPPER_CELL, "copper_cell");
		blockWithItem(RegistryManager.CREATIVE_EMBER);
		dial(RegistryManager.EMBER_DIAL, "ember_dial");
		dial(RegistryManager.ITEM_DIAL, "item_dial");

		ExistingModelFile emitterModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_emitter"));
		ModelFile fluidPipeEndModel = models().withExistingParent("fluid_pipe_end", new ResourceLocation(Embers.MODID, "pipe_end"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/pipe_tex"));
		ModelFile fluidPipeEndModel2 = models().withExistingParent("fluid_pipe_end_2", new ResourceLocation(Embers.MODID, "pipe_end_2"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/pipe_tex"));
		simpleBlockItem(RegistryManager.EMBER_EMITTER.get(), emitterModel);

		getMultipartBuilder(RegistryManager.EMBER_EMITTER.get())
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
		.condition(BlockStateProperties.FACING, Direction.WEST).end()
		.part().modelFile(fluidPipeEndModel).addModel()
		.condition(BlockStateProperties.DOWN, true).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(180).addModel()
		.condition(BlockStateProperties.UP, true).end()
		.part().modelFile(fluidPipeEndModel).rotationX(90).addModel()
		.condition(BlockStateProperties.SOUTH, true).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(90).rotationY(180).addModel()
		.condition(BlockStateProperties.NORTH, true).end()
		.part().modelFile(fluidPipeEndModel).rotationX(90).rotationY(90).addModel()
		.condition(BlockStateProperties.WEST, true).end()
		.part().modelFile(fluidPipeEndModel2).rotationX(90).rotationY(270).addModel()
		.condition(BlockStateProperties.EAST, true).end();

		ExistingModelFile receiverModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "ember_receiver"));
		directionalBlock(RegistryManager.EMBER_RECEIVER.get(), receiverModel);
		simpleBlockItem(RegistryManager.EMBER_RECEIVER.get(), receiverModel);

		ExistingModelFile leverModel = models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_lever"));
		leverBlock(RegistryManager.CAMINITE_LEVER.get(), leverModel, models().getExistingFile(new ResourceLocation(Embers.MODID, "caminite_lever_on")));
		simpleBlockItem(RegistryManager.CAMINITE_LEVER.get(), leverModel);

		simpleBlockItem(RegistryManager.ITEM_PIPE.get(), models().withExistingParent("item_pipe_inventory", new ResourceLocation(Embers.MODID, "pipe_inventory"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex")));

		ModelFile itemPipeCenterModel = models().withExistingParent("item_pipe_center", new ResourceLocation(Embers.MODID, "pipe_center"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		ModelFile itemPipeEndModel = models().withExistingParent("item_pipe_end", new ResourceLocation(Embers.MODID, "pipe_end"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		ModelFile itemPipeConnectionModel = models().withExistingParent("item_pipe_connection", new ResourceLocation(Embers.MODID, "pipe_connection"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		ModelFile itemPipeEndModel2 = models().withExistingParent("item_pipe_end_2", new ResourceLocation(Embers.MODID, "pipe_end_2"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));
		ModelFile itemPipeConnectionModel2 = models().withExistingParent("item_pipe_connection_2", new ResourceLocation(Embers.MODID, "pipe_connection_2"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));

		getMultipartBuilder(RegistryManager.ITEM_PIPE.get())
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
		.condition(PipeBlockBase.EAST, PipeConnection.PIPE).end();

		ModelFile itemExtractorCenterModel = models().withExistingParent("item_extractor_center", new ResourceLocation(Embers.MODID, "extractor_center"))
				.texture("pipe", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"))
				.texture("particle", new ResourceLocation(Embers.MODID, "block/item_pipe_tex"));

		simpleBlockItem(RegistryManager.ITEM_EXTRACTOR.get(), itemExtractorCenterModel);
		getMultipartBuilder(RegistryManager.ITEM_EXTRACTOR.get())
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
		.condition(PipeBlockBase.EAST, PipeConnection.PIPE).end();
	}

	public void blockWithItem(RegistryObject<? extends Block> registryObject) {
		//block model
		simpleBlock(registryObject.get());
		//itemblock model
		simpleBlockItem(registryObject.get(), cubeAll(registryObject.get()));
	}

	public void blockWithItem(RegistryObject<? extends Block> registryObject, String model) {
		ExistingModelFile modelFile = models().getExistingFile(new ResourceLocation(Embers.MODID, model));
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
		itemModels().getBuilder(loc.toString())
		.parent(new ModelFile.UncheckedModelFile("item/generated"))
		.texture("layer0", new ResourceLocation(loc.getNamespace(), "item/" + texture));
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
}
