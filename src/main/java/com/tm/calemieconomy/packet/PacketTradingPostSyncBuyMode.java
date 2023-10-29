package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncBuyMode extends PacketBlockEntity {

    private boolean buyMode;

    public PacketTradingPostSyncBuyMode() {}

    public PacketTradingPostSyncBuyMode(BlockPos pos, boolean buyMode) {
        super(pos);
        this.buyMode = buyMode;
    }

    public PacketTradingPostSyncBuyMode(FriendlyByteBuf buf) {
        super(buf);
        this.buyMode = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(buyMode);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {

            if (post.buyMode != buyMode) {
                post.price.setExtremum(post.price.getStartingPrice());
            }

            post.buyMode = buyMode;
            post.markUpdated();
        }
    }
}
