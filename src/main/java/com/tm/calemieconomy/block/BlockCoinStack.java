package com.tm.calemieconomy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCoinStack extends Block {

    private static final VoxelShape AABB = Block.box(1, 0, 1, 15, 16, 15);

    public BlockCoinStack() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .color(MaterialColor.RAW_IRON)
                .sound(SoundType.METAL)
                .dynamicShape()
                .strength(1));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
        return direction == Direction.DOWN && !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state2, level, pos, pos2);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return AABB;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }
}
