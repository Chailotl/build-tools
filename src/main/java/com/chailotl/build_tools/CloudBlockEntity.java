package com.chailotl.build_tools;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CloudBlockEntity extends BlockEntity
{
	private int life = 0;
	private int lifetime = 20 * 4;

	public CloudBlockEntity(BlockPos pos, BlockState state)
	{
		super(Main.CLOUD_BLOCK_ENTITY, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, CloudBlockEntity be)
	{
		//Main.LOGGER.info("hello world!");
		if (be.life++ >= be.lifetime)
		{
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}
}