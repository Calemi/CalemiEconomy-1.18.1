package com.tm.calemieconomy.block;

import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockCurrencyNetworkCableOpaque extends BlockContainerBase {

    public BlockCurrencyNetworkCableOpaque() {
        super(Block.Properties.of(Material.STONE).sound(SoundType.WOOD).strength(2));
    }

    BlockCurrencyNetworkCableOpaque(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltip, new TranslatableComponent("ce.lore.currency_network_cable"));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.CURRENCY_NETWORK_CABLE.get().create(pos, state);
    }
}
