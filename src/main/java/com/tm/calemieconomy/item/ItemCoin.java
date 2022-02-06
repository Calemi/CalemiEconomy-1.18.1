package com.tm.calemieconomy.item;

import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.block.base.BlockItemBase;
import com.tm.calemieconomy.main.CalemiEconomy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCoin extends BlockItemBase {

    public final int value;

    public ItemCoin(int value, Block coinStack) {
        super(coinStack, CalemiEconomy.TAB);
        this.value = value;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag advanced) {

        tooltipList.add(new TranslatableComponent("ce.lore.coin.value").withStyle(ChatFormatting.GRAY).append(" (1): ").append(CurrencyHelper.formatCurrency(value).withStyle(ChatFormatting.GOLD)));

        if (stack.getCount() > 1) {
            tooltipList.add(new TranslatableComponent("ce.lore.coin.value").withStyle(ChatFormatting.GRAY).append(" (" + stack.getCount() + "): ").append(CurrencyHelper.formatCurrency(value * stack.getCount()).withStyle(ChatFormatting.GOLD)));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        ItemStack stack = context.getItemInHand();

        if (context.getPlayer() == null || context.getPlayer().isCreative() || stack.getCount() >= 8) {
            return place(new BlockPlaceContext(context));
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        context.getItemInHand().shrink(7);
        return context.getLevel().setBlock(context.getClickedPos(), state, 26);
    }
}
