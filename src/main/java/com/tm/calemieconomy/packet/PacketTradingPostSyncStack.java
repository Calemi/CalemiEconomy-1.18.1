package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostSyncStack extends PacketBlockEntity {

    private ItemStack stackForSale;

    public PacketTradingPostSyncStack() {}

    public PacketTradingPostSyncStack(BlockPos pos, ItemStack stackForSale) {
        super(pos);
        this.stackForSale = stackForSale;
    }

    public PacketTradingPostSyncStack(FriendlyByteBuf buf) {
        super(buf);
        this.stackForSale = buf.readItem();
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeItem(stackForSale);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {
            post.setStackForSale(stackForSale);;
            post.markUpdated();
        }
    }
}
