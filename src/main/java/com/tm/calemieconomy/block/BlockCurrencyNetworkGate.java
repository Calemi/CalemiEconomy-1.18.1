package com.tm.calemieconomy.block;

import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemieconomy.blockentity.BlockEntityCurrencyNetworkGate;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockCurrencyNetworkGate extends BlockCurrencyNetworkCableOpaque {

    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public BlockCurrencyNetworkGate() {
        registerDefaultState(getStateDefinition().any().setValue(CONNECTED, true));
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltip, new TranslatableComponent("ce.lore.currency_network_gate"));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.CURRENCY_NETWORK_GATE.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, InitBlockEntityTypes.CURRENCY_NETWORK_GATE.get(), BlockEntityCurrencyNetworkGate::tick);
    }

    /*
        Methods for Block properties.
     */

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(CONNECTED, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED);
    }
}
