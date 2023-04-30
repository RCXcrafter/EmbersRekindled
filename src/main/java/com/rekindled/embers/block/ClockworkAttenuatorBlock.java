package com.rekindled.embers.block;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.ClockworkAttenuatorBlockEntity;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ClockworkAttenuatorBlock extends DialBaseBlock implements EntityBlock {

	public static final String DIAL_TYPE = "work";

	public ClockworkAttenuatorBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public List<String> getDisplayInfo(Level world, BlockPos pos, BlockState state, int maxLines) {
		List<String> text = super.getDisplayInfo(world, pos, state, maxLines);
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof ClockworkAttenuatorBlockEntity) {
			DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.attenuator_multiplier");
			double activeSpeed = ((ClockworkAttenuatorBlockEntity) tile).activeSpeed;
			double inactiveSpeed = ((ClockworkAttenuatorBlockEntity) tile).inactiveSpeed;
			boolean active = world.hasNeighborSignal(pos);
			text.add((active ? ChatFormatting.GREEN : ChatFormatting.DARK_GREEN)+I18n.get(Embers.MODID + ".tooltip.attenuator.on", multiplierFormat.format(activeSpeed)));
			text.add((!active ? ChatFormatting.RED : ChatFormatting.DARK_RED)+I18n.get(Embers.MODID + ".tooltip.attenuator.off", multiplierFormat.format(inactiveSpeed)));
		}
		return text;
	}

	@Override
	protected void getBEData(Direction facing, ArrayList<String> text, BlockEntity blockEntity, int maxLines) {}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ClockworkAttenuatorBlockEntity attenuatorEntity) {
			if (level.hasNeighborSignal(pos))
				attenuatorEntity.activeSpeed = player.isSecondaryUseActive() ? attenuatorEntity.getPrevious(attenuatorEntity.activeSpeed) : attenuatorEntity.getNext(attenuatorEntity.activeSpeed);
			else
				attenuatorEntity.inactiveSpeed = player.isSecondaryUseActive() ? attenuatorEntity.getPrevious(attenuatorEntity.inactiveSpeed) : attenuatorEntity.getNext(attenuatorEntity.inactiveSpeed);
			attenuatorEntity.setChanged();
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.CLOCKWORK_ATTENUATOR_ENTITY.get().create(pPos, pState);
	}

	@Override
	public String getDialType() {
		return DIAL_TYPE;
	}
}
