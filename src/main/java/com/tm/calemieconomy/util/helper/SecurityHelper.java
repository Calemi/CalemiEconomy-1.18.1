package com.tm.calemieconomy.util.helper;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.UnitMessenger;
import com.tm.calemieconomy.config.CEConfig;
import com.tm.calemieconomy.security.ISecurityHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SecurityHelper {

    public static boolean canUseSecuredBlock(Location location, Player player, boolean printError) {

        BlockEntity blockEntity = location.getBlockEntity();

        if (blockEntity instanceof ISecurityHolder securityHolder) {

            if (securityHolder.getSecurityProfile().isOwner(player.getName().getString()) || player.isCreative() || !CEConfig.security.useSecurity.get()) {
                return true;
            }

            else if (printError) printErrorMessage(location, player);

            return false;
        }

        return true;
    }

    public static void printErrorMessage (Location location, Player player) {

        if (player.getLevel().isClientSide()) {
            UnitMessenger message = new UnitMessenger("security");
            message.sendErrorMessage(message.getMessage("error.notyours"), player);
        }
    }
}
