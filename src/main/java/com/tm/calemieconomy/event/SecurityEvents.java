package com.tm.calemieconomy.event;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.util.helper.SecurityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class SecurityEvents {

    /**
     * Sets the owner of a secured Block when placed.
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

        BlockEntity blockEntity = event.getWorld().getBlockEntity(event.getPos());

        //Checks if the Entity is a Player and the Location is a TileEntityBase and implements ISecurity.
        if (event.getEntity() instanceof Player player && blockEntity instanceof BlockEntityBase blockEntityBase && blockEntity instanceof ISecurityHolder securityHolder) {
            securityHolder.getSecurityProfile().setOwner(player);
            blockEntityBase.markUpdated();
        }
    }

    /**
     * Prevents Secured Blocks from being broken from other Players.
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {

        Location location = new Location(event.getPlayer().getLevel(), event.getPos());

        if (!event.getPlayer().isCreative()) {

            if (!SecurityHelper.canEditSecuredBlock(location, event.getPlayer())) {

                event.setCanceled(true);
                SecurityHelper.printErrorMessage(location, event.getPlayer());
            }

            else if (location.getBlockEntity() instanceof BlockEntityTradingPost post) {

                if (post.adminMode) {
                    event.setCanceled(true);
                }
            }
        }
    }

    /**
     * Prevents Secured Blocks from being exploded.
     */
    @SubscribeEvent
    public void onBlockExploded(ExplosionEvent event) {

        List<BlockPos> affectedBlocks = event.getExplosion().getToBlow();
        List<BlockPos> securedBlocksFound = new ArrayList<>();

        for (BlockPos pos : affectedBlocks) {

            BlockEntity blockEntity = event.getWorld().getBlockEntity(pos);

            if (blockEntity instanceof BlockEntityBase && blockEntity instanceof ISecurityHolder) {
                securedBlocksFound.add(pos);
            }
        }

        affectedBlocks.removeAll(securedBlocksFound);
    }
}
