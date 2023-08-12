package com.rekindled.embers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rekindled.embers.api.block.IDial;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.blockentity.EmberEmitterBlockEntity;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.render.EmbersRenderTypes;
import com.rekindled.embers.render.PipeModel;
import com.rekindled.embers.util.EmberGenUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
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
	public static double gaugeAngle = 0;
	public static long seed = 0;
	public static BlockPos lastTarget = null;
	public static ResourceLocation GAUGE = new ResourceLocation(Embers.MODID, "textures/gui/ember_meter_overlay.png"); 
	public static ResourceLocation GAUGE_POINTER = new ResourceLocation(Embers.MODID, "textures/gui/ember_meter_pointer.png"); 

	public static void onClientTick(ClientTickEvent event) {
		if (event.side == LogicalSide.CLIENT && event.phase == Phase.START) {
			Minecraft mc = Minecraft.getInstance();
			if (!mc.isPaused()) {
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
	}

	//do not render the normal block highlight when the glowing highlight is drawn
	public static void onBlockHighlight(RenderHighlightEvent.Block event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui)
			return;
		Pair<BlockPos, Direction> target = Misc.getHammerTarget(mc.player);
		if (target != null && event.getTarget().getBlockPos().equals(target.getLeft())) {
			event.setCanceled(true);
		}
	}

	public static void onLevelRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.options.hideGui)
				return;

			Player player = mc.player;
			Pair<BlockPos, Direction> target = Misc.getHammerTarget(player);
			if (target != null && player.level().isLoaded(target.getLeft())) {
				BlockPos targetPos = target.getLeft();
				Direction targetDir = target.getRight();
				Vec3 camPos = event.getCamera().getPosition();
				VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(EmbersRenderTypes.GLOW_LINES);
				float red = 1.0F;
				float green = 0.25F + 0.5f*((float)Math.sin(Math.toRadians(4.0f*(event.getRenderTick() + event.getPartialTick())))+1.0f) * 0.25F;
				float blue = 0.062745F;
				float alpha = 0.8F;
				double x = targetPos.getX() - camPos.x;
				double y = targetPos.getY() - camPos.y;
				double z = targetPos.getZ() - camPos.z;
				PoseStack.Pose pose = event.getPoseStack().last();

				Shapes.DoubleLineConsumer lineDrawer = (fromX, fromY, fromZ, toX, toY, toZ) -> {
					float f = (float)(toX - fromX);
					float f1 = (float)(toY - fromY);
					float f2 = (float)(toZ - fromZ);
					float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
					f /= f3;
					f1 /= f3;
					f2 /= f3;
					consumer.vertex(pose.pose(), (float)(fromX + x), (float)(fromY + y), (float)(fromZ + z)).color(red, green, blue, alpha).normal(pose.normal(), f, f1, f2).endVertex();
					consumer.vertex(pose.pose(), (float)(toX+ x), (float)(toY + y), (float)(toZ + z)).color(red, green, blue, alpha).normal(pose.normal(), f, f1, f2).endVertex();
				};

				//LevelRenderer.renderShape(event.getPoseStack(), consumer, player.level.getBlockState(targetPos).getShape(player.level, targetPos), x, y, z, red, green, blue, alpha);
				player.level().getBlockState(targetPos).getShape(player.level(), targetPos).forAllEdges(lineDrawer);

				if (mc.hitResult instanceof BlockHitResult result && result != null && result.getType() == BlockHitResult.Type.BLOCK && !result.getBlockPos().equals(targetPos) && mc.level.getBlockEntity(result.getBlockPos()) instanceof IEmberPacketReceiver) {
					lastTarget = result.getBlockPos();
				}
				if (lastTarget != null) {
					Vec3 hitPos = Vec3.atCenterOf(lastTarget.subtract(targetPos));
					Vec3 motion = EmberEmitterBlockEntity.getBurstVelocity(targetDir);
					Vec3 oldPos = new Vec3(0.5, 0.5, 0.5);
					Vec3 newPos = oldPos.add(motion);

					for (int i = 0; i <= 80; ++i) {
						Vec3 targetVector = hitPos.subtract(newPos);
						double length = targetVector.length();
						targetVector = targetVector.scale(0.3 / length);
						double weight = 0;
						if (length <= 3) {
							weight = 0.9 * ((3.0 - length) / 3.0);
							if (length <= 0.2) {
								break;
							}
						}
						motion = new Vec3(
								(0.9 - weight) * motion.x + (0.1 + weight) * targetVector.x,
								(0.9 - weight) * motion.y + (0.1 + weight) * targetVector.y,
								(0.9 - weight) * motion.z + (0.1 + weight) * targetVector.z);
						newPos = oldPos.add(motion);
						lineDrawer.consume(oldPos.x, oldPos.y, oldPos.z, newPos.x, newPos.y, newPos.z);
						oldPos = newPos;
					}
				} else {
					lineDrawer.consume(0.5, 0.5, 0.5, 0.5 + targetDir.getStepX(), 0.5 + targetDir.getStepY(), 0.5 + targetDir.getStepZ());
				}
			} else {
				lastTarget = null;
			}
		}
	}

	public static void renderIngameOverlay(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height) {
		Minecraft mc = gui.getMinecraft();
		if (mc.options.hideGui)
			return;

		Player player = mc.player;

		if (mc.hitResult instanceof BlockHitResult result) {
			ClientLevel world = mc.level;
			if (result != null) {
				if (result.getType() == BlockHitResult.Type.BLOCK) {
					BlockPos pos = result.getBlockPos();
					BlockState state = world.getBlockState(pos);
					Direction facing = result.getDirection();
					List<String> text = new ArrayList<String>();

					if (state.getBlock() instanceof IDial) {
						text.addAll(((IDial) state.getBlock()).getDisplayInfo(world, result.getBlockPos(), state, (height / 2 - 100) / 11));
					} else if (Misc.isWearingLens(player)) {
						BlockEntity tileEntity = world.getBlockEntity(result.getBlockPos());
						if (tileEntity != null) {
							addCapabilityInformation(text, state, tileEntity, facing);
						}
					}
					if (!text.isEmpty()) {
						for (int i = 0; i < text.size(); i++) {
							graphics.drawString(mc.font, text.get(i), width / 2 - mc.font.width(text.get(i)) / 2, height / 2 + 40 + 11 * i, 0xFFFFFF);
						}
					}
				}
			}
		}

		if (player.getMainHandItem().getItem() == RegistryManager.ATMOSPHERIC_GAUGE.get() || player.getOffhandItem().getItem() == RegistryManager.ATMOSPHERIC_GAUGE.get()) {
			int x = width / 2;
			int y = height / 2;

			graphics.pose().pushPose();

			//int offsetX = 0;

			graphics.blit(GAUGE, x - 16, y - 16, 0, 0, 0, 32, 32, 32, 32);

			//double angle = 195.0;
			//EmberWorldData data = EmberWorldData.get(world);
			if (player != null) {
				//if (data.emberData != null){
				//if (data.emberData.containsKey(""+((int)player.posX) / 16 + " " + ((int)player.posZ) / 16)){
				double ratio = EmberGenUtil.getEmberDensity(seed, player.getBlockX(), player.getBlockZ());
				if (gaugeAngle == 0) {
					gaugeAngle = 165.0 + 210.0 * ratio;
				} else {
					gaugeAngle = gaugeAngle * 0.99 + 0.01 * (165.0 + 210.0 * ratio);
				}
				//}
				//}
			}

			graphics.pose().translate(x, y, 0);
			graphics.pose().mulPose(Axis.ZP.rotationDegrees((float) gaugeAngle));
			graphics.pose().translate(-2.5, -2.5, 0);

			graphics.blit(GAUGE_POINTER, 0, 0, 0, 0, 0, 12, 5, 16, 16);

			graphics.pose().popPose();
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

	public static final ModelResourceLocation ITEM_CENTER = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "item_pipe_center"), "");
	public static final ModelResourceLocation ITEM_EXTRACTOR = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "item_extractor_center"), "");
	public static final ModelResourceLocation ITEM_CONNECTION = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "item_pipe_connection"), "");
	public static final ModelResourceLocation ITEM_END = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "item_pipe_end"), "");
	public static final ModelResourceLocation ITEM_CONNECTION_2 = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "item_pipe_connection_opposite"), "");
	public static final ModelResourceLocation ITEM_END_2 = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "item_pipe_end_opposite"), "");

	public static final ModelResourceLocation FLUID_CENTER = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "fluid_pipe_center"), "");
	public static final ModelResourceLocation FLUID_EXTRACTOR = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "fluid_extractor_center"), "");
	public static final ModelResourceLocation FLUID_CONNECTION = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "fluid_pipe_connection"), "");
	public static final ModelResourceLocation FLUID_END = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "fluid_pipe_end"), "");
	public static final ModelResourceLocation FLUID_CONNECTION_2 = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "fluid_pipe_connection_opposite"), "");
	public static final ModelResourceLocation FLUID_END_2 = new ModelResourceLocation(new ResourceLocation(Embers.MODID, "fluid_pipe_end_opposite"), "");

	public static PipeModel itemPipe;
	public static PipeModel itemExtractor;
	public static PipeModel fluidPipe;
	public static PipeModel fluidExtractor;

	public static void onModelRegister(ModelEvent.RegisterAdditional event) {
		event.register(ITEM_CENTER);
		event.register(ITEM_EXTRACTOR);
		event.register(ITEM_CONNECTION);
		event.register(ITEM_END);
		event.register(ITEM_CONNECTION_2);
		event.register(ITEM_END_2);

		event.register(FLUID_CENTER);
		event.register(FLUID_EXTRACTOR);
		event.register(FLUID_CONNECTION);
		event.register(FLUID_END);
		event.register(FLUID_CONNECTION_2);
		event.register(FLUID_END_2);
	}

	public static void onModelBake(ModelEvent.ModifyBakingResult event) {
		Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
		itemPipe = new PipeModel(modelRegistry.get(ITEM_CENTER), "item_pipe");
		itemExtractor = new PipeModel(modelRegistry.get(ITEM_EXTRACTOR), "item_pipe");
		fluidPipe = new PipeModel(modelRegistry.get(FLUID_CENTER), "fluid_pipe");
		fluidExtractor = new PipeModel(modelRegistry.get(FLUID_EXTRACTOR), "fluid_pipe");
		for (ResourceLocation resourceLocation : event.getModels().keySet()) {
			if (resourceLocation.getNamespace().equals(Embers.MODID)) {
				if (resourceLocation.getPath().equals("item_pipe") && !resourceLocation.toString().contains("inventory")) {
					modelRegistry.put(resourceLocation, itemPipe);
				} else if (resourceLocation.getPath().equals("item_extractor") && !resourceLocation.toString().contains("inventory")) {
					modelRegistry.put(resourceLocation, itemExtractor);
				} else if (resourceLocation.getPath().equals("fluid_pipe") && !resourceLocation.toString().contains("inventory")) {
					modelRegistry.put(resourceLocation, fluidPipe);
				} else if (resourceLocation.getPath().equals("fluid_extractor") && !resourceLocation.toString().contains("inventory")) {
					modelRegistry.put(resourceLocation, fluidExtractor);
				}
			}
		}
	}

	public static void afterModelBake(ModelEvent.BakingCompleted event) {
		itemPipe.init(event.getModelManager());
		itemExtractor.init(event.getModelManager());
		fluidPipe.init(event.getModelManager());
		fluidExtractor.init(event.getModelManager());
	}
}