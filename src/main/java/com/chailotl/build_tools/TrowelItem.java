package com.chailotl.build_tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TrowelItem extends Item
{
	public TrowelItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		World world = context.getWorld();

		PlayerEntity player = context.getPlayer();
		PlayerInventory inventory = player.getInventory();

		// Find valid slots
		List<Integer> slots = new ArrayList<>();
		for (int i = 0; i < 9; ++i)
		{
			Item item = inventory.getStack(i).getItem();

			// FluidModificationItem will probably replace with an empty bucket
			if (item instanceof BlockItem && !(item instanceof FluidModificationItem))
			{
				slots.add(i);
			}
		}

		if (slots.isEmpty()) { return ActionResult.PASS; }
		if (world.isClient) { return ActionResult.SUCCESS; }

		int slot = slots.get(world.random.nextInt(slots.size()));

		// Place block
		ItemStack stack = inventory.getStack(slot);

		ActionResult result = stack.useOnBlock(context);
		if (result.isAccepted())
		{
			Block block = ((BlockItem) stack.getItem()).getBlock();
			BlockSoundGroup soundGroup = block.getDefaultState().getSoundGroup();
			world.playSound(null, context.getBlockPos(), soundGroup.getPlaceSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1.0F) / 2.0F, soundGroup.getPitch() * 0.8F);

			if (!player.getAbilities().creativeMode)
			{
				stack.decrement(1);
				context.getStack().setCount(1);
			}

			return ActionResult.success(world.isClient);
		}
		else
		{
			return ActionResult.FAIL;
		}
	}
}