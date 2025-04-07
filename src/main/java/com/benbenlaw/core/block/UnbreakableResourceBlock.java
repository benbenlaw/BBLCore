package com.benbenlaw.core.block;

import com.benbenlaw.core.util.FakePlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class UnbreakableResourceBlock extends Block {

    public int dropHeightModifier;
    public Supplier<Item> toolToCollectTheBlockAsItem;
    public TagKey<Item> toolToCollectTheBlockAsTag;
    public String lootTable;
    public FakePlayer fakePlayer;
    public UnbreakableResourceBlock(Properties properties, int dropHeightModifier, String toolToCollectTheBlock, String lootTable) {
        super(properties);
        this.dropHeightModifier = dropHeightModifier;
        this.lootTable = lootTable;

        if (toolToCollectTheBlock.startsWith("#")) {
            this.toolToCollectTheBlockAsTag = TagKey.create(Registries.ITEM, ResourceLocation.parse(toolToCollectTheBlock.substring(1)));
        } else {
            this.toolToCollectTheBlockAsItem = () -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(toolToCollectTheBlock));
        }
    }


    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack tool) {

        BlockPos dropPos = new BlockPos(pos.getX(), pos.getY() + dropHeightModifier, pos.getZ());
        boolean isCorrectTool = false;

        if (toolToCollectTheBlockAsTag != null) {
            isCorrectTool = tool.is(this.toolToCollectTheBlockAsTag);
        }

        if (toolToCollectTheBlockAsItem != null) {
            isCorrectTool = tool.getItem() == toolToCollectTheBlockAsItem.get();
        }

        if (!isCorrectTool) {
            dropResources(state, level, (dropPos), blockEntity, player, tool);
            level.setBlockAndUpdate(pos, this.defaultBlockState());
        } else {
            popResource(level, pos, new ItemStack(this.asItem(), 1));
        }
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ServerLevel level = params.getLevel();

        if (fakePlayer == null) {
            fakePlayer = FakePlayerUtil.createFakePlayer(level, "resource_block_fake_player");
        }

        LootParams lootparams =  (new LootParams.Builder((ServerLevel) fakePlayer.level())).withParameter(LootContextParams.THIS_ENTITY, fakePlayer).withParameter(LootContextParams.ORIGIN, fakePlayer.position()).create(LootContextParamSets.GIFT);
        LootTable table = level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.parse(lootTable)));

        List <ItemStack> drops = new java.util.ArrayList<>(List.of(ItemStack.EMPTY));
        List <ItemStack> loot = table.getRandomItems(lootparams);
        drops.addAll(loot);

        return drops;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if (toolToCollectTheBlockAsItem != null) {
            tooltipComponents.add(Component.literal(toolToCollectTheBlockAsItem.get().toString()));
        }

        if (toolToCollectTheBlockAsTag != null) {
            tooltipComponents.add(Component.literal(toolToCollectTheBlockAsTag.toString()));
        }

    }
}
