package com.tm.calemieconomy.init;

import com.tm.calemieconomy.main.CEReference;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class InitKeyBindings {

    public static final KeyMapping openWalletButton = new KeyMapping("Open Wallet", 71, CEReference.MOD_NAME);

    public static void init () {
        ClientRegistry.registerKeyBinding(openWalletButton);
    }
}
