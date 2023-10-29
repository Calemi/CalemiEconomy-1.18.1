package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSellAll extends PacketBlockEntity {

    public PacketTradingPostSellAll() {}

    public PacketTradingPostSellAll(BlockPos pos) {
        super(pos);
    }

    public PacketTradingPostSellAll(FriendlyByteBuf buf) {
        super(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {

            int foundCount = 0;

            for (ItemStack stack : player.getInventory().items) {

                if (ItemStack.isSameItemSameTags(stack, post.getStackForSale())) {
                    foundCount += stack.getCount();
                }
            }

            if (foundCount <= 0) {
                return;
            }

            int sets = foundCount / post.tradeAmount;
            int amount = post.tradeAmount * sets;

            long price = post.price.getBulkPrice(sets);
            BlockTradingPost.handleTrade(player.getLevel(), player, post, sets, amount, price);
        }
    }
}
