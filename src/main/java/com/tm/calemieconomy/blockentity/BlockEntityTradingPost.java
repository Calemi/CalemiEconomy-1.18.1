package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuTradingPost;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.security.SecurityProfile;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.util.helper.NetworkHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockEntityTradingPost extends BlockEntityContainerBase implements ISecurityHolder, ICurrencyNetworkUnit {

    private final SecurityProfile profile = new SecurityProfile();
    private Location bankLocation;

    private ItemStack stackForSale = ItemStack.EMPTY;
    public int tradeAmount;
    public int tradePrice;
    public boolean buyMode = false;
    public boolean adminMode = false;
    public boolean hasValidTradeOffer;

    public int broadcastDelay;

    public BlockEntityTradingPost(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.TRADING_POST.get(), pos, state);
        tradeAmount = 1;
        tradePrice = 0;
        hasValidTradeOffer = false;
    }

    /**
     * Called every tick.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityTradingPost post) {

        post.hasValidTradeOffer = post.getStackForSale() != null && !post.getStackForSale().isEmpty();

        if (!level.isClientSide()) {

            if (post.broadcastDelay > 0) {

                if (level.getGameTime() % 20 == 0) {
                    post.broadcastDelay--;
                }
            }
        }
    }

    public ItemStack getStackForSale () {
        return stackForSale;
    }

    public void setStackForSale (ItemStack stack) {
        stackForSale = stack;
    }

    public int getStock () {

        if (getStackForSale() != null) {

            int count = 0;

            for (int i = 0; i < getContainerSize(); i++) {

                if (ItemStack.isSame(getItem(i), getStackForSale())) {

                    if (getStackForSale().hasTag()) {

                        if (getItem(i).hasTag() && Objects.requireNonNull(getItem(i).getTag()).equals(getStackForSale().getTag())) {
                            count += getItem(i).getCount();
                        }
                    }

                    else count += getItem(i).getCount();
                }
            }

            LogHelper.log(CEReference.MOD_NAME, "STOCK: " + count);

            return count;
        }

        return 0;
    }

    public TextComponent getTradeInfo(boolean withLocation){
        String msgKey = "ce.msg.trading_post.broadcast.";

        TextComponent message = new TextComponent("");

        if (adminMode) {
            message.append(new TranslatableComponent(msgKey + "admin").withStyle(ChatFormatting.GOLD));
        }

        else {
            message.append(ChatFormatting.GOLD + getSecurityProfile().getOwnerName());
        }

        message.append(ChatFormatting.AQUA + " ").append(new TranslatableComponent(msgKey + "is").append(" "));
        message.append(new TranslatableComponent(buyMode ? msgKey + "buying" : msgKey + "selling"));
        message.append(ChatFormatting.GOLD + " x").append(ChatFormatting.GOLD + String.valueOf(tradeAmount));
        message.append(" ").append(getStackForSale().getDisplayName()).append(" ");
        message.append(new TranslatableComponent(msgKey + "for").append(" "));
        message.append(tradePrice > 0 ? CurrencyHelper.formatCurrency(tradePrice).withStyle(ChatFormatting.GOLD) : new TranslatableComponent(msgKey + "free").withStyle(ChatFormatting.GOLD));

        if (withLocation) {
            message.append(" ");
            message.append(new TranslatableComponent(msgKey + "at").append(" "));
            message.append(ChatFormatting.GOLD + getLocation().toString());
        }

        return message;
    }

    /**
     * Security Methods
     */

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

    /**
     * Network Methods
     */

    @Override
    public Direction[] getConnectedDirections() {
        return new Direction[] {Direction.DOWN};
    }

    @Override
    public BlockEntityBank getBank() {
        BlockEntityBank bank = NetworkHelper.getConnectedBank(getLocation(), bankLocation);
        if (bank == null) bankLocation = null;
        return bank;
    }

    @Override
    public Location getBankLocation() {
        return bankLocation;
    }

    @Override
    public void setBankLocation(Location location) {
        bankLocation = location;
    }

    /**
     * Container Methods
     */

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.trading_post");
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory playerInv, Player player) {
        return new MenuTradingPost(containerID, playerInv, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        profile.loadFromNBT(tag);

        CompoundTag stackTag = tag.getCompound("StackForSale");
        stackForSale = ItemStack.of(stackTag);

        tradeAmount = tag.getInt("TradeAmount");
        tradePrice = tag.getInt("TradePrice");
        adminMode = tag.getBoolean("AdminMode");
        buyMode = tag.getBoolean("BuyMode");

        broadcastDelay = tag.getInt("BroadcastDelay");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        profile.saveToNBT(tag);

        CompoundTag stackTag = new CompoundTag();
        stackForSale.save(stackTag);
        tag.put("StackForSale", stackTag);

        tag.putInt("TradeAmount", tradeAmount);
        tag.putInt("TradePrice", tradePrice);
        tag.putBoolean("AdminMode", adminMode);
        tag.putBoolean("BuyMode", buyMode);

        tag.putInt("BroadcastDelay", broadcastDelay);
    }
}
