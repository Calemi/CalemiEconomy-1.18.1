package com.tm.calemieconomy.packet;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.ChatHelper;
import com.tm.calemieconomy.block.BlockTradingPost;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.config.CEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPost {

    private String command;
    private BlockPos pos;
    private ItemStack stackForSale;
    private boolean buyMode;
    private int tradeAmount;
    private int tradePrice;

    public PacketTradingPost () {}

    /**
     * Used to sync the data of the Trading Post.
     * @param command Used to determine the type of packet to send.
     * @param pos The Block position of the Tile Entity.
     * @param stackForSale The Item Stack's string conversion.
     * @param buyMode The state of the buyMode option.
     * @param tradeAmount The number of the amount option.
     * @param tradePrice The number of the price option.
     */
    public PacketTradingPost (String command, BlockPos pos, ItemStack stackForSale, boolean buyMode, int tradeAmount, int tradePrice) {
        this.command = command;
        this.pos = pos;
        this.stackForSale = stackForSale;
        this.buyMode = buyMode;
        this.tradeAmount = tradeAmount;
        this.tradePrice = tradePrice;
    }

    /**
     * Use this constructor to broadcast.
     */
    public PacketTradingPost (String command, BlockPos pos) {
        this(command, pos, ItemStack.EMPTY, false, 0, 0);
    }

    /**
     * Use this constructor to sync the current mode.
     */
    public PacketTradingPost (String command, BlockPos pos, boolean buyMode) {
        this(command, pos, ItemStack.EMPTY, buyMode, 0, 0);
    }

    /**
     * Use this constructor to sync the stack for sale.
     */
    public PacketTradingPost (String command, BlockPos pos, ItemStack stack) {
        this(command, pos, stack, false, 0, 0);
    }

    /**
     * Use this constructor to sync the options
     */
    public PacketTradingPost (String command, BlockPos pos, int tradeAmount, int tradePrice) {
        this(command, pos, ItemStack.EMPTY, false, tradeAmount, tradePrice);
    }

    public PacketTradingPost (FriendlyByteBuf buf) {
        command = buf.readUtf(11).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        stackForSale = buf.readItem();
        buyMode = buf.readBoolean();
        tradeAmount = buf.readInt();
        tradePrice = buf.readInt();
    }

    public void toBytes (FriendlyByteBuf buf) {
        buf.writeUtf(command, 11);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeItem(stackForSale);
        buf.writeBoolean(buyMode);
        buf.writeInt(tradeAmount);
        buf.writeInt(tradePrice);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.getLevel(), pos);

                //Checks if the Tile Entity is a Trading Post.
                if (location.getBlockEntity() instanceof BlockEntityTradingPost post) {

                    //Handles broadcasting.
                    if (command.equalsIgnoreCase("broadcast")) {

                        if (post.hasValidTradeOffer)  {

                            if (post.broadcastDelay <= 0) {

                                ChatHelper.broadcastMessage(player.getLevel(), post.getTradeInfo(true));
                                post.broadcastDelay = CEConfig.economy.tradingPostBroadcastDelay.get();
                            }

                            else BlockTradingPost.MESSENGER.sendErrorMessage(BlockTradingPost.MESSENGER.getMessage("error.wait", post.broadcastDelay), player);
                        }

                        else BlockTradingPost.MESSENGER.sendErrorMessage(BlockTradingPost.MESSENGER.getMessage("error.invalid"), player);
                    }

                    //Handles syncing the buyMode option.
                    if (command.equalsIgnoreCase("syncmode")) {
                        post.buyMode = this.buyMode;
                    }

                    //Handles syncing the Item Stack for sale.
                    else if (command.equalsIgnoreCase("syncstack")) {
                        post.setStackForSale(stackForSale);
                    }

                    //Handles syncing the options on the server.
                    else if (command.equalsIgnoreCase("syncoptions")) {
                        post.tradeAmount = this.tradeAmount;
                        post.tradePrice = this.tradePrice;
                    }

                    post.markUpdated();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
