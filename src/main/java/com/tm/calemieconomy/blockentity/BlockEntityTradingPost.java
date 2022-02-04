package com.tm.calemieconomy.blockentity;

import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.menu.MenuTradingPost;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.security.SecurityProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityTradingPost extends BlockEntityContainerBase implements ISecurityHolder, ICurrencyNetwork {

    private final SecurityProfile profile = new SecurityProfile();

    public BlockEntityTradingPost(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.TRADING_POST.get(), pos, state);
    }

    /**
     * Security Methods
     */

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

    /**
     * Network Methods
     */

    @Override
    public Direction[] getConnectedDirections() {
        return new Direction[] {Direction.DOWN};
    }

    /**
     * Container Methods
     */

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.trading_post");
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory playerInv, Player player) {
        return new MenuTradingPost(containerID, playerInv, this);
    }
}
