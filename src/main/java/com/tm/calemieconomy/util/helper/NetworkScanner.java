package com.tm.calemieconomy.util.helper;

import com.tm.calemicore.util.BlockScanner;
import com.tm.calemicore.util.Location;
import com.tm.calemieconomy.blockentity.ICurrencyNetwork;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NetworkScanner extends BlockScanner {

    public NetworkScanner(Location location) {
        super(location, null, 2304, true);
    }

    public void startNetworkScan(Direction[] directions) {

        for (Direction dir : directions) {
            scanNetwork(new Location(origin, dir), dir);
        }
    }

    private void scanNetwork(Location location, Direction oldDir) {

        if (buffer.size() >= maxScanSize) {
            return;
        }

        BlockEntity blockEntity = location.getBlockEntity();

        if (blockEntity != null) {

            if (blockEntity instanceof ICurrencyNetwork network) {

                for (Direction dir : network.getConnectedDirections()) {

                    if (oldDir == dir.getOpposite()) {

                        if (!contains(location)) {

                            buffer.add(location);

                            for (Direction searchDir : network.getConnectedDirections()) {
                                scanNetwork(new Location(location, searchDir), searchDir);
                            }
                        }
                    }
                }
            }
        }
    }
}
