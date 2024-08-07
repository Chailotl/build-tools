package com.chailotl.build_tools;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CloudBlock extends BlockWithEntity implements BlockEntityProvider
{
	public CloudBlock(Settings settings)
	{
		super(settings.noCollision().sounds(BlockSoundGroup.POWDER_SNOW).nonOpaque().allowsSpawning(CloudBlock::never).solidBlock(CloudBlock::never).suffocates(CloudBlock::never).blockVision(CloudBlock::never));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec()
	{
		return null;
	}

	private static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos)
	{
		return false;
	}

	private static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType)
	{
		return false;
	}

	// Transparent block stuff
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction)
	{
		return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
	}

	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.empty();
	}

	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
	{
		return 1.0F;
	}

	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos)
	{
		return true;
	}

	// Block entity stuff
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new CloudBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		return world.isClient ? null : validateTicker(type, Main.CLOUD_BLOCK_ENTITY, CloudBlockEntity::tick);
	}
}