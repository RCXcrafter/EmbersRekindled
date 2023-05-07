package com.rekindled.embers;

import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.apiimpl.UpgradeUtilImpl;
import com.rekindled.embers.blockentity.render.BinBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CopperChargerBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CrystalCellBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.EmberBoreBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.FluidVesselBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.GeologicSeparatorBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MelterTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MixerCentrifugeBottomBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MixerCentrifugeTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.ReservoirBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.StampBaseBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.StamperBlockEntityRenderer;
import com.rekindled.embers.datagen.EmbersBiomeModifiers;
import com.rekindled.embers.datagen.EmbersBlockStates;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersConfiguredFeatures;
import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.datagen.EmbersItemModels;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.datagen.EmbersLang;
import com.rekindled.embers.datagen.EmbersLootTables;
import com.rekindled.embers.datagen.EmbersPlacedFeatures;
import com.rekindled.embers.datagen.EmbersRecipes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.AncientGolemEntity;
import com.rekindled.embers.entity.render.AncientGolemRenderer;
import com.rekindled.embers.entity.render.EmberPacketRenderer;
import com.rekindled.embers.entity.render.EmberProjectileRenderer;
import com.rekindled.embers.item.EmberStorageItem;
import com.rekindled.embers.model.AncientGolemModel;
import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.network.message.MessageWorldSeed;
import com.rekindled.embers.particle.GlowParticle;
import com.rekindled.embers.particle.SmokeParticle;
import com.rekindled.embers.particle.SparkParticle;
import com.rekindled.embers.particle.StarParticle;
import com.rekindled.embers.particle.VaporParticle;
import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.capability.IResearchCapability;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;

@Mod(Embers.MODID)
public class Embers {

	public static final String MODID = "embersrekindled";

	public static final CreativeModeTab TAB_EMBERS = new CreativeModeTab(Embers.MODID) {
		public ItemStack makeIcon() {
			return new ItemStack(RegistryManager.EMBER_CRYSTAL.get());
		}
	};

	public Embers() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::gatherData);
		modEventBus.addListener(this::registerCaps);
		modEventBus.addListener(this::entityAttributes);

		RegistryManager.BLOCKS.register(modEventBus);
		RegistryManager.ITEMS.register(modEventBus);
		RegistryManager.FLUIDTYPES.register(modEventBus);
		RegistryManager.FLUIDS.register(modEventBus);
		RegistryManager.ENTITY_TYPES.register(modEventBus);
		RegistryManager.BLOCK_ENTITY_TYPES.register(modEventBus);
		RegistryManager.PARTICLE_TYPES.register(modEventBus);
		RegistryManager.SOUND_EVENTS.register(modEventBus);
		RegistryManager.RECIPE_TYPES.register(modEventBus);
		RegistryManager.RECIPE_SERIALIZERS.register(modEventBus);
		if (FMLLoader.getLaunchHandler().isData()) {
			EmbersConfiguredFeatures.CONFIGURED_FEATURES.register(modEventBus);
			EmbersPlacedFeatures.PLACED_FEATURES.register(modEventBus);
		}
		EmbersSounds.init();
		//TODO: move this to apiimpl when I port that
		UpgradeUtil.IMPL = new UpgradeUtilImpl();

		ConfigManager.register();
	}

	public void commonSetup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
		RegistryManager.registerDispenserBehaviour(event);
		MinecraftForge.EVENT_BUS.addListener(ResearchManager::onJoin);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ResearchManager::attachCapability);
		MinecraftForge.EVENT_BUS.addListener(ResearchManager::onClone);
		ResearchManager.initResearches();
		MinecraftForge.EVENT_BUS.addListener(Embers::onJoin);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TagsUpdatedEvent.class, e -> Misc.tagItems.clear());
	}

	public void registerCaps(RegisterCapabilitiesEvent event) {
		event.register(IEmberCapability.class);
		event.register(IResearchCapability.class);
	}

	public void entityAttributes(EntityAttributeCreationEvent event) {
		event.put(RegistryManager.ANCIENT_GOLEM.get(), AncientGolemEntity.createAttributes().build());
	}

	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		if (event.includeClient()) {
			gen.addProvider(true, new EmbersLang(gen));
			ItemModelProvider itemModels = new EmbersItemModels(gen, existingFileHelper);
			gen.addProvider(true, itemModels);
			gen.addProvider(true, new EmbersBlockStates(gen, existingFileHelper));
			gen.addProvider(true, new EmbersSounds(gen, existingFileHelper));
		} if (event.includeServer()) {
			gen.addProvider(true, new EmbersLootTables(gen));
			gen.addProvider(true, new EmbersRecipes(gen));
			BlockTagsProvider blockTags = new EmbersBlockTags(gen, existingFileHelper);
			gen.addProvider(true, blockTags);
			gen.addProvider(true, new EmbersItemTags(gen, blockTags, existingFileHelper));
			gen.addProvider(true, new EmbersFluidTags(gen, existingFileHelper));
			gen.addProvider(true, new EmbersConfiguredFeatures(gen, existingFileHelper));
			gen.addProvider(true, new EmbersPlacedFeatures(gen, existingFileHelper));
			gen.addProvider(true, new EmbersBiomeModifiers(gen, existingFileHelper));
		}
	}

	public static void onJoin(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof ServerPlayer && !event.getLevel().isClientSide()) {
			ServerPlayer player = (ServerPlayer) event.getEntity();
			PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageWorldSeed(((ServerLevel) event.getLevel()).getSeed()));
		}
	}

	@Mod.EventBusSubscriber(modid = Embers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModEvents {

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			MinecraftForge.EVENT_BUS.addListener(EmbersClientEvents::onClientTick);
			MinecraftForge.EVENT_BUS.addListener(EmbersClientEvents::onLevelRender);
			EntityRenderers.register(RegistryManager.EMBER_PACKET.get(), EmberPacketRenderer::new);
			EntityRenderers.register(RegistryManager.EMBER_PROJECTILE.get(), EmberProjectileRenderer::new);
			EntityRenderers.register(RegistryManager.ANCIENT_GOLEM.get(), AncientGolemRenderer::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void overlayRegister(RegisterGuiOverlaysEvent event) {
			event.registerAboveAll("embers_ingame_overlay", EmbersClientEvents.INGAME_OVERLAY);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void addResourceListener(RegisterClientReloadListenersEvent event) {
			event.registerReloadListener(new DecimalFormats());
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void addParticleProvider(RegisterParticleProvidersEvent event) {
			event.register(RegistryManager.GLOW_PARTICLE.get(), GlowParticle.Provider::new);
			event.register(RegistryManager.STAR_PARTICLE.get(), StarParticle.Provider::new);
			event.register(RegistryManager.SPARK_PARTICLE.get(), SparkParticle.Provider::new);
			event.register(RegistryManager.SMOKE_PARTICLE.get(), SmokeParticle.Provider::new);
			event.register(RegistryManager.VAPOR_PARTICLE.get(), VaporParticle.Provider::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerBlockEntityRenderer(RegistryManager.EMBER_BORE_ENTITY.get(), EmberBoreBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MELTER_TOP_ENTITY.get(), MelterTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.FLUID_VESSEL_ENTITY.get(), FluidVesselBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.STAMPER_ENTITY.get(), StamperBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.STAMP_BASE_ENTITY.get(), StampBaseBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.BIN_ENTITY.get(), BinBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), MixerCentrifugeBottomBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), MixerCentrifugeTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.RESERVOIR_ENTITY.get(), ReservoirBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.CRYSTAL_CELL_ENTITY.get(), CrystalCellBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.GEOLOGIC_SEPARATOR_ENTITY.get(), GeologicSeparatorBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.COPPER_CHARGER_ENTITY.get(), CopperChargerBlockEntityRenderer::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(AncientGolemRenderer.LAYER_LOCATION, AncientGolemModel::createLayer);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
			event.register(new EmberStorageItem.ColorHandler(), RegistryManager.EMBER_JAR.get(), RegistryManager.EMBER_CARTRIDGE.get());
		}
	}
}
