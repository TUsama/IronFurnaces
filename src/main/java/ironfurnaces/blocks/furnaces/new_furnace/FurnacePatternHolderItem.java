package ironfurnaces.blocks.furnaces.new_furnace;

import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
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

        CompoundTag beTag = BlockItem.getBlockEntityData(stack);
        if (beTag != null) {
            if (beTag.contains(FurnaceSettingsV2.NBT_KEY, CompoundTag.TAG_COMPOUND)) {
                FurnaceSettingsV2.CODEC.parse(NbtOps.INSTANCE, beTag.getCompound(FurnaceSettingsV2.NBT_KEY))
                        .result()
                        .ifPresentOrElse(fp::setWholeSettingV2, () -> {
                            if (player != null) {
                                player.sendSystemMessage(Component.translatable("item.ironfurnaces.pattern_holder_item.read_setting_failed"));
                            }
                        });
                fp.setChanged();
                changed = true;
            }
            FurnacePattern furnacePatternFromTag = IPatternAccessor.getFurnacePatternFromTag(stack);
            if (furnacePatternFromTag != null) {
                fp.updatePattern(furnacePatternFromTag);
                fp.setChanged();
                changed = true;
            } else {
                if (player != null)
                    player.sendSystemMessage(Component.translatable("item.ironfurnaces.pattern_holder_item.read_pattern_failed"));
            }

        }
/*
        try {
            FurnaceMode mode = state.getValue(ModBlockState.FURNACE_MODE);

            fp.updateFurnaceMode(mode);

            fp.setChanged();
            changed = true;
        } catch (Exception exception) {
            IronFurnaces.LOGGER.error(exception);
        }
*/
        return changed;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack itemInHand = context.getItemInHand();
        FurnacePattern furnacePatternFromTag = IPatternAccessor.getFurnacePatternFromTag(itemInHand);
        if (furnacePatternFromTag == null) {
            Player player = context.getPlayer();
            if (player != null)
                player.sendSystemMessage(Component.translatable("item.ironfurnaces.furnace_pattern_holder.refuse_place_empty_tag"));
            return InteractionResult.FAIL;
        }

        return super.place(context);
    }
}
