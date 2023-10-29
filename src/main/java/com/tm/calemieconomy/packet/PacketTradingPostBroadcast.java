package com.tm.calemieconomy.packet;

import com.tm.calemicore.util.helper.ChatHelper;
import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.config.CEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPostBroadcast extends PacketBlockEntity {

    public PacketTradingPostBroadcast() {}

    public PacketTradingPostBroadcast(BlockPos pos) {
        super(pos);
    }

    public PacketTradingPostBroadcast(FriendlyByteBuf buf) {
        super(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx, ServerPlayer player, BlockEntity blockEntity) {

        if (blockEntity instanceof BlockEntityTradingPost post) {

            if (post.hasValidTradeOffer)  {

                if (post.broadcastDelay <= 0) {

                    MutableComponent info = post.getTradeInfo().append(" ").append(post.getPriceInfo(false)).append(" ").append(new TranslatableComponent(post.msgKey + "at")).append(" ").append(post.getLocationInfo());
                    ChatHelper.broadcastMessage(player.getLevel(), info);
                    post.broadcastDelay = CEConfig.economy.tradingPostBroadcastDelay.get();
                }

                else BlockTradingPost.MESSENGER.sendErrorMessage(BlockTradingPost.MESSENGER.getMessage("error.wait", post.broadcastDelay), player);
            }

            else BlockTradingPost.MESSENGER.sendErrorMessage(BlockTradingPost.MESSENGER.getMessage("error.invalid"), player);
        }
    }
}
