package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncPrice extends PacketBlockEntity {

    private long price;

    public PacketTradingPostSyncPrice() {}

    public PacketTradingPostSyncPrice(BlockPos pos, long price) {
        super(pos);
        this.price = price;
    }

    public PacketTradingPostSyncPrice(FriendlyByteBuf buf) {
        super(buf);
        this.price = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeLong(price);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.price.setStartingPrice(price);
            post.markUpdated();
        }
    }
}
