package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncVaryRate extends PacketBlockEntity {

    private float varyRate;

    public PacketTradingPostSyncVaryRate() {}

    public PacketTradingPostSyncVaryRate(BlockPos pos, float varyRate) {
        super(pos);
        this.varyRate = varyRate;
    }

    public PacketTradingPostSyncVaryRate(FriendlyByteBuf buf) {
        super(buf);
        this.varyRate = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeFloat(varyRate);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.price.varyRate = varyRate;
            post.markUpdated();
        }
    }
}
