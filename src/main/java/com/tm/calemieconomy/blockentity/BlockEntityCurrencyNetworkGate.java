package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.block.BlockCurrencyNetworkGate;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.main.CEReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityCurrencyNetworkGate extends BlockEntityCurrencyNetworkCable {

    private boolean hasChanged = false;

    public BlockEntityCurrencyNetworkGate(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.CURRENCY_NETWORK_GATE.get(), pos, state);
    }

    /**
     * Changes the Block's "CONNECTED" property by the value.
     */
    public static void setConnectedState(Location location, boolean value) {

        location.level.setBlock(location.getBlockPos(), InitItems.CURRENCY_NETWORK_GATE.get().defaultBlockState().setValue(BlockCurrencyNetworkGate.CONNECTED, value), 3);

        /*if (location.getBlockEntity() != null) {
            location.level.setBlockEntity(location.getBlockEntity());
        }*/
    }

    /**
     * Called every tick.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityCurrencyNetworkGate gate) {

        if (level != null) {

            if (level.getBestNeighborSignal(pos) > 0) {

                if (!gate.hasChanged) {
                    setConnectedState(gate.getLocation(), false);
                }

                gate.hasChanged = true;
            }

            else {

                if (gate.hasChanged) {
                    setConnectedState(gate.getLocation(), true);
                }

                gate.hasChanged = false;
            }
        }
    }

    @Override
    public Direction[] getConnectedDirections () {

        if (getLocation() != null && getLocation().getBlock() instanceof BlockCurrencyNetworkGate gate) {

            if (getLocation().getBlockState().getValue(BlockCurrencyNetworkGate.CONNECTED)) {
                return Direction.values();
            }
        }

        return new Direction[] {};
    }
}
