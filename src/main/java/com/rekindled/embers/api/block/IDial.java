package com.rekindled.embers.api.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IDial {
    List<String> getDisplayInfo(Level world, BlockPos pos, BlockState state);

    void updateBEData(Level world, BlockState state, BlockPos pos);

    String getDialType();
}
