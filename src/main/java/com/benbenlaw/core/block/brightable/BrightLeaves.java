package com.benbenlaw.core.block.brightable;

import com.benbenlaw.core.block.colored.util.ColorMap;
import com.benbenlaw.core.block.colored.util.IColored;
import com.benbenlaw.core.item.CoreDataComponents;
import com.benbenlaw.core.item.colored.ColoredBlockItem;
import com.benbenlaw.core.item.colored.ColoredItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BrightLeaves extends LeavesBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public BrightLeaves(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(DISTANCE, 7).setValue(PERSISTENT, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, DISTANCE, PERSISTENT, WATERLOGGED);
    }
}