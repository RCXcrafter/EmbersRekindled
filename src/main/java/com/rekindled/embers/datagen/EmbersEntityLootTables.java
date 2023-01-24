package com.rekindled.embers.datagen;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.Registry;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;

public class EmbersEntityLootTables extends EntityLoot {

	@Nonnull
	@Override
	protected Iterable<EntityType<?>> getKnownEntities() {
		return ForgeRegistries.ENTITY_TYPES.getValues().stream()
				.filter((entity) -> Embers.MODID.equals(Objects.requireNonNull(Registry.ENTITY_TYPE.getKey(entity)).getNamespace()))
				.collect(Collectors.toList());
	}

	@Override
	protected void addTables() {
		add(RegistryManager.ANCIENT_GOLEM.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(RegistryManager.ARCHAIC_BRICK.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 11.0F)))
								.apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(RegistryManager.ANCIENT_MOTIVE_CORE.get()))));
	}
}
