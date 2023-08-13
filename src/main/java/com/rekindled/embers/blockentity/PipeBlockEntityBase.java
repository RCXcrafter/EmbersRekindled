package com.rekindled.embers.blockentity;

import com.rekindled.embers.api.block.IPipeConnection;
import com.rekindled.embers.block.PipeBlockBase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class PipeBlockEntityBase extends BlockEntity {

	public PipeConnection[] connections = {
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE
	};

	boolean loaded = false;

	public static final ModelProperty<int[]> DATA_TYPE = new ModelProperty<int[]>();

	public PipeBlockEntityBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	public void initConnections() {
		for (Direction direction : Direction.values()) {
			Block block = level.getBlockState(worldPosition).getBlock();
			if (block instanceof PipeBlockBase pipeBlock) {
				BlockState facingState = level.getBlockState(worldPosition.relative(direction));
				BlockEntity facingBE = level.getBlockEntity(worldPosition.relative(direction));
				if (!(facingBE instanceof PipeBlockEntityBase) || ((PipeBlockEntityBase) facingBE).getConnection(direction.getOpposite()) != PipeConnection.DISABLED) {
					if (facingState.is(pipeBlock.getConnectionTag())) {
						if (facingBE instanceof PipeBlockEntityBase && ((PipeBlockEntityBase) facingBE).getConnection(direction.getOpposite()) == PipeConnection.DISABLED
								|| facingState.getBlock() instanceof IPipeConnection && !((IPipeConnection) facingState.getBlock()).connectPipe(facingState, direction.getOpposite())) {
							connections[direction.get3DDataValue()] = PipeConnection.DISABLED;
						} else {
							connections[direction.get3DDataValue()] = PipeConnection.PIPE;
						}
					} else {
						if (pipeBlock.connected(direction, facingState)) {
							connections[direction.get3DDataValue()] = PipeConnection.LEVER;
						} else if (pipeBlock.connectToTile(facingBE, direction)) {
							connections[direction.get3DDataValue()] = PipeConnection.END;
						} else {
							connections[direction.get3DDataValue()] = PipeConnection.NONE;
						}
					}
				}
			}
			level.getChunkAt(worldPosition).setUnsaved(true);


			level.updateNeighbourForOutputSignal(worldPosition, block);
		}
		loaded = true;
		setChanged();
	}

	@Override
	public ModelData getModelData() {
		int[] data = {
				connections[0].visualIndex,
				connections[1].visualIndex,
				connections[2].visualIndex,
				connections[3].visualIndex,
				connections[4].visualIndex,
				connections[5].visualIndex
		};
		return ModelData.builder().with(DATA_TYPE, data).build();
	}

	public void setConnection(Direction direction, PipeConnection connection) {
		connections[direction.get3DDataValue()] = connection;
		requestModelDataUpdate();
		setChanged();
	}

	public PipeConnection getConnection(Direction direction) {
		return connections[direction.get3DDataValue()];
	}

	public void setConnections(PipeConnection[] connections) {
		this.connections = connections;
		requestModelDataUpdate();
		setChanged();
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		if (level.isClientSide()) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		loadConnections(nbt);
		loaded = true;
	}

	public void loadConnections(CompoundTag nbt) {
		for (Direction direction : Direction.values()) {
			if (nbt.contains("connection" + direction.get3DDataValue()))
				connections[direction.get3DDataValue()] = PipeConnection.values()[nbt.getInt("connection" + direction.get3DDataValue())];
		}
		requestModelDataUpdate();
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		writeConnections(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		writeConnections(nbt);
		return nbt;
	}

	public void writeConnections(CompoundTag nbt) {
		for (Direction direction : Direction.values()) {
			nbt.putInt("connection" + direction.get3DDataValue(), getConnection(direction).index);
		}
	}

	public static enum PipeConnection implements StringRepresentable {
		NONE("none", 0, 0, false),
		DISABLED("disabled", 1, 0, false),
		PIPE("pipe", 2, 1, true),
		END("end", 3, 2, true),
		LEVER("lever", 4, 2, false);

		private final String name;
		public final int index;
		public final int visualIndex;
		public final boolean transfer;
		public static final PipeConnection[] visualValues = {
				NONE,
				PIPE,
				END
		};

		private PipeConnection(String name, int index, int visualIndex, boolean transfer) {
			this.name = name;
			this.index = index;
			this.visualIndex = visualIndex;
			this.transfer = transfer;
		}

		public static PipeConnection[] visual() {
			return visualValues;
		}

		public String toString() {
			return this.name;
		}

		public String getSerializedName() {
			return this.name;
		}
	}
}
