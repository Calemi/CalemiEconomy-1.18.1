package com.tm.calemieconomy.blockentity;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.blockentity.BlockEntityContainerBase;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.helper.StringHelper;
import com.tm.calemieconomy.file.DirtyFile;
import com.tm.calemieconomy.file.TradesFile;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.main.CEReference;
import com.tm.calemieconomy.menu.MenuTradingPost;
import com.tm.calemieconomy.security.ISecurityHolder;
import com.tm.calemieconomy.security.SecurityProfile;
import com.tm.calemieconomy.util.TradingPostHelper;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BlockEntityTradingPost extends BlockEntityContainerBase implements ISecurityHolder, ICurrencyNetworkUnit {

    private final SecurityProfile profile = new SecurityProfile();
    private Location bankLocation;

    public long dirtyDate = 0;

    public TradingPostPrice price = new TradingPostPrice(this);
    private ItemStack stackForSale = ItemStack.EMPTY;
    public int tradeAmount;
    public boolean buyMode = false;
    public boolean adminMode = false;
    public boolean hasValidTradeOffer;
    public int broadcastDelay;
    public String fileKey = "";

    private long lastSystemTimeSeconds = 0;

    public final String msgKey = "ce.msg.trading_post.broadcast.";

    public BlockEntityTradingPost(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.TRADING_POST.get(), pos, state);
        tradeAmount = 1;
        hasValidTradeOffer = false;
        lastSystemTimeSeconds = System.nanoTime() / 1000000000;
    }

    public void injectValuesFromFile() {

        if (fileKey.isEmpty()) {
            return;
        }

        TradesFile.TradeEntry entry = TradesFile.list.get(fileKey);

        if (entry == null) {
            System.out.println("[" + CEReference.MOD_NAME + "]: Could not find trade: " + fileKey + " in file!");
            return;
        }

        setStackForSale(entry.getStackForSale());
        tradeAmount = entry.getAmount();
        price.setStartingPrice(entry.getStartingPrice());
        price.isDynamic = entry.isDynamicPrice();
        price.setExtremum(entry.getPriceExtremum());
        price.varyRate = entry.getPriceVaryRate();
        price.stableRate = entry.getPriceStableRate();
        buyMode = entry.isBuyMode();
        adminMode = entry.isAdminMode();
        markUpdated();
    }

    /**
     * Called every tick.
     */
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityTradingPost post) {

        if (level.getGameTime() % 20 == 0) {

            if (!level.isClientSide()) {

                TradingPostHelper.allTradingPosts.put(post.getBlockPos(), post);

                long systemTimeSeconds = System.nanoTime() / 1000000000;

                if (systemTimeSeconds - post.lastSystemTimeSeconds >= 60) {
                    post.price.stabilize();
                    post.markUpdated();
                    post.lastSystemTimeSeconds = System.nanoTime() / 1000000000;
                }

                if (post.dirtyDate != DirtyFile.dirtyDate) {
                    post.dirtyDate = DirtyFile.dirtyDate;
                    post.injectValuesFromFile();
                }
            }
        }

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

    public TextComponent getTradeInfo() {

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
        message.append(" ").append(getStackForSale().getDisplayName());

        return message;
    }

    public TextComponent getPriceInfo(boolean detailed) {

        TextComponent message = new TextComponent("");
        message.append(new TranslatableComponent(msgKey + "price").append(": "));

        ChatFormatting gradeColor = ChatFormatting.RED;

        double ratio = 1 - (((double)price.getPrice() - price.getExtremum()) / (price.getStartingPrice() - price.getExtremum()));

        if (ratio < 0.9F) {
            gradeColor = ChatFormatting.GOLD;
        }

        if (ratio < 0.5F) {
            gradeColor = ChatFormatting.YELLOW;
        }

        if (ratio < 0.1F) {
            gradeColor = ChatFormatting.GREEN;
        }

        if (price.getStartingPrice() == price.getExtremum()) {
            gradeColor = ChatFormatting.GREEN;
        }

        if (price.isDynamic && detailed) {
            message.append(new TranslatableComponent(msgKey + "resting").withStyle(ChatFormatting.GREEN));
            message.append(ChatFormatting.GREEN + ": ");
            message.append(formatPrice(price.getStartingPrice(), ChatFormatting.GREEN));
            message.append(" | ");
            message.append(new TranslatableComponent(msgKey + "current").withStyle(gradeColor));
            message.append(gradeColor + ": ");

            message.append(formatPrice(price.getPrice(), gradeColor));
            message.append(" | ");

            if (!buyMode) {
                message.append(new TranslatableComponent(msgKey + "max").withStyle(ChatFormatting.RED));
            }

            else {
                message.append(new TranslatableComponent(msgKey + "min").withStyle(ChatFormatting.RED));
            }

            message.append(ChatFormatting.RED + ": ");
            message.append(formatPrice(price.getExtremum(), ChatFormatting.RED));
        }

        else {

            if (price.isDynamic) {
                message.append(formatPrice(price.getPrice(), gradeColor));
                message.append(" ");
                message.append(new TranslatableComponent(msgKey + "dynamic"));
            }

            else message.append(formatPrice(price.getPrice(), ChatFormatting.GOLD));
        }

        return message;
    }

    public TextComponent getLocationInfo() {
        TextComponent message = new TextComponent("");
        message.append(ChatFormatting.GOLD + getLocation().toString());
        return message;
    }

    public TextComponent getStockInfo() {
        TextComponent message = new TextComponent("Stock: ");

        if (getStock() < tradeAmount) {
            message.append(ChatFormatting.RED + "Out of stock!");
        }

        else {
            message.append(ChatFormatting.GOLD + StringHelper.insertCommas(getStock()));
        }

        return message;
    }

    private TextComponent formatPrice(long price, ChatFormatting chatFormatting) {
        TextComponent message = new TextComponent("");
        message.append(price > 0 ? CurrencyHelper.formatCurrency(price, true).withStyle(chatFormatting) : new TranslatableComponent(msgKey + "free").withStyle(chatFormatting));
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

        dirtyDate = tag.getLong("DirtyDate");

        profile.loadFromNBT(tag);

        CompoundTag stackTag = tag.getCompound("StackForSale");
        stackForSale = ItemStack.of(stackTag);

        price.load(tag);
        tradeAmount = tag.getInt("TradeAmount");
        adminMode = tag.getBoolean("AdminMode");
        buyMode = tag.getBoolean("BuyMode");
        broadcastDelay = tag.getInt("BroadcastDelay");
        fileKey = tag.getString("FileKey");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putLong("DirtyDate", dirtyDate);

        profile.saveToNBT(tag);

        CompoundTag stackTag = new CompoundTag();
        stackForSale.save(stackTag);
        tag.put("StackForSale", stackTag);

        price.save(tag);
        tag.putInt("TradeAmount", tradeAmount);
        tag.putBoolean("AdminMode", adminMode);
        tag.putBoolean("BuyMode", buyMode);
        tag.putInt("BroadcastDelay", broadcastDelay);
        tag.putString("FileKey", fileKey);
    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(this::createUnSidedHandler);

    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        itemHandler.invalidate();
    }
}
