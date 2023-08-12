package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.api.block.IPipeConnection;
import com.rekindled.embers.blockentity.PipeBlockEntityBase;
import com.rekindled.embers.blockentity.PipeBlockEntityBase.PipeConnection;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public abstract class PipeBlockBase extends BaseEntityBlock implements SimpleWaterloggedBlock {

	public static final VoxelShape CENTER_AABB = Block.box(6,6,6,10,10,10);
	public static final VoxelShape PIPE_DOWN_AABB = Block.box(6,0,6,10,6,10);
	public static final VoxelShape END_DOWN_AABB = Shapes.or(Block.box(5,0,5,11,4,11), PIPE_DOWN_AABB);
	public static final VoxelShape PIPE_UP_AABB = Block.box(6,10,6,10,16,10);
	public static final VoxelShape END_UP_AABB = Shapes.or(Block.box(5,12,5,11,16,11), PIPE_UP_AABB);
	public static final VoxelShape PIPE_NORTH_AABB = Block.box(6,6,0,10,10,6);
	public static final VoxelShape END_NORTH_AABB = Shapes.or(Block.box(5,5,0,11,11,4), PIPE_NORTH_AABB);
	public static final VoxelShape PIPE_SOUTH_AABB = Block.box(6,6,10,10,10,16);
	public static final VoxelShape END_SOUTH_AABB = Shapes.or(Block.box(5,5,12,11,11,16), PIPE_SOUTH_AABB);
	public static final VoxelShape PIPE_WEST_AABB = Block.box(0,6,6,6,10,10);
	public static final VoxelShape END_WEST_AABB = Shapes.or(Block.box(0,5,5,4,11,11), PIPE_WEST_AABB);
	public static final VoxelShape PIPE_EAST_AABB = Block.box(10,6,6,16,10,10);
	public static final VoxelShape END_EAST_AABB = Shapes.or(Block.box(12,5,5,16,11,11), PIPE_EAST_AABB);
	public static final VoxelShape[] PIPE_AABBS = new VoxelShape[] { PIPE_DOWN_AABB, PIPE_UP_AABB, PIPE_NORTH_AABB, PIPE_SOUTH_AABB, PIPE_WEST_AABB, PIPE_EAST_AABB };
	public static final VoxelShape[] END_AABBS = new VoxelShape[] { END_DOWN_AABB, END_UP_AABB, END_NORTH_AABB, END_SOUTH_AABB, END_WEST_AABB, END_EAST_AABB };
	public static final VoxelShape[] SHAPES = new VoxelShape[729];

	static {
		makeShapes(CENTER_AABB, SHAPES);
	}

	public abstract TagKey<Block> getConnectionTag();

	public abstract TagKey<Block> getToggleConnectionTag();

	public abstract boolean connectToTile(BlockEntity blockEntity, Direction face);

	public abstract boolean unclog(BlockEntity blockEntity, Level level, BlockPos pos);

	public PipeBlockBase(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!Misc.isHoldingHammer(player, hand)) {
			if (player.getItemInHand(hand).is(EmbersItemTags.PIPE_UNCLOGGER)) {
				if (unclog(level.getBlockEntity(pos), level, pos))
					return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		}
		BlockEntity BE = level.getBlockEntity(pos);
		if (BE instanceof PipeBlockEntityBase pipe) {
			double reach = player.getBlockReach();
			Vec3 eyePosition = player.getEyePosition();
			Vec3 lookVector = player.getLookAngle().multiply(reach, reach, reach).add(eyePosition);

			Vec3[] hitPositions = new Vec3[6];
			BlockHitResult centerHit = getCenterShape().clip(eyePosition, lookVector, pos);

			for (int i = 0; i < 6; i++) {
				BlockHitResult partHit = null;
				if (pipe.connections[i] == PipeConnection.END) {
					partHit = END_AABBS[i].clip(eyePosition, lookVector, pos);
				} else if (pipe.connections[i] == PipeConnection.PIPE) {
					partHit = PIPE_AABBS[i].clip(eyePosition, lookVector, pos);
				}
				if (partHit != null) {
					hitPositions[i] = partHit.getLocation();
				}
			}
			int closestHit = -1;
			double closestDistance = reach;
			if (centerHit != null)
				closestDistance = eyePosition.distanceTo(centerHit.getLocation());
			for (int i = 0; i < 6; i++) {
				if (hitPositions[i] != null) {
					double dist = eyePosition.distanceTo(hitPositions[i]);
					if (dist < closestDistance) {
						closestDistance = dist;
						closestHit = i;
					}
				}
			}
			if (closestHit == -1) {
				Direction face = hit.getDirection();
				if (pipe.getConnection(face) != PipeConnection.DISABLED)
					return InteractionResult.PASS;
				BlockPos facingPos = pos.relative(face);
				BlockState facingState = level.getBlockState(facingPos);

				if (facingState.is(getToggleConnectionTag()) && level.getBlockEntity(facingPos) instanceof PipeBlockEntityBase facingPipe) {
					pipe.setConnection(face, PipeConnection.PIPE);
					facingPipe.setConnection(face.getOpposite(), PipeConnection.PIPE);
					level.updateNeighbourForOutputSignal(pos, this);
					level.updateNeighbourForOutputSignal(facingPos, this);
					level.playLocalSound(pos.getX() + 0.5 + face.getStepX() * 0.5, pos.getY() + 0.5 + face.getStepY() * 0.5, pos.getZ() + 0.5 + face.getStepZ() * 0.5, EmbersSounds.PIPE_CONNECT.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
					return InteractionResult.SUCCESS;
				}
				BlockEntity blockEntity = level.getBlockEntity(facingPos);
				if (connectToTile(blockEntity, face)) {
					pipe.setConnection(face, PipeConnection.END);
					level.updateNeighbourForOutputSignal(pos, this);
					level.playLocalSound(pos.getX() + 0.5 + face.getStepX() * 0.4, pos.getY() + 0.5 + face.getStepY() * 0.4, pos.getZ() + 0.5 + face.getStepZ() * 0.4, EmbersSounds.PIPE_CONNECT.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
					return InteractionResult.SUCCESS;
				}
			} else {
				Direction direction = Direction.from3DDataValue(closestHit);
				if (!pipe.getConnection(direction).transfer)
					return InteractionResult.PASS;
				BlockPos facingPos = pos.relative(direction);
				BlockState facingState = level.getBlockState(facingPos);

				if (pipe.getConnection(direction) == PipeConnection.PIPE && facingState.is(getToggleConnectionTag()) && level.getBlockEntity(facingPos) instanceof PipeBlockEntityBase facingPipe) {
					pipe.setConnection(direction, PipeConnection.DISABLED);
					facingPipe.setConnection(direction.getOpposite(), PipeConnection.DISABLED);
					level.updateNeighbourForOutputSignal(pos, this);
					level.updateNeighbourForOutputSignal(facingPos, this);
					level.playLocalSound(pos.getX() + 0.5 + direction.getStepX() * 0.5, pos.getY() + 0.5 + direction.getStepY() * 0.5, pos.getZ() + 0.5 + direction.getStepZ() * 0.5, EmbersSounds.PIPE_DISCONNECT.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
					return InteractionResult.SUCCESS;
				}
				if (pipe.getConnection(direction) == PipeConnection.END && !facingState.is(getConnectionTag()) && !connected(direction, facingState)) {
					pipe.setConnection(direction, PipeConnection.DISABLED);
					level.updateNeighbourForOutputSignal(pos, this);
					level.playLocalSound(pos.getX() + 0.5 + direction.getStepX() * 0.4, pos.getY() + 0.5 + direction.getStepY() * 0.4, pos.getZ() + 0.5 + direction.getStepZ() * 0.4, EmbersSounds.PIPE_DISCONNECT.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	public static int getShapeIndex(PipeConnection down, PipeConnection up, PipeConnection north, PipeConnection south, PipeConnection west, PipeConnection east) {
		return (((((down.visualIndex * 3 + up.visualIndex) * 3 + north.visualIndex) * 3 + south.visualIndex) * 3 + west.visualIndex) * 3) + east.visualIndex;
	}

	public VoxelShape getCenterShape() {
		return CENTER_AABB;
	}

	public static void makeShapes(VoxelShape center, VoxelShape[] shapes) {
		for (PipeConnection down : PipeConnection.visual()) {
			for (PipeConnection up : PipeConnection.visual()) {
				for (PipeConnection north : PipeConnection.visual()) {
					for (PipeConnection south : PipeConnection.visual()) {
						for (PipeConnection west : PipeConnection.visual()) {
							for (PipeConnection east : PipeConnection.visual()) {
								VoxelShape shape = center;
								if (down == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_DOWN_AABB, BooleanOp.OR);
								else if (down == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_DOWN_AABB, BooleanOp.OR);
								if (up == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_UP_AABB, BooleanOp.OR);
								else if (up == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_UP_AABB, BooleanOp.OR);
								if (north == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_NORTH_AABB, BooleanOp.OR);
								else if (north == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_NORTH_AABB, BooleanOp.OR);
								if (south == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_SOUTH_AABB, BooleanOp.OR);
								else if (south == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_SOUTH_AABB, BooleanOp.OR);
								if (west == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_WEST_AABB, BooleanOp.OR);
								else if (west == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_WEST_AABB, BooleanOp.OR);
								if (east == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_EAST_AABB, BooleanOp.OR);
								else if (east == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_EAST_AABB, BooleanOp.OR);
								shapes[getShapeIndex(down, up, north, south, west, east)] = shape.optimize();
							}
						}
					}
				}
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity BE = level.getBlockEntity(pos);
		if (BE instanceof PipeBlockEntityBase pipe) {
			return SHAPES[getShapeIndex(pipe.connections[0], pipe.connections[1], pipe.connections[2], pipe.connections[3], pipe.connections[4], pipe.connections[5])];
		}
		return CENTER_AABB;
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}

		BlockEntity BE = pLevel.getBlockEntity(pCurrentPos);
		if (BE instanceof PipeBlockEntityBase pipe) {
			BlockEntity facingBE = pLevel.getBlockEntity(pFacingPos);
			if (!(facingBE instanceof PipeBlockEntityBase) || ((PipeBlockEntityBase) facingBE).getConnection(pFacing.getOpposite()) != PipeConnection.DISABLED) {
				boolean enabled = pipe.getConnection(pFacing) != PipeConnection.DISABLED;
				if (pFacingState.is(getConnectionTag()) && enabled) {
					if (facingBE instanceof PipeBlockEntityBase && ((PipeBlockEntityBase) facingBE).getConnection(pFacing.getOpposite()) == PipeConnection.DISABLED
							|| pFacingState.getBlock() instanceof IPipeConnection && !((IPipeConnection) pFacingState.getBlock()).connectPipe(pFacingState, pFacing.getOpposite())) {
						pipe.setConnection(pFacing, PipeConnection.DISABLED);
					} else {
						pipe.setConnection(pFacing, PipeConnection.PIPE);
					}
				} else {
					BlockEntity blockEntity = pLevel.getBlockEntity(pFacingPos);
					if (connected(pFacing, pFacingState)) {
						pipe.setConnection(pFacing, PipeConnection.LEVER);
					} else if ((connectToTile(blockEntity, pFacing) && enabled)) {
						pipe.setConnection(pFacing, PipeConnection.END);
					} else if (enabled) {
						pipe.setConnection(pFacing, PipeConnection.NONE);
					}
				}
			}
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	public static boolean facingConnected(Direction facing, BlockState state, DirectionProperty property) {
		return !state.hasProperty(property) || state.getValue(property) == facing;
	}

	public abstract boolean connected(Direction direction, BlockState state);

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (!pState.is(pNewState.getBlock())) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity != null) {
				IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
				if (handler != null) {
					Misc.spawnInventoryInWorld(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, handler);
					pLevel.updateNeighbourForOutputSignal(pPos, this);
				}
			}
			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
