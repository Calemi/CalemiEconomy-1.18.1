package com.tm.calemieconomy.block;

import com.tm.calemicore.util.helper.LoreHelper;
import com.tm.calemieconomy.config.CEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockRaritaniumOre extends Block {

    static final Random rand = new Random();

    public BlockRaritaniumOre(MaterialColor color, SoundType sound) {
        super(BlockBehaviour.Properties.of(Material.STONE)
                .color(color)
                .sound(sound)
                .strength(3F, 3F)
                .requiresCorrectToolForDrops());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltipList, TooltipFlag advanced) {
        LoreHelper.addInformationLoreFirst(tooltipList, new TranslatableComponent("ce.lore.raritanium_ore").append(" [" + CEConfig.worldGen.raritaniumOreGenMinY.get() + ", " + CEConfig.worldGen.raritaniumOreGenMaxY.get() + "]"));
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? rand.nextInt(3 + fortune) + 1 : 0;
    }
}
