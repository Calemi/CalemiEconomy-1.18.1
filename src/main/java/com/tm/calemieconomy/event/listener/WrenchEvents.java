package com.tm.calemieconomy.event.listener;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemieconomy.util.IBlockCurrencyHolder;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WrenchEvents {

    /**
     * Called when a Block is taken by a Security Wrench.
     * Also saves the currency of the Block within the drop.
     */
    public static void onBlockWrenched (Location location) {

        BlockEntity blockEntity = location.getBlockEntity();
        ItemStack stack = new ItemStack(location.getBlock().asItem(), 1);

        if (!location.level.isClientSide()) {
            ItemHelper.spawnStackAtLocation(location.level, location, stack);
        }

        //Handles currency saving.
        if (blockEntity instanceof IBlockCurrencyHolder currencyHolder) {

            if (currencyHolder.getCurrency() > 0) {
                CompoundTag tag = stack.getOrCreateTag();
                CurrencyHelper.saveToNBT(tag, currencyHolder.getCurrency());
                stack.setTag(tag);
            }
        }

        location.setBlockToAir();
    }

    /**
     * Handles transforming the Item's NBT into currency for the Block.
     */
    @SubscribeEvent
    public void onBlockPlace (BlockEvent.EntityPlaceEvent event) {

        //Checks if the entity is a Player.
        if (event.getEntity() instanceof Player player) {

            BlockEntity blockEntity = event.getWorld().getBlockEntity(event.getPos());

            if (blockEntity instanceof BlockEntityBase blockEntityBase && blockEntity instanceof IBlockCurrencyHolder currencyHolder) {
                ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                ItemStack offStack = player.getItemInHand(InteractionHand.OFF_HAND);

                if (transferCurrencyToBlock(mainStack, blockEntityBase, currencyHolder));
                else transferCurrencyToBlock(offStack, blockEntityBase, currencyHolder);
            }
        }
    }

    /**
     * Used to check if a stack hold currency, and if it does, transfer it to the block.
     * Called twice for main & off hand.
     */
    private boolean transferCurrencyToBlock(ItemStack stack, BlockEntityBase blockEntity, IBlockCurrencyHolder currencyHolder) {

        //Checks if the held Item is a Block.
        if (stack.getItem() instanceof BlockItem) {

            long currency = CurrencyHelper.loadFromNBT(stack.getOrCreateTag());

            if (currency != 0) {
                currencyHolder.setCurrency(currency);
                blockEntity.markUpdated();
            }

            return true;
        }

        return false;
    }
}
