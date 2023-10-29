package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostResetCurrentPrice extends PacketBlockEntity {

    public PacketTradingPostResetCurrentPrice() {}

    public PacketTradingPostResetCurrentPrice(BlockPos pos) {
        super(pos);
    }

    public PacketTradingPostResetCurrentPrice(FriendlyByteBuf buf) {
        super(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.price.resetCurrentPrice();
            post.markUpdated();
        }
    }
}
