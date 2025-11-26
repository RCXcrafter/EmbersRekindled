package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmbersColors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmberLanternBlock extends Block implements SimpleWaterloggedBlock {

	public static final GlowParticleOptions EMBER = new GlowParticleOptions(EmbersColors.EMBER_ID, 2.0F, 120);
	public static final VoxelShape LANTERN_AABB = Shapes.or(Block.box(6,0,6,10,2,10),Block.box(4,2,4,12,4,12),Block.box(5,4,5,7,10,7),Block.box(9,4,5,11,10,7),Block.box(9,4,9,11,10,11),Block.box(5,4,9,7,10,11),Block.box(4,9,4,12,11,12),Block.box(6,11,6,10,13,10));
	public static final VoxelShape LANTERN_INTERACTION = Block.box(4,2,4,12,11,12);
	public static final VoxelShape LANTERN_CEILING_AABB = LANTERN_AABB.move(0, 0.1875, 0);
	public static final VoxelShape LANTERN_CEILING_INTERACTION = LANTERN_INTERACTION.move(0, 0.1875, 0);

	public EmberLanternBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.HANGING, false).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (state.getValue(BlockStateProperties.HANGING))
			return LANTERN_CEILING_AABB;
		return LANTERN_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		if (state.getValue(BlockStateProperties.HANGING))
			return LANTERN_CEILING_INTERACTION;
		return LANTERN_INTERACTION;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		float yOffset = 0.375f;
		if (state.getValue(BlockStateProperties.HANGING))
			yOffset += 0.1875f;
		for (int i = 0; i < 3; i ++) {
			level.addParticle(EMBER, pos.getX()+0.5f, pos.getY()+yOffset, pos.getZ()+0.5f, (random.nextFloat()-0.5f)*0.003f, (random.nextFloat())*0.003f, (random.nextFloat()-0.5f)*0.003f);
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(BlockStateProperties.HANGING, pContext.getClickedFace() == Direction.DOWN).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.HANGING).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
