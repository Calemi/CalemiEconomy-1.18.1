package com.tm.calemieconomy.block;

import com.tm.calemicore.util.Location;
import com.tm.calemicore.util.UnitMessenger;
import com.tm.calemicore.util.helper.ContainerHelper;
import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import com.tm.calemieconomy.block.base.BlockContainerBase;
import com.tm.calemieconomy.blockentity.BlockEntityBank;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.event.ItemTradeEvent;
import com.tm.calemieconomy.init.InitBlockEntityTypes;
import com.tm.calemieconomy.init.InitItems;
import com.tm.calemieconomy.init.InitSounds;
import com.tm.calemieconomy.init.InitStats;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.menu.MenuTradingPostBulkTrade;
import com.tm.calemieconomy.util.helper.CEContainerHelper;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkHooks;
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

        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        Location location = new Location(level, pos);

        ItemStack heldStack = player.getItemInHand(hand);
        ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

        BlockEntity te = location.getBlockEntity();

        //Makes sure the Block is a Trading Post.
        if (te instanceof BlockEntityTradingPost post) {

            if (!player.isCrouching()) {

                //If the Player is holding a Security Wrench, open the GUI.
                if (heldStack.getItem() == InitItems.SECURITY_WRENCH.get()) {

                    if (post.adminMode) {
                        if (player.isCreative()) return super.use(state, level, pos, player, hand, hit);
                        else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.admin"), player);
                    }

                    else return super.use(state, level, pos, player, hand, hit);
                }
            }

            if (!walletStack.isEmpty() && post.hasValidTradeOffer) {

                if (!level.isClientSide() && player.isCrouching()) {
                    openBulkTradeGui((ServerPlayer) player, post);
                }

                else if (!player.isCrouching()) {
                    handleTrade(level, player, post, 1, post.tradeAmount, post.price.getPrice());
                }
            }

            else if (!level.isClientSide() && walletStack.isEmpty()) {
                MESSENGER.sendMessage(MESSENGER.getMessage("hold-wallet"), player);
            }

            else if (!level.isClientSide() && !post.hasValidTradeOffer) {
                MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.invalid"), player);
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Handles the trading system. Decides if the trade is valid and if it is a sell or a purchase.
     */
    public static void handleTrade(Level level, Player player, BlockEntityTradingPost post, int sets, int tradeAmount, long tradePrice) {

        BlockEntityBank bank = post.getBank();
        ItemStack walletStack = CurrencyHelper.getCurrentWallet(player);

        //Checks if there is a connected bank OR if the price is free OR if the Trading Post is in admin mode.
        if (bank != null || tradePrice <= 0 || post.adminMode) {

            //Checks if the trade is set up properly
            if (post.hasValidTradeOffer) {

                //If the Trading Post is in buy mode, handle a sell.
                if (post.buyMode) {
                    handleSell(walletStack, level, player, post, bank, sets, tradeAmount, tradePrice);
                }

                //If not, handle a purchase.
                else {
                    handlePurchase(walletStack, level, player, post, bank, sets, tradeAmount, tradePrice);
                }
            }

            else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.invalid"), player);
        }

        else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.no-bank"),player);
    }

    /**
     * Handles the selling system.
     */
    public static void handleSell(ItemStack walletStack, Level level, Player player, BlockEntityTradingPost post, BlockEntityBank bank, int sets, int sellAmount, long sellPrice) {

        ItemWallet wallet = (ItemWallet) walletStack.getItem();

        //Checks if the player has the required amount of items.
        if (ContainerHelper.countItems(player.getInventory(), post.getStackForSale(), true) >= sellAmount) {

            //Generates the base Item Stack to purchase.
            ItemStack stackForSale = new ItemStack(post.getStackForSale().getItem(), sellAmount);

            //Sets any NBT to the purchased item.
            if (post.getStackForSale().hasTag()) {
                stackForSale.setTag(post.getStackForSale().getTag());
            }

            //Checks if the Trading Post can fit the amount of items being bought.
            if (ContainerHelper.canInsertStack(post, stackForSale) || post.adminMode) {

                //Checks if the player's current Wallet can fit added funds.
                if (wallet.canDepositCurrency(walletStack, sellPrice)) {

                    //Checks if the connected Bank has enough funds to spend. Bypasses this check if in admin mode
                    if (post.adminMode || sellPrice <= 0 || bank.canWithdrawCurrency(sellPrice)) {

                        //Removes the Items from the player.
                        ContainerHelper.consumeItems(player.getInventory(), post.getStackForSale(), sellAmount, true);

                        //Checks if not in admin mode.
                        if (!post.adminMode) {

                            //Adds Items to the Trading Post
                            ContainerHelper.insertOverflowingStack(post, stackForSale);

                            //Subtracts funds from the connected Bank
                            bank.withdrawCurrency(sellPrice);
                        }

                        else {
                            player.awardStat(InitStats.ITEM_SOLD.get().get(post.getStackForSale().getItem()), sellAmount);
                            InitStats.CustomStats.TOTAL_SOLD.addToPlayer(player, sellAmount);
                            MinecraftForge.EVENT_BUS.post(new ItemTradeEvent.Sell(player, post.getStackForSale(), sellAmount, sellPrice));
                        }

                        //Adds funds to the Player's current wallet.
                        wallet.depositCurrency(walletStack, sellPrice);

                        SoundHelper.playAtPlayer(player, InitSounds.COIN.get(), 0.1F, 1F);

                        for (int i = 0; i < sets; i++) {
                            post.price.vary();
                        }

                        post.markUpdated();
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
    public static void handlePurchase(ItemStack walletStack, Level level, Player player, BlockEntityTradingPost post, BlockEntityBank bank, int sets, int buyAmount, long buyPrice) {

        ItemWallet wallet = (ItemWallet) walletStack.getItem();

        //Checks if the Trading Post has enough stock. Bypasses this check if in admin mode.
        if (post.getStock() >= buyAmount || post.adminMode) {

            if (CEContainerHelper.canInsertStack(player.getInventory(), post.getStackForSale(), buyAmount, 0, 36)) {

                //Checks if the Player has enough funds in his current Wallet.
                if (wallet.canWithdrawCurrency(walletStack, buyPrice)) {

                    //Checks if the connected Bank can store the possible funds.
                    if (post.adminMode || buyPrice <= 0 || bank.canDepositCurrency(buyPrice)) {

                        //Generates the base Item Stack to purchase.
                        ItemStack stackForSale = new ItemStack(post.getStackForSale().getItem(), buyAmount);

                        //Sets any NBT to the purchased item.
                        if (post.getStackForSale().hasTag()) {
                            stackForSale.setTag(post.getStackForSale().getTag());
                        }

                        if (!level.isClientSide()) {

                            //Inserts the stack into the Player's inventory.
                            CEContainerHelper.insertStack(player.getInventory(), stackForSale, buyAmount, 0, 36);

                            //Adds funds to the connected Bank.
                            if (!post.adminMode && buyPrice > 0) bank.depositCurrency(buyPrice);

                            post.markUpdated();
                        }

                        //Subtracts funds from the Player's current Wallet.
                        wallet.withdrawCurrency(walletStack, buyPrice);

                        //Removes the amount of Items for sale.
                        if (!post.adminMode) {
                            ContainerHelper.consumeItems(post, post.getStackForSale(), buyAmount, true);
                        }

                        else {
                            player.awardStat(InitStats.ITEM_BOUGHT.get().get(post.getStackForSale().getItem()), buyAmount);
                            InitStats.CustomStats.TOTAL_BOUGHT.addToPlayer(player, buyAmount);
                            MinecraftForge.EVENT_BUS.post(new ItemTradeEvent.Buy(player, post.getStackForSale(), buyAmount, buyPrice));

                        }

                        SoundHelper.playAtPlayer(player, InitSounds.COIN.get(), 0.1F, 1F);

                        for (int i = 0; i < sets; i++) {
                            post.price.vary();
                        }

                        post.markUpdated();
                    }

                    else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.bank-full"), player);
                }

                else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.wallet-empty"), player);
            }

            else if (!level.isClientSide()) MESSENGER.sendErrorMessage(MESSENGER.getMessage("error.inv-full"), player);
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

    private void openBulkTradeGui(ServerPlayer player, BlockEntityTradingPost post) {
        NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, unused) -> {
            return new MenuTradingPostBulkTrade(id, playerInventory, post);
        }, new TranslatableComponent("container.trading_post_bulk_trade")), post.getBlockPos());
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
