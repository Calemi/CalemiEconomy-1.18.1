package com.tm.calemieconomy.util.helper;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.UnitMessenger;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.security.ISecurityHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SecurityHelper {

    public static final UnitMessenger MESSENGER = new UnitMessenger("security");

    public static boolean canEditSecuredBlock(Location location, Player player) {

        BlockEntity blockEntity = location.getBlockEntity();

        if (blockEntity instanceof ISecurityHolder securityHolder) {
            return securityHolder.getSecurityProfile().isOwner(player) || player.isCreative() || !CEConfig.security.useSecurity.get();
        }

        return true;
    }

    public static void printErrorMessage (Location location, Player player) {

        BlockEntity blockEntity = location.getBlockEntity();

        if (blockEntity instanceof ISecurityHolder securityHolder) {
            MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.notyours").append(" ").append(securityHolder.getSecurityProfile().getOwnerName()), player);
        }
    }
}
