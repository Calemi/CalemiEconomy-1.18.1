package com.tm.calemieconomy.init;

import com.tm.calemieconomy.main.CEReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InitSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CEReference.MOD_ID);

    public static final RegistryObject<SoundEvent> COIN = SOUNDS.register("item.coin_sound", () -> new SoundEvent(new ResourceLocation(CEReference.MOD_ID, "item.coin_sound")));
    public static final RegistryObject<SoundEvent> MONEY_BAG_CHEAP = SOUNDS.register("item.money_bag_cheap_sound", () -> new SoundEvent(new ResourceLocation(CEReference.MOD_ID, "item.money_bag_cheap_sound")));
    public static final RegistryObject<SoundEvent> MONEY_BAG_RICH = SOUNDS.register("item.money_bag_rich_sound", () -> new SoundEvent(new ResourceLocation(CEReference.MOD_ID, "item.money_bag_rich_sound")));
}
