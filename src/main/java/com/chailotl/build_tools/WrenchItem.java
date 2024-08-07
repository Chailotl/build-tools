package com.chailotl.build_tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WrenchItem extends Item
{
	public WrenchItem(Settings settings)
	{
		super(settings);
	}

	private BlockState getNextState(BlockState state)
	{
		BlockState newState = null;

		if (state.contains(Properties.SLAB_TYPE))
		{
			SlabType slabType = switch(state.get(Properties.SLAB_TYPE))
			{
				case TOP -> SlabType.BOTTOM;
				case BOTTOM -> SlabType.TOP;
				default -> state.get(Properties.SLAB_TYPE);
			};

			newState = state.with(Properties.SLAB_TYPE, slabType);
		}
		else if (state.contains(Properties.ROTATION))
		{
			int rot = state.get(Properties.ROTATION) + 1;

			if (rot == 16) { rot = 0; }

			newState = state.with(Properties.ROTATION, rot);
		}
		else if (state.contains(Properties.AXIS))
		{
			Direction.Axis axis = switch(state.get(Properties.AXIS))
			{
				case X -> Direction.Axis.Y;
				case Y -> Direction.Axis.Z;
				case Z -> Direction.Axis.X;
			};

			newState = state.with(Properties.AXIS, axis);
		}
		else if (state.contains(Properties.FACING))
		{
			Direction direction = switch (state.get(Properties.FACING)) {
				case UP -> Direction.NORTH;
				case NORTH -> Direction.EAST;
				case EAST -> Direction.SOUTH;
				case SOUTH -> Direction.WEST;
				case WEST -> Direction.DOWN;
				case DOWN -> Direction.UP;
			};

			newState = state.with(Properties.FACING, direction);
		}
		else if (state.contains(Properties.HOPPER_FACING))
		{
			Direction direction = switch(state.get(Properties.HOPPER_FACING))
			{
				case DOWN -> Direction.NORTH;
				case NORTH -> Direction.EAST;
				case EAST -> Direction.SOUTH;
				case SOUTH -> Direction.WEST;
				case WEST -> Direction.DOWN;
				default -> state.get(Properties.HOPPER_FACING);
			};

			newState = state.with(Properties.HOPPER_FACING, direction);
		}
		else if (state.contains(Properties.HORIZONTAL_FACING))
		{
			Direction direction = switch(state.get(Properties.HORIZONTAL_FACING)) {
				case NORTH -> Direction.EAST;
				case EAST -> Direction.SOUTH;
				case SOUTH -> Direction.WEST;
				case WEST -> Direction.NORTH;
				default -> state.get(Properties.HORIZONTAL_FACING);
			};

			newState = state.with(Properties.HORIZONTAL_FACING, direction);

			if (state.contains(Properties.BLOCK_HALF) && state.get(Properties.HORIZONTAL_FACING) == Direction.WEST)
			{
				BlockHalf blockHalf = state.get(Properties.BLOCK_HALF);
				newState = newState.with(Properties.BLOCK_HALF, blockHalf == BlockHalf.TOP ? BlockHalf.BOTTOM : BlockHalf.TOP);
			}

			if (state.contains(Properties.ATTACHMENT) && state.get(Properties.HORIZONTAL_FACING) == Direction.WEST)
			{
				Attachment attachment = switch(state.get(Properties.ATTACHMENT)) {
					case FLOOR -> Attachment.SINGLE_WALL;
					case SINGLE_WALL -> Attachment.DOUBLE_WALL;
					case DOUBLE_WALL -> Attachment.CEILING;
					case CEILING -> Attachment.FLOOR;
				};

				newState = newState.with(Properties.ATTACHMENT, attachment);
			}

			if (state.contains(Properties.BLOCK_FACE) && state.get(Properties.HORIZONTAL_FACING) == Direction.WEST)
			{
				BlockFace blockFace = switch(state.get(Properties.BLOCK_FACE)) {
					case FLOOR -> BlockFace.WALL;
					case WALL -> BlockFace.CEILING;
					case CEILING -> BlockFace.FLOOR;
				};

				newState = newState.with(Properties.BLOCK_FACE, blockFace);
			}
		}
		else if (state.contains(Properties.STRAIGHT_RAIL_SHAPE))
		{
			RailShape railShape = switch(state.get(Properties.STRAIGHT_RAIL_SHAPE))
			{
				case NORTH_SOUTH -> RailShape.EAST_WEST;
				case EAST_WEST -> RailShape.NORTH_SOUTH;
				default -> state.get(Properties.STRAIGHT_RAIL_SHAPE);
			};

			newState = state.with(Properties.STRAIGHT_RAIL_SHAPE, railShape);
		}
		else if (state.contains(Properties.RAIL_SHAPE))
		{
			RailShape railShape = switch(state.get(Properties.RAIL_SHAPE))
			{
				case NORTH_SOUTH -> RailShape.EAST_WEST;
				case EAST_WEST -> RailShape.NORTH_EAST;
				case NORTH_EAST -> RailShape.SOUTH_EAST;
				case SOUTH_EAST -> RailShape.SOUTH_WEST;
				case SOUTH_WEST -> RailShape.NORTH_WEST;
				case NORTH_WEST -> RailShape.NORTH_SOUTH;
				default -> state.get(Properties.RAIL_SHAPE);
			};

			newState = state.with(Properties.RAIL_SHAPE, railShape);
		}
		else if (state.contains(Properties.HANGING))
		{
			newState = state.with(Properties.HANGING, !state.get(Properties.HANGING));
		}

		return newState;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		BlockState newState = null;
		Block block = state.getBlock();

		// Ignore some blocks and properties
		if (block == Blocks.END_PORTAL_FRAME ||
			(state.contains(Properties.CHEST_TYPE) && state.get(Properties.CHEST_TYPE) != ChestType.SINGLE) ||
			(state.contains(Properties.EXTENDED) && state.get(Properties.EXTENDED) == true))
		{
			return ActionResult.PASS;
		}

		// Get next state
		newState = getNextState(state);

		while (newState != null && !newState.canPlaceAt(world, pos))
		{
			newState = getNextState(newState);

			// Break if looped
			if (newState.equals(state))
			{
				newState = null;
				break;
			}
		}

		// Apply block state
		if (newState != null)
		{
			context.getPlayer().playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
			world.setBlockState(pos, newState);
			return ActionResult.SUCCESS;
		}
		else
		{
			return ActionResult.PASS;
		}
	}
}