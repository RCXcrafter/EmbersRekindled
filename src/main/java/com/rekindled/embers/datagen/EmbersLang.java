package com.rekindled.embers.datagen;

import java.util.function.Supplier;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;

import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class EmbersLang extends LanguageProvider {

	public EmbersLang(DataGenerator gen) {
		super(gen, Embers.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		for (FluidStuff fluid : RegistryManager.fluidList) {
			addBlock(fluid.FLUID_BLOCK, fluid.localizedName);
			addFluid(fluid.name, fluid.localizedName);
			addItem(fluid.FLUID_BUCKET, fluid.localizedName + " Bucket");
		}

		add("itemGroup." + Embers.MODID, "Embers Rekindled");

		addBlock(RegistryManager.COPPER_CELL, "Copper Cell");
		addBlock(RegistryManager.CREATIVE_EMBER, "Creative Ember Source");
		addBlock(RegistryManager.EMBER_DIAL, "Ember Dial");
		addBlock(RegistryManager.EMBER_EMITTER, "Ember Emitter");
		addBlock(RegistryManager.EMBER_RECEIVER, "Ember Receiver");
		addBlock(RegistryManager.CAMINITE_LEVER, "Caminite Lever");
		addBlock(RegistryManager.ITEM_PIPE, "Item Pipe");
		addBlock(RegistryManager.ITEM_EXTRACTOR, "Item Extractor");

		addItem(RegistryManager.TINKER_HAMMER, "Tinker's Hammer");

		add(Embers.MODID + ".decimal_format.ember", "0.#");
		add(Embers.MODID + ".tooltip.emberdial.ember", "Ember: %s/%s");
		add(Embers.MODID + ".tooltip.aiming_block", "Aiming: %s");
		add(Embers.MODID + ".tooltip.item.ember", "Ember: %s/%s");


		//subtitles
		addSubtitle(RegistryManager.ALCHEMY_FAIL, "Energetic Alchemy fails...");
		addSubtitle(RegistryManager.ALCHEMY_SUCCESS, "Energetic Alchemy succeeds!");
		addSubtitle(RegistryManager.ALCHEMY_START, "Energetic Alchemy begins");

		addSubtitle(RegistryManager.BEAM_CANNON_FIRE, "Beam Cannon fires");
		addSubtitle(RegistryManager.BEAM_CANNON_HIT, "Beam impacts");

		addSubtitle(RegistryManager.CRYSTAL_CELL_GROW, "Crystal Cell grows");

		addSubtitle(RegistryManager.ACTIVATOR, "Ember Activator releases Ember");
		addSubtitle(RegistryManager.PRESSURE_REFINERY, "Pressure Refinery releases Ember");
		addSubtitle(RegistryManager.IGNEM_REACTOR, "Ignem Reactor releases Ember");

		addSubtitle(RegistryManager.BORE_START, "Ember Bore rumbles to life");
		addSubtitle(RegistryManager.BORE_STOP, "Ember Bore sighs and stops");

		addSubtitle(RegistryManager.CATALYTIC_PLUG_START, "Catalytic Plug begins injecting catalyst");
		addSubtitle(RegistryManager.CATALYTIC_PLUG_STOP, "Catalytic Plug is empty");

		addSubtitle(RegistryManager.STAMPER_DOWN, "Stamp slams down");
		addSubtitle(RegistryManager.STAMPER_UP, "Stamp recedes");

		addSubtitle(RegistryManager.METAL_SEED_PING, "Metal Seed splits off pieces");

		addSubtitle(RegistryManager.INFERNO_FORGE_FAIL, "Reforging fails...");
		addSubtitle(RegistryManager.INFERNO_FORGE_SUCCESS, "Reforging succeeds!");
		addSubtitle(RegistryManager.INFERNO_FORGE_START, "Inferno Forge begins forging");
		addSubtitle(RegistryManager.INFERNO_FORGE_OPEN, "Inferno Forge opens");
		addSubtitle(RegistryManager.INFERNO_FORGE_CLOSE, "Inferno Forge closes");

		addSubtitle(RegistryManager.EMBER_EMIT, "Ember packet emitted");
		addSubtitle(RegistryManager.EMBER_EMIT_BIG, "Big Ember packet emitted");
		addSubtitle(RegistryManager.EMBER_RECEIVE, "Ember packet received");
		addSubtitle(RegistryManager.EMBER_RECEIVE_BIG, "Big Ember packet received");
		addSubtitle(RegistryManager.EMBER_RELAY, "Ember packet relayed");

		addSubtitle(RegistryManager.STEAM_ENGINE_START_STEAM, "Steam Engine spins up");
		addSubtitle(RegistryManager.STEAM_ENGINE_START_BURN, "Steam Engine begins burning fuel");
		addSubtitle(RegistryManager.STEAM_ENGINE_STOP, "Steam Engine rattles and stops");

		addSubtitle(RegistryManager.MINI_BOILER_RUPTURE, "Mini Boiler ruptures violently");

		addSubtitle(RegistryManager.PUMP_SLOW, "Mechanical Pump grinds its balls on sandpaper");
		addSubtitle(RegistryManager.PUMP_MID, "Mechanical Pump pumps");
		addSubtitle(RegistryManager.PUMP_FAST, "Mechanical Pump pumps at incredibly fast speeds");

		addSubtitle(RegistryManager.PIPE_CONNECT, "Pipe connected");
		addSubtitle(RegistryManager.PIPE_DISCONNECT, "Pipe disconnected");

		addSubtitle(RegistryManager.FIREBALL_BIG, "Big fireball is unleashed");
		addSubtitle(RegistryManager.FIREBALL_BIG_HIT, "Big fireball impacts");
		addSubtitle(RegistryManager.FIREBALL, "Fireball is released");
		addSubtitle(RegistryManager.FIREBALL_HIT, "Fireball impacts");

		addSubtitle(RegistryManager.BLAZING_RAY_FIRE, "Blazing Ray fires");
		addSubtitle(RegistryManager.BLAZING_RAY_EMPTY, "Blazing Ray clicks");

		addSubtitle(RegistryManager.CINDER_STAFF_CHARGE, "Cinder Staff charges");
		addSubtitle(RegistryManager.CINDER_STAFF_FAIL, "Cinder Staff fizzles");

		addSubtitle(RegistryManager.EXPLOSION_CHARM_ABSORB, "Explosion is absorbed");

		addSubtitle(RegistryManager.ASHEN_AMULET_BURN, "Item burns to ash");

		addSubtitle(RegistryManager.HEATED_ITEM_LEVELUP, "Item levels up");
		addSubtitle(RegistryManager.RESONATING_BELL, "Resonating Bell rings");
		addSubtitle(RegistryManager.CINDER_JET, "Cinder Jet boosts");
		addSubtitle(RegistryManager.INFLICTOR_GEM, "Inflictor Gem absorbs damage");

		addSubtitle(RegistryManager.METALLURGIC_DUST, "Metallurgic Dust transmutes ore");
		addSubtitle(RegistryManager.METALLURGIC_DUST_FAIL, "Metallurgic Dust transmutes ore to worthless material");

		addSubtitle(RegistryManager.ANCIENT_GOLEM_STEP, "Ancient Golem walks");
		addSubtitle(RegistryManager.ANCIENT_GOLEM_HURT, "Ancient Golem is hit");
		addSubtitle(RegistryManager.ANCIENT_GOLEM_PUNCH, "Ancient Golem punches");
		addSubtitle(RegistryManager.ANCIENT_GOLEM_DEATH, "Ancient Golem is destroyed");
	}

	public void addSubtitle(RegistryObject<SoundEvent> soundEvent, String subtitle) {
		add("subtitles." + Embers.MODID + "." + soundEvent.getId().getPath(), subtitle);
	}

	public void addFluid(String name, String localizedName) {
		add("fluid." + Embers.MODID + "." + name, localizedName);
		add("fluid_type." + Embers.MODID + "." + name, localizedName);
	}

	public void addLore(Supplier<? extends Item> key, String lore) {
		add(key.get().getDescriptionId() + ".lore", lore);
	}
}
