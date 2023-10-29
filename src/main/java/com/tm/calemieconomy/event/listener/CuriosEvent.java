package com.tm.calemieconomy.event.listener;

import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.main.CalemiEconomy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod.EventBusSubscriber(modid = CEReference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CuriosEvent {

    private static final ResourceLocation EMPTY_WALLET_SLOT = new ResourceLocation(CEReference.MOD_ID, "gui/empty_wallet_slot");

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        event.addSprite(EMPTY_WALLET_SLOT);
    }

    @SubscribeEvent
    public static void sendImc(InterModEnqueueEvent event) {

        LogHelper.log(CEReference.MOD_NAME, "Curios Loaded: " + CalemiEconomy.isCuriosLoaded);

        if (CalemiEconomy.isCuriosLoaded) {

            LogHelper.log(CEReference.MOD_NAME, "Found Curios. Adding Wallet Slot.");
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("wallet").icon(EMPTY_WALLET_SLOT).build());
        }
    }
}
