package ironfurnaces.blocks.furnaces.new_furnace;

import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.data_component.PatternHolderInfo;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FurnacePatternHolderItem extends BlockItem {
    public FurnacePatternHolderItem(Block block, Properties properties) {
        super(block, properties);
    }


    private static void applyJovialFromItemTag(Level level, BlockPos pos, ItemStack stack) {
        String key = ModBlockState.JOVIAL_STATE.getName();
        String raw;

        if (!stack.has(DataComponents.BLOCK_STATE)) return;
        raw = stack.get(DataComponents.BLOCK_STATE).properties().get(key);


        var jovial = ModBlockState.JOVIAL_STATE.getPossibleValues().stream()
                .filter(x -> x.getSerializedName().equals(raw))
                .findFirst()
                .orElse(null);

        if (jovial == null) {
            return;
        }

        BlockState current = level.getBlockState(pos);
        if (current.hasProperty(ModBlockState.JOVIAL_STATE)
                && current.getValue(ModBlockState.JOVIAL_STATE) != jovial) {
            level.setBlock(pos, current.setValue(ModBlockState.JOVIAL_STATE, jovial), 3);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        FurnacePattern furnacePatternFromTag = IPatternAccessor.getFurnacePatternFromTag(stack);
        if (furnacePatternFromTag == null) {
            return super.getName(stack);
        }

        return Component.translatable(
                "block.ironfurnaces." + furnacePatternFromTag.id().getPath()
        );
    }

    private boolean resetSettings(ItemStack stack, Player player, int selected) {
        FurnaceSettingsV2.removeSetting(stack);
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!player.isCrouching()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            boolean success = resetSettings(stack, player, player.getInventory().getSelectedSlot());
            if (success) {
                player.sendSystemMessage(Component.translatable(
                        "item.ironfurnaces.pattern_holder_item.reset_setting_success"
                ));
                level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.2F, 2.0F);
            } else {
                player.sendSystemMessage(Component.translatable(
                        "item.ironfurnaces.pattern_holder_item.reset_setting_failed"
                ));
                level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                        SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 0.2F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }


        }

        return super.use(level, player, usedHand);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
            BlockPos pos,
            Level level,
            @Nullable Player player,
            ItemStack stack,
            BlockState state
    ) {
        boolean changed = super.updateCustomBlockEntityTag(pos, level, player, stack, state);

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FurnacePatternBlockEntity fp)) {
            return changed;
        }

        if (player != null) {
            fp.ensureOwner(player);
            if (player instanceof ServerPlayer serverPlayer) OwnerRainbowContextHelper.markDirty(serverPlayer);
        }

        if (stack.has(ModDataComponents.PATTERN_HOLDER_INFO)) {
            PatternHolderInfo patternHolderInfo = stack.get(ModDataComponents.PATTERN_HOLDER_INFO);
            patternHolderInfo.writeToBlockEntity(fp);
            changed = true;
        }

        return changed;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (context.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        ItemStack itemInHand = context.getItemInHand();
        FurnacePattern furnacePatternFromTag = IPatternAccessor.getFurnacePatternFromTag(itemInHand);
        if (furnacePatternFromTag == null) {
            Player player = context.getPlayer();
            if (player != null) {
                player.sendSystemMessage(Component.translatable("item.ironfurnaces.furnace_pattern_holder.refuse_place_empty_tag"));
            }
            return InteractionResult.FAIL;
        }

        InteractionResult result = super.place(context);

        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            applyJovialFromItemTag(context.getLevel(), context.getClickedPos(), itemInHand);
        }


        return result;
    }


}
