package com.tm.calemieconomy.security;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SecurityProfile {

    private String ownerName = "";

    public String getOwnerName () {
        return ownerName;
    }

    public void setOwner (Player player) {
        ownerName = player.getName().getString();
    }

    public boolean hasOwner () {
        return ownerName.isEmpty();
    }

    public boolean isOwner (String ownerName) {
        return this.ownerName.equalsIgnoreCase(ownerName);
    }

    public void loadFromNBT(CompoundTag tag) {
        CompoundTag securityTag = tag.getCompound("Security");
        ownerName = securityTag.getString("OwnerName");
    }

    public void saveToNBT(CompoundTag tag) {
        CompoundTag securityTag = new CompoundTag();
        securityTag.putString("OwnerName", getOwnerName());
        tag.put("Security", securityTag);
    }
}
