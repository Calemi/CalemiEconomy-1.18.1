package com.tm.calemieconomy.packet;

import com.tm.calemicore.util.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class PacketBlockEntity {

    private BlockPos pos;

    public PacketBlockEntity() {}

    public PacketBlockEntity(BlockPos pos) {
        this.pos = pos;
    }

    public PacketBlockEntity(FriendlyByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public abstract void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity);

    public void handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player == null) {
                return;
            }

            Location location = new Location(player.getLevel(), pos);

            if (location.getBlockEntity() == null) {
                return;
            }

            handle(ctx, player, location.getBlockEntity());
        });

        ctx.get().setPacketHandled(true);
    }
}
