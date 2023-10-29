package com.tm.calemieconomy.main;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * A static reference class used to store common public values.
 * ex. the mod's name & version.
 */
public class CEReference {

    public static final String MOD_ID = "calemieconomy";
    public static final String MOD_NAME = "Calemi's Economy";
    public static final String CONFIG_DIR = FMLPaths.CONFIGDIR.get().toString() + "/" + MOD_ID;

    public static final ResourceLocation GUI_TABS = new ResourceLocation(CEReference.MOD_ID, "textures/gui/tabs.png");
}