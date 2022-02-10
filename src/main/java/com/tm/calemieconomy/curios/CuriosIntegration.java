package com.tm.calemieconomy.curios;

import com.tm.calemieconomy.init.InitItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosIntegration {

    /**
     * Adds the Curios capability to the Wallet.
     */
    public static ICapabilityProvider walletCapability () {

        ICurio curio = new ICurio() {

            @Override
            public ItemStack getStack() {
                return new ItemStack(InitItems.WALLET.get());
            }

            @Override
            public boolean canRightClickEquip () {
                return false;
            }
        };

        return new ICapabilityProvider() {

            private final LazyOptional<ICurio> curioOpt = LazyOptional.of(() -> curio);

            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
                return CuriosCapability.ITEM.orEmpty(cap, curioOpt);
            }
        };
    }
}
