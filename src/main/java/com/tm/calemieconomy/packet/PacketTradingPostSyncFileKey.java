package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncFileKey extends PacketBlockEntity {

    private String fileKey;

    public PacketTradingPostSyncFileKey() {}

    public PacketTradingPostSyncFileKey(BlockPos pos, String fileKey) {
        super(pos);
        this.fileKey = fileKey;
    }

    public PacketTradingPostSyncFileKey(FriendlyByteBuf buf) {
        super(buf);
        this.fileKey = buf.readUtf(100);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeUtf(fileKey, 100);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.fileKey = fileKey;
            post.markUpdated();
        }
    }
}
