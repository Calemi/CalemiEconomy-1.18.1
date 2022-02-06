package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.security.SecurityProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityCurrencyNetworkCable extends BlockEntityBase implements ICurrencyNetwork, ISecurityHolder {

    private final SecurityProfile profile = new SecurityProfile();

    public BlockEntityCurrencyNetworkCable(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockEntityCurrencyNetworkCable(BlockPos pos, BlockState state) {
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

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        profile.loadFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        profile.saveToNBT(tag);
    }
}
