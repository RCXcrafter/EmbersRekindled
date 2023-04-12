package com.rekindled.embers.block;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmberDialBlock extends DialBaseBlock {

	public static final String DIAL_TYPE = "ember";

	public EmberDialBlock(Properties pProperties) {
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
			IEmberCapability cap = blockEntity.getCapability(EmbersCapabilities.EMBER_CAPABILITY, state.getValue(FACING).getOpposite()).orElse(null);
			if (cap != null) {
				if (cap.getEmber() == cap.getEmberCapacity())
					return 15;
				return (int) (15.0 * cap.getEmber() / cap.getEmberCapacity());
			}
		}
		return 0;
	}

	@Override
	protected void getBEData(Direction facing, ArrayList<String> text, BlockEntity blockEntity, int maxLines) {
		IEmberCapability cap = blockEntity.getCapability(EmbersCapabilities.EMBER_CAPABILITY, facing).orElse(null);
		if (cap != null){
			text.add(formatEmber(cap.getEmber(), cap.getEmberCapacity()));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static String formatEmber(double ember, double emberCapacity) {
		DecimalFormat emberFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember");
		return I18n.get(Embers.MODID + ".tooltip.emberdial.ember", emberFormat.format(ember), emberFormat.format(emberCapacity));
	}

	@Override
	public String getDialType() {
		return DIAL_TYPE;
	}
}
