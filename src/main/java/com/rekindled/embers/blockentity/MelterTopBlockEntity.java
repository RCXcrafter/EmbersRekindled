package com.rekindled.embers.blockentity;

import java.util.List;
import java.util.Random;

import org.joml.Vector3f;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class MelterTopBlockEntity extends OpenTankBlockEntity implements IExtraCapabilityInformation {

	public static int capacity = FluidType.BUCKET_VOLUME * 4;
	public double angle = 0;
	int ticksExisted = 0;
	public float renderOffset;
	int previousFluid;

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			MelterTopBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);

	public MelterTopBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MELTER_TOP_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(capacity) {
			@Override
			public void onContentsChanged() {
				MelterTopBlockEntity.this.setChanged();
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if(Misc.isGaseousFluid(resource)) {
					MelterTopBlockEntity.this.setEscapedFluid(resource);
					return resource.getAmount();
				}
				int filled = super.fill(resource, action);
				return filled;
			}
		};
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.put("inventory", inventory.serializeNBT());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public int getCapacity(){
		return tank.getCapacity();
	}

	public FluidStack getFluidStack() {
		return tank.getFluid();
	}

	public FluidTank getTank() {
		return tank;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MelterTopBlockEntity blockEntity) {
		blockEntity.ticksExisted ++;
		if (blockEntity.ticksExisted % 10 == 0){

			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,pos.getY()+0.5,pos.getZ()+1));
			for (int i = 0; i < items.size(); i ++){
				ItemStack stack = blockEntity.inventory.insertItem(0, items.get(i).getItem(), false);
				if (!stack.isEmpty()){
					items.get(i).setItem(stack);
				} else {
					items.get(i).remove(RemovalReason.DISCARDED);
				}
			}
		}
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, MelterTopBlockEntity blockEntity) {
		blockEntity.angle++;

		//I know I'm supposed to use onLoad for stuff on the first tick but the tank isn't synced to the client yet when that happens
		//also I have the angle thing anyways
		if (blockEntity.angle == 1)
			blockEntity.previousFluid = blockEntity.tank.getFluidAmount();
		if (blockEntity.tank.getFluidAmount() != blockEntity.previousFluid) {
			blockEntity.renderOffset = blockEntity.renderOffset + blockEntity.tank.getFluidAmount() - blockEntity.previousFluid;
			blockEntity.previousFluid = blockEntity.tank.getFluidAmount();
		}

		if (blockEntity.shouldEmitParticles())
			blockEntity.updateEscapeParticles();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
			return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		holder.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel serverLevel) {
			for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(this.getUpdatePacket());
			}
		}
	}

	@SuppressWarnings("resource")
	@Override
	protected void updateEscapeParticles() {
		Vector3f color = IClientFluidTypeExtensions.of(lastEscaped.getFluid().getFluidType()).modifyFogColor(Minecraft.getInstance().gameRenderer.getMainCamera(), 0, (ClientLevel) this.level, 6, 0, new Vector3f(1, 1, 1));
		Random random = new Random();
		for (int i = 0; i < 3; i++) {
			float xOffset = 0.5f + (random.nextFloat() - 0.5f) * 2 * 0.2f;
			float yOffset = 0.9f;
			float zOffset = 0.5f + (random.nextFloat() - 0.5f) * 2 * 0.2f;
			level.addParticle(new VaporParticleOptions(color, 2.0f), worldPosition.getX() + xOffset, worldPosition.getY() + yOffset, worldPosition.getZ() + zOffset, 0, 1 / 5f, 0);
		}
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.FLUID_HANDLER || capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<String> strings, Capability<?> capability, Direction facing) {
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", null));
		if(capability == ForgeCapabilities.FLUID_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.fluid", I18n.get(Embers.MODID + ".tooltip.goggles.fluid.metal")));
	}
}
