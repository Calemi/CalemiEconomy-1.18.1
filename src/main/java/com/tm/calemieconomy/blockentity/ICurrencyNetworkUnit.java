package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.Location;

public interface ICurrencyNetworkUnit extends ICurrencyNetwork {

    BlockEntityBank getBank();

    Location getBankLocation();

    void setBankLocation(Location location);
}
