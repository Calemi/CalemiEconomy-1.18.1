package com.tm.calemieconomy.item;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemieconomy.blockentity.BlockEntityBase;
import com.tm.calemieconomy.event.WrenchEvents;
import com.tm.calemieconomy.main.CalemiEconomy;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.util.helper.SecurityHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemSecurityWrench extends Item {

    public ItemSecurityWrench () {
        super(new Item.Properties().tab(CalemiEconomy.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipList, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("ce.lore.security_wrench"));
        LoreHelper.addControlsLoreFirst(tooltipList, new TranslatableComponent("ce.lore.security_wrench.use"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltipList, new TranslatableComponent("ce.lore.security_wrench.sneak-use"), LoreHelper.ControlType.SNEAK_USE);
    }

    /**
     * Handles calling the Wrench event.
     */
    @Override
    public InteractionResult onItemUseFirst (ItemStack stack, UseOnContext context) {

        Player player = context.getPlayer();

        Location location = new Location(context.getLevel(), context.getClickedPos());

        //Checks if the Player exists.
        if (player != null && player.isCrouching()) {

            //Checks if the Tile Entity exists and if its a the mod's Tile Entity
            if (location.getBlockEntity() != null && location.getBlockEntity() instanceof ISecurityHolder) {

                //Checks if the Player is the owner of the secured block. Bypassed by creative mode or config option.
                if (SecurityHelper.canEditSecuredBlock(location, player)) {

                    WrenchEvents.onBlockWrenched(location);
                    return InteractionResult.SUCCESS;
                }

                else if (!location.level.isClientSide()) SecurityHelper.printErrorMessage(location, player);
            }
        }

        return super.onItemUseFirst(stack, context);
    }
}
