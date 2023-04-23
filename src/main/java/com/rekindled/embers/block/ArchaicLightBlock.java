package com.rekindled.embers.block;

import com.rekindled.embers.particle.GlowParticleOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ArchaicLightBlock extends Block {

	public static final GlowParticleOptions EMBER = new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, 2.0F, 120);

	public ArchaicLightBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		for (int i = 0; i < 12; i ++) {
			int chance = random.nextInt(3);
			if (chance == 0) {
				level.addParticle(EMBER, pos.getX()-0.03125f+1.0625f*random.nextInt(2), pos.getY()+0.125f+0.75f*random.nextFloat(), pos.getZ()+0.125f+0.75f*random.nextFloat(), (random.nextFloat()-0.5f)*0.03f, (random.nextFloat())*0.03f, (random.nextFloat()-0.5f)*0.03f);
			} else if (chance == 1) {
				level.addParticle(EMBER, pos.getX()+0.125f+0.75f*random.nextFloat(), pos.getY()-0.03125f+1.0625f*random.nextInt(2), pos.getZ()+0.125f+0.75f*random.nextFloat(), (random.nextFloat()-0.5f)*0.03f, (random.nextFloat())*0.03f, (random.nextFloat()-0.5f)*0.03f);
			} else if (chance == 2) {
				level.addParticle(EMBER, pos.getX()+0.125f+0.75f*random.nextFloat(), pos.getY()+0.125f+0.75f*random.nextFloat(), pos.getZ()-0.03125f+1.0625f*random.nextInt(2), (random.nextFloat()-0.5f)*0.03f, (random.nextFloat())*0.03f, (random.nextFloat()-0.5f)*0.03f);
			}
		}
	}
}
