package com.tm.calemieconomy.item;

import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitSounds;
import com.tm.calemieconomy.main.CalemiEconomy;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMoneyBag extends Item {

    private final boolean isRich;

    public ItemMoneyBag(boolean isRich) {
        super(new Item.Properties().tab(CalemiEconomy.TAB));
        this.isRich = isRich;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipList, TooltipFlag advanced) {
        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("ce.lore.money_bag"));
        LoreHelper.addControlsLoreFirst(tooltipList, new TranslatableComponent("ce.lore.money_bag.use"), LoreHelper.ControlType.USE);
    }

    /**
     * Handles opening the Money Bag.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        stack.shrink(1);

        if (isRich) {
            SoundHelper.playAtPlayer(player, InitSounds.MONEY_BAG_RICH.get(), 0.1F, 1F);
            giveCoins(level, player, CEConfig.economy.richMoneyBagMin.get(), CEConfig.economy.richMoneyBagMax.get());
        }

        else {
            SoundHelper.playAtPlayer(player, InitSounds.MONEY_BAG_CHEAP.get(), 0.1F, 1F);
            giveCoins(level, player, CEConfig.economy.cheapMoneyBagMin.get(), CEConfig.economy.cheapMoneyBagMax.get());
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    private void giveCoins(Level level, Player player, int minAmount, int maxAmount) {

        if (!level.isClientSide()) {

            int amount = minAmount + MathHelper.random.nextInt(maxAmount - minAmount);

            int dollars = (int)Math.floor((float)amount / 100);
            amount -= (dollars * 100);
            int quarters = (int)Math.floor((float)amount / 25);
            amount -= (quarters * 25);
            int nickels = (int)Math.floor((float)amount / 5);
            amount -= (nickels * 5);
            int pennies = amount;

            ItemHelper.spawnStackAtEntity(level, player, new ItemStack(InitItems.COIN_PLATINUM.get(), dollars));
            ItemHelper.spawnStackAtEntity(level, player, new ItemStack(InitItems.COIN_GOLD.get(), quarters));
            ItemHelper.spawnStackAtEntity(level, player, new ItemStack(InitItems.COIN_SILVER.get(), nickels));
            ItemHelper.spawnStackAtEntity(level, player, new ItemStack(InitItems.COIN_COPPER.get(), pennies));
        }
    }
}
