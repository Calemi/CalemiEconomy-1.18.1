package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemieconomy.api.CurrencyHelper;
import com.tm.calemieconomy.api.ICurrencyHolder;
import com.tm.calemieconomy.security.ISecurityHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEntityBase extends BlockEntity {

    public BlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * @return The Location of the Block Entity.
     */
    public Location getLocation() {
        return new Location(getLevel(), getBlockPos());
    }

    /**
     * Call this method to send server NBT data to the client.
     */
    public void markUpdated() {
        setChanged();
        getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    /**
     * Creates this Block Entity from a CompoundTag when loaded.
     * @param tag The CompoundTag to load from.
     */
    @Override
    public void load(CompoundTag tag) {

        if (this instanceof ISecurityHolder security) {
            security.getSecurityProfile().loadFromNBT(tag);
        }

        if (this instanceof ICurrencyHolder currency) {
            currency.setCurrency(CurrencyHelper.loadFromNBT(tag));
        }

        super.load(tag);
    }

    /**
     * Saves this Block Entity to a CompoundTag when saved.
     * @param tag The CompoundTag to save to.
     */
    @Override
    protected void saveAdditional(CompoundTag tag) {

        if (this instanceof ISecurityHolder security) {
            security.getSecurityProfile().saveToNBT(tag);
        }

        if (this instanceof ICurrencyHolder currency) {
            CurrencyHelper.saveToNBT(tag, currency.getCurrency());
        }

        super.saveAdditional(tag);
    }

    //Packet Methods

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) load(pkt.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public CompoundTag getTileData() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}