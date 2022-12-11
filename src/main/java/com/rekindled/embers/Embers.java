package com.rekindled.embers;

import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.datagen.EmbersBlockStates;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.datagen.EmbersItemModels;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.datagen.EmbersLang;
import com.rekindled.embers.datagen.EmbersLootTables;
import com.rekindled.embers.datagen.EmbersRecipes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.render.EmberPacketRenderer;
import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.particle.GlowParticle;
import com.rekindled.embers.particle.SmokeParticle;
import com.rekindled.embers.particle.SparkParticle;
import com.rekindled.embers.particle.StarParticle;
import com.rekindled.embers.particle.VaporParticle;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Embers.MODID)
public class Embers {

	public static final String MODID = "embersrekindled";

	public static final CreativeModeTab TAB_EMBERS = new CreativeModeTab(Embers.MODID) {
		public ItemStack makeIcon() {
			return new ItemStack(RegistryManager.TINKER_HAMMER.get());
		}
	};

	public Embers() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::gatherData);
		modEventBus.addListener(this::registerCaps);

		RegistryManager.BLOCKS.register(modEventBus);
		RegistryManager.ITEMS.register(modEventBus);
		RegistryManager.FLUIDTYPES.register(modEventBus);
		RegistryManager.FLUIDS.register(modEventBus);
		RegistryManager.ENTITY_TYPES.register(modEventBus);
		RegistryManager.BLOCK_ENTITY_TYPES.register(modEventBus);
		RegistryManager.PARTICLE_TYPES.register(modEventBus);
		RegistryManager.SOUND_EVENTS.register(modEventBus);
	}

	public void commonSetup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
		RegistryManager.registerDispenserBehaviour(event);
	}

	public void registerCaps(RegisterCapabilitiesEvent event) {
		event.register(IEmberCapability.class);
	}

	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();

		if (event.includeClient()) {
			gen.addProvider(true, new EmbersLang(gen));
			ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
			ItemModelProvider itemModels = new EmbersItemModels(gen, existingFileHelper);
			gen.addProvider(true, itemModels);
			gen.addProvider(true, new EmbersBlockStates(gen, existingFileHelper));
			gen.addProvider(true, new EmbersSounds(gen, existingFileHelper));
		} if (event.includeServer()) {
			gen.addProvider(true, new EmbersLootTables(gen));
			gen.addProvider(true, new EmbersRecipes(gen));
			BlockTagsProvider blockTags = new EmbersBlockTags(gen, event.getExistingFileHelper());
			gen.addProvider(true, blockTags);
			gen.addProvider(true, new EmbersItemTags(gen, blockTags, event.getExistingFileHelper()));
			gen.addProvider(true, new EmbersFluidTags(gen, event.getExistingFileHelper()));
		}
	}

	@Mod.EventBusSubscriber(modid = Embers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModEvents {

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			MinecraftForge.EVENT_BUS.addListener(EmbersClientEvents::onClientTick);
			EntityRenderers.register(RegistryManager.EMBER_PACKET.get(), EmberPacketRenderer::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void overlayRegister(RegisterGuiOverlaysEvent event) {
			event.registerAboveAll("dial_overlay", EmbersClientEvents::renderDialOverlay);
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
	}
}
