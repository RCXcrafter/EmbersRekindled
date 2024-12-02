package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.rekindled.embers.api.misc.AlchemyResult;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public abstract class AlchemyRecipeBase implements IAlchemyRecipe {

	public final ResourceLocation id;

	public final Ingredient tablet;
	public final ArrayList<Ingredient> aspects;
	public final ArrayList<Ingredient> inputs;

	public final ItemStack output;
	public final ItemStack failure;

	public Long cachedSeed = null;
	public ArrayList<Ingredient> code = new ArrayList<Ingredient>();

	public AlchemyRecipeBase(ResourceLocation id, Ingredient tablet, ArrayList<Ingredient> aspects, ArrayList<Ingredient> inputs, ItemStack output, ItemStack failure) {
		this.id = id;
		this.tablet = tablet;
		this.aspects = aspects;
		this.inputs = inputs;
		this.output = output;
		this.failure = failure;
	}

	@Override
	public ArrayList<Ingredient> getCode(long seed) {
		if (cachedSeed == null || cachedSeed != seed) {
			code.clear();
			Random rand = new Random(seed - id.getPath().hashCode());
			for (int i = 0; i < inputs.size(); i++) {
				code.add(aspects.get(rand.nextInt(aspects.size())));
			}
			cachedSeed = seed;
		}
		return code;
	}

	@Override
	public boolean matches(AlchemyContext context, Level pLevel) {
		if (!tablet.test(context.tablet) || inputs.size() != context.contents.size())
			return false;

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			boolean matched = false;
			for (int j = 0; j < remaining.size(); j++) {
				if (inputs.get(i).test(remaining.get(j).input)) {
					matched = true;
					remaining.remove(j);
					break;
				}
			}
			if (!matched)
				return false;
		}
		return true;
	}

	@Override
	public boolean matchesCorrect(AlchemyContext context, Level pLevel) {
		getCode(context.seed);
		if (!tablet.test(context.tablet) || code.size() != context.contents.size())
			return false;

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			boolean matched = false;
			for (int j = 0; j < remaining.size(); j++) {
				if (code.get(i).test(remaining.get(j).aspect) && inputs.get(i).test(remaining.get(j).input)) {
					matched = true;
					remaining.remove(j);
					break;
				}
			}
			if (!matched)
				return false;
		}
		return true;
	}

	@Override
	public ItemStack assemble(AlchemyContext context, RegistryAccess registry) {
		getCode(context.seed);
		int blackPins = 0;
		int whitePins = 0;

		ArrayList<Ingredient> remainingCode = new ArrayList<Ingredient>(code);
		for (int i = 0; i < context.contents.size(); i++) {
			for (int j = 0; j < remainingCode.size(); j++) {
				if (remainingCode.get(j).test(context.contents.get(i).aspect)) {
					whitePins++;
					remainingCode.remove(j);
					break;
				}
			}
		}

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			for (int j = 0; j < remaining.size(); j++) {
				if (code.get(i).test(remaining.get(j).aspect) && inputs.get(i).test(remaining.get(j).input)) {
					blackPins++;
					remaining.remove(j);
					break;
				}
			}
		}
		whitePins -= blackPins;

		if (blackPins < code.size()) {
			ItemStack waste = failure.copy();
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("blackPins", blackPins);
			nbt.putInt("whitePins", whitePins);

			ListTag aspectNBT = new ListTag();
			ListTag inputNBT = new ListTag();
			for (PedestalContents contents : context.contents) {
				aspectNBT.add(contents.aspect.serializeNBT());
				inputNBT.add(contents.input.serializeNBT());
			}
			nbt.put("aspects", aspectNBT);
			nbt.put("inputs", inputNBT);

			waste.setTag(nbt);
			return waste;
		}
		return output;
	}

	@Override
	public AlchemyResult getResult(AlchemyContext context) {
		getCode(context.seed);
		int blackPins = 0;
		int whitePins = 0;

		ArrayList<Ingredient> remainingCode = new ArrayList<Ingredient>(code);
		for (int i = 0; i < context.contents.size(); i++) {
			for (int j = 0; j < remainingCode.size(); j++) {
				if (remainingCode.get(j).test(context.contents.get(i).aspect)) {
					whitePins++;
					remainingCode.remove(j);
					break;
				}
			}
		}

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			for (int j = 0; j < remaining.size(); j++) {
				if (code.get(i).test(remaining.get(j).aspect) && inputs.get(i).test(remaining.get(j).input)) {
					blackPins++;
					remaining.remove(j);
					break;
				}
			}
		}
		whitePins -= blackPins;

		//ensure that the ingredient order matches the recipe
		List<PedestalContents> contents = new ArrayList<PedestalContents>(context.contents);
		List<PedestalContents> sortedContents = new ArrayList<PedestalContents>();
		for (Ingredient input : inputs) {
			for (PedestalContents pedestal : contents) {
				if (input.test(pedestal.input)) {
					sortedContents.add(pedestal);
					contents.remove(pedestal);
					break;
				}
			}
		}

		return new AlchemyResult(sortedContents, getResultItem(), blackPins, whitePins); //TODO: failures too?
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public Ingredient getCenterInput() {
		return tablet;
	}

	@Override
	public List<Ingredient> getInputs() {
		return inputs;
	}

	@Override
	public List<Ingredient> getAspects() {
		return aspects;
	}

	@Override
	public ItemStack getResultItem() {
		return output;
	}

	@Override
	public ItemStack getfailureItem() {
		return failure;
	}
}
