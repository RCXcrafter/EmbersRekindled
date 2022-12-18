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
		addBlock(RegistryManager.ITEM_DIAL, "Item Dial");
		addBlock(RegistryManager.EMBER_EMITTER, "Ember Emitter");
		addBlock(RegistryManager.EMBER_RECEIVER, "Ember Receiver");
		addBlock(RegistryManager.CAMINITE_LEVER, "Caminite Lever");
		addBlock(RegistryManager.ITEM_PIPE, "Item Pipe");
		addBlock(RegistryManager.ITEM_EXTRACTOR, "Item Extractor");
		addBlock(RegistryManager.EMBER_BORE, "Ember Bore");
		addBlock(RegistryManager.EMBER_BORE_EDGE, "Ember Bore");

		addItem(RegistryManager.TINKER_HAMMER, "Tinker's Hammer");
		addItem(RegistryManager.EMBER_CRYSTAL, "Ember Crystal");
		addItem(RegistryManager.EMBER_SHARD, "Ember Shard");
		addItem(RegistryManager.EMBER_GRIT, "Ember Grit");

		add(Embers.MODID + ".decimal_format.ember", "0.#");
		add(Embers.MODID + ".tooltip.emberdial.ember", "Ember: %s/%s");
		add(Embers.MODID + ".tooltip.aiming_block", "Aiming: %s");
		add(Embers.MODID + ".tooltip.item.ember", "Ember: %s/%s");

		add(Embers.MODID + ".decimal_format.item_amount", "0x");
		add(Embers.MODID + ".tooltip.itemdial.slot", "Slot %s: %s");
		add(Embers.MODID + ".tooltip.itemdial.item", "%s %s");
		add(Embers.MODID + ".tooltip.itemdial.noitem", "NONE");
		add(Embers.MODID + ".tooltip.itemdial.too_many", "%s More...");


		//subtitles
		addSubtitle(EmbersSounds.ALCHEMY_FAIL, "Energetic Alchemy fails...");
		addSubtitle(EmbersSounds.ALCHEMY_SUCCESS, "Energetic Alchemy succeeds!");
		addSubtitle(EmbersSounds.ALCHEMY_START, "Energetic Alchemy begins");

		addSubtitle(EmbersSounds.BEAM_CANNON_FIRE, "Beam Cannon fires");
		addSubtitle(EmbersSounds.BEAM_CANNON_HIT, "Beam impacts");

		addSubtitle(EmbersSounds.CRYSTAL_CELL_GROW, "Crystal Cell grows");

		addSubtitle(EmbersSounds.ACTIVATOR, "Ember Activator releases Ember");
		addSubtitle(EmbersSounds.PRESSURE_REFINERY, "Pressure Refinery releases Ember");
		addSubtitle(EmbersSounds.IGNEM_REACTOR, "Ignem Reactor releases Ember");

		addSubtitle(EmbersSounds.BORE_START, "Ember Bore rumbles to life");
		addSubtitle(EmbersSounds.BORE_STOP, "Ember Bore sighs and stops");

		addSubtitle(EmbersSounds.CATALYTIC_PLUG_START, "Catalytic Plug begins injecting catalyst");
		addSubtitle(EmbersSounds.CATALYTIC_PLUG_STOP, "Catalytic Plug is empty");

		addSubtitle(EmbersSounds.STAMPER_DOWN, "Stamp slams down");
		addSubtitle(EmbersSounds.STAMPER_UP, "Stamp recedes");

		addSubtitle(EmbersSounds.METAL_SEED_PING, "Metal Seed splits off pieces");

		addSubtitle(EmbersSounds.INFERNO_FORGE_FAIL, "Reforging fails...");
		addSubtitle(EmbersSounds.INFERNO_FORGE_SUCCESS, "Reforging succeeds!");
		addSubtitle(EmbersSounds.INFERNO_FORGE_START, "Inferno Forge begins forging");
		addSubtitle(EmbersSounds.INFERNO_FORGE_OPEN, "Inferno Forge opens");
		addSubtitle(EmbersSounds.INFERNO_FORGE_CLOSE, "Inferno Forge closes");

		addSubtitle(EmbersSounds.EMBER_EMIT, "Ember packet emitted");
		addSubtitle(EmbersSounds.EMBER_EMIT_BIG, "Big Ember packet emitted");
		addSubtitle(EmbersSounds.EMBER_RECEIVE, "Ember packet received");
		addSubtitle(EmbersSounds.EMBER_RECEIVE_BIG, "Big Ember packet received");
		addSubtitle(EmbersSounds.EMBER_RELAY, "Ember packet relayed");

		addSubtitle(EmbersSounds.STEAM_ENGINE_START_STEAM, "Steam Engine spins up");
		addSubtitle(EmbersSounds.STEAM_ENGINE_START_BURN, "Steam Engine begins burning fuel");
		addSubtitle(EmbersSounds.STEAM_ENGINE_STOP, "Steam Engine rattles and stops");

		addSubtitle(EmbersSounds.MINI_BOILER_RUPTURE, "Mini Boiler ruptures violently");

		addSubtitle(EmbersSounds.PUMP_SLOW, "Mechanical Pump grinds its balls on sandpaper");
		addSubtitle(EmbersSounds.PUMP_MID, "Mechanical Pump pumps");
		addSubtitle(EmbersSounds.PUMP_FAST, "Mechanical Pump pumps at incredibly fast speeds");

		addSubtitle(EmbersSounds.PIPE_CONNECT, "Pipe connected");
		addSubtitle(EmbersSounds.PIPE_DISCONNECT, "Pipe disconnected");

		addSubtitle(EmbersSounds.FIREBALL_BIG, "Big fireball is unleashed");
		addSubtitle(EmbersSounds.FIREBALL_BIG_HIT, "Big fireball impacts");
		addSubtitle(EmbersSounds.FIREBALL, "Fireball is released");
		addSubtitle(EmbersSounds.FIREBALL_HIT, "Fireball impacts");

		addSubtitle(EmbersSounds.BLAZING_RAY_FIRE, "Blazing Ray fires");
		addSubtitle(EmbersSounds.BLAZING_RAY_EMPTY, "Blazing Ray clicks");

		addSubtitle(EmbersSounds.CINDER_STAFF_CHARGE, "Cinder Staff charges");
		addSubtitle(EmbersSounds.CINDER_STAFF_FAIL, "Cinder Staff fizzles");

		addSubtitle(EmbersSounds.EXPLOSION_CHARM_ABSORB, "Explosion is absorbed");

		addSubtitle(EmbersSounds.ASHEN_AMULET_BURN, "Item burns to ash");

		addSubtitle(EmbersSounds.HEATED_ITEM_LEVELUP, "Item levels up");
		addSubtitle(EmbersSounds.RESONATING_BELL, "Resonating Bell rings");
		addSubtitle(EmbersSounds.CINDER_JET, "Cinder Jet boosts");
		addSubtitle(EmbersSounds.INFLICTOR_GEM, "Inflictor Gem absorbs damage");

		addSubtitle(EmbersSounds.METALLURGIC_DUST, "Metallurgic Dust transmutes ore");
		addSubtitle(EmbersSounds.METALLURGIC_DUST_FAIL, "Metallurgic Dust transmutes ore to worthless material");

		addSubtitle(EmbersSounds.ANCIENT_GOLEM_STEP, "Ancient Golem walks");
		addSubtitle(EmbersSounds.ANCIENT_GOLEM_HURT, "Ancient Golem is hit");
		addSubtitle(EmbersSounds.ANCIENT_GOLEM_PUNCH, "Ancient Golem punches");
		addSubtitle(EmbersSounds.ANCIENT_GOLEM_DEATH, "Ancient Golem is destroyed");
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
