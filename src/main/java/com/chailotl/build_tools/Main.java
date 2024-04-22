package com.chailotl.build_tools;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer, ClientModInitializer
{
	public static final String MOD_ID = "build_tools";
   public static final Logger LOGGER = LoggerFactory.getLogger("build_tools");

	// Blocks
	public static final Block CLOUD_BLOCK = new CloudBlock(Block.Settings.create().strength(0));
	public static final BlockEntityType<CloudBlockEntity> CLOUD_BLOCK_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		new Identifier(MOD_ID, "cloud"),
		FabricBlockEntityTypeBuilder.create(CloudBlockEntity::new, CLOUD_BLOCK).build()
	);

	// Items
	public static final Item WRENCH = new WrenchItem(new FabricItemSettings().maxCount(1));
	public static final Item TROWEL = new TrowelItem(new FabricItemSettings().maxCount(1));
	public static final Item CLOUD_BUCKET = new CloudBucketItem(CLOUD_BLOCK, SoundEvents.BLOCK_POWDER_SNOW_PLACE, (new Item.Settings()).maxCount(1));

	@Override
	public void onInitialize()
	{
		//LOGGER.info("Hello Fabric world!");

		// Items
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "wrench"), WRENCH);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "trowel"), TROWEL);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "cloud_bucket"), CLOUD_BUCKET);

		// Blocks
		Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "cloud"), CLOUD_BLOCK);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
			content.addAfter(Items.BRUSH, WRENCH, TROWEL);
			content.addAfter(Items.POWDER_SNOW_BUCKET, CLOUD_BUCKET);
		});
	}

	@Override
	public void onInitializeClient()
	{
		BlockRenderLayerMap.INSTANCE.putBlock(CLOUD_BLOCK, RenderLayer.getTranslucent());
	}
}