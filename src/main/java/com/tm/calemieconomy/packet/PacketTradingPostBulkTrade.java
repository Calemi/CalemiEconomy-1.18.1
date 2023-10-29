package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostBulkTrade extends PacketBlockEntity {

    private int sets;

    public PacketTradingPostBulkTrade() {}

    public PacketTradingPostBulkTrade(BlockPos pos, int sets) {
        super(pos);
        this.sets = sets;
    }

    public PacketTradingPostBulkTrade(FriendlyByteBuf buf) {
        super(buf);
        this.sets = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(sets);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            int amount = post.tradeAmount * sets;
            long price = post.price.getBulkPrice(sets);
            BlockTradingPost.handleTrade(player.getLevel(), player, post, sets, amount, price);
        }
    }
}
