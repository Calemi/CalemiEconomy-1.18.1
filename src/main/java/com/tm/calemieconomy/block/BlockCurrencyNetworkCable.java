package com.tm.calemieconomy.block;

import com.tm.calemieconomy.blockentity.ICurrencyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class BlockCurrencyNetworkCable extends BlockCurrencyNetworkCableOpaque {

    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty DOWNUP = BooleanProperty.create("downup");
    private static final BooleanProperty NORTHSOUTH = BooleanProperty.create("northsouth");
    private static final BooleanProperty EASTWEST = BooleanProperty.create("eastwest");

    private static final VoxelShape CORE_AABB = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape DOWN_AABB = Block.box(6, 0, 6, 10, 10, 10);
    private static final VoxelShape UP_AABB = Block.box(6, 6, 6, 10, 16, 10);
    private static final VoxelShape NORTH_AABB = Block.box(6, 6, 0, 10, 10, 5);
    private static final VoxelShape EAST_AABB = Block.box(6, 6, 6, 16, 10, 10);
    private static final VoxelShape SOUTH_AABB = Block.box(6, 6, 6, 10, 10, 16);
    private static final VoxelShape WEST_AABB = Block.box(0, 6, 6, 10, 10, 10);
    private static final VoxelShape DOWNUP_AABB = Block.box(6, 0, 6, 10, 16, 11);
    private static final VoxelShape NORTHSOUTH_AABB = Block.box(6, 6, 0, 10, 10, 16);
    private static final VoxelShape EASTWEST_AABB = Block.box(0, 6, 6, 16, 10, 10);

    public BlockCurrencyNetworkCable() {
        super(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2));

        registerDefaultState(getStateDefinition().any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(DOWNUP, false)
                .setValue(NORTHSOUTH, false)
                .setValue(EASTWEST, false));
    }

    /**
     * Checks if the Block at the given pos can connect to the Block given by the Direction.
     */
    private boolean canCableConnectTo (LevelAccessor level, BlockPos pos, Direction facing) {
        BlockPos otherPos = pos.relative(facing);
        return canBeConnectedTo(level, otherPos, facing.getOpposite());
    }

    /**
     * Checks if the INetwork at the given pos contains a connectable position that is the same as the given.
     */
    private boolean canBeConnectedTo(LevelAccessor level, BlockPos pos, Direction facing) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof ICurrencyNetwork currencyNetwork) {

            for (Direction dir : currencyNetwork.getConnectedDirections()) {

                if (facing == dir) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
        Methods for Block properties.
     */

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getState(context.getLevel(), context.getClickedPos());
    }

    private BlockState getState (LevelAccessor world, BlockPos pos) {

        boolean down = canCableConnectTo(world, pos, Direction.DOWN);
        boolean up = canCableConnectTo(world, pos, Direction.UP);
        boolean north = canCableConnectTo(world, pos, Direction.NORTH);
        boolean east = canCableConnectTo(world, pos, Direction.EAST);
        boolean south = canCableConnectTo(world, pos, Direction.SOUTH);
        boolean west = canCableConnectTo(world, pos, Direction.WEST);

        boolean downup = down && up && (!north && !east && !south && !west);
        boolean northsouth = north && south && (!down && !up && !east && !west);
        boolean eastwest = east && west && (!north && !south && !down && !up);

        if (downup || northsouth || eastwest) {
            down = false;
            up = false;
            north = false;
            east = false;
            south = false;
            west = false;
        }

        return getStateDefinition().any()
                .setValue(DOWN, down)
                .setValue(UP, up)
                .setValue(NORTH, north)
                .setValue(EAST, east)
                .setValue(SOUTH, south)
                .setValue(WEST, west)
                .setValue(DOWNUP, downup)
                .setValue(NORTHSOUTH, northsouth)
                .setValue(EASTWEST, eastwest);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        return getState(level, pos);
    }

    @Override
    protected void createBlockStateDefinition (StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST, DOWNUP, NORTHSOUTH, EASTWEST);
    }

    /*
        Methods for Blocks that are not full and solid cubes.
     */

    private VoxelShape getCollision (BlockState state) {

        List<VoxelShape> collidingBoxes = new ArrayList<>();

        if (state.getValue(DOWN)) collidingBoxes.add(DOWN_AABB);
        if (state.getValue(UP)) collidingBoxes.add(UP_AABB);
        if (state.getValue(NORTH)) collidingBoxes.add(NORTH_AABB);
        if (state.getValue(EAST)) collidingBoxes.add(EAST_AABB);
        if (state.getValue(SOUTH)) collidingBoxes.add(SOUTH_AABB);
        if (state.getValue(WEST)) collidingBoxes.add(WEST_AABB);

        if (state.getValue(DOWNUP)) collidingBoxes.add(DOWNUP_AABB);
        if (state.getValue(NORTHSOUTH)) collidingBoxes.add(NORTHSOUTH_AABB);
        if (state.getValue(EASTWEST)) collidingBoxes.add(EASTWEST_AABB);

        if (!state.getValue(DOWNUP) && !state.getValue(NORTHSOUTH) && !state.getValue(EASTWEST)) collidingBoxes.add(CORE_AABB);

        VoxelShape[] shapes = new VoxelShape[collidingBoxes.size()];

        for (int i = 0; i < shapes.length; i++) {
            shapes[i] = collidingBoxes.get(i);
        }

        return Shapes.or(Block.box(0, 0, 0, 0, 0, 0), shapes);
    }

    /*
        Methods for Blocks that are not full and solid cubes.
    */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollision(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollision(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }


}
