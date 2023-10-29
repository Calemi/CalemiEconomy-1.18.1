package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncDynamicPriceMode extends PacketBlockEntity {

    private boolean isDynamic;

    public PacketTradingPostSyncDynamicPriceMode() {}

    public PacketTradingPostSyncDynamicPriceMode(BlockPos pos, boolean isDynamic) {
        super(pos);
        this.isDynamic = isDynamic;
    }

    public PacketTradingPostSyncDynamicPriceMode(FriendlyByteBuf buf) {
        super(buf);
        this.isDynamic = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(isDynamic);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.price.isDynamic = isDynamic;

            if (!post.price.isDynamic) {
                post.price.resetCurrentPrice();
            }

            post.markUpdated();
        }
    }
}
