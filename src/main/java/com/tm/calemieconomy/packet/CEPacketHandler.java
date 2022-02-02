package com.tm.calemieconomy.packet;

import com.tm.calemieconomy.main.CEReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CEPacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CEReference.MOD_ID, CEReference.MOD_ID),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        CEPacketHandler.INSTANCE.registerMessage(++id, PacketSyncContainerCurrency.class, PacketSyncContainerCurrency::toBytes, PacketSyncContainerCurrency::new, PacketSyncContainerCurrency::handle);

    }
}
