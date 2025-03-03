package com.rekindled.embers.blockentity;

import java.util.Random;

import org.joml.Vector3f;

import com.rekindled.embers.api.block.IPipeConnection;
import com.rekindled.embers.block.PipeBlockBase;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class PipeBlockEntityBase extends BlockEntity {

	public static final int PRIORITY_BLOCK = 0;
	public static final int PRIORITY_PIPE = PRIORITY_BLOCK;
	static Random random = new Random();

	public PipeConnection[] connections = {
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE,
			PipeConnection.NONE
	};

	public boolean[] from = new boolean[Direction.values().length]; //just in case they like make minecraft 4 dimensional or something
	public boolean clogged = false;
	public Direction lastTransfer;
	public int ticksExisted;
	public int lastRobin;

	public boolean loaded = false;
	public boolean saveConnections = true;
	public boolean syncConnections = true;
	public boolean syncCloggedFlag = true;
	public boolean syncTransfer = true;

	public static final ModelProperty<int[]> DATA_TYPE = new ModelProperty<int[]>();

	public PipeBlockEntityBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	public void setFrom(Direction facing, boolean flag) {
		from[facing.get3DDataValue()] = flag;
	}

	public void resetFrom() {
		for (Direction facing : Direction.values()) {
			setFrom(facing, false);
		}
	}

	protected boolean isFrom(Direction facing) {
		return from[facing.get3DDataValue()];
	}

	protected boolean isAnySideUnclogged() {
		for (Direction facing : Direction.values()) {
			if (!getConnection(facing).transfer)
				continue;
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(facing));
			if (tile instanceof PipeBlockEntityBase && !((PipeBlockEntityBase) tile).clogged)
				return true;
		}
		return false;
	}

	public void initConnections() {
		Block block = level.getBlockState(worldPosition).getBlock();
		for (Direction direction : Direction.values()) {
			if (block instanceof PipeBlockBase pipeBlock) {
				BlockState facingState = level.getBlockState(worldPosition.relative(direction));
				BlockEntity facingBE = level.getBlockEntity(worldPosition.relative(direction));
				if (!(facingBE instanceof PipeBlockEntityBase) || ((PipeBlockEntityBase) facingBE).getConnection(direction.getOpposite()) != PipeConnection.DISABLED) {
					if (facingState.is(pipeBlock.getConnectionTag())) {
						if (facingBE instanceof PipeBlockEntityBase && ((PipeBlockEntityBase) facingBE).getConnection(direction.getOpposite()) == PipeConnection.DISABLED) {
							connections[direction.get3DDataValue()] = PipeConnection.DISABLED;
						} else {
							connections[direction.get3DDataValue()] = PipeConnection.PIPE;
						}
					} else {
						if (pipeBlock.connected(direction, facingState)) {
							connections[direction.get3DDataValue()] = PipeConnection.LEVER;
						} else if (pipeBlock.connectToTile(facingBE, direction)) {
							if (facingState.getBlock() instanceof IPipeConnection) {
								connections[direction.get3DDataValue()] = ((IPipeConnection) facingState.getBlock()).getPipeConnection(facingState, direction.getOpposite());
							} else {
								connections[direction.get3DDataValue()] = PipeConnection.END;
							}
						} else {
							connections[direction.get3DDataValue()] = PipeConnection.NONE;
						}
					}
				}
			}
		}
		syncConnections = true;
		Misc.sendToTrackingPlayers(level, worldPosition, getUpdatePacket());
		loaded = true;
		setChanged();
		level.getChunkAt(worldPosition).setUnsaved(true);
		level.updateNeighbourForOutputSignal(worldPosition, block);
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
		syncConnections = true;
		requestModelDataUpdate();
		setChanged();
	}

	public PipeConnection getConnection(Direction direction) {
		return connections[direction.get3DDataValue()];
	}

	public void setConnections(PipeConnection[] connections) {
		this.connections = connections;
		syncConnections = true;
		requestModelDataUpdate();
		setChanged();
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientTick(Level level, BlockPos pos, BlockState state, PipeBlockEntityBase blockEntity) {
		if (blockEntity.lastTransfer != null && Misc.isWearingLens(Minecraft.getInstance().player)) {
			float vx = blockEntity.lastTransfer.getStepX() / 1;
			float vy = blockEntity.lastTransfer.getStepY() / 1;
			float vz = blockEntity.lastTransfer.getStepZ() / 1;
			double x = pos.getX() + 0.4f + random.nextFloat() * 0.2f;
			double y = pos.getY() + 0.4f + random.nextFloat() * 0.2f;
			double z = pos.getZ() + 0.4f + random.nextFloat() * 0.2f;
			float r = blockEntity.clogged ? 255f : 16f;
			float g = blockEntity.clogged ? 16f : 255f;
			float b = 16f;
			for (int i = 0; i < 3; i++) {
				level.addParticle(new GlowParticleOptions(new Vector3f(r / 255.0F, g / 255.0F, b / 255.0F), new Vec3(vx, vy, vz), 2.0f), x, y, z, vx, vy, vz);
			}
		}
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		if (level.isClientSide()) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
		}
	}

	protected void resetSync() {
		syncCloggedFlag = false;
		syncTransfer = false;
		syncConnections = false;
	}

	protected boolean requiresSync() {
		return syncCloggedFlag || syncTransfer || syncConnections || !loaded;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("clogged"))
			clogged = nbt.getBoolean("clogged");
		if (nbt.contains("lastTransfer"))
			lastTransfer = Misc.readNullableFacing(nbt.getInt("lastTransfer"));
		for (Direction facing : Direction.values())
			if (nbt.contains("from" + facing.get3DDataValue()))
				from[facing.get3DDataValue()] = nbt.getBoolean("from" + facing.get3DDataValue());
		if (nbt.contains("lastRobin"))
			lastRobin = nbt.getInt("lastRobin");
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
		if (saveConnections)
			writeConnections(nbt);
		writeCloggedFlag(nbt);
		writeLastTransfer(nbt);
		for (Direction facing : Direction.values())
			nbt.putBoolean("from" + facing.get3DDataValue(), from[facing.get3DDataValue()]);
		nbt.putInt("lastRobin", lastRobin);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		if (saveConnections)
			writeConnections(nbt);
		if (syncCloggedFlag)
			writeCloggedFlag(nbt);
		if (syncTransfer)
			writeLastTransfer(nbt);
		return nbt;
	}

	public void writeConnections(CompoundTag nbt) {
		for (Direction direction : Direction.values()) {
			nbt.putInt("connection" + direction.get3DDataValue(), getConnection(direction).index);
		}
	}

	public void writeCloggedFlag(CompoundTag nbt) {
		nbt.putBoolean("clogged", clogged);
	}

	public void writeLastTransfer(CompoundTag nbt) {
		nbt.putInt("lastTransfer", Misc.writeNullableFacing(lastTransfer));
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		if (requiresSync()) {
			Packet<ClientGamePacketListener> packet = ResettingBEDataPacket.create(this);
			return packet;
		}
		return null;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	public class ResettingBEDataPacket extends ClientboundBlockEntityDataPacket {

		public ResettingBEDataPacket(FriendlyByteBuf pBuffer) {
			super(pBuffer);
		}

		public void handle(ClientGamePacketListener pHandler) {
			super.handle(pHandler);
			resetSync();
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
