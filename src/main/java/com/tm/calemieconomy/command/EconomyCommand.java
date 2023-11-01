package com.tm.calemieconomy.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.file.DirtyFile;
import com.tm.calemieconomy.file.ScheduledRandomPriceModifier;
import com.tm.calemieconomy.file.ScheduledRandomPriceModifiersFile;
import com.tm.calemieconomy.file.TradesFile;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.util.TradingPostHelper;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class EconomyCommand {

    /**
     * Registers all the commands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralArgumentBuilder<CommandSourceStack> plotCommand = Commands.literal("trades");

        plotCommand.requires(commandSource -> true)
                .then(find())
                .then(findByPlayer())
                .then(checkstock())
                .then(modifiers())
                .then(reload());


        dispatcher.register(plotCommand);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> modifiers() {

        return Commands.literal("modifiers").executes(ctx -> {

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[Modifiers]---"), Util.NIL_UUID);

            for (ScheduledRandomPriceModifier priceModifier : ScheduledRandomPriceModifiersFile.list) {

                MutableComponent msg = new TextComponent(ChatFormatting.GREEN + "- " + priceModifier.getName() + ": " + ChatFormatting.GOLD + priceModifier.getPickedStack().getHoverName().getString());

                if (priceModifier.isBuyMode()) {
                    msg.append(new TextComponent(" sells for "));
                }

                else {
                    msg.append(new TextComponent(" costs "));
                }

                msg.append("" + ChatFormatting.GOLD + (int) (priceModifier.getPriceModifier() * 100) + "%" + ChatFormatting.WHITE + " of its original price.");

                ctx.getSource().getPlayerOrException().sendMessage(msg, Util.NIL_UUID);
            }

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[End of List]---"), Util.NIL_UUID);

            return Command.SINGLE_SUCCESS;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> find() {

        return Commands.literal("find").then(Commands.argument("item", ItemArgument.item()).executes(ctx -> {

            ItemInput itemInput = ItemArgument.getItem(ctx, "item");

            boolean foundPost = false;

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[Trading Posts]---"), Util.NIL_UUID);

            for (BlockEntityTradingPost post : TradingPostHelper.allTradingPosts.values()) {

                if (post.getStackForSale().isEmpty()) continue;

                if (post.getStackForSale().getItem() == (itemInput.getItem())) {
                    MutableComponent info = post.getTradeInfo().append(" ").append(post.getPriceInfo(false)).append(" ").append(new TranslatableComponent(post.msgKey + "at")).append(" ").append(post.getLocationInfo()).append(" ").append(post.getStockInfo());
                    ctx.getSource().getPlayerOrException().sendMessage(info, Util.NIL_UUID);
                    foundPost = true;
                }
            }

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[End of List]---"), Util.NIL_UUID);

            if (!foundPost) {
                ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "Could not find any Trading Posts trading that item."), Util.NIL_UUID);
            }

            return Command.SINGLE_SUCCESS;

        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> findByPlayer() {

        return Commands.literal("findbyplayer").then(Commands.argument("playerName", StringArgumentType.word()).executes(ctx -> {

            String playerName = StringArgumentType.getString(ctx, "playerName");

            boolean foundPost = false;

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[Trading Posts]---"), Util.NIL_UUID);

            for (BlockEntityTradingPost post : TradingPostHelper.allTradingPosts.values()) {

                if (post.adminMode || post.getStackForSale().isEmpty()) continue;

                if (post.getSecurityProfile().getOwnerName().equalsIgnoreCase(playerName)) {
                    MutableComponent info = post.getTradeInfo().append(" ").append(post.getPriceInfo(false)).append(" ").append(new TranslatableComponent(post.msgKey + "at")).append(" ").append(post.getLocationInfo()).append(" ").append(post.getStockInfo());
                    ctx.getSource().getPlayerOrException().sendMessage(info, Util.NIL_UUID);
                    foundPost = true;
                }
            }

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[End of List]---"), Util.NIL_UUID);

            if (!foundPost) {
                ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "Could not find any Trading Posts from that player."), Util.NIL_UUID);
            }

            return Command.SINGLE_SUCCESS;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> checkstock() {

        return Commands.literal("checkstock").executes(ctx -> {

            boolean foundPost = false;

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[Trading Posts]---"), Util.NIL_UUID);

            for (BlockEntityTradingPost post : TradingPostHelper.allTradingPosts.values()) {

                if (post.getSecurityProfile().isOwner(ctx.getSource().getPlayerOrException()) && post.hasValidTradeOffer) {

                    String msgKey = "ce.msg.trading_post.broadcast.";

                    TextComponent message = new TextComponent("Your ");
                    message.append(ChatFormatting.GOLD + "Trading Post ");
                    message.append(new TranslatableComponent(post.buyMode ? msgKey + "buying" : msgKey + "selling"));
                    message.append(ChatFormatting.GOLD + " x").append(ChatFormatting.GOLD + String.valueOf(post.tradeAmount));
                    message.append(" ").append(post.getStackForSale().getDisplayName()).append(" ");
                    message.append(new TranslatableComponent(msgKey + "for").append(" "));
                    message.append(post.price.getPrice() > 0 ? CurrencyHelper.formatCurrency(post.price.getPrice(), true).withStyle(ChatFormatting.GOLD) : new TranslatableComponent(msgKey + "free").withStyle(ChatFormatting.GOLD));
                    message.append(" ");
                    message.append(new TranslatableComponent(msgKey + "at").append(" "));
                    message.append(ChatFormatting.GOLD + post.getLocation().toString());
                    message.append(new TextComponent(" has ").append(ChatFormatting.GOLD + "" + post.getStock()).append(" item(s) in stock."));

                    ctx.getSource().getPlayerOrException().sendMessage(message, Util.NIL_UUID);

                    foundPost = true;
                }
            }

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "---[End of List]---"), Util.NIL_UUID);

            if (!foundPost) {
                ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.YELLOW + "Could not find any Trading Posts you own."), Util.NIL_UUID);
            }

            return Command.SINGLE_SUCCESS;
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reload() {

        return Commands.literal("reload").requires((player) -> player.hasPermission(3)).executes(ctx -> {

            TradesFile.init();
            ScheduledRandomPriceModifiersFile.init();
            DirtyFile.markDirty();

            ctx.getSource().getPlayerOrException().sendMessage(new TextComponent(ChatFormatting.GREEN + "Reload Complete!"), Util.NIL_UUID);

            return Command.SINGLE_SUCCESS;
        });
    }
}
