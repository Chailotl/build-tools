package com.chailotl.build_tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PowderSnowBucketItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CloudBucketItem extends PowderSnowBucketItem
{
	public CloudBucketItem(Block block, SoundEvent placeSound, Settings settings)
	{
		super(block, placeSound, settings);
	}

	private ActionResult originalUseOnBlock(ItemUsageContext context)
	{
		ActionResult actionResult = this.place(new ItemPlacementContext(context));
		if (!actionResult.isAccepted() && this.isFood())
		{
			ActionResult actionResult2 = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
			return actionResult2 == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : actionResult2;
		}
		else
		{
			return actionResult;
		}
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		ActionResult actionResult = originalUseOnBlock(context);
		context.getStack().setCount(1); // Do not remove

		return actionResult;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		Vec3d vec3d = user.getEyePos();
		Vec3d vec3d2 = user.getRotationVector(user.getPitch(), user.getYaw());
		ItemStack itemStack = user.getStackInHand(hand);
		Vec3d vec3d4 = vec3d.add(vec3d2.multiply(3.0));
		ActionResult actionResult = super.place(new ItemPlacementContext(user, hand, itemStack, new BlockHitResult(vec3d4, user.getHorizontalFacing(), BlockPos.ofFloored(vec3d4), false)));
		return new TypedActionResult<>(actionResult, itemStack);
	}
}