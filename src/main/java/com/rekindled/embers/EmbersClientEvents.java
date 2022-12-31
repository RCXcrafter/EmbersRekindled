package com.rekindled.embers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.api.block.IDial;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;

@OnlyIn(Dist.CLIENT)
public class EmbersClientEvents {

	public static final IGuiOverlay INGAME_OVERLAY = EmbersClientEvents::renderIngameOverlay;
	public static int ticks = 0;

	public static void onClientTick(ClientTickEvent event) {
		if (event.side == LogicalSide.CLIENT && event.phase == Phase.START) {
			Minecraft mc = Minecraft.getInstance();
			ticks++;

			if (mc.hitResult instanceof BlockHitResult result) {
				Level world = mc.level;
				if (result != null && world != null) {
					if (result.getType() == BlockHitResult.Type.BLOCK) {
						BlockState state = world.getBlockState(result.getBlockPos());
						if (state.getBlock() instanceof IDial) {
							((IDial) state.getBlock()).updateBEData(world, state, result.getBlockPos());
						}
					}
				}
			}
		}
	}

	public static void renderIngameOverlay(ForgeGui gui, PoseStack poseStack, float partialTicks, int width, int height) {
		Minecraft mc = gui.getMinecraft();
		if (mc.options.hideGui)
			return;

		if (mc.hitResult instanceof BlockHitResult result) {
			ClientLevel world = mc.level;
			if (result != null) {
				if (result.getType() == BlockHitResult.Type.BLOCK) {
					BlockPos pos = result.getBlockPos();
					BlockState state = world.getBlockState(pos);
					Direction facing = result.getDirection();
					List<String> text = new ArrayList<String>();

					if (state.getBlock() instanceof IDial) {
						text.addAll(((IDial) state.getBlock()).getDisplayInfo(world, result.getBlockPos(), state));
					} else if (Misc.isWearingLens(mc.player)) {
						BlockEntity tileEntity = world.getBlockEntity(result.getBlockPos());
						if (tileEntity != null) {
							addCapabilityInformation(text, state, tileEntity, facing);
						}
					} else {
						return;
					}

					poseStack.pushPose();
					for (int i = 0; i < text.size(); i++) {
						mc.font.drawShadow(poseStack, text.get(i), width/ 2 - mc.font.width(text.get(i)) / 2, height / 2 + 40 + 11 * i, 0xFFFFFF);
						mc.font.draw(poseStack, text.get(i), width/ 2 - mc.font.width(text.get(i)) / 2, height / 2 + 40 + 11 * i, 0xFFFFFF);
					}
					poseStack.popPose();
				}
			}
		}
	}

	private static void addCapabilityInformation(List<String> text, BlockState state, BlockEntity tile, Direction facing) {
		addCapabilityItemDescription(text, tile, facing);
		addCapabilityFluidDescription(text, tile, facing);
		addCapabilityEmberDescription(text, tile, facing);
		//if (ConfigManager.isMysticalMechanicsIntegrationEnabled())
		//MysticalMechanicsIntegration.addCapabilityInformation(text, tile, facing);
		if (tile.getCapability(EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY, facing).isPresent())
			text.add(I18n.get(Embers.MODID + ".tooltip.goggles.upgrade"));
		if (state.is(EmbersBlockTags.MECH_CORE_PROXYABLE))
			text.add(I18n.get(Embers.MODID + ".tooltip.goggles.accessor_slot"));
		if (tile instanceof IMechanicallyPowered)
			text.add(I18n.get(Embers.MODID + ".tooltip.goggles.actuator_slot"));
		if (tile instanceof IExtraCapabilityInformation)
			((IExtraCapabilityInformation) tile).addOtherDescription(text, facing);
	}

	public static void addCapabilityItemDescription(List<String> text, BlockEntity tile, Direction facing) {
		Capability<IItemHandler> capability = ForgeCapabilities.ITEM_HANDLER;
		if (tile.getCapability(capability, facing).isPresent()) {
			IExtraCapabilityInformation.EnumIOType ioType = IExtraCapabilityInformation.EnumIOType.BOTH;
			String filter = null;
			if (tile instanceof IExtraCapabilityInformation && ((IExtraCapabilityInformation) tile).hasCapabilityDescription(capability)) {
				((IExtraCapabilityInformation) tile).addCapabilityDescription(text, capability, facing);
			} else {
				text.add(IExtraCapabilityInformation.formatCapability(ioType, Embers.MODID + ".tooltip.goggles.item", filter));
			}
		}
	}

	public static void addCapabilityFluidDescription(List<String> text, BlockEntity tile, Direction facing) {
		Capability<IFluidHandler> capability = ForgeCapabilities.FLUID_HANDLER;
		if (tile.getCapability(capability, facing).isPresent()) {
			IExtraCapabilityInformation.EnumIOType ioType = IExtraCapabilityInformation.EnumIOType.BOTH;
			String filter = null;
			if (tile instanceof IExtraCapabilityInformation && ((IExtraCapabilityInformation) tile).hasCapabilityDescription(capability)) {
				((IExtraCapabilityInformation) tile).addCapabilityDescription(text, capability, facing);
			} else {
				//fluid handlers no longer tell you if you can insert or remove fluids anymore

				/*IFluidHandler handler = tile.getCapability(capability, facing).orElse(null);
				for (IFluidTankProperties properties : handler.getTankProperties()) {
					boolean input = properties.canFill();
					boolean output = properties.canDrain();
					if (!input && !output)
						ioType = IExtraCapabilityInformation.EnumIOType.NONE;
					else if (input && !output)
						ioType = IExtraCapabilityInformation.EnumIOType.INPUT;
					else if (output && !input)
						ioType = IExtraCapabilityInformation.EnumIOType.OUTPUT;
				}*/
				text.add(IExtraCapabilityInformation.formatCapability(ioType, Embers.MODID + ".tooltip.goggles.fluid", filter));
			}

		}
	}

	public static void addCapabilityEmberDescription(List<String> text, BlockEntity tile, Direction facing) {
		Capability<IEmberCapability> capability = EmbersCapabilities.EMBER_CAPABILITY;
		if (tile.getCapability(capability, facing).isPresent()) {
			IExtraCapabilityInformation.EnumIOType ioType = IExtraCapabilityInformation.EnumIOType.BOTH;
			if (tile instanceof IExtraCapabilityInformation && ((IExtraCapabilityInformation) tile).hasCapabilityDescription(capability)) {
				((IExtraCapabilityInformation) tile).addCapabilityDescription(text, capability, facing);
			} else {
				text.add(IExtraCapabilityInformation.formatCapability(ioType, Embers.MODID + ".tooltip.goggles.ember", null));
			}
		}
	}
}