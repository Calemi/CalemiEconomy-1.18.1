package com.tm.calemieconomy.blockentity;

import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.security.SecurityProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityNetworkCable extends BlockEntityBase implements ICurrencyNetwork, ISecurityHolder {

    private final SecurityProfile profile = new SecurityProfile();

    public BlockEntityNetworkCable(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.CURRENCY_NETWORK_CABLE.get(), pos, state);
    }

    @Override
    public Direction[] getConnectedDirections() {
        return Direction.values();
    }

    @Override
    public SecurityProfile getSecurityProfile() {
        return profile;
    }
}
