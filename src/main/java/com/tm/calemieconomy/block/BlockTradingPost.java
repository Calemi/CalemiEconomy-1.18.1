package com.tm.calemieconomy.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.UnitMessenger;
import com.tm.calemicore.util.helper.InventoryHelper;
import com.tm.calemicore.util.helper.ItemHelper;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import com.tm.calemieconomy.block.base.BlockContainerBase;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitSounds;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockTradingPost extends BlockContainerBase {

    public static final UnitMessenger MESSENGER = new UnitMessenger("trading_post");

    private static final VoxelShape AABB = Block.box(0, 0, 0, 16, 5, 16);

    public BlockTradingPost() {
        super(Block.Properties.of(Material.STONE).sound(SoundType.WOOD).strength(2).noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        LoreHelper.addInformationLoreFirst(tooltip, new TranslatableComponent("ce.lore.trading_post"));
        LoreHelper.addControlsLoreFirst(tooltip, new TranslatableComponent("ce.lore.trading_post.use"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltip, new TranslatableComponent("ce.lore.trading_post.use-wrench"), LoreHelper.ControlType.USE);
        LoreHelper.addControlsLore(tooltip, new TranslatableComponent("ce.lore.trading_post.sneak-use"), LoreHelper.ControlType.SNEAK_USE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        Location location = new Location(level, pos);

        ItemStack heldStack = player.getItemInHand(hand);
        ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

        BlockEntity te = location.getBlockEntity();

        //Makes sure the Block is a Trading Post.
        if (te instanceof BlockEntityTradingPost post) {

            //If the Player is crouching and holding a Security Wrench, open the GUI.
            if (!player.isCrouching() && heldStack.getItem() == InitItems.SECURITY_WRENCH.get()) {

                if (post.adminMode) {
                    if (player.isCreative()) return super.use(state, level, pos, player, hand, hit);
                    else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.admin"), player);
                }

                else return super.use(state, level, pos, player, hand, hit);
            }

            //Else, if the Player is not crouching and has a Wallet, handle a possible trade.
            else if (!player.isCrouching() && !walletStack.isEmpty()) {
                handleTrade(level, player, post);
            }

            //Otherwise, handle printing what the owner is trading.
            else if (!level.isClientSide()) {

                if (post.hasValidTradeOffer) {

                    MESSENGER.sendMessage(post.getTradeInfo(false), player);
                    MESSENGER.sendMessage(MESSENGER.getMessage("hold-wallet"), player);
                }

                else {
                    MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.invalid"), player);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Handles the trading system. Decides if the trade is valid and if it is a sell or a purchase.
     */
    private void handleTrade(Level level, Player player, BlockEntityTradingPost post) {

        BlockEntityBank bank = post.getBank();
        ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

        //Checks if there is a connected bank OR if the price is free OR if the Trading Post is in admin mode.
        if (bank != null || post.tradePrice <= 0 || post.adminMode) {

            //Checks if the trade is set up properly
            if (post.hasValidTradeOffer) {

                //If the Trading Post is in buy mode, handle a sell.
                if (post.buyMode) {
                    handleSell(walletStack, level, player, post, bank);
                }

                //If not, handle a purchase.
                else handlePurchase(walletStack, level, player, post, bank);
            }

            else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.invalid"), player);
        }

        else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.no-bank"),player);
    }

    /**
     * Handles the selling system.
     */
    private void handleSell(ItemStack walletStack, Level level, Player player, BlockEntityTradingPost post, BlockEntityBank bank) {

        ItemWallet wallet = (ItemWallet) walletStack.getItem();

        //Checks if the player has the required amount of items.
        if (InventoryHelper.countItems(player.getInventory(), post.getStackForSale(), true) >= post.tradeAmount) {

            //Generates the base Item Stack to purchase.
            ItemStack stackForSale = new ItemStack(post.getStackForSale().getItem(), post.tradeAmount);

            //Sets any NBT to the purchased item.
            if (post.getStackForSale().hasTag()) {
                stackForSale.setTag(post.getStackForSale().getTag());
            }

            //Checks if the Trading Post can fit the amount of items being bought.
            if (InventoryHelper.canInsertStack(post, stackForSale) || post.adminMode) {

                //Checks if the player's current Wallet can fit added funds.
                if (wallet.canDepositCurrency(walletStack, post.tradePrice)) {

                    //Checks if the connected Bank has enough funds to spend. Bypasses this check if in admin mode
                    if (post.adminMode || post.tradePrice <= 0 || bank.canWithdrawCurrency(post.tradePrice)) {

                        //Removes the Items from the player.
                        InventoryHelper.consumeItems(player.getInventory(), post.getStackForSale(), post.tradeAmount, true);

                        //Checks if not in admin mode.
                        if (!post.adminMode) {

                            //Adds Items to the Trading Post
                            InventoryHelper.insertOverflowingStack(post, stackForSale);

                            //Subtracts funds from the connected Bank
                            bank.withdrawCurrency(post.tradePrice);
                        }

                        //Adds funds to the Player's current wallet.
                        wallet.depositCurrency(walletStack, post.tradePrice);

                        SoundHelper.playAtPlayer(player, InitSounds.COIN.get(), 0.1F, 1F);
                    }

                    else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.bank-empty"), player);
                }

                else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.wallet-full"), player);
            }

            else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.stock-full"), player);
        }

        else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.no-items"), player);
    }

    /**
     * Handles the purchasing system.
     */
    private void handlePurchase(ItemStack walletStack, Level level, Player player, BlockEntityTradingPost post, BlockEntityBank bank) {

        ItemWallet wallet = (ItemWallet) walletStack.getItem();

        //Checks if the Trading Post has enough stock. Bypasses this check if in admin mode.
        if (post.getStock() >= post.tradeAmount || post.adminMode) {

            //Checks if the Player has enough funds in his current Wallet.
            if (wallet.canWithdrawCurrency(walletStack, post.tradePrice)) {

                //Checks if the connected Bank can store the possible funds.
                if (post.adminMode || post.tradePrice <= 0|| bank.canDepositCurrency(post.tradePrice)) {

                    //Generates the base Item Stack to purchase.
                    ItemStack stackForSale = new ItemStack(post.getStackForSale().getItem(), post.tradeAmount);

                    //Sets any NBT to the purchased item.
                    if (post.getStackForSale().hasTag()) {
                        stackForSale.setTag(post.getStackForSale().getTag());
                    }

                    if (!level.isClientSide()) {

                        //Generates and spawns the purchased Items.
                        ItemHelper.spawnOverflowingStackAtEntity(player.getLevel(), player, stackForSale);

                        //Adds funds to the connected Bank.
                        if (!post.adminMode) bank.depositCurrency(post.tradePrice);

                        post.markUpdated();
                    }

                    //Subtracts funds from the Player's current Wallet.
                    wallet.withdrawCurrency(walletStack, post.tradePrice);

                    //Removes the amount of Items for sale.
                    if (!post.adminMode) InventoryHelper.consumeItems(post, post.getStackForSale(), post.tradeAmount, true);

                    SoundHelper.playAtPlayer(player, InitSounds.COIN.get(), 0.1F, 1F);
                }

                else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.bank-full"), player);
            }

            else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.wallet-empty"), player);
        }

        else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.stock-empty"), player);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        super.setPlacedBy(level, pos, state, placer, stack);

        if (placer instanceof Player player) {

            if (player.isCreative() && !player.isCrouching()) {

                Location location = new Location(level, pos);
                BlockEntity blockEntity = location.getBlockEntity();

                if (blockEntity instanceof BlockEntityTradingPost post) {
                    post.adminMode = true;
                    if (!level.isClientSide()) MESSENGER.sendMessage(MESSENGER.getMessage("admin-mode"), player);
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return InitBlockEntityTypes.TRADING_POST.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, InitBlockEntityTypes.TRADING_POST.get(), BlockEntityTradingPost::tick);
    }

    /*
        Methods for Blocks that are not full and solid cubes.
    */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }
}
