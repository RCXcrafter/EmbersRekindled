package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.CaminiteValveBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CaminiteValveEdgeBlock extends MechEdgeBlockBase implements EntityBlock {

	public static final VoxelShape NORTH_AABB = Shapes.or(Block.box(0,0,4,16,16,12), Block.box(1,0,3,7,16,13), Block.box(9,0,3,15,16,13), Block.box(4,4,1,12,12,15), Block.box(6,6,0,10,10,1));
	public static final VoxelShape SOUTH_AABB = Shapes.or(Block.box(0,0,4,16,16,12), Block.box(1,0,3,7,16,13), Block.box(9,0,3,15,16,13), Block.box(4,4,1,12,12,15), Block.box(6,6,15,10,10,16));
	public static final VoxelShape WEST_AABB = Shapes.or(Block.box(4,0,0,12,16,16), Block.box(3,0,1,13,16,7), Block.box(3,0,9,13,16,15), Block.box(1,4,4,15,12,12), Block.box(0,6,6,1,10,10));
	public static final VoxelShape EAST_AABB = Shapes.or(Block.box(4,0,0,12,16,16), Block.box(3,0,1,13,16,7), Block.box(3,0,9,13,16,15), Block.box(1,4,4,15,12,12), Block.box(15,6,6,16,10,10));
	public static final VoxelShape NORTHEAST_AABB = Shapes.or(Block.box(0,0,4,12,16,12), Block.box(4,0,4,12,16,16));
	public static final VoxelShape SOUTHEAST_AABB = Shapes.or(Block.box(0,0,4,12,16,12), Block.box(4,0,0,12,16,12));
	public static final VoxelShape SOUTHWEST_AABB = Shapes.or(Block.box(4,0,4,16,16,12), Block.box(4,0,0,12,16,12));
	public static final VoxelShape NORTHWEST_AABB = Shapes.or(Block.box(4,0,4,16,16,12), Block.box(4,0,4,12,16,16));
	public static final VoxelShape[] SHAPES = new VoxelShape[] { NORTH_AABB, NORTHEAST_AABB, EAST_AABB, SOUTHEAST_AABB, SOUTH_AABB, SOUTHWEST_AABB, WEST_AABB, NORTHWEST_AABB };
	public static final VoxelShape X_INTERACTION = Block.box(0,1,1,16,15,15);
	public static final VoxelShape Z_INTERACTION = Block.box(1,1,0,15,15,16);
	public static final VoxelShape[] INTERACTION_SHAPES = new VoxelShape[] { Z_INTERACTION, Shapes.empty(), X_INTERACTION, Shapes.empty(), Z_INTERACTION, Shapes.empty(), X_INTERACTION, Shapes.empty() };

	public CaminiteValveEdgeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof CaminiteValveBlockEntity valveEntity) {
			ItemStack heldItem = player.getItemInHand(hand);
			if (!heldItem.isEmpty() && valveEntity.getReservoir() != null) {
				IFluidHandler cap = valveEntity.getReservoir().getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null);
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
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(EDGE).index];
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return INTERACTION_SHAPES[state.getValue(EDGE).index];
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.CAMINITE_VALVE.get();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return pState.getValue(EDGE).corner ? null : RegistryManager.CAMINITE_VALVE_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pState.getValue(EDGE).corner ? null : BaseEntityBlock.createTickerHelper(pBlockEntityType, RegistryManager.CAMINITE_VALVE_ENTITY.get(), CaminiteValveBlockEntity::commonTick);
	}
}
