package com.tm.calemieconomy.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemieconomy.util.helper.SecurityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public abstract class BlockContainerBase extends BaseEntityBlock {

    public BlockContainerBase(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        if (location.getBlockEntity() instanceof BlockEntityContainerBase blockEntity) {

            if (SecurityHelper.canEditSecuredBlock(location, player)) {

                if (!level.isClientSide()) {
                    NetworkHooks.openGui((ServerPlayer) player, blockEntity, pos);
                }

                return InteractionResult.SUCCESS;
            }

            else if (hand == InteractionHand.MAIN_HAND && !level.isClientSide()) SecurityHelper.printErrorMessage(location, player);
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock())) {

            BlockEntity blockentity = level.getBlockEntity(pos);

            if (blockentity instanceof BlockEntityContainerBase blockEntity) {

                for (ItemStack stack : blockEntity.items) {
                    ItemHelper.spawnStackAtLocation(level, blockEntity.getLocation(), stack);
                }
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
