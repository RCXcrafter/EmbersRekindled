package com.rekindled.embers.datagen;

import java.util.function.Supplier;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;
import com.rekindled.embers.RegistryManager.StoneDecoBlocks;

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

		addBlock(RegistryManager.CAMINITE_BRICKS, "Caminite Bricks");
		addDeco(RegistryManager.CAMINITE_BRICKS_DECO, "Caminite Brick");
		addBlock(RegistryManager.ARCHAIC_BRICKS, "Archaic Bricks");
		addDeco(RegistryManager.ARCHAIC_BRICKS_DECO, "Archaic Brick");
		addBlock(RegistryManager.ARCHAIC_EDGE, "Archaic Edge");
		addBlock(RegistryManager.ARCHAIC_TILE, "Archaic Tile");
		addDeco(RegistryManager.ARCHAIC_TILE_DECO, "Archaic Tile");
		addBlock(RegistryManager.ARCHAIC_LIGHT, "Archaic Light");
		addBlock(RegistryManager.EMBER_LANTERN, "Ember Lantern");

		addBlock(RegistryManager.COPPER_CELL, "Copper Cell");
		addBlock(RegistryManager.CREATIVE_EMBER, "Creative Ember Source");
		addBlock(RegistryManager.EMBER_DIAL, "Ember Dial");
		addBlock(RegistryManager.ITEM_DIAL, "Item Dial");
		addBlock(RegistryManager.FLUID_DIAL, "Fluid Dial");
		addBlock(RegistryManager.EMBER_EMITTER, "Ember Emitter");
		addBlock(RegistryManager.EMBER_RECEIVER, "Ember Receptor");
		addBlock(RegistryManager.CAMINITE_LEVER, "Caminite Lever");
		addBlock(RegistryManager.ITEM_PIPE, "Item Pipe");
		addBlock(RegistryManager.ITEM_EXTRACTOR, "Item Extractor");
		addBlock(RegistryManager.EMBER_BORE, "Ember Bore");
		addBlock(RegistryManager.EMBER_BORE_EDGE, "Ember Bore");
		addBlock(RegistryManager.MECHANICAL_CORE, "Mechanical Core");
		addBlock(RegistryManager.EMBER_ACTIVATOR, "Ember Activator");
		addBlock(RegistryManager.MELTER, "Melter");
		addBlock(RegistryManager.FLUID_PIPE, "Fluid Pipe");
		addBlock(RegistryManager.FLUID_EXTRACTOR, "Fluid Extractor");
		addBlock(RegistryManager.FLUID_VESSEL, "Fluid Vessel");
		addBlock(RegistryManager.STAMPER, "Stamper");
		addBlock(RegistryManager.STAMP_BASE, "Stamp Base");
		addBlock(RegistryManager.BIN, "Bin");
		addBlock(RegistryManager.MIXER_CENTRIFUGE, "Mixer Centrifuge");
		addBlock(RegistryManager.ITEM_DROPPER, "Item Dropper");
		addBlock(RegistryManager.PRESSURE_REFINERY, "Pressure Refinery");
		addBlock(RegistryManager.EMBER_EJECTOR, "Ember Ejector");
		addBlock(RegistryManager.EMBER_FUNNEL, "Ember Funnel");
		addBlock(RegistryManager.EMBER_RELAY, "Ember Relay");
		addBlock(RegistryManager.MIRROR_RELAY, "Mirror Relay");
		addBlock(RegistryManager.BEAM_SPLITTER, "Beam Splitter");
		addBlock(RegistryManager.ITEM_VACUUM, "Item Vacuum");
		addBlock(RegistryManager.HEARTH_COIL, "Hearth Coil");
		addBlock(RegistryManager.HEARTH_COIL_EDGE, "Hearth Coil");
		addBlock(RegistryManager.RESERVOIR, "Reservoir");
		addBlock(RegistryManager.RESERVOIR_EDGE, "Reservoir");
		addBlock(RegistryManager.CAMINITE_RING, "Caminite Ring");
		addBlock(RegistryManager.CAMINITE_RING_EDGE, "Caminite Ring");
		addBlock(RegistryManager.CAMINITE_VALVE, "Caminite Valve");
		addBlock(RegistryManager.CAMINITE_VALVE_EDGE, "Caminite Valve");
		addBlock(RegistryManager.CRYSTAL_CELL, "Crystal Cell");
		addBlock(RegistryManager.CRYSTAL_CELL_EDGE, "Crystal Cell");
		addBlock(RegistryManager.CLOCKWORK_ATTENUATOR, "Clockwork Attenuator");
		addBlock(RegistryManager.GEOLOGIC_SEPARATOR, "Geologic Separator");
		addBlock(RegistryManager.COPPER_CHARGER, "Copper Charger");
		addBlock(RegistryManager.EMBER_SIPHON, "Ember Siphon");

		addItem(RegistryManager.TINKER_HAMMER, "Tinker's Hammer");
		addItem(RegistryManager.TINKER_LENS, "Tinker's Lens");
		addItem(RegistryManager.ANCIENT_CODEX, "Ancient Codex");
		addItem(RegistryManager.ATMOSPHERIC_GAUGE, "Atmospheric Gauge");
		addItem(RegistryManager.EMBER_JAR, "Ember Jar");
		addItem(RegistryManager.EMBER_CARTRIDGE, "Ember Cartridge");
		addItem(RegistryManager.CLOCKWORK_PICKAXE, "Clockwork Pickaxe");
		addItem(RegistryManager.CLOCKWORK_AXE, "Clockwork Axe");
		addItem(RegistryManager.GRANDHAMMER, "Grandhammer");
		addItem(RegistryManager.BLAZING_RAY, "Blazing Ray");
		addItem(RegistryManager.CINDER_STAFF, "Cinder Staff");

		addItem(RegistryManager.EMBER_CRYSTAL, "Ember Crystal");
		addItem(RegistryManager.EMBER_SHARD, "Ember Shard");
		addItem(RegistryManager.EMBER_GRIT, "Ember Grit");
		addItem(RegistryManager.CAMINITE_BLEND, "Caminite Blend");
		addItem(RegistryManager.CAMINITE_BRICK, "Caminite Brick");
		addItem(RegistryManager.ARCHAIC_BRICK, "Archaic Brick");
		addItem(RegistryManager.ANCIENT_MOTIVE_CORE, "Ancient Motive Core");

		addItem(RegistryManager.RAW_CAMINITE_PLATE, "Raw Caminite Plate");
		addItem(RegistryManager.RAW_INGOT_STAMP, "Raw Ingot Stamp");
		addItem(RegistryManager.RAW_NUGGET_STAMP, "Raw Nugget Stamp");
		addItem(RegistryManager.RAW_PLATE_STAMP, "Raw Plate Stamp");

		addItem(RegistryManager.CAMINITE_PLATE, "Caminite Plate");
		addItem(RegistryManager.INGOT_STAMP, "Ingot Stamp");
		addItem(RegistryManager.NUGGET_STAMP, "Nugget Stamp");
		addItem(RegistryManager.PLATE_STAMP, "Plate Stamp");

		addItem(RegistryManager.IRON_PLATE, "Iron Plate");
		//addItem(RegistryManager.GOLD_PLATE, "Gold Plate");
		addItem(RegistryManager.COPPER_PLATE, "Copper Plate");
		addItem(RegistryManager.COPPER_NUGGET, "Copper Nugget");

		addBlock(RegistryManager.LEAD_ORE, "Lead Ore");
		addBlock(RegistryManager.DEEPSLATE_LEAD_ORE, "Deepslate Lead Ore");
		addBlock(RegistryManager.RAW_LEAD_BLOCK, "Block of Raw Lead");
		addBlock(RegistryManager.LEAD_BLOCK, "Block of Lead");
		addItem(RegistryManager.RAW_LEAD, "Raw Lead");
		addItem(RegistryManager.LEAD_INGOT, "Lead Ingot");
		addItem(RegistryManager.LEAD_NUGGET, "Lead Nugget");
		addItem(RegistryManager.LEAD_PLATE, "Lead Plate");

		addBlock(RegistryManager.SILVER_ORE, "Silver Ore");
		addBlock(RegistryManager.DEEPSLATE_SILVER_ORE, "Deepslate Silver Ore");
		addBlock(RegistryManager.RAW_SILVER_BLOCK, "Block of Raw Silver");
		addBlock(RegistryManager.SILVER_BLOCK, "Block of Silver");
		addItem(RegistryManager.RAW_SILVER, "Raw Silver");
		addItem(RegistryManager.SILVER_INGOT, "Silver Ingot");
		addItem(RegistryManager.SILVER_NUGGET, "Silver Nugget");
		addItem(RegistryManager.SILVER_PLATE, "Silver Plate");

		addBlock(RegistryManager.DAWNSTONE_BLOCK, "Block of Dawnstone");
		addItem(RegistryManager.DAWNSTONE_INGOT, "Dawnstone Ingot");
		addItem(RegistryManager.DAWNSTONE_NUGGET, "Dawnstone Nugget");
		addItem(RegistryManager.DAWNSTONE_PLATE, "Dawnstone Plate");

		addItem(RegistryManager.ANCIENT_GOLEM_SPAWN_EGG, "Ancient Golem Spawn Egg");


		//tooltips
		add(Embers.MODID + ".decimal_format.ember", "0.#");
		add(Embers.MODID + ".tooltip.emberdial.ember", "Ember: %s/%s");
		add(Embers.MODID + ".tooltip.aiming_block", "Aiming: %s");
		add(Embers.MODID + ".tooltip.item.ember", "Ember: %s/%s");
		add(Embers.MODID + ".tooltip.attenuator.on", "When active: %s Speed");
		add(Embers.MODID + ".tooltip.attenuator.off", "When inactive: %s Speed");

		add(Embers.MODID + ".decimal_format.item_amount", "0x");
		add(Embers.MODID + ".decimal_format.ember_multiplier", "0.##x");
		add(Embers.MODID + ".decimal_format.heat", "0.#");
		add(Embers.MODID + ".decimal_format.attenuator_multiplier", "0.##x");

		add(Embers.MODID + ".tooltip.dial.ember_multiplier", "Production Multiplier: %s");
		add(Embers.MODID + ".tooltip.dial.heat", "Heat: %s/%s");

		add(Embers.MODID + ".tooltip.itemdial.slot", "Slot %s: %s");
		add(Embers.MODID + ".tooltip.itemdial.item", "%s %s");
		add(Embers.MODID + ".tooltip.itemdial.noitem", "NONE");
		add(Embers.MODID + ".tooltip.itemdial.too_many", "%s More...");

		add(Embers.MODID + ".tooltip.fluiddial.fluid", "%s: %s/%s");
		add(Embers.MODID + ".tooltip.fluiddial.nofluid", "0/%s");
		add(Embers.MODID + ".tooltip.fluiddial.separator", "%s, %s");
		add(Embers.MODID + ".tooltip.fluiddial.ingot", "1 Ingot");
		add(Embers.MODID + ".tooltip.fluiddial.ingots", "%s Ingots");
		add(Embers.MODID + ".tooltip.fluiddial.nugget", "1 Nugget");
		add(Embers.MODID + ".tooltip.fluiddial.nuggets", "%s Nuggets");
		add(Embers.MODID + ".tooltip.fluiddial.millibucket", "%s mB");

		add(Embers.MODID + ".tooltip.side.center", "Center");
		add(Embers.MODID + ".tooltip.side.north", "North");
		add(Embers.MODID + ".tooltip.side.east", "East");
		add(Embers.MODID + ".tooltip.side.south", "South");
		add(Embers.MODID + ".tooltip.side.west", "West");
		add(Embers.MODID + ".tooltip.side.up", "Top");
		add(Embers.MODID + ".tooltip.side.down", "Bottom");

		add(Embers.MODID + ".tooltip.goggles.input", "§9⬊§r %s");
		add(Embers.MODID + ".tooltip.goggles.output", "§6⬉§r %s");
		add(Embers.MODID + ".tooltip.goggles.storage", "• %s");

		add(Embers.MODID + ".tooltip.goggles.item", "Items");
		add(Embers.MODID + ".tooltip.goggles.fluid", "Fluids");
		add(Embers.MODID + ".tooltip.goggles.ember", "Ember");
		add(Embers.MODID + ".tooltip.goggles.mechanical", "Mechanical Power");
		add(Embers.MODID + ".tooltip.goggles.filter", "%s (%s)");

		add(Embers.MODID + ".tooltip.goggles.item.any", "Any");
		add(Embers.MODID + ".tooltip.goggles.item.fuel", "Fuel");
		add(Embers.MODID + ".tooltip.goggles.item.ember", "Ember");
		add(Embers.MODID + ".tooltip.goggles.item.stamp", "Stamp");
		add(Embers.MODID + ".tooltip.goggles.item.ember_storage", "Ember Storage");
		add(Embers.MODID + ".tooltip.goggles.item.combustion", "Combustibles");
		add(Embers.MODID + ".tooltip.goggles.item.catalysis", "Catalysts");
		add(Embers.MODID + ".tooltip.goggles.item.ash", "Ash");
		add(Embers.MODID + ".tooltip.goggles.fluid.any", "Any");
		add(Embers.MODID + ".tooltip.goggles.fluid.metal", "Molten Metals");
		add(Embers.MODID + ".tooltip.goggles.fluid.water", "Water");
		add(Embers.MODID + ".tooltip.goggles.fluid.steam", "Steam");
		add(Embers.MODID + ".tooltip.goggles.fluid.water_or_steam", "Water or Steam");
		add(Embers.MODID + ".tooltip.goggles.fluid.redstone", "Alchemical Slurry");

		add(Embers.MODID + ".tooltip.goggles.upgrade_slot", "• Machine Upgrade Slot");
		add(Embers.MODID + ".tooltip.goggles.upgrade", "• Machine Upgrade");
		add(Embers.MODID + ".tooltip.goggles.accessor_slot", "• Machine Accessor Slot");
		add(Embers.MODID + ".tooltip.goggles.actuator_slot", "• Actuator Slot");


		//jei stuff
		add(Embers.MODID + ".jei.recipe.boring", "Boring");
		add(Embers.MODID + ".jei.recipe.boring.weight", "Weight: %s");
		add(Embers.MODID + ".jei.recipe.boring.min_height", "Min. Height: %s");
		add(Embers.MODID + ".jei.recipe.boring.max_height", "Max. Height: %s");
		add(Embers.MODID + ".jei.recipe.boring.dimensions", "Dimensions:");
		add(Embers.MODID + ".jei.recipe.boring.biomes", "Biomes:");
		add(Embers.MODID + ".jei.recipe.ember_activation", "Ember Activation");
		add(Embers.MODID + ".jei.recipe.ember_activation.ember", "Ember: %s");
		add(Embers.MODID + ".jei.recipe.melting", "Melting");
		add(Embers.MODID + ".jei.recipe.geologic_separator", "Geologic Separator Bonus");
		add(Embers.MODID + ".jei.recipe.stamping", "Stamping");
		add(Embers.MODID + ".jei.recipe.mixing", "Mixing");
		add(Embers.MODID + ".jei.recipe.metal_coefficient", "Production Multiplier");


		//ancient codex
		add(Embers.MODID + ".research.controls", "Right-click entries to mark them as complete.;Incompleted entries are marked with a star §6*§r;Categories will open as you complete entries.;;Enter text to search and highlight entries.;You can search entries matching multiple words with §f|§r.;ex: §fEmber|Generator§r");
		add(Embers.MODID + ".research.prerequisite", "Needed for %s");
		add(Embers.MODID + ".research.prerequisite.locked", "§6*§8 Needs %s");
		add(Embers.MODID + ".research.prerequisite.unlocked", "§a✔§r Needs %s");

		add(Embers.MODID + ".research.null", "");
		add(Embers.MODID + ".research.world", "Natural Energy");
		add(Embers.MODID + ".research.mechanisms", "Machinations of Fire");
		add(Embers.MODID + ".research.metallurgy", "Metallurgic Constructs");
		add(Embers.MODID + ".research.alchemy", "Transmutation");
		add(Embers.MODID + ".research.materia", "Arcane Materia");
		add(Embers.MODID + ".research.core", "Glow of the Core");
		add(Embers.MODID + ".research.smithing", "Augment Smithing");

		add(Embers.MODID + ".research.multipage", "%s (%s/%s)");

		add(Embers.MODID + ".research.page.empty.desc", "");

		add(Embers.MODID + ".research.page.ores", "Ores");
		add(Embers.MODID + ".research.page.ores.title", "Raw Minerals");
		add(Embers.MODID + ".research.page.ores.tags", "Copper;Lead;Silver;Nickel;Ferrous;Aluminum;Aluminium;Ore;");
		add(Embers.MODID + ".research.page.ores.desc", "Deep in the vaults of the solid earth, the novice digger may find many useful minerals, namely Copper, Lead and Silver. Copper has many applications as a conductor, Lead as a dull and malleable metal, and Silver as a channeler for the arcane."/* Quartz, as well, proves a good source of raw crystalline matter."*/);

		add(Embers.MODID + ".research.page.hammer", "Tinker Hammer");
		add(Embers.MODID + ".research.page.hammer.title", "Smashing");
		add(Embers.MODID + ".research.page.hammer.tags", "Hammer;Plate;Tool;");
		add(Embers.MODID + ".research.page.hammer.desc", "With a dense head of lead and iron, the Tinker Hammer proves a wieldy tool. It can be used to pound two ingots of metal into a plate, and you can imagine it being useful for getting certain machines to cooperate.");

		add(Embers.MODID + ".research.page.ancient_golem", "Ancient Constructs");
		add(Embers.MODID + ".research.page.ancient_golem.title", "Antique Adversaries");
		add(Embers.MODID + ".research.page.ancient_golem.tags", "Golem;Rock Goblin;Annoying;Cyclops;Archaic;");
		add(Embers.MODID + ".research.page.ancient_golem.desc", "Rarely in the world, strange constructs resembling humanoids have been known to be found. These enigmatic golems will emit rays of pure heat, and possess unnatural strength, yet seem to require no fuel or sustenance. How such an automaton could come to be is beyond you, but perhaps the unknown materials and mechanisms within it may prove useful.");

		add(Embers.MODID + ".research.page.gauge", "Atmospheric Gauge");
		add(Embers.MODID + ".research.page.gauge.title", "Prospecting");
		add(Embers.MODID + ".research.page.gauge.tags", "Ember;Gauge;Tool;Info;");
		add(Embers.MODID + ".research.page.gauge.desc", "Evidenced by the existence of hot molten stone deep in the earth, or the glowing heat sustaining the strange golems you fight, you have come to believe that at the center of your world lies a powerful energy source. Using some copper and iron, you have found a way to create a device capable of measuring the density of this energy, henceforth referred to as \"Ember\", at a position in the world.");

		add(Embers.MODID + ".research.page.caminite", "Caminite");
		add(Embers.MODID + ".research.page.caminite.title", "Sturdy Ceramic");
		add(Embers.MODID + ".research.page.caminite.tags", "Ceramic;Caminite;Brick;Clay;Sand;");
		add(Embers.MODID + ".research.page.caminite.desc", "Caminite is a very sturdy ceramic, mixed from a bit of clay and sand. The mixture yields, when baked, a material that you believe will be hard enough for your purposes throughout all of your mechanical ambitions.");

		add(Embers.MODID + ".research.page.activator", "Ember Activator");
		add(Embers.MODID + ".research.page.activator.title", "Extraction");
		add(Embers.MODID + ".research.page.activator.tags", "Ember;Activator;Generator;");
		add(Embers.MODID + ".research.page.activator.desc", "The Ember Activator is an ingenious device, finally allowing you to extract usable energy from Ember Crystals and Shards. Simply insert said Crystals and Shards into the bottom of the device using a Hopper or other item transportation method, and soon, marked by a fiery burst, activated Ember will appear within the copper cage atop the machine.");

		add(Embers.MODID + ".research.page.crystals", "Crystallized Ember");
		add(Embers.MODID + ".research.page.crystals.title", "Solid Heat");
		add(Embers.MODID + ".research.page.crystals.tags", "Ember;Crystal;Shard;Ore;");
		add(Embers.MODID + ".research.page.crystals.desc", "The Ember Bore's excavation yields several curious glowing crystals. These crystals come in several sizes, the larger Ember Crystal and the smaller Ember Shard. These crystals are clear evidence that the power of the Core of the world both exists and can be indirectly tapped: through the immolation of these crystals, you believe, you may be able to activate the power within them for other use.");

		add(Embers.MODID + ".research.page.bore", "Ember Bore");
		add(Embers.MODID + ".research.page.bore.title", "Diggy Diggy Hole");
		add(Embers.MODID + ".research.page.bore.tags", "Ember;Bore;Ore;");
		add(Embers.MODID + ".research.page.bore.desc", "While bedrock may halt your downward advances, it will not halt your machines. The Ember Bore must be fueled with normal furnace fuel items, and must be placed at a position where the blades of the Bore can touch bedrock. When fueled, the Bore will dig up Ember Crystals from deeper into the earth, for your future use. Hoppers and other item transporters must be used to extract and insert fuel and crystals from the Bore.");

		add(Embers.MODID + ".research.page.pipes", "Pipes");
		add(Embers.MODID + ".research.page.pipes.title", "Transportation");
		add(Embers.MODID + ".research.page.pipes.tags", "Pipe;Transport;Item;Fluid;Liquid;");
		add(Embers.MODID + ".research.page.pipes.desc", "One of the simplest mechanisms necessary for your ambitions is the pipe, a simple means of moving materials. You have developed both iron and lead pipes: Iron Pipes can be used to transport fluids, while Lead Pipes can be used to transport items. Pipes can only insert to tanks or inventories, however: to extract, you must craft a corresponding Pump, place it adjacent to the container, and power it with redstone.");
		add(Embers.MODID + ".research.page.routing.desc", "Items and Fluids in pipes follow simple rules to determine where they will go. They will always go forward, and will never bounce back or, heavens forbid, pop out of the pipe. At an intersection, they will always prioritize the opposite exit, so long as the other exits are weighed equally. When a tie must be broken, items and fluids will round-robin.");
		add(Embers.MODID + ".research.page.valves.desc", "Extractors have a number of special properties, listed here: They never connect to other Extractors. When turned off, they can receive items instead of only extracting from containers. When turned on by a redstone signal, they will never accept items from pipes. This makes Extractors useful as valves in pipe systems, for flow control.");
		add(Embers.MODID + ".research.page.pipe_tools.desc", "Pipes will automatically connect to other adjacent pipes. When this is not wanted, your Tinker's Hammer can be used to disconnect them. (Note that both sides can be disconnected individually) Sometimes, routing in a pipe network can go awry. When this happens, pipes will expel smoke to show that they are clogged. To unclog a pipe, simply right-click it with a stick.");

		add(Embers.MODID + ".research.page.requisition", "Item Requisition");
		add(Embers.MODID + ".research.page.requisition.title", "Storage System?");
		add(Embers.MODID + ".research.page.requisition.tags", "Pipe;Storage;Pull;Item;Filter;");
		add(Embers.MODID + ".research.page.requisition.desc", "Normally, an item pipe system is push-only. This intricate nodule allows inventories to keep a stock of items in an inventory. Simply right-click with an item to set a filter. You can also link this node to a distant Item Extractor to automatically extract items from the extractor to satisfy the stock requirements.");

		add(Embers.MODID + ".research.page.golem_eye", "Eye of the Ancients");
		add(Embers.MODID + ".research.page.golem_eye.title", "Advanced Filters");
		add(Embers.MODID + ".research.page.golem_eye.tags", "Filter;Eye;Golem;");
		add(Embers.MODID + ".research.page.golem_eye.desc", "Sometimes filtering by items is not enough for advanced item transfer systems. An eye of a golem can be obtained by killing it with a pickaxe. You can then use the eye in hand to have it remember any 2 items alongside a filter characteristic based on what the two items have in common.");
		add(Embers.MODID + ".research.page.filter_existing.desc", "By sneak-right-clicking an Item Requisition, you can attune a golem eye to filter items that exist in an attached inventory. This filter will not work in Item Transfers, as they have no attached inventory.");
		add(Embers.MODID + ".research.page.filter_not_existing.desc", "By sneak-right-clicking a Dawnstone Anvil, you can attune a golem eye to filter items that don't exist in an attached inventory. This filter will not work in Item Transfers, as they have no attached inventory.");


		add(Embers.MODID + ".research.page.tank", "Fluid Vessel");
		add(Embers.MODID + ".research.page.tank.title", "Liquid Container");
		add(Embers.MODID + ".research.page.tank.tags", "Tank;Storage;Liquid;Fluid;Portable;Caminite;");
		add(Embers.MODID + ".research.page.tank.desc", "Liquids such as water or lava are common occurrences in your world. This tank, a simple construction of metal and caminite, should be able to hold a substantial quality of any fluid, and will retain its contents when broken and replaced.");

		add(Embers.MODID + ".research.page.bin", "Bin");
		add(Embers.MODID + ".research.page.bin.title", "Storage Bin");
		add(Embers.MODID + ".research.page.bin.tags", "Bin;Chest;Storage;Item;Hopper;");
		add(Embers.MODID + ".research.page.bin.desc", "A simple barrel of lead with an open top, the Bin can hold up to a stack of any item. It can also be used to automatically collect outputs from some machines.");

		add(Embers.MODID + ".research.page.pressure_refinery", "Pressure Refinery");
		add(Embers.MODID + ".research.page.pressure_refinery.title", "Steam Chamber");
		add(Embers.MODID + ".research.page.pressure_refinery.tags", "Boiler;Refinery;Ember;Pressure;Generator;Water;Steam;Heat;");
		add(Embers.MODID + ".research.page.pressure_refinery.desc", "While the Activator can refine crystalline Ember, it produces only a modest amount. The Pressure Refinery is a means of getting much higher yields per crystal. It requires both water and Ember to be pumped into it, resulting in a default 1.5x yield from the Activator. However, if placed on a metal block with lava or fire around it, it can reach up to 3x yields depending on how many hot blocks there are around the base.");

		add(Embers.MODID + ".research.page.mini_boiler", "Mini Boiler");
		add(Embers.MODID + ".research.page.mini_boiler.title", "Piggy-backing for explosions");
		add(Embers.MODID + ".research.page.mini_boiler.tags", "Boiler;Pressure;Water;Steam;Heat;Explosion;Oil;Gas;");
		add(Embers.MODID + ".research.page.mini_boiler.desc", "Ember is a highly energetic substance, that produces heat both, when consumed and when produced. It would be a shame to let that heat go to waste. By attaching this pressure vessel to the side of an Ember-consuming or -producing machine, you can boil water into steam for other purposes. But beware that at high pressures, the boiler can easily rupture...");

		add(Embers.MODID + ".research.page.reaction_chamber", "Reaction Chamber");
		add(Embers.MODID + ".research.page.reaction_chamber.title", "It's a condenser! It's a combuster!");
		add(Embers.MODID + ".research.page.reaction_chamber.tags", "Boiler;Pressure;Reaction;Water;Steam;Gas;Condensation;");
		add(Embers.MODID + ".research.page.reaction_chamber.desc", "By applying a Spark Plug to a hollow metal vessel you've created a device that can react certain fluids into other fluids. Its main purpose is burning fuel gas into large amounts of steam, but it can also be used to condense excess steam back into water. You can insert reactive fluid from any side and extract the result from any other side. Excess fluid will be safely vented.");


		add(Embers.MODID + ".research.page.dials", "Dials");
		add(Embers.MODID + ".research.page.dials.title", "Careful Measurements");
		add(Embers.MODID + ".research.page.dials.tags", "Info;Item;Fluid;Liquid;Ember;Dial;Gauge;");
		add(Embers.MODID + ".research.page.dials.desc", "Dials are a simple way to get information from a machine. Three exist: the Ember, Item and Fluid Dials. Attached to their respective containers, each dial will graphically display the contents of that container when looked at.");

		add(Embers.MODID + ".research.page.tinker_lens", "Tinker's Lens");
		add(Embers.MODID + ".research.page.tinker_lens.title", "What's What?");
		add(Embers.MODID + ".research.page.tinker_lens.tags", "Info;Modifier;Augment;Armor;Tool;");
		add(Embers.MODID + ".research.page.tinker_lens.desc", "Despite you creating all this machinery yourself, some of its functionality can be a bit obtuse at times. Fortunately you've devised an eyepiece you can use to examine machines more closely. When held in either hand, you will always be able to tell which purpose a face on a machine has. Note that this tool is not a periscope, so you might have some trouble with Mechanical Cores...");



		add(Embers.MODID + ".research.page.melter", "Melter");
		add(Embers.MODID + ".research.page.melter.title", "Melting Things Down");
		add(Embers.MODID + ".research.page.melter.tags", "Ore;Machine;Melt;Duplication;Processing;Liquid;Fluid;Item;Multiblock;Multi Block;");
		add(Embers.MODID + ".research.page.melter.desc", "Using the power of activated Ember, you have devised a way to melt down objects. When the Melter's bottom block is provided with Ember, it will melt down meltable items in its top block into their liquid state, where the molten fluid can be piped out for external use.");

		add(Embers.MODID + ".research.page.geo_separator", "Geologic Separator");
		add(Embers.MODID + ".research.page.geo_separator.title", "Electrum Ore");
		add(Embers.MODID + ".research.page.geo_separator.tags", "Extra;Liquid;Fluid;Upgrade;");
		add(Embers.MODID + ".research.page.geo_separator.desc", "Many ores and minerals found in the earth aren't just pure metal. They're amalgamated with impurities like other minerals. The Geologic Separator is a machine upgrade devised to filter out some of those impurities when melting ores and thus providing a little bit of extra output. It must be placed besides a melter, with the tap facing towards the bottom block of the melter to work.");

		add(Embers.MODID + ".research.page.stamper", "Stamper");
		add(Embers.MODID + ".research.page.stamper.title", "Pound It Flat");
		add(Embers.MODID + ".research.page.stamper.tags", "Ore;Machine;Melt;Aspectus;Plate;Duplication;Processing;Stamp;Liquid;Fluid;Item;Multiblock;Multi Block;Bin;");
		add(Embers.MODID + ".research.page.stamper.desc", "To shape molten metal into useful forms, you have devised the Stamper. To stamp molten metal requires two parts: the Stamper itself and the Stamper Base. Place the Stamper two blocks above the Stamper Base, give it Ember, as well as a particular Stamp. Pipe molten metal into the Stamper Base and the Stamper should begin to process it. You may place a Bin beneath the Stamper Base to automatically collect the products.");

		add(Embers.MODID + ".research.page.hearth_coil", "Hearth Coil");
		add(Embers.MODID + ".research.page.hearth_coil.title", "Open Fire");
		add(Embers.MODID + ".research.page.hearth_coil.tags", "Furnace;Coil;Hearth;Machine;Smelt;Processing;Item;Multiblock;Multi Block;");
		add(Embers.MODID + ".research.page.hearth_coil.desc", "Gone are the days of using solid fuels in a stone furnace to cook your items. Making use of Ember power, you have developed the Hearth Coil. When supplied with Ember, the Coil will heat up. The hotter it gets, the faster it will smelt the items on top of it. All smelted items will be contained within the inventory of the Coil, and can be piped out from the bottom or from some Accessor.");

		add(Embers.MODID + ".research.page.mixer", "Mixer Centrifuge");
		add(Embers.MODID + ".research.page.mixer.title", "Mix It Up");
		add(Embers.MODID + ".research.page.mixer.tags", "Mix;Mixer;Melt;Machine;Processing;Liquid;Fluid;Multiblock;Multi Block;");
		add(Embers.MODID + ".research.page.mixer.desc", "While you have been using various metals to control the flow of Ember before, through the Mixer Centrifuge, you believe you may be able to create new alloys. Each face of the bottom of the Centrifuge is its own tank. When fluid is pumped into these tanks, in a particular combination, and the top block of the Centrifuge is given Ember, a molten alloy will be created which can be pumped out of the top block.");

		add(Embers.MODID + ".research.page.access", "Mechanical Access");
		add(Embers.MODID + ".research.page.access.title", "Interface");
		add(Embers.MODID + ".research.page.access.tags", "Core;Mechanical;Proxy;Capability;Side;Port;Interface;Access;Upgrade;");
		add(Embers.MODID + ".research.page.access.desc", "Machines such as the Ember Bore or Hearth Coil are a bit large. As such, they only have one port with which to interface. Using the Mechanical Core, however, you can extend this port: the Mechanical Core will act as a proxy to the inventory, fluid storage, or Ember storage of any large machine it's facing towards. Up to three Mechanical Cores can be chained to provide a more extended interface.");

		add(Embers.MODID + ".research.page.reservoir", "Reservoir");
		add(Embers.MODID + ".research.page.reservoir.title", "Massive Tanks");
		add(Embers.MODID + ".research.page.reservoir.tags", "Tank;Storage;Liquid;Fluid;Caminite;Multiblock;Multi Block;Valve;");
		add(Embers.MODID + ".research.page.reservoir.desc", "The Reservoir is a simple means of storing large quantities of fluids. While it will only store about 40 buckets' worth of fluid by default, its capacity can be extended by placing Caminite Rings atop the Reservoir base, by 40 buckets for each ring. The Reservoir may only be filled or drained through the port on its bottom, or through a Mechanical Core or Accessor attached to that port.");
		add(Embers.MODID + ".research.page.reservoir_valve.desc", "Caminite Valves can also be added for ease of access. They hold the exact same amount of fluid per layer as a regular Caminite Ring, but they have 4 extra sides for fluid input and output.");

		add(Embers.MODID + ".research.page.transfer", "Item Transfer");
		add(Embers.MODID + ".research.page.transfer.title", "Filtering");
		add(Embers.MODID + ".research.page.transfer.tags", "Pipe;Transport;Item;Fluid;Liquid;Filter;Transfer;");
		add(Embers.MODID + ".research.page.transfer.desc", "Item Pipes may be a simple way to get items from place to place, but only with the development of the Item Transfer have you created a way to filter items out. The Item Transfer functions much like a normal pipe, with a few rules. It can only connect to pipes on its front and on its back, and it will take priority over all other pipes when items are being sent. It can also be given an item filter by right-clicking it.");
		add(Embers.MODID + ".research.page.fluid_transfer.desc", "Fluid Transfers are the fluid equivalent to Item Transfers. They function equivalent in almost all aspects. Only to set the filter on a Fluid Transfer, a bucket or other container holding some fluid is needed instead. The fluid inside the container is not consumed when setting a filter.");

		add(Embers.MODID + ".research.page.breaker", "Automatic Breaker");
		add(Embers.MODID + ".research.page.breaker.title", "Breaking Blocks");
		add(Embers.MODID + ".research.page.breaker.tags", "Machine;Break;Block;Item;Bin;");
		add(Embers.MODID + ".research.page.breaker.desc", "The Automatic Breaker is a simple contraption. Place it facing in a particular direction, and the grinding blade on its front will break the block in front of it. By default, these blocks will simply drop into the world, but they can be automatically collected by placing a Bin behind the Breaker.");

		add(Embers.MODID + ".research.page.pump", "Mechanical Pump");
		add(Embers.MODID + ".research.page.pump.title", "Pump It Up");
		add(Embers.MODID + ".research.page.pump.tags", "Machine;Pump;Fluid;Liquid;Mechanical;");
		add(Embers.MODID + ".research.page.pump.desc", "Since the Pressure Refinery consumes water to boost its Ember production, you need a way to collect it automatically. A mechanical pump should do fine, but Ember converts poorly to mechanical energy. Running this pump solely using Ember will be very slow and it would likely be good to build multiple of them.");

		add(Embers.MODID + ".research.page.vacuum", "Item Vacuum");
		add(Embers.MODID + ".research.page.vacuum.title", "Automated Collection");
		add(Embers.MODID + ".research.page.vacuum.tags", "Pipe;Transport;Item;Filter;Transfer;Collect;Vacuum;Hopper;");
		add(Embers.MODID + ".research.page.vacuum.desc", "The Item Vacuum is a means of sucking in items in the world. Simply power it with redstone, and it will draw in all items in a large area in front of it. It will automatically push items through pipes attached to its back face.");
		add(Embers.MODID + ".research.page.vacuum_transfer.desc", "Item Vacuums don't have an internal inventory, they will directly push items into a connected pipe or container. Using this property, you can filter which items will be sucked up by attaching an Item Transfer to its back face.");

		add(Embers.MODID + ".research.page.dropper", "Item Dropper");
		add(Embers.MODID + ".research.page.dropper.title", "Dropped Down");
		add(Embers.MODID + ".research.page.dropper.tags", "Pipe;Transport;Item;Drop;Hopper;Trash;Void;");
		add(Embers.MODID + ".research.page.dropper.desc", "The Item Dropper is very simple. When items are piped into the port on its top face, it will quickly spew them out in a vertical line into the world, directly beneath it.");

		add(Embers.MODID + ".research.page.dawnstone", "Dawnstone");
		add(Embers.MODID + ".research.page.dawnstone.title", "Bright Alloy");
		add(Embers.MODID + ".research.page.dawnstone.tags", "Dawnstone;Alloy;Metal;Metallurgy;");
		add(Embers.MODID + ".research.page.dawnstone.desc", "Dawnstone is a strong alloy of gold and copper in equal parts, created using the Mixer Centrifuge. With Ember imbued into its very nature in its creation, you believe this alloy should allow you to create the material structure for many more advanced Ember mechanisms.");

		add(Embers.MODID + ".research.page.emitters", "Ember Transfer");
		add(Embers.MODID + ".research.page.emitters.title", "Send and Receive");
		add(Embers.MODID + ".research.page.emitters.tags", "Hammer;Transfer;Transport;Ember;Receptor;Emitter;Receive;Send;Redstone;Lever;Link;Connect;");
		add(Embers.MODID + ".research.page.emitters.desc", "The actual manipulation of the Ember you receive from your Activator or other refineries involves two main blocks: Ember Emitters and Ember Receptors. Emitters function as the sending end. Simply place them on a machine and they will suck out Ember from the machine into their internal buffer. Then, while powered by redstone, they will periodically expel Ember.");
		add(Embers.MODID + ".research.page.receivers.desc", "Receptors function as the receiving end. When they receive an Ember burst from an Emitter, they will push the Ember into the attached machine, no redstone required. Note that if Ember is received, but the receptor can't take all of it, some of it will be lost, signified by a plume of smoke and sparks.");
		add(Embers.MODID + ".research.page.linking.desc", "To link Emitters and Receptors together, simply right-click the Emitter with your Tinker Hammer, and right-click the Receptor with the hammer. There is no distance limit to linking, but after an Ember Burst has been in transit for a while, it can fade away, losing all of the Ember it carried.");

		add(Embers.MODID + ".research.page.relays", "Ember Relay");
		add(Embers.MODID + ".research.page.relays.title", "Redirection");
		add(Embers.MODID + ".research.page.relays.tags", "Hammer;Transfer;Transport;Ember;Relay;Mirror;Link;Connect;");
		add(Embers.MODID + ".research.page.relays.desc", "A burst of ember can only travel a limited distance, Ember Relays can be used to extend this distance. Relays are linked just like Emitters and Receptors, right-click on the source and then right-click on the destination of the burst. If linked properly, the burst will flow through the Relay and move towards its destination.");
		add(Embers.MODID + ".research.page.mirror_relay.desc", "Sometimes you want to reverse the direction of an Ember Burst besides just relaying it, for this purpose you have devised the Mirror Relay. It functions identically to an Ember Relay except that ember bounces off it like light off a mirror instead of flowing though it.");

		add(Embers.MODID + ".research.page.copper_cell", "Copper Cell");
		add(Embers.MODID + ".research.page.copper_cell.title", "Capacitor");
		add(Embers.MODID + ".research.page.copper_cell.tags", "Cell;Copper;Ember;Storage;Portable;Capacitor;");
		add(Embers.MODID + ".research.page.copper_cell.desc", "The Copper Cell fulfills a very simple purpose: Ember storage. It can be given Ember using an Ember Receptor, or have Ember removed from it through an Ember Emitter. Note that when a Copper Cell is broken, it will retain its stored Ember.");

		add(Embers.MODID + ".research.page.clockwork_attenuator", "Clockwork Attenuator");
		add(Embers.MODID + ".research.page.clockwork_attenuator.title", "Throttle Control");
		add(Embers.MODID + ".research.page.clockwork_attenuator.tags", "Dial;Control;Redstone;Speed;Upgrade;");
		add(Embers.MODID + ".research.page.clockwork_attenuator.desc", "Fine-tuning your machine setups is hard when all of your machines run until they run out of fuel, without compromise. This dial can directly control a machine's speed. It has two internal settings that toggle on a redstone signal: active and inactive. The settings can be modified by right-clicking and sneak-right-clicking the dial while the respective setting is in action.");



		add(Embers.MODID + ".research.page.gearbox", "Gearboxes");
		add(Embers.MODID + ".research.page.gearbox.title", "Mechanical Junction");
		add(Embers.MODID + ".research.page.gearbox.tags", "Gear;Box;Gearbox;Mechanical;Split;Divide;Junction;");
		add(Embers.MODID + ".research.page.gearbox.desc", "Gearbox Frames are the heart-piece of mechanical transfer. One gear can be attached on each side, but only sides with axles or other machines attached will need one. There is an input side marked by a strip, and all other sides are outputs. The input power will be divided between each used output, so if two axles were attached, each would receive half power.");

		add(Embers.MODID + ".research.page.mergebox", "Mergeboxes");
		add(Embers.MODID + ".research.page.mergebox.title", "Mechanical Fusion");
		add(Embers.MODID + ".research.page.mergebox.tags", "Gear;Box;Gearbox;Mergebox;Mechanical;Merge;Fusion;");
		add(Embers.MODID + ".research.page.mergebox.desc", "Mergebox Frames are another important part in mechanical systems. They function opposite to a regular gearbox, instead of splitting power, power will be merged together. Note that all inputs must be the same speed to be summed up. Additionally, anytime the power changes, the mergebox will disengage the output gear for a short time as the internal ratios adjust.");

		add(Embers.MODID + ".research.page.axle_iron", "Iron Axle");
		add(Embers.MODID + ".research.page.axle_iron.title", "Axial Rotation");
		add(Embers.MODID + ".research.page.axle_iron.tags", "Axle;Mechanical;Iron Axle;Transport;Transfer;Connect;");
		add(Embers.MODID + ".research.page.axle_iron.desc", "Axles are used to transfer mechanical power between generators, gearboxes and machines that use it. Axles don't lose power over distance, so their transfer range is virtually unlimited.");

		add(Embers.MODID + ".research.page.gear_iron", "Iron Gear");
		add(Embers.MODID + ".research.page.gear_iron.title", "Cogs of the Machine");
		add(Embers.MODID + ".research.page.gear_iron.tags", "Gear;Mechanical;Iron Gear;Gold Gear;Dawnstone Gear;Attach;Interface;Cog;");
		add(Embers.MODID + ".research.page.gear_iron.desc", "Gears are used as interfaces for connecting axles to gearboxes or machines. Iron Gears are cheap and robust, but they have a power limit. Power past the limit is mostly lost.");
		add(Embers.MODID + ".research.page.gear_gold.desc", "This gear is mostly the same as an iron gear, but it has a higher power limit.");
		add(Embers.MODID + ".research.page.gear_redstone.desc", "These redstone-encrusted gears provide the ability to control gearboxes externally. When a redstone signal is applied to a gearbox, the Redstone Gear will start to turn, while the inverted Redstone Gear will turn off. These gears have a similar power limit to gold gears.");
		add(Embers.MODID + ".research.page.gear_dawnstone.desc", "Dawnstone Gears are more suitable for high speed rotations, they don't have the same limit Iron Gears have. At high rotational speeds, the gear will visually glow a fiery orange. This side effect is mostly harmless however.");

		add(Embers.MODID + ".research.page.actuator", "Mechanical Actuator");
		add(Embers.MODID + ".research.page.actuator.title", "Alternate Energy");
		add(Embers.MODID + ".research.page.actuator.tags", "Gear;Mechanical;Machine;Upgrade;Bore;Pump;Stamp;Mixer;Hammer;");
		add(Embers.MODID + ".research.page.actuator.desc", "Some machines that are powered using Ember could potentially be powered by mechanical energy instead. In order to function, it must be attached to a machine that can accept machine upgrades. Similar to a gearbox, a gear needs to be attached to the opposing side in order to attach an axle to it.");
		add(Embers.MODID + ".research.page.actuator_multi.desc", "This altered version has 4 input sides for mechanical energy. The power from each side is combined together internally, but most machines have diminishing returns for the amount of power provided, so more power may not always be better.");
		add(Embers.MODID + ".research.page.actuator_bore.desc", "The Ember Bore is almost purely mechanical and lends itself well to being powered by mechanical energy. Powering it this way is more coal efficient, but initially a bit slower.");
		add(Embers.MODID + ".research.page.actuator_pump.desc", "The Pump is a machine that really suffers from being powered by Ember. Even small amounts of mechanical power speed it up significantly, and it can be pushed to become quite fast.");
		add(Embers.MODID + ".research.page.actuator_stamper.desc", "The Stamper is another machine with moving parts, but the fact that Ember is so energetic and the motion of the Stamper is so simple mean that directly applying mechanical power will only speed it up a little bit. It is quite a bit more efficient in terms of Ember cost however.");
		add(Embers.MODID + ".research.page.actuator_mixer.desc", "Mixer Centrifuges use Ember and moving parts to bind liquid metal together, and while the moving parts can certainly be powered by mechanical energy, not all recipe processes can be powered purely by mechanical energy.");
		add(Embers.MODID + ".research.page.actuator_auto_hammer.desc", "The Automatic Hammer is very similar to the Stamper, and similar speedup and ember savings can be achieved by hooking up mechanical power instead.");

		add(Embers.MODID + ".research.page.steam_engine", "Steam Engine");
		add(Embers.MODID + ".research.page.steam_engine.title", "Industrial Revolution");
		add(Embers.MODID + ".research.page.steam_engine.tags", "Mechanical;Engine;Generator;Steam;Water;Coal;Fuel;");
		add(Embers.MODID + ".research.page.steam_engine.desc", "The Steam Engine is the easiest way to produce mechanical power. All it needs is a bit of burnable material like coal and some water, and it will produce a meager amount of rotation on its output face. While Steam Engines are more fuel efficient than powering an Ember Bore with the same amount of fuel, they can also be wasteful if improperly handled: If the engine runs out of water as its burning a piece of fuel, the rest of that piece will simply be lost.");
		add(Embers.MODID + ".research.page.steam_engine_overclock.desc", "Since you've already dabbled with producing steam from Ember-powered machinery, you could put this steam to use for powering your engines. Directly pumping in steam from the outside significantly increases the effectiveness of the engine, and it will output a lot more power, but the steam will be consumed very quickly, possibly requiring several boilers per engine or attaching them to machines that use a lot of Ember.");


		add(Embers.MODID + ".research.page.pulser", "Ember Ejector");
		add(Embers.MODID + ".research.page.pulser.title", "Full Power Destruction");
		add(Embers.MODID + ".research.page.pulser.tags", "Ember;Funnel;Ejector;Transport;Transfer;Receptor;Emitter;Receive;Send;Redstone;Lever;Link;Connect;Loss;");
		add(Embers.MODID + ".research.page.pulser.desc", "Simple Ember Emitters are useful and all, but sometimes you may want a bit more raw throughput. The Ember Ejector is a sturdier Emitter -- functionally identical to its predecessor, but capable of draining and emitting ten times as much Ember in a given time. Note that Receptors are quickly overwhelmed with this massive quantity and any surplus Ember may be lost in transfer.");
		add(Embers.MODID + ".research.page.ember_funnel.desc", "Since losing Ember in transfer isn't good, you've devised the Ember Funnel that catches Ember from multiple sources and rapidly transfers it to the connected machine.");

		add(Embers.MODID + ".research.page.splitter", "Beam Splitter");
		add(Embers.MODID + ".research.page.splitter.title", "Ember Division");
		add(Embers.MODID + ".research.page.splitter.tags", "Ember;Split;Transport;Transfer;Link;Connect;");
		add(Embers.MODID + ".research.page.splitter.desc", "Using the new Dawnstone alloy, you have discovered a way to divide Ember bursts. The Beam Splitter has four faces: two with a smaller copper port, and two with larger copper-and-dawnstone ports. Each of the larger ports will function as its own Ember Emitter. When Ember is fed into the Splitter, it will split the Ember it receives between those two ports, which can each be bound independently.");

		add(Embers.MODID + ".research.page.dawnstone_anvil", "Dawnstone Anvil");
		add(Embers.MODID + ".research.page.dawnstone_anvil.title", "Repairs");
		add(Embers.MODID + ".research.page.dawnstone_anvil.tags", "Dawnstone;Anvil;Repair;Break;Damage;Hammer;");
		add(Embers.MODID + ".research.page.dawnstone_anvil.desc", "The resilience of Dawnstone lends itself well to use in and Anvil. The Dawnstone Anvil may be used to both repair items, or break down items into their component parts. Place a damaged item on the anvil, then its repair material, then give it a good couple whacks with the Tinker Hammer to repair it. Place the damaged item alone and give it some strong blows, and it will break apart into its component pieces.");

		add(Embers.MODID + ".research.page.autohammer", "Automatic Hammer");
		add(Embers.MODID + ".research.page.autohammer.title", "Automatic Hammer");
		add(Embers.MODID + ".research.page.autohammer.tags", "Hammer;Anvil;Machine;Processing;");
		add(Embers.MODID + ".research.page.autohammer.desc", "Hammering bits of metal into your tools by hand takes a very long time. With a bit of Ember power, you believe you have a solution. The Automatic Hammer simply requires redstone power and a bit of Ember power, and will handle the hammering of the Dawnstone Anvil much more forcefully and faster than you manually could.");

		add(Embers.MODID + ".research.page.crystal_cell", "Crystal Cell");
		add(Embers.MODID + ".research.page.crystal_cell.title", "Mass Storage");
		add(Embers.MODID + ".research.page.crystal_cell.tags", "Cell;Crystal;Ember;Storage;Capacitor;");
		add(Embers.MODID + ".research.page.crystal_cell.desc", "Your past methods of Ember storage have never had much capacity, and the Crystal Cell is your solution. The Crystal Cell will start small, and can have Ember inserted and extracted from it. It can also have Ember fuel items such as Ember Crystals inserted into it as well. Each fuel item will increase the maximum capacity of the Cell, eventually causing physical growth in the glowing crystal atop it.");

		add(Embers.MODID + ".research.page.charger", "Copper Charger");
		add(Embers.MODID + ".research.page.charger.title", "Ember Charging");
		add(Embers.MODID + ".research.page.charger.tags", "Charge;Ember;Machine;");
		add(Embers.MODID + ".research.page.charger.desc", "The Charger is a very simple device. Provide it with Ember, and it will charge the item inside it, if the item can be charged. The item inside can be simply right-clicked in or out.");

		add(Embers.MODID + ".research.page.jars", "Mantle Containers");
		add(Embers.MODID + ".research.page.jars.title", "Portable Ember");
		add(Embers.MODID + ".research.page.jars.tags", "Ember;Portable;Storage;Mantle;Cartridge;Jar;Battery;");
		add(Embers.MODID + ".research.page.jars.desc", "For field purposes, carrying around a Crystal Cell is simply impractical. With a bit of glass and Ember crystal, you have developed the Mantle Jar and Mantle Cartridge. Both can contain Ember, and can be charged at the Copper Charger. However, while Mantle Jars can be accessed by Ember-powered items anywhere in the inventory, Mantle Cartridges must be held in your hand to be used.");

		add(Embers.MODID + ".research.page.clockwork_tools", "Clockwork Tools");
		add(Embers.MODID + ".research.page.clockwork_tools.title", "Power Tools");
		add(Embers.MODID + ".research.page.clockwork_tools.tags", "Ember;Weapon;Tool;Clockwork;Pickaxe;Hammer;Axe;Sword;Shovel;");
		add(Embers.MODID + ".research.page.clockwork_tools.desc", "Making use of your recently-discovered Ember containers, you have fashioned tools of Dawnstone. All three tools share certain properties: They require accessible Ember in your inventory to be used, and they are unbreakable. They also have high base damage, allowing all of them to double as weapons. Additionally, when powered with Ember they will set hit enemies on fire.");
		add(Embers.MODID + ".research.page.clockwork_pickaxe.desc", "The Clockwork Pickaxe functions as a powerful pickaxe and shovel at the same time. Like the other tools, it requires Ember to break blocks or attack enemies. As it doubles as a weapon, it can receive both, weapon and tool enchantments.");
		add(Embers.MODID + ".research.page.clockwork_hammer.desc", "The Grandhammer is a powerful melee weapon. Like the other tools it requires Ember to break blocks or attack enemies. If it is used to break a block, it will obliterate it completely, leaving no drops. Like the pickaxe, it can receive weapon and tool enchantments, but due to its nature it cannot be enchanted with Fortune or Silk Touch.");
		add(Embers.MODID + ".research.page.clockwork_axe.desc", "The Clockwork Axe functions as a powerful axe. Like the other tools it requires Ember to break blocks or attack enemies. It can receive weapon and tool enchantments, like the other tools.");

		add(Embers.MODID + ".research.page.cinder_staff", "Cinder Staff");
		add(Embers.MODID + ".research.page.cinder_staff.title", "Magic Missile");
		add(Embers.MODID + ".research.page.cinder_staff.tags", "Ember;Weapon;Staff;Projectile;Missile;Cinder;");
		add(Embers.MODID + ".research.page.cinder_staff.desc", "Strange, isn't it, how silver metal can have such control over the chaotic fire of Ember. As the Cinder Staff channels Ember from accessible containers in your inventory, it charges up a large sphere of pure Ember in front of you. The longer it is charged, the more damaging the sphere will be, culminating in just shy of enough damage to instantly kill a typical monster.");

		add(Embers.MODID + ".research.page.blazing_ray", "Blazing Ray");
		add(Embers.MODID + ".research.page.blazing_ray.title", "Ember Cannon");
		add(Embers.MODID + ".research.page.blazing_ray.tags", "Ember;Weapon;Gun;Ray;Cannon;Projectile;Missile;Blaze;Blazing;");
		add(Embers.MODID + ".research.page.blazing_ray.desc", "While the Blazing Ray may resemble a mundane firearm in shape, it more functionally resembles a piece of artillery. When fired, it will unleash a straight beam of pure Ember, shooting forth until it strikes a solid block or a creature.");

		add(Embers.MODID + ".research.page.aspecti", "Metal Aspecti");
		add(Embers.MODID + ".research.page.aspecti.title", "Alchemic Foci");
		add(Embers.MODID + ".research.page.aspecti.tags", "Alchemy;Focus;Aspect;Aspectus;Foci;Stamp;Shard;Transmutation;");
		add(Embers.MODID + ".research.page.aspecti.desc", "Wrapping molten metal about an Ember Shard, you have devised the Aspectus. These items can focus the alchemical energies produced by burning ash into a particular elemental alignment, when placed on Alchemy Pedestals during a Transmutation.");

		add(Embers.MODID + ".research.page.cinder_plinth", "Cinder Plinth");
		add(Embers.MODID + ".research.page.cinder_plinth.title", "Incineration");
		add(Embers.MODID + ".research.page.cinder_plinth.tags", "Alchemy;Machine;Ash;Processing;Void;Trash;Transmutation;Plinth;Incineration;Incinerate;Cinder;");
		add(Embers.MODID + ".research.page.cinder_plinth.desc", "The Cinder Plinth is a simple machine: place any item into it, power it with Ember, and it will burn the item into black Ash. It can automatically place the ash into a Bin placed beneath it as well. Ash has several uses: it can be burned as a poor fuel item, it can be applied to stone to change its color and texture, and you believe the energies released in its combustion may have other applications.");

		add(Embers.MODID + ".research.page.beam_cannon", "Beam Cannon");
		add(Embers.MODID + ".research.page.beam_cannon.title", "Heat Ray");
		add(Embers.MODID + ".research.page.beam_cannon.tags", "Alchemy;Weapon;Machine;Cannon;Projectile;Beam;Ray;Transmutation;");
		add(Embers.MODID + ".research.page.beam_cannon.desc", "The Beam Cannon is perhaps your most dangerous device yet. Aim it by shift-right-clicking with a Tinker Hammer on a target block, then right-clicking the Cannon with the hammer. When it is given enough Ember, it will fire off a beam of pure radiant heat. This beam can kill nearly any unarmored creature, and also initiate transmutation at the Exchange Tablet.");

		add(Embers.MODID + ".research.page.alchemy", "Energetic Alchemy");
		add(Embers.MODID + ".research.page.alchemy.title", "Transmutation");
		add(Embers.MODID + ".research.page.alchemy.tags", "Alchemy;Transmutation;Tablet;Exchange;Ash;Pedestal;Aspect;Aspectus;");
		add(Embers.MODID + ".research.page.alchemy.desc", "Ember alchemy is truly a marvelous discovery. The first key is the Exchange Tablet: right-click on its various faces to insert items into their respective slots. The next is the Alchemy Pedestal: these bear aspecti and can contain ash. For an alchemy recipe, place a pedestal for each needed aspectus about the Tablet, fill it with ash within the determined range, and strike the Tablet with the Beam Cannon.");

		add(Embers.MODID + ".research.page.catalytic_plug", "Catalytic Plug");
		add(Embers.MODID + ".research.page.catalytic_plug.title", "Overdrive Injection");
		add(Embers.MODID + ".research.page.catalytic_plug.tags", "Speed;Upgrade;Redstone;Alchemy;Alchemical Redstone;");
		add(Embers.MODID + ".research.page.catalytic_plug.desc", "Catalytic Plugs are fantastic devices for the impatient. When hooked up to a machine the plug doubles the speed of the machine, but only if also supplied with Alchemical Slurry from the back, which is consumed in the process. Up to 2 Catalytic Plugs can be attached to a single machine, to quadruple its speed.");

		add(Embers.MODID + ".research.page.ember_siphon", "Ember Siphon");
		add(Embers.MODID + ".research.page.ember_siphon.title", "Reversing The Flow");
		add(Embers.MODID + ".research.page.ember_siphon.tags", "Upgrade;Reverse;Siphon;Ember;Charge;Discharge;");
		add(Embers.MODID + ".research.page.ember_siphon.desc", "The Ember Siphon is a machine upgrade that can be placed underneath a Charger to invert it's function. Instead of filling an ember container with ember, it will empty it instead, and store the drained ember in its internal buffer. Ember can then be extracted from the charger or the sides of the siphon using the usual transfer mechanisms.");



		add(Embers.MODID + ".research.page.waste", "Experimentation");
		add(Embers.MODID + ".research.page.waste.title", "Alchemical Process");
		add(Embers.MODID + ".research.page.waste.tags", "Waste;Alchemy;Transmutation;Experiment;Ash;Stamp;");
		add(Embers.MODID + ".research.page.waste.desc", "You have quickly discovered that alchemical transmutations do not always work. For all but one particular combination of ash values, your alchemy will fail and result in Alchemic Waste. This waste can be placed in the Stamper to reclaim some of the Ash used in the alchemy, and can be visibly analyzed to determine how far off you were from the true recipe of your desired product.");

		add(Embers.MODID + ".research.page.hellish_synthesis", "Assorted Syntheses");
		add(Embers.MODID + ".research.page.hellish_synthesis.title", "Strange Materials");
		add(Embers.MODID + ".research.page.hellish_synthesis.tags", "Alchemy;Transmutation;Nether;Netherrack;Soulsand;Soul;Sand;");
		add(Embers.MODID + ".research.page.hellish_synthesis.desc", "Through alchemical processes, you have discovered a way to create several new substances. These include strange red rock and sand, which you have read in ancient documents make up the land of a strange faraway realm.");

		add(Embers.MODID + ".research.page.dwarven_oil", "Dwarven Oil");
		add(Embers.MODID + ".research.page.dwarven_oil.title", "Black Gold");
		add(Embers.MODID + ".research.page.dwarven_oil.tags", "Alchemy;Nether;Soulsand;Soul;Sand;Oil;Gas;Fuel;");
		add(Embers.MODID + ".research.page.dwarven_oil.desc", "Melting the strange brown sand you've synthesized prior yields a thick black sludge. Through further processing in a centrifuge with steam or other gas, you can turn this sludge into dwarven oil. The oil does not expand as much when boiled and can thus be used to create much safer boiler setups.");

		add(Embers.MODID + ".research.page.archaic_brick", "Archaic Bricks");
		add(Embers.MODID + ".research.page.archaic_brick.title", "Ancient Building Material");
		add(Embers.MODID + ".research.page.archaic_brick.tags", "Alchemy;Transmutation;Archaic;Brick;");
		add(Embers.MODID + ".research.page.archaic_brick.desc", "The ancient sentinels that roam the land often break apart into what appears to be bricks. Through peculiar alchemy you've discovered that you can replicate them, so long you have one to begin with. These should go well with your caminite buildings.");

		add(Embers.MODID + ".research.page.motive_core", "Motive Cores");
		add(Embers.MODID + ".research.page.motive_core.title", "Precursor Technology");
		add(Embers.MODID + ".research.page.motive_core.tags", "Alchemy;Transmutation;Archaic;Core;Motive;Golem;");
		add(Embers.MODID + ".research.page.motive_core.desc", "Alchemy doesn't just allow you to replicate bricks. Motive Cores appear to be the lifeblood and spark of Ancient Golems, and through alchemy, this power now rests at your fingertips.");

		add(Embers.MODID + ".research.page.adhesive", "Adhesive");
		add(Embers.MODID + ".research.page.adhesive.title", "Sticky Solution");
		add(Embers.MODID + ".research.page.adhesive.tags", "Alchemy;Transmutation;Slime;Glue;Adhesive;");
		add(Embers.MODID + ".research.page.adhesive.desc", "The peculiar animated slimes you happen across underground are useful, but altogether too rare to be practical. The synthesized Adhesive will function as a replacement for balls of slime in recipes.");

		add(Embers.MODID + ".research.page.tyrfing", "Tyrfing");
		add(Embers.MODID + ".research.page.tyrfing.title", "Wicked Blade");
		add(Embers.MODID + ".research.page.tyrfing.tags", "Alchemy;Transmutation;Ash;Weapon;Sword;Armor;Lead;Damage;");
		add(Embers.MODID + ".research.page.tyrfing.desc", "A strange construct to be sure, the Tyrfing is a pulsating lead weapon with a design shaped through alchemy. While it is slightly dulled against most targets compared to the Lead Sword used to create it, its unique shape permits it to deal high damage to armored foes. The more armor your opponent is wearing, the more damage the blade will inflict.");

		add(Embers.MODID + ".research.page.ashen_cloak", "Ashen Armor");
		add(Embers.MODID + ".research.page.ashen_cloak.title", "Cloak of the Mage");
		add(Embers.MODID + ".research.page.ashen_cloak.tags", "Alchemy;Transmutation;Ash;Ashen;Cloak;Inflictor;Gem;Fabric;");
		add(Embers.MODID + ".research.page.ashen_cloak.desc", "By imbuing mundane cloth with alchemical ash, you have managed to synthesize a flexible yet strong Ashen fabric, with which you have designed armor. The armor on its own is fairly strong, and you believe it looks quite dashing, but you have a feeling it may be able to upgraded far further...");

		add(Embers.MODID + ".research.page.inflictor", "Inflictor Gems");
		add(Embers.MODID + ".research.page.inflictor.title", "Absorbing Pain");
		add(Embers.MODID + ".research.page.inflictor.tags", "Alchemy;Transmutation;Ash;Ashen;Cloak;Inflictor;Gem;Fabric;");
		add(Embers.MODID + ".research.page.inflictor.desc", "The Inflictor Gem is a strange crystal indeed. While holding it in your hand, when taking damage, it will absorb the type of damage taken. By stitching one to your Ashen Cloak in a crafting table with a piece of string, it will reduce the damage you take from that damage source by approximately a third. They can be removed by placing an Ashen Cloak in a crafting table on its own, and up to seven may be placed on a Cloak at a time.");

		add(Embers.MODID + ".research.page.glimmer", "Glimmer Crystal");
		add(Embers.MODID + ".research.page.glimmer.title", "Light on, Light off");
		add(Embers.MODID + ".research.page.glimmer.tags", "Alchemy;Transmutation;Light;Glimmer;Crystal;Tool;Quartz;");
		add(Embers.MODID + ".research.page.glimmer.desc", "By infusing quartz with heat and ember, you have devised a luminescent material that appears to be eager to part with its own luminosity. This allows you to place light sources at will, although it will deplete the crystal. However, while you are exposed to sunlight, the crystal will slowly regenerate itself.");

		add(Embers.MODID + ".research.page.metallurgic_dust", "Metallurgic Dust");
		add(Embers.MODID + ".research.page.metallurgic_dust.title", "This for that");
		add(Embers.MODID + ".research.page.metallurgic_dust.tags", "Alchemy;Transmutation;Ore;Dust;Metal;Metallurgy;");
		add(Embers.MODID + ".research.page.metallurgic_dust.desc", "Sometimes what you find in the bowels of the earth isn't what you were looking for. And indeed, what we've learnt about alchemy can easily solve that. By combining the transmutative properties of metal seeds with redstone and ember grit, a fine white powder is created. When it comes into contact with ore, it will quickly transmute the entire vein into a different type. Some of the ore may be transmuted into worthless stone however...");

		add(Embers.MODID + ".research.page.cluster", "Ember Clusters");
		add(Embers.MODID + ".research.page.cluster.title", "Purified Crystals");
		add(Embers.MODID + ".research.page.cluster.tags", "Alchemy;Transmutation;Ember;Shard;Crystal;Cluster;");
		add(Embers.MODID + ".research.page.cluster.desc", "The smaller crystalline Ember items you have used in the past are handy, but you believe you will soon require more power. By alchemically fusing several Ember Crystals and Ember Shards together, you have created the Ember Cluster, a larger bunch of crystals that you believe will help you energize future machines.");

		add(Embers.MODID + ".research.page.field_chart", "Field Chart");
		add(Embers.MODID + ".research.page.field_chart.title", "Tactical Overview");
		add(Embers.MODID + ".research.page.field_chart.tags", "Field;Chart;Ember;Gauge;Info;");
		add(Embers.MODID + ".research.page.field_chart.desc", "Finding spots with High Ember concentration can be tedious when your only measurement device is an Atmospheric Gauge. However, it seems that Ember resonates with itself, and by suitable manufacture you've devised the Field Chart, a map of Ember concentrations for a larger area than can be covered on foot.");

		add(Embers.MODID + ".research.page.wildfire", "Wildfire Core");
		add(Embers.MODID + ".research.page.wildfire.title", "Controlling the Flame");
		add(Embers.MODID + ".research.page.wildfire.tags", "Alchemy;Transmutation;Wildfire;Core;");
		add(Embers.MODID + ".research.page.wildfire.desc", "The Ember Cluster, however, is a bit too uncontrolled for all purposes. While it is suitable for the immolation of items in the Combustor or Catalyzer, a Wildfire Core must be created from it for refined purposes.");

		add(Embers.MODID + ".research.page.injector", "Metal Crystals");
		add(Embers.MODID + ".research.page.injector.title", "Seeds of the World");
		add(Embers.MODID + ".research.page.injector.tags", "Alchemy;Transmutation;Crystal;Seed;Metal;Ember;Injector;Machine;Grow;Processing;");
		add(Embers.MODID + ".research.page.injector.desc", "You have discovered a means of synthesizing metal Crystal Seeds for various metal types through alchemy, as well as the Ember Injector, a machine designed to control them. By placing a Crystal Seed in the world, then placing Ember Injectors facing it and powering them with Ember, the Seed will begin to be fed and grow slowly. At a certain threshold, the Seed will let loose several metal nuggets which can be collected.");
		add(Embers.MODID + ".research.page.crystal_level.desc", "By injecting Ember into a seed, it not only grows and produces metal, but will also become more pure over time. The more pure a crystal is, the more metal it produces when fully grown. You can see a crystal's current purity level by examining it with a Tinker's Lens. Note that all purity is lost when the seed is broken and moved elsewhere.");

		add(Embers.MODID + ".research.page.combustor", "Combustion Chamber");
		add(Embers.MODID + ".research.page.combustor.title", "Immolation");
		add(Embers.MODID + ".research.page.combustor.tags", "Combustion;Combust;Machine;Reactor;Chamber;Upgrade;Multiblock;Multi Block;");
		add(Embers.MODID + ".research.page.combustor.desc", "The Combustion Chamber is a simple machine that does little on its own. It can, quite simply, burn combustible fuels. Each fuel used has a particular power level: Coal has a power level of two, Nether Brick items have a power level of three, and Blaze Powder has a power level of four.");

		add(Embers.MODID + ".research.image.combustor_coal", "Coefficient: 2x");
		add(Embers.MODID + ".research.image.combustor_nether_brick", "Coefficient: 3x");
		add(Embers.MODID + ".research.image.combustor_blaze_powder", "Coefficient: 4x");

		add(Embers.MODID + ".research.page.catalyzer", "Catalysis Chamber");
		add(Embers.MODID + ".research.page.catalyzer.title", "Chemical Power");
		add(Embers.MODID + ".research.page.catalyzer.tags", "Catalysis;Catalyst;Machine;Reactor;Chamber;Upgrade;Multiblock;Multi Block;");
		add(Embers.MODID + ".research.page.catalyzer.desc", "The Catalysis Chamber is a simple machine that does little on its own, simply using up catalyst items. Each catalyst item has a particular catalyst level: Redstone has a level of two, Gunpowder a level of three, and Glowstone Dust a level of four.");

		add(Embers.MODID + ".research.image.catalyzer_redstone", "Coefficient: 2x");
		add(Embers.MODID + ".research.image.catalyzer_gunpowder", "Coefficient: 3x");
		add(Embers.MODID + ".research.image.catalyzer_glowstone", "Coefficient: 4x");

		add(Embers.MODID + ".research.page.reactor", "Ignem Reactor");
		add(Embers.MODID + ".research.page.reactor.title", "Ember Chemistry");
		add(Embers.MODID + ".research.page.reactor.tags", "Wildfire;Core;Catalysis;Catalyst;Combustion;Combust;Generator;Reactor;Chamber;Multiblock;Multi Block;");
		add(Embers.MODID + ".research.page.reactor.desc", "The Ignem Reactor is a very powerful means of refining Ember crystals. It must be placed adjacent to the top parts of both a Combustion and Catalysis Chamber. When both the Combustion Chamber and Catalysis Chamber are active, the Reactor will refine crystalline Ember with a multiplier equal to the fuel level and catalyst level of the attached chambers added together, plus one. The catalyst level and combustion level must also be fairly close.");

		add(Embers.MODID + ".research.page.ember_pipe", "Volatile Ember Conduit");
		add(Embers.MODID + ".research.page.ember_pipe.title", "Rigged From The Start");
		add(Embers.MODID + ".research.page.ember_pipe.tags", "Ember;Pipe;Wildfire;Volatile;Conduit;");
		add(Embers.MODID + ".research.page.ember_pipe.desc", "You've reached the point where the Ignem Reactor produces so much Ember that it cannot be extracted in a timely manner anymore. This ludicrously expensive conduit can be powered directly with a redstone signal to extract larger and larger packets of Ember. This comes at the cost of being completely uncontrollable, and when two ember packets collide, the conduit segment will detonate!");

		add(Embers.MODID + ".research.page.materia", "Isolated Materia");
		add(Embers.MODID + ".research.page.materia.title", "Universal Repair");
		add(Embers.MODID + ".research.page.materia.tags", "Alchemy;Transmutation;Repair;Metal;Materia;Isolated;Matter;Anvil;");
		add(Embers.MODID + ".research.page.materia.desc", "The Isolated Materia is a peculiar material. It has the property of being able to transform into the materials of other items in certain circumstances. For your purposes at the moment, it will enable you to repair items at the Dawnstone Anvil, replacing their repair material with the Materia. You can foresee it having uses in the future as well...");

		add(Embers.MODID + ".research.page.stirling", "Wildfire Stirling");
		add(Embers.MODID + ".research.page.stirling.title", "Reductio Ad Absurdum");
		add(Embers.MODID + ".research.page.stirling.tags", "Wildfire;Stirling;Ember;Upgrade;Steam;");
		add(Embers.MODID + ".research.page.stirling.desc", "This piece of machinery is the counterpart of the Catalytic Plug. When attached to a machine and supplied with steam, the Wildfire Stirling will reduce the ember cost of all machine operations by half. A second Stirling can be attached to quarter the cost.");



		add(Embers.MODID + ".research.page.cost_reduction", "Ember Jewelry");
		add(Embers.MODID + ".research.page.cost_reduction.title", "Cost Reduction");
		add(Embers.MODID + ".research.page.cost_reduction.tags", "Ember;Ring;Amulet;Belt;Bauble;");
		add(Embers.MODID + ".research.page.cost_reduction.desc", "Using clockwork tools or Ember-powered weaponry can be quite cumbersome, as Mantle Jars or Cartridges need to be refilled periodically. These pieces of jewelry counteract this by reducing the amount of Ember consumed by a factor. The ring, amulet and belt all have different reduction rates, but they all stack together additively.");

		add(Embers.MODID + ".research.page.mantle_bulb", "Mantle Bulb");
		add(Embers.MODID + ".research.page.mantle_bulb.title", "Tucked Away");
		add(Embers.MODID + ".research.page.mantle_bulb.tags", "Ember;Storage;Bulb;Mantle;Bauble;Portable;");
		add(Embers.MODID + ".research.page.mantle_bulb.desc", "If holding a Mantle Cartridge in your offhand or even just holding a Mantle Jar in your pack is too unwieldy for you, this bauble can help. Instead of holding it in your inventory, this Ember container can be worn in any bauble slot. This comes, however, at a vastly reduced capacity.");

		add(Embers.MODID + ".research.page.explosion_charm", "Explosion Charm");
		add(Embers.MODID + ".research.page.explosion_charm.title", "Manufacted Nethicite");
		add(Embers.MODID + ".research.page.explosion_charm.tags", "Explosion;Charm;Bauble;");
		add(Embers.MODID + ".research.page.explosion_charm.desc", "While wearing this charm, explosions that go off near you will be dissipated, their destructive force absorbed into the charm, but unfortunately, for no other special effect.");

		add(Embers.MODID + ".research.page.explosion_pedestal", "Explosion Charm Pedestal");
		add(Embers.MODID + ".research.page.explosion_pedestal.title", "Vylon Sphere");
		add(Embers.MODID + ".research.page.explosion_pedestal.tags", "Explosion;Charm;Pedestal;Bauble;");
		add(Embers.MODID + ".research.page.explosion_pedestal.desc", "Explosion Charms are useful, but normally only usable while on your person. By making minor modifications to the pedestal used in alchemy, you've managed to suspend a charm on it to act as a passive explosion absorbent.");

		add(Embers.MODID + ".research.page.nonbeliever_amulet", "Amulet of the Heretic");
		add(Embers.MODID + ".research.page.nonbeliever_amulet.title", "And it goes away");
		add(Embers.MODID + ".research.page.nonbeliever_amulet.tags", "Amulet;Heretic;Magic;Witch;Bauble;");
		add(Embers.MODID + ".research.page.nonbeliever_amulet.desc", "Damn witches and their resistance to the otherworldly... This amulet will be the gateway to becoming like them. When you are hit by any magical attack while wearing the amulet, 90%% of the damage will be nullified. Note however, that you will always take at least half a heart of damage from magical attacks.");

		add(Embers.MODID + ".research.page.ashen_amulet", "Ashen Amulet");
		add(Embers.MODID + ".research.page.ashen_amulet.title", "Ashes to Ashes");
		add(Embers.MODID + ".research.page.ashen_amulet.tags", "Amulet;Ash;Ashen;Bauble;");
		add(Embers.MODID + ".research.page.ashen_amulet.desc", "Turning things to ash is... certainly a display of power. Not very well thought-out power, but power that rests in this amulet regardless. When a creature is slain or a block is broken while wearing this amulet, it will instantly be turned to ash, and drop nothing but ash.");

		add(Embers.MODID + ".research.page.dawnstone_mail", "Dawnstone Mail");
		add(Embers.MODID + ".research.page.dawnstone_mail.title", "Defense Position");
		add(Embers.MODID + ".research.page.dawnstone_mail.tags", "Mail;Dawnstone;Knockback;Armor;Bauble;");
		add(Embers.MODID + ".research.page.dawnstone_mail.desc", "Dawnstone is a material with many properties that make it suitable for vestments and armor. This Dawnstone Mail will protect the wearer from all knockback. Just don't question why mail is made from plates.");



		add(Embers.MODID + ".research.page.modifiers", "Attaching Modifiers");
		add(Embers.MODID + ".research.page.modifiers.title", "Upgrades");
		add(Embers.MODID + ".research.page.modifiers.tags", "Modifier;Augment;Anvil;Hammer;Level;Tool;Weapon;Armor;");
		add(Embers.MODID + ".research.page.modifiers.desc", "You believe that with sufficiently advanced mechanisms, you may be able to create devices that grant additional abilities to your equipment. These items can be applied to armor, tools, or weapons, and must be hammered on at a Dawnstone Anvil. Place the original item down, then the modifier, then hammer away. Modifiers have levels, which will boost their power.");

		add(Embers.MODID + ".research.page.heat", "Heat");
		add(Embers.MODID + ".research.page.heat.title", "Well Worn");
		add(Embers.MODID + ".research.page.heat.tags", "Modifier;Augment;Anvil;Hammer;Level;Tool;Weapon;Armor;Heat;Core;Archaic;Ancient;Motive;");
		add(Embers.MODID + ".research.page.heat.desc", "Just as you used the Dawnstone Anvil to break apart items or repair them, you can also use it to attach modifiers to equipment. Place a tool, sword, or armor piece on the anvil, then place an Ancient Motive Core on the anvil. When the hammer swings down, the tool will gain the ability to absorb heat. This heat is accrued through normal use of the tool.");

		add(Embers.MODID + ".research.page.dismantling", "Detaching Modifiers");
		add(Embers.MODID + ".research.page.dismantling.title", "We have to go back");
		add(Embers.MODID + ".research.page.dismantling.tags", "Modifier;Augment;Anvil;Hammer;Level;Tool;Weapon;Armor;Remove;Detach;Break;");
		add(Embers.MODID + ".research.page.dismantling.desc", "Sometimes you might add an augment to a tool or armor and eventually find that it no longer suits you. Augments are not permanent additions however, and the Dawnstone Anvil can help you deal with that. By placing the tool or armor on the anvil without an item on top and hammering it, you can remove all modifiers for no cost. The core itself and its accumulated heat will always remain on the tool however, and such tools cannot be completely destroyed.");

		add(Embers.MODID + ".research.page.inferno_forge", "Inferno Forge");
		add(Embers.MODID + ".research.page.inferno_forge.title", "Leveling Up");
		add(Embers.MODID + ".research.page.inferno_forge.tags", "Modifier;Augment;Anvil;Hammer;Level;Tool;Weapon;Armor;Heat;Inferno;Forge;Machine;Multiblock;Multi Block;");
		add(Embers.MODID + ".research.page.inferno_forge.desc", "Once the Heat bar of an item has filled up, it must be tempered. When supplied with Ember power, the Inferno Forge can do just this. Open up the hatch on top, toss in your Heat-filled item, as well as some crystalline Ember. The more Ember crystal you put in, the more likely you will succeed. Once you level up the item, you grant it a modifier slot. Modifiers can then be attached to the item at the Dawnstone Anvil, up to the level of the item.");

		add(Embers.MODID + ".research.page.superheater", "Superheater");
		add(Embers.MODID + ".research.page.superheater.title", "Heating Up");
		add(Embers.MODID + ".research.page.superheater.tags", "Modifier;Augment;Tool;Weapon;Armor;Smelt;Cook;");
		add(Embers.MODID + ".research.page.superheater.desc", "The Superheater is a simple augment that can be applied to items that have accrued Heat. When applied, all broken blocks and dropped items from creatures will be cooked automatically, and additional damage will be added to the item as it burns targets. The higher the modifier level, the higher the burning damage. It will require accessible Ember in your inventory to function.");

		add(Embers.MODID + ".research.page.cinder_jet", "Cinder Jet");
		add(Embers.MODID + ".research.page.cinder_jet.title", "Dashing Forward");
		add(Embers.MODID + ".research.page.cinder_jet.tags", "Modifier;Augment;Armor;Dash;Jet;Cinder;");
		add(Embers.MODID + ".research.page.cinder_jet.desc", "The Cinder Jet is an armor augment that allows you to perform a dash. When applied, whenever you begin to sprint and enough Ember is accessible from the inventory, it will propel its vessel forth with increasing velocity depending on its modifier level.");

		add(Embers.MODID + ".research.page.blasting_core", "Blasting Core");
		add(Embers.MODID + ".research.page.blasting_core.title", "Volatile");
		add(Embers.MODID + ".research.page.blasting_core.tags", "Modifier;Augment;Tool;Weapon;Armor;Explosion;");
		add(Embers.MODID + ".research.page.blasting_core.desc", "The Blasting Core is an augment that creates explosions. When applied to tools, it will break nearby blocks whenever a block is broken with the tool. When applied to weapons, an explosion will occur and damage nearby creatures. When applied to armors, an explosion will occur when the armor sustains damage and blow back nearby creatures. All three effects increase in power with modifier level.");

		add(Embers.MODID + ".research.page.caster_orb", "Caster Orb");
		add(Embers.MODID + ".research.page.caster_orb.title", "Ember Launcher");
		add(Embers.MODID + ".research.page.caster_orb.tags", "Modifier;Augment;Tool;Weapon;Projectile;Missile;");
		add(Embers.MODID + ".research.page.caster_orb.desc", "The Caster Orb is a tool and weapon augment that creates projectiles of activated Ember. Whenever an item with this augment is swung, and sufficient Ember is accessible in the inventory, a small projectile of Ember will be fired forth. The size and damage of this projectile increase with modifier level.");
		add(Embers.MODID + ".research.page.caster_orb_addendum.desc", "Since the Caster Orb allows tools and weapons to shoot projectiles made of Ember, a tool or weapon augmented with it can also accept augments that modify projectiles.");

		add(Embers.MODID + ".research.page.flame_barrier", "Flame Barrier");
		add(Embers.MODID + ".research.page.flame_barrier.title", "Fiery Defenses");
		add(Embers.MODID + ".research.page.flame_barrier.tags", "Modifier;Augment;Armor;Fire;Shield;Barrier;Defense;");
		add(Embers.MODID + ".research.page.flame_barrier.desc", "The Flame Barrier is an armor augment that creates a shield of flames around the wearer when struck. Upon taking a hit, if this augment is present on a player's armor, the attacker will be ignited and take some damage. Each flame blast requires some Ember. This damage increases with modifier level.");

		add(Embers.MODID + ".research.page.eldritch_insignia", "Eldritch Insignia");
		add(Embers.MODID + ".research.page.eldritch_insignia.title", "Eerie Mark");
		add(Embers.MODID + ".research.page.eldritch_insignia.tags", "Modifier;Augment;Armor;Fear;Eerie;");
		add(Embers.MODID + ".research.page.eldritch_insignia.desc", "While the Eldritch Insignia augment is placed on your armor, certain mobs will become afraid of you. You will gain the ability to intimidate creatures that would otherwise attack into submission, only to attack back if provoked. The proportion of creatures that will fear the bearer of these symbols increases with modifier level.");

		add(Embers.MODID + ".research.page.intelligent_apparatus", "Intelligent Apparatus");
		add(Embers.MODID + ".research.page.intelligent_apparatus.title", "Fallen Wisdom");
		add(Embers.MODID + ".research.page.intelligent_apparatus.tags", "Modifier;Augment;Armor;Experience;Wisdom;");
		add(Embers.MODID + ".research.page.intelligent_apparatus.desc", "The Intelligent Apparatus is a simple armor augment that boosts experience obtained from creatures slain by the wearer of such augmented armor. The amount of additional experience dropped increases with modifier level.");

		add(Embers.MODID + ".research.page.resonating_bell", "Resonating Bell");
		add(Embers.MODID + ".research.page.resonating_bell.title", "Vibrations");
		add(Embers.MODID + ".research.page.resonating_bell.tags", "Modifier;Augment;Tool;Weapon;Light;Bell;");
		add(Embers.MODID + ".research.page.resonating_bell.desc", "The Resonating Bell allows a tool or weapon to ring through blocks and find particular objects. By right-clicking on a block, nearby blocks of that type will illuminate with glowing light, unless the block was already a very significant part of nearby material. The area the resonation effects increases with modifier level.");

		add(Embers.MODID + ".research.page.tinker_lens_augment", "Tinker's Lens");
		add(Embers.MODID + ".research.page.tinker_lens_augment.title", "What's What");
		add(Embers.MODID + ".research.page.tinker_lens_augment.tags", "Modifier;Augment;Armor;Info;");
		add(Embers.MODID + ".research.page.tinker_lens_augment.desc", "Holding this thing in your hand all the time is inconvenient. Fortunately, you can apply this to a helmet to receive the benefit of holding it all the time while wearing the helmet. This augment doesn't take up any slots, but it does need a Motive Core applied first.");

		add(Embers.MODID + ".research.page.anti_tinker_lens", "Smoky Tinker's Lens");
		add(Embers.MODID + ".research.page.anti_tinker_lens.title", "Blind to the Truth");
		add(Embers.MODID + ".research.page.anti_tinker_lens.tags", "Modifier;Augment;Armor;Info;");
		add(Embers.MODID + ".research.page.anti_tinker_lens.desc", "Sometimes, too much information can be a curse. Some helmets, like the Ashen Goggles can provide information about blocks like a Tinker's Lens, but this is not always desirable. This augment, when put on such a helmet, will turn this information off. This augment doesn't take up any slots, but it does need a Motive Core applied first.");

		add(Embers.MODID + ".research.page.mystical_mechanics", "Mystical Mechanics");
		add(Embers.MODID + ".research.page.mystical_mechanics.title", "Not the mod you're looking for");
		add(Embers.MODID + ".research.page.mystical_mechanics.tags", "Mechanical;Gear;Axle;Engine;");
		add(Embers.MODID + ".research.page.mystical_mechanics.desc", "Embers has Mystical Mechanics compatibility, but it's an optional dependency, and it's currently either turned off in the config, or Mystical Mechanics is not installed.");

		add(Embers.MODID + ".research.page.baubles", "Baubles");
		add(Embers.MODID + ".research.page.baubles.title", "Not the mod you're looking for");
		add(Embers.MODID + ".research.page.baubles.tags", "Bauble;Amulet;Ring;Belt;Charm;");
		add(Embers.MODID + ".research.page.baubles.desc", "Embers has Baubles compatibility, but it's an optional dependency, and it's currently either turned off in the config, or Baubles is not installed.");

		add(Embers.MODID + ".research.page.diffraction_barrel", "Diffraction Barrel");
		add(Embers.MODID + ".research.page.diffraction_barrel.title", "This... is my Boomstick!");
		add(Embers.MODID + ".research.page.diffraction_barrel.tags", "Modifier;Augment;Weapon;Projectile;Gun;Shotgun;Spread;");
		add(Embers.MODID + ".research.page.diffraction_barrel.desc", "The Blazing Ray can be a quite inaccurate weapon when fired in quick succession. This Barrel augment can be placed on not only the Blazing Ray, but any Ember projectile weapon, and will effectively turn said weapon into a shotgun of sorts. Note that instead of a ray, the Blazing Ray would fire a spread of projectiles instead.");

		add(Embers.MODID + ".research.page.focal_lens", "Focal Lens");
		add(Embers.MODID + ".research.page.focal_lens.title", "Piercing Blaze");
		add(Embers.MODID + ".research.page.focal_lens.tags", "Modifier;Augment;Weapon;Projectile;Homing;Pierce;");
		add(Embers.MODID + ".research.page.focal_lens.desc", "A Focal Lens augment can help firing precision when using Ember weaponry a lot. Dependent on whether you place it on a weapon that shoots fireballs or rays, it will either produce homing projectiles, or a piercing ray instead.");

		add(Embers.MODID + ".research.page.winding_gears", "Winding Gears");
		add(Embers.MODID + ".research.page.winding_gears.title", "Windup Toy");
		add(Embers.MODID + ".research.page.winding_gears.tags", "Modifier;Augment;Armor;Tool;Weapon;Gear;Wind;Spool;Bounce;");
		add(Embers.MODID + ".research.page.winding_gears.desc", "When Winding Gears are applied to a tool, a spool will be shown on your HUD in place of the experience bar to show how much it is wound up. Winding the tool up by right-clicking will grant you auto-attack (hold left-click to attack over and over). The tool will however wind down slowly over time. Winding up also has some other effects, as detailed on the following pages.");
		add(Embers.MODID + ".research.page.winding_gears_boots.desc", "Winding Gears can also be applied to your boots. You will jump much higher based on the charge left in the spring. You will also bounce without taking fall damage when impacting the ground. Additionally, if you sprint before jumping, you will also cross a much larger distance. You must also hold a wound up tool in hand to use these effect.");

		add(Embers.MODID + ".research.page.shifting_scales", "Shifting Scales");
		add(Embers.MODID + ".research.page.shifting_scales.title", "Extraneous Armor");
		add(Embers.MODID + ".research.page.shifting_scales.tags", "Modifier;Augment;Armor;Scale;Heart;Health;Defense;");
		add(Embers.MODID + ".research.page.shifting_scales.desc", "Shifting Scales can be applied to any piece of armor. While you are wearing the armor and are resting in place (not moving and not recently attacked), you will grow one third of a protective scale. On each hit you take, some of the scales will break away, and protect you from most external damage, but not from damage incurred by hunger or drowning.");

		add(Embers.MODID + ".research.page.pipes_category", "Pipes");
		add(Embers.MODID + ".research.page.mystical_mechanics_category", "Mystical Mechanics");
		add(Embers.MODID + ".research.page.simple_alchemy_category", "Simple Alchemy");
		add(Embers.MODID + ".research.page.wildfire_category", "Wildfire Core");
		add(Embers.MODID + ".research.page.baubles_category", "Baubles");
		add(Embers.MODID + ".research.page.weapon_augments_category", "Weapon Augments");
		add(Embers.MODID + ".research.page.armor_augments_category", "Armor Augments");
		add(Embers.MODID + ".research.page.projectile_augments_category", "Projectile Augments");
		add(Embers.MODID + ".research.page.misc_augments_category", "Other Augments");


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

	public void addDeco(StoneDecoBlocks deco, String name) {
		if (deco.stairs != null)
			addBlock(deco.stairs, name + " Stairs");
		if (deco.slab != null)
			addBlock(deco.slab, name + " Slab");
		if (deco.wall != null)
			addBlock(deco.wall, name + " Wall");
	}
}
