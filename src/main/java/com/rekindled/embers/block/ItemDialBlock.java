package com.rekindled.embers.block;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.rekindled.embers.Embers;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class ItemDialBlock extends DialBaseBlock {

	public static final String DIAL_TYPE = "item";

	public ItemDialBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos.relative(state.getValue(FACING), -1));
		if (blockEntity != null) {
			IItemHandler cap = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, state.getValue(FACING).getOpposite()).orElse(null);
			if (cap != null) {
				double contents = 0.0;
				double capacity = 0.0;
				for (int i = 0; i < cap.getSlots(); i++) {
					contents += cap.getStackInSlot(i).getCount();
					capacity += cap.getSlotLimit(i);
				}
				if (contents == capacity)
					return 15;
				return (int) (15.0 * contents / capacity);
			}
		}
		return 0;
	}

	@Override
	protected void getBEData(Direction facing, ArrayList<String> text, BlockEntity blockEntity, int maxLines) {
		IItemHandler cap = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facing).orElse(null);
		if (cap != null){
			for (int i = 0; i < cap.getSlots() && i < maxLines; i++) {
				ItemStack stack = cap.getStackInSlot(i);
				String item;
				item = formatItemStack(stack);
				text.add(I18n.get(Embers.MODID + ".tooltip.itemdial.slot", i, item));
			}
			if (cap.getSlots() > maxLines) {
				text.add(I18n.get(Embers.MODID + ".tooltip.itemdial.too_many", cap.getSlots() - maxLines + 1));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static String formatItemStack(ItemStack stack) {
		DecimalFormat stackFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.item_amount");
		if (!stack.isEmpty())
			return I18n.get(Embers.MODID + ".tooltip.itemdial.item", stackFormat.format(stack.getCount()), stack.getHoverName().getString());
		else
			return I18n.get(Embers.MODID + ".tooltip.itemdial.noitem");
	}

	@Override
	public String getDialType() {
		return DIAL_TYPE;
	}
}
