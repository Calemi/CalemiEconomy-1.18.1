package com.tm.calemieconomy.block;

import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemieconomy.block.base.BlockContainerBase;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockBank extends BlockContainerBase {

    public BlockBank() {
        super(Block.Properties.of(Material.STONE).sound(SoundType.WOOD).strength(-1.0F, 3600000.0F));
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable BlockGetter level, List<Component> tooltipList, TooltipFlag advanced) {
        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("ce.lore.bank"));
        LoreHelper.addControlsLoreFirst(tooltipList, new TranslatableComponent("ce.lore.bank.use"), LoreHelper.ControlType.USE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.BANK.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, InitBlockEntityTypes.BANK.get(), BlockEntityBank::tick);
    }
}
