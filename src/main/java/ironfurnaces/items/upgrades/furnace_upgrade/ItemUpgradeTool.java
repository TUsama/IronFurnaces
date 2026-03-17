package ironfurnaces.items.upgrades.furnace_upgrade;

import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderBlock;
import ironfurnaces.items.upgrades.furnace_upgrade.render.UpgradeToolClientExtensions;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRuleDatagen;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ItemUpgradeTool extends Item {
    public ItemUpgradeTool(Properties properties) {
        super(properties);
    }

    private static @NotNull InteractionResult whenSuccess(UseOnContext context, FurnacePatternBlockEntity patternBlockEntity, FurnacePattern to, Player player, Level level, BlockPos pos) {
         patternBlockEntity.updatePattern(to);
        if (player != null) player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.success"));
        context.getItemInHand().shrink(1);
        level.playLocalSound(pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7f, 1.0f, true);
        return InteractionResult.CONSUME;
    }

    private static @NotNull InteractionResult whenVanillaSuccess(UseOnContext context, FurnacePattern to, Player player, Level level, BlockPos pos) {
        level.setBlock(pos, ModBlocks.PATTERN_HOLDER.getDefaultState().setValue(HorizontalDirectionalBlock.FACING, level.getBlockState(pos).getValue(HorizontalDirectionalBlock.FACING)), 3);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FurnacePatternBlockEntity furnacePatternBlockEntity){
            furnacePatternBlockEntity.updatePattern(to);
        }
        if (player != null) player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.success"));
        context.getItemInHand().shrink(1);
        level.playLocalSound(pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7f, 1.0f, true);
        return InteractionResult.CONSUME;
    }

    private static @NotNull InteractionResult whenInvalided(UseOnContext context, Player player) {
        if (player != null)
            player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.invalided_upgrade_tool"));
        context.getItemInHand().shrink(context.getItemInHand().getCount());
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal(""));
        PatternUpgradeRule patternUpgradeRule = IUpgradeStorage.get(stack);
        if (patternUpgradeRule != null) {
            String from = patternUpgradeRule.from().getPath();
            String to = patternUpgradeRule.to().getPath();
            var fromName = (from.equals(PatternUpgradeRuleDatagen.VANILLA_FURNACE.getPath()) ? Component.translatable("block.minecraft.furnace") : Component.translatable("block.ironfurnaces." + from)).withStyle(ChatFormatting.YELLOW);
            var toName = Component.translatable("block.ironfurnaces." + to).withStyle(ChatFormatting.GREEN);
            tooltip.add(Component.translatable("item.ironfurnaces.upgrade_tool.upgrade_rule", fromName, toName));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".upgrade_right_click").setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))));
        } else {
            tooltip.add(Component.translatable("item.ironfurnaces.upgrade_tool.broken_upgrade_tool").withStyle(style -> style.withBold(true).withColor(ChatFormatting.RED)));

        }

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            BlockState blockState = level.getBlockState(pos);

            PatternUpgradeRule patternUpgradeRule = IUpgradeStorage.get(context.getItemInHand());
            if (patternUpgradeRule != null) {


                ResourceLocation from1 = patternUpgradeRule.from();


                FurnacePattern to = FurnacePatternManager.get(patternUpgradeRule.to());
                if (to == null) return whenInvalided(context, player);
                if (from1.equals(PatternUpgradeRuleDatagen.VANILLA_FURNACE)) {
                    if (blockState.is(Blocks.FURNACE)){
                        return whenVanillaSuccess(context, to, player, level, pos);
                    } else {
                        if (player != null)
                            player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.mismatch_pattern", Component.translatable("block.minecraft.furnace"), blockState.getBlock() instanceof FurnacePatternHolderBlock ? Component.translatable("block.ironfurnaces." + ((FurnacePatternBlockEntity) level.getBlockEntity(pos)).getPattern().id().getPath()) : blockState.getBlock().getName()));
                        return InteractionResult.FAIL;
                    }

                } else {
                    if (blockEntity instanceof FurnacePatternBlockEntity patternBlockEntity) {
                        FurnacePattern from = FurnacePatternManager.get(from1);
                        if (from == null) {
                            return whenInvalided(context, player);
                        }
                        if (patternBlockEntity.getPattern().equals(from)) {
                            return whenSuccess(context, patternBlockEntity, to, player, level, pos);

                        } else {
                            if (player != null) {

                                player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.mismatch_pattern", Component.translatable("block.ironfurnaces." + from.id().getPath()), blockState.getBlock() instanceof FurnacePatternHolderBlock ? Component.translatable("block.ironfurnaces." + patternBlockEntity.getPattern().id().getPath()) : blockState.getBlock().getName()));
                            }
                            return InteractionResult.FAIL;
                        }
                    } else {
                        return whenInvalided(context, player);
                    }


                }
            }


        }
        return super.useOn(context);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new UpgradeToolClientExtensions());
    }

}
