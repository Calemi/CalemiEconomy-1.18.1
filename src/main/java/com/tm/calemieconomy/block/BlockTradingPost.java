package com.tm.calemieconomy.block;

import com.tm.calemicore.util.UnitMessenger;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockTradingPost extends BlockContainerBase {

    public static final UnitMessenger TRADING_POST = new UnitMessenger("trading_post");

    private static final VoxelShape AABB = Block.box(0, 0, 0, 16, 5, 16);

    public BlockTradingPost() {
        super(Block.Properties.of(Material.STONE).sound(SoundType.WOOD).strength(2).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltip, new TranslatableComponent("ce.lore.trading_post"));
        LoreHelper.addControlsLoreFirst(tooltip, new TranslatableComponent("ce.lore.trading_post.use"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltip, new TranslatableComponent("ce.lore.trading_post.use-wrench"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltip, new TranslatableComponent("ce.lore.trading_post.sneak-use"), LoreHelper.ControlType.SNEAK_USE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.TRADING_POST.get().create(pos, state);
    }

    /*
        Methods for Blocks that are not full and solid cubes.
    */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }
}
