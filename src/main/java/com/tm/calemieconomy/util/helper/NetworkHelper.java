package com.tm.calemieconomy.util.helper;

import com.tm.calemicore.util.Location;
import com.tm.calemieconomy.blockentity.BlockEntityBank;

public class NetworkHelper {

    public static BlockEntityBank getConnectedBank (Location unitLocation, Location bankLocation) {

        if (bankLocation != null && bankLocation.getBlockEntity() instanceof BlockEntityBank bank) {

            if (bank.isOnlyConnectedBank()) {

                if (bank.getConnectedUnits().contains(unitLocation)) {
                    return bank;
                }
            }
        }

        return null;
    }
}
