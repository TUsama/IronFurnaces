package ironfurnaces.items.upgrades.furnace_upgrade;

import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.config.RainbowConfig;
import ironfurnaces.items.upgrades.furnace_upgrade.render.UpgradeToolClientExtensions;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.RainbowLimitHelper;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.upgrade.PatternUpgradeRule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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


    private static @NotNull InteractionResult whenInvalided(UseOnContext context, Player player) {
        if (player != null)
            player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.invalided_upgrade_tool"));
        context.getItemInHand().shrink(context.getItemInHand().getCount());
        return InteractionResult.FAIL;
    }

    private static @NotNull InteractionResult whenBlockSuccess(
            UseOnContext context,
            Block toBlock,
            @Nullable Player player,
            Level level,
            BlockPos pos,
            BlockState oldState
    ) {
        BlockState newState = toBlock.defaultBlockState();

        if (oldState.hasProperty(HorizontalDirectionalBlock.FACING)
                && newState.hasProperty(HorizontalDirectionalBlock.FACING)) {
            newState = newState.setValue(HorizontalDirectionalBlock.FACING, oldState.getValue(HorizontalDirectionalBlock.FACING));
        }

        level.setBlock(pos, newState, 3);

        if (player != null) {
            player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.success"));
        }
        context.getItemInHand().shrink(1);
        level.playLocalSound(pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7f, 1.0f, true);
        return InteractionResult.CONSUME;
    }

    private static @NotNull InteractionResult whenPatternSuccess(
            UseOnContext context,
            @Nullable BlockEntity blockEntity,
            FurnacePattern to,
            @Nullable Player player,
            Level level,
            BlockPos pos,
            BlockState oldState
    ) {
        FurnacePatternBlockEntity currentPatternBe =
                blockEntity instanceof FurnacePatternBlockEntity be ? be : null;

        if (to.isRainbow()) {
            boolean allowed = RainbowLimitHelper.canApplyRainbowPattern(
                    player,
                    level,
                    pos,
                    currentPatternBe,
                    RainbowConfig.config.max_rainbow_furnace_per_player.get()
            );

            if (!allowed) {
                if (player != null) {
                    player.sendSystemMessage(Component.translatable(
                            "item.ironfurnaces.upgrade_tool.rainbow_limit_reached",
                            RainbowConfig.config.max_rainbow_furnace_per_player.get()
                    ));
                }
                return InteractionResult.FAIL;
            }
        }

        if (currentPatternBe != null) {
            currentPatternBe.ensureOwner(player);
            currentPatternBe.updatePattern(to);
            OwnerRainbowContextHelper.markDirty(player instanceof ServerPlayer sp ? sp : null);

            if (!level.isClientSide && player != null) {
                PlayerDataHandler.editFurnacesList(player, x -> x.add(level.dimension(), pos));
            }
        } else {
            BlockState newState = ModBlocks.PATTERN_HOLDER.getDefaultState();
            if (oldState.hasProperty(HorizontalDirectionalBlock.FACING)
                    && newState.hasProperty(HorizontalDirectionalBlock.FACING)) {
                newState = newState.setValue(HorizontalDirectionalBlock.FACING, oldState.getValue(HorizontalDirectionalBlock.FACING));
            }

            level.setBlock(pos, newState, 3);

            BlockEntity newBe = level.getBlockEntity(pos);
            if (newBe instanceof FurnacePatternBlockEntity furnacePatternBlockEntity) {
                furnacePatternBlockEntity.ensureOwner(player);
                furnacePatternBlockEntity.updatePattern(to);
                furnacePatternBlockEntity.transferStacksInUnavailableSlotsToPlayer(player);
                OwnerRainbowContextHelper.markDirty(player instanceof ServerPlayer sp ? sp : null);
                if (!level.isClientSide && player != null) {
                    PlayerDataHandler.editFurnacesList(player, x -> x.add(level.dimension(), pos));
                }
            } else {
                return InteractionResult.FAIL;
            }
        }

        if (player != null) {
            player.sendSystemMessage(Component.translatable("item.ironfurnaces.upgrade_tool.success"));
        }
        context.getItemInHand().shrink(1);
        level.playLocalSound(pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7f, 1.0f, true);
        return InteractionResult.CONSUME;
    }


    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal(""));
        PatternUpgradeRule rule = IUpgradeStorage.get(stack);
        if (rule != null) {
            var fromName = FurnacePattern.toDisplayName(rule.from()).copy().withStyle(ChatFormatting.YELLOW);
            var toName = FurnacePattern.toDisplayName(rule.to()).copy().withStyle(ChatFormatting.GREEN);

            tooltip.add(Component.translatable("item.ironfurnaces.upgrade_tool.upgrade_rule", fromName, toName));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tooltip." + IronFurnaces.MOD_ID + ".upgrade_right_click")
                    .setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        } else {
            tooltip.add(Component.translatable("item.ironfurnaces.upgrade_tool.broken_upgrade_tool")
                    .withStyle(style -> style.withBold(true).withColor(ChatFormatting.RED)));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return super.useOn(context);
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        BlockState blockState = level.getBlockState(pos);

        PatternUpgradeRule rule = IUpgradeStorage.get(context.getItemInHand());
        if (rule == null) {
            return super.useOn(context);
        }

        if (!rule.isFrom(blockState, blockEntity)) {
            if (player != null) {
                Component expected = FurnacePattern.toDisplayName(rule.from());
                Component actual =
                        blockEntity instanceof FurnacePatternBlockEntity patternBlockEntity
                                ? FurnacePattern.toDisplayName(patternBlockEntity.getPattern().id())
                                : blockState.getBlock().getName();

                player.sendSystemMessage(Component.translatable(
                        "item.ironfurnaces.upgrade_tool.mismatch_pattern",
                        expected,
                        actual
                ));
            }
            return InteractionResult.FAIL;
        }

        FurnacePattern toPattern = rule.getToPattern();
        if (toPattern != null) {
            return whenPatternSuccess(context, blockEntity, toPattern, player, level, pos, blockState);
        }

        Block toBlock = rule.getToBlock();
        if (toBlock != null) {
            return whenBlockSuccess(context, toBlock, player, level, pos, blockState);
        }

        return whenInvalided(context, player);
    }

    //? forge {
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new UpgradeToolClientExtensions());
    }
    //?}


}
