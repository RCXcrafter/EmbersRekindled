package com.rekindled.embers.block;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.FluidVesselBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidVesselBlock extends AbstractCauldronBlock implements EntityBlock, SimpleWaterloggedBlock {

	protected static final VoxelShape VESSEL_AABB = Shapes.or(Block.box(0,0,0,4,16,4),Block.box(12,0,0,16,16,4),Block.box(12,0,12,16,16,16),Block.box(0,0,12,4,16,16),Block.box(4,0,12,12,16,14),Block.box(4,0,2,12,16,4),Block.box(2,0,4,4,16,12),Block.box(12,0,4,14,16,12),Block.box(4,0,4,12,2,12),Block.box(6,6,0,10,10,2),Block.box(6,6,14,10,10,16),Block.box(14,6,6,16,10,10),Block.box(0,6,6,2,10,10));

	public FluidVesselBlock(Properties properties) {
		super(properties, Map.of());
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof FluidVesselBlockEntity vesselEntity) {
			ItemStack heldItem = player.getItemInHand(hand);
			if (!heldItem.isEmpty()) {
				IFluidHandler cap = vesselEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection()).orElse(null);
				if (cap != null) {
					boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, cap);

					if (didFill) {
						return InteractionResult.SUCCESS;
					}
				}
				//prevent buckets from placing their fluid in the world when clicking on the vessel
				if (heldItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
					return InteractionResult.CONSUME_PARTIAL;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return VESSEL_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
		return false;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return false;
	}

	@Override
	public boolean isFull(BlockState state) {
		return false;
	}

	@Override
	public boolean canReceiveStalactiteDrip(Fluid fluid) {
		return true;
	}

	@Override
	public void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
		if (level.getBlockEntity(pos) instanceof FluidVesselBlockEntity vesselEntity) {
			IFluidHandler cap = vesselEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).orElse(null);
			if (cap != null) {
				int amount = 333;
				if (fluid == Fluids.LAVA)
					amount = FluidType.BUCKET_VOLUME;

				cap.fill(new FluidStack(fluid, amount), FluidAction.EXECUTE);

				if (fluid.getFluidType().getTemperature() > 500) {
					level.levelEvent(LevelEvent.SOUND_DRIP_LAVA_INTO_CAULDRON, pos, 0);
				} else {
					level.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, pos, 0);
				}
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.FLUID_VESSEL_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? BaseEntityBlock.createTickerHelper(pBlockEntityType, RegistryManager.FLUID_VESSEL_ENTITY.get(), FluidVesselBlockEntity::clientTick) : null;
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
		List<ItemStack> items = super.getDrops(pState, pBuilder);
		BlockEntity blockentity = pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof FluidVesselBlockEntity) {
			CompoundTag nbt = blockentity.saveWithoutMetadata();
			if (nbt != null) {
				for (ItemStack stack : items) {
					if (stack.getItem() == RegistryManager.FLUID_VESSEL_ITEM.get()) {
						IFluidHandler cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
						if (cap != null) {
							cap.fill(FluidStack.loadFluidStackFromNBT(nbt), FluidAction.EXECUTE);
						}
					}
				}
			}
		}
		return items;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
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
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
