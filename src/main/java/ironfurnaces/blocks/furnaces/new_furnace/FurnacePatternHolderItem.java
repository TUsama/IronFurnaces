package ironfurnaces.blocks.furnaces.new_furnace;

import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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

    private boolean resetSettings(ItemStack stack, Player player, int selected) {
        CompoundTag beTag = stack.getOrCreateTagElement(BlockItem.BLOCK_ENTITY_TAG);
        beTag.remove(FurnaceSettingsV2.NBT_KEY);

        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!player.isCrouching()) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide) {
            boolean success = resetSettings(stack, player, player.getInventory().selected);
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
                        SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }


        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
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
    private static void applyJovialFromItemTag(Level level, BlockPos pos, ItemStack stack) {
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains("BlockStateTag", CompoundTag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag stateTag = root.getCompound("BlockStateTag");
        String key = ModBlockState.JOVIAL_STATE.getName();

        if (!stateTag.contains(key, CompoundTag.TAG_STRING)) {
            return;
        }

        String raw = stateTag.getString(key);
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
    public InteractionResult place(BlockPlaceContext context) {
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

        if (result.consumesAction() && !context.getLevel().isClientSide) {
            applyJovialFromItemTag(context.getLevel(), context.getClickedPos(), itemInHand);
        }


        return result;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return PatternHolderItemRenderer.INSTANCE;
            }
        });
    }
}
