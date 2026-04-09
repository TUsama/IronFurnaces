package ironfurnaces.blocks.furnaces.new_furnace;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ironfurnaces.Config;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.capability.rainbow.OwnerRainbowContextHelper;
import ironfurnaces.items.IJovialSetter;
import ironfurnaces.items.ItemFurnaceCopyV2;
import ironfurnaces.items.ItemJovial;
import ironfurnaces.items.JovialState;
import ironfurnaces.items.upgrades.furnace_pattern.IPatternAccessor;
import ironfurnaces.registration.ModBlockEntities;
import ironfurnaces.registration.ModBlockState;
import ironfurnaces.registration.ModDataComponents;
import ironfurnaces.registration.ModMenus;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.cache.AugmentCache;
import ironfurnaces.tileentity.furnaces.menu.FurnacePatternMenu;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.IFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.NormalFurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.RainbowFurnacePattern;
import ironfurnaces.tileentity.furnaces.setting.FurnaceSettingsV2;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

//? 1.20.1 {
import net.minecraftforge.registries.ForgeRegistries;
//? } else {
/*import net.minecraft.core.component.DataComponents;
*///?}
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.network.chat.Component.translatable;

public class FurnacePatternHolderBlock extends BaseEntityBlock implements EntityBlock {

    public static final String ID = "pattern_holder";

    public FurnacePatternHolderBlock(Properties properties) {
        super(properties.destroyTime(3F));
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.LIT, false)
                //.setValue(ModBlockState.FURNACE_MODE, FurnaceMode.FURNACE)
                .setValue(ModBlockState.HANDLING_RECIPE_TYPE, AugmentCache.HandlingRecipeType.NORMAL)
                .setValue(ModBlockState.JOVIAL_STATE, JovialState.NONE));
    }
    //? >1.20.1 {
    /*private final static MapCodec<FurnacePatternHolderBlock> CODEC = simpleCodec(FurnacePatternHolderBlock::new);
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    *///?}
    @Nullable
    private FurnacePatternBlockEntity getPatternHolder(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof FurnacePatternBlockEntity holder ? holder : null;
    }

    private Optional<Block> getReferenceBlock(BlockGetter level, BlockPos pos) {
        FurnacePatternBlockEntity holder = getPatternHolder(level, pos);
        if (holder == null) {
            return Optional.empty();
        }

        FurnacePattern pattern = holder.getPattern();
        if (pattern == null) {
            return Optional.empty();
        }

        return pattern.referenceBlock()
                //~ if >1.20.1 'ForgeRegistries.BLOCKS::getValue' -> 'BuiltInRegistries.BLOCK::get'
                .map(ForgeRegistries.BLOCKS::getValue)
                .filter(block -> block != null && block != Blocks.AIR);
    }

    private BlockState getReferenceStateOrNull(BlockGetter level, BlockPos pos) {
        return getReferenceBlock(level, pos)
                .map(Block::defaultBlockState)
                .orElse(null);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        if (reference != null) {
            return reference.getBlock().getExplosionResistance(reference, level, pos, explosion);
        }
        return super.getExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        if (reference != null) {
            //~ if >1.20.1 'reference.getBlock().getDestroyProgress(reference, player, level, pos)' -> 'reference.getDestroyProgress(player, level, pos)'
            return reference.getBlock().getDestroyProgress(reference, player, level, pos);
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        if (reference != null) {
            return reference.getLightBlock(level, pos);
        }
        return super.getLightBlock(state, level, pos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        if (reference != null) {
            return reference.propagatesSkylightDown(level, pos);
        }
        return super.propagatesSkylightDown(state, level, pos);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        if (reference != null) {
            return reference.getShadeBrightness(level, pos);
        }
        return super.getShadeBrightness(state, level, pos);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        if (reference != null) {
            return reference.getSoundType(level, pos, entity);
        }
        return super.getSoundType(state, level, pos, entity);
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        BlockState particleState = reference != null ? reference : state;

        level.levelEvent(player, 2001, pos, Block.getId(particleState));
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockState reference = getReferenceStateOrNull(level, pos);
        if (reference == null) {
            return super.addRunningEffects(state, level, pos, entity);
        }

        Vec3 movement = entity.getDeltaMovement();
        if (movement.x * movement.x + movement.z * movement.z < 1.0E-7D) {
            return false;
        }

        RandomSource random = level.random;
        double x = entity.getX() + (random.nextDouble() - 0.5D) * (double) entity.getBbWidth();
        double y = entity.getY() + 0.1D;
        double z = entity.getZ() + (random.nextDouble() - 0.5D) * (double) entity.getBbWidth();

        level.addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, reference),
                x, y, z,
                movement.x * -4.0D,
                1.5D,
                movement.z * -4.0D
        );
        return true;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createFurnaceTicker(Level level, BlockEntityType<T> serverType, BlockEntityType<? extends FurnacePatternBlockEntity> clientType) {
        return level.isClientSide ? null : createTickerHelper(serverType, clientType, FurnacePatternBlockEntity::serverTick);
    }

    @Override
    public boolean isOcclusionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return super.isOcclusionShapeFullBlock(state, level, pos);
    }

    @Override
    public @org.jetbrains.annotations.Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createFurnaceTicker(level, blockEntityType, ModBlockEntities.PATTERN_HOLDER.get());
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (Config.disableLightupdates.get()) {
            return 0;
        }
        return state.getValue(BlockStateProperties.LIT) ? 14 : 0;
    }

    @Override
            //~ if >1.20.1 'BlockGetter' -> 'LevelReader'
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FurnacePatternBlockEntity furnace) {
            FurnacePattern pattern = furnace.getPattern();
            if (pattern != null && pattern != FurnacePattern.FALLBACK) {
                IPatternAccessor.writePatternToItemStack(stack, pattern);
            }
            furnace.getSettingsV2().writeToStack(stack);


            if (furnace.hasCustomName()) {
                stack.setHoverName(furnace.getCustomName());
            }
        }

        return stack;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return (BlockState) this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
            //~ if >1.20.1 'BlockGetter level' -> 'Item.TooltipContext context'
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        //~ if >1.20.1 'level' -> 'context'
        super.appendHoverText(stack, level, tooltip, flag);

        FurnacePattern furnacePatternFromTag = IPatternAccessor.getFurnacePatternFromTag(stack);
        if (furnacePatternFromTag != null) {
            int s = 0;
            if (furnacePatternFromTag instanceof NormalFurnacePattern pattern1){
                s = pattern1.smeltTick();
            } else if (furnacePatternFromTag instanceof RainbowFurnacePattern pattern){
                s = pattern.baseSmeltTickPerItem();
            }
            if (s != 0){
                tooltip.add(translatable("ironfurnaces.block.furnace.work_speed", Component.literal(String.valueOf(s)).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));
            }

        } else {
            tooltip.add(translatable("block.ironfurnaces.furnace_pattern_holder.without_pattern")
                    .withStyle(ChatFormatting.RED));
        }

        tooltip.addAll(FurnaceSettingsV2.getSettingFromStack(stack).toTooltips());

        tooltip.add(translatable("item.ironfurnaces.furnace_pattern_holder.reset_setting_hint"));

    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof FurnacePatternBlockEntity te)) {
            return;
        }
        //I think this should be done by minecraft itselt, see applyImplicitComponents().
        //? 1.20.1 {
        if (stack.hasCustomHoverName() && !stack.getDisplayName().getString().contains("[")) {
            te.setCustomName(stack.getDisplayName());
        }
        //?}
        if (!level.isClientSide && entity instanceof Player player) {
            te.ensureOwner(player);
            PlayerDataHandler.editFurnacesList(player, x -> x.add(level.dimension(), pos));

        }
    }
    //? 1.20.1 {
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack itemInHand = player.getItemInHand(handIn);
        Item item = itemInHand.getItem();
        if (player.isCrouching()) {
            if (itemInHand.isEmpty()){
                IJovialSetter.clearJovial(level, pos);
                return InteractionResult.SUCCESS;
            } else if (item instanceof ItemFurnaceCopyV2 || item instanceof ItemJovial) {
                return InteractionResult.PASS;
            }
        } else {
            if (item instanceof ItemFurnaceCopyV2 || item instanceof ItemJovial) {
                return InteractionResult.PASS;
            }
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FurnacePatternBlockEntity furnacePatternBlockEntity && furnacePatternBlockEntity.getPattern() != FurnacePattern.FALLBACK) {
            ModMenus.NEW_FURNACE_MENU.open(((ServerPlayer) player), Component.literal(""), new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("");
                }

                @Override
                public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                    FurnacePatternBlockEntity blockEntity1 = (FurnacePatternBlockEntity) level.getBlockEntity(pos);
                    if (player instanceof ServerPlayer serverPlayer) blockEntity1.addPlayer(serverPlayer);
                    return new FurnacePatternMenu(ModMenus.NEW_FURNACE_MENU.get(), containerId, blockEntity1, playerInventory, pos, blockEntity1.getDataAccess());
                }
            }, buf -> {

                buf.writeJsonWithCodec(FurnacePattern.REF_CODEC, furnacePatternBlockEntity.getPattern());
                buf.writeJsonWithCodec(IFurnaceStats.CODEC, furnacePatternBlockEntity.getUsedStats());
                buf.writeBlockPos(pos);
            });
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
            return InteractionResult.CONSUME;
        } else {
            player.sendSystemMessage(translatable("block.ironfurnaces.furnace_pattern_holder.refuse_open_empty_pattern"));
            return InteractionResult.PASS;
        }

    }
    //?} else {

    /*@Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isCrouching()){
            IJovialSetter.clearJovial(level, pos);
            return InteractionResult.CONSUME;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof FurnacePatternBlockEntity furnacePatternBlockEntity && furnacePatternBlockEntity.getPattern() != FurnacePattern.FALLBACK) {
                ModMenus.NEW_FURNACE_MENU.open(((ServerPlayer) player), Component.literal(""), new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.literal("");
                    }

                    @Override
                    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                        FurnacePatternBlockEntity blockEntity1 = (FurnacePatternBlockEntity) level.getBlockEntity(pos);
                        if (player instanceof ServerPlayer serverPlayer) blockEntity1.addPlayer(serverPlayer);
                        return new FurnacePatternMenu(ModMenus.NEW_FURNACE_MENU.get(), containerId, blockEntity1, playerInventory, pos, blockEntity1.getDataAccess());
                    }
                }, buf -> {

                    buf.writeJsonWithCodec(FurnacePattern.REF_CODEC, furnacePatternBlockEntity.getPattern());
                    buf.writeJsonWithCodec(IFurnaceStats.CODEC, furnacePatternBlockEntity.getUsedStats());
                    buf.writeBlockPos(pos);
                });
                player.awardStat(Stats.INTERACT_WITH_FURNACE);
                return InteractionResult.CONSUME;
            } else {
                player.sendSystemMessage(translatable("block.ironfurnaces.furnace_pattern_holder.refuse_open_empty_pattern"));
                return super.useWithoutItem(state, level, pos, player, hitResult);
            }
        }

    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        Item item = stack.getItem();

        if (item instanceof ItemFurnaceCopyV2 || item instanceof ItemJovial) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    *///?}

    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        super.animateTick(state, world, pos, rand);
        if (state.getValue(BlockStateProperties.LIT)) {
            if (world.getBlockEntity(pos) == null) {
                return;
            }
            if (!(world.getBlockEntity(pos) instanceof FurnacePatternBlockEntity v2)) {
                return;
            }
            RecipeType<?> currentRecipeType = v2.getAugments().getCurrentRecipeType().recipeType.get();
            if (currentRecipeType == RecipeType.SMOKING) {
                double lvt_5_1_ = (double) pos.getX() + 0.5D;
                double lvt_7_1_ = (double) pos.getY();
                double lvt_9_1_ = (double) pos.getZ() + 0.5D;
                if (rand.nextDouble() < 0.1D) {
                    world.playLocalSound(lvt_5_1_, lvt_7_1_, lvt_9_1_, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }

                world.addParticle(ParticleTypes.SMOKE, lvt_5_1_, lvt_7_1_ + 1.1D, lvt_9_1_, 0.0D, 0.0D, 0.0D);

            } else if (currentRecipeType == RecipeType.BLASTING) {
                double lvt_5_1_ = (double) pos.getX() + 0.5D;
                double lvt_7_1_ = (double) pos.getY();
                double lvt_9_1_ = (double) pos.getZ() + 0.5D;
                if (rand.nextDouble() < 0.1D) {
                    world.playLocalSound(lvt_5_1_, lvt_7_1_, lvt_9_1_, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }

                Direction lvt_11_1_ = (Direction) state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                Direction.Axis lvt_12_1_ = lvt_11_1_.getAxis();
                double lvt_13_1_ = 0.52D;
                double lvt_15_1_ = rand.nextDouble() * 0.6D - 0.3D;
                double lvt_17_1_ = lvt_12_1_ == Direction.Axis.X ? (double) lvt_11_1_.getStepX() * 0.52D : lvt_15_1_;
                double lvt_19_1_ = rand.nextDouble() * 9.0D / 16.0D;
                double lvt_21_1_ = lvt_12_1_ == Direction.Axis.Z ? (double) lvt_11_1_.getStepZ() * 0.52D : lvt_15_1_;
                world.addParticle(ParticleTypes.SMOKE, lvt_5_1_ + lvt_17_1_, lvt_7_1_ + lvt_19_1_, lvt_9_1_ + lvt_21_1_, 0.0D, 0.0D, 0.0D);

            } else {
                double d0 = (double) pos.getX() + 0.5D;
                double d1 = (double) pos.getY();
                double d2 = (double) pos.getZ() + 0.5D;
                if (rand.nextDouble() < 0.1D) {
                    world.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);

                }

                Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                Direction.Axis direction$axis = direction.getAxis();
                double d3 = 0.52D;
                double d4 = rand.nextDouble() * 0.6D - 0.3D;
                double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
                double d6 = rand.nextDouble() * 6.0D / 16.0D;
                double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
                world.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);

            }
        }

        if (world.getBlockEntity(pos) instanceof FurnacePatternBlockEntity v2 && v2.getPattern().isRainbow()) {
            if (state.getValue(BlockStateProperties.LIT) && v2.getMode().equals(FurnaceMode.GENERATOR)) {
                 {
                    for (Direction direction : Direction.values()) {
                        if (Direction.from3DDataValue(direction.get3DDataValue()) != Direction.UP
                                && Direction.from3DDataValue(direction.get3DDataValue()) != Direction.DOWN) {
                            double d0 = (double) pos.getX() + 0.5D;
                            double d1 = (double) pos.getY();
                            double d2 = (double) pos.getZ() + 0.5D;
                            Direction.Axis direction$axis = direction.getAxis();
                            double d3 = 0.52D;
                            double d4 = rand.nextDouble() * 0.6D - 0.3D;
                            double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
                            double d6 = rand.nextDouble() * 6.0D / 16.0D;
                            double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;

                            for (int i = 0; i < 10; i++) {
                                world.addParticle(ParticleTypes.CRIT, d0 + d5, d1 + d6, d2 + d7, rand.nextGaussian() * 0.05D, 0.0D, rand.nextGaussian() * 0.05D);
                                //~ if >1.20.1 'ParticleTypes.AMBIENT_ENTITY_EFFECT' -> 'ParticleTypes.EFFECT'
                                world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, d0 + d5, d1 + d6, d2 + d7, rand.nextGaussian() * 0.05D, 0.0D, rand.nextGaussian() * 0.05D);
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (oldState.getBlock() != newState.getBlock()) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof FurnacePatternBlockEntity furnace) {

                if (!world.isClientSide) {
                    UUID ownerUuid = furnace.getOwnerUuid();
                    if (ownerUuid != null && world.getServer() != null) {
                        Player owner = world.getServer().getPlayerList().getPlayer(ownerUuid);
                        if (owner != null) {
                            PlayerDataHandler.editFurnacesList(owner, x -> x.remove(world.dimension(), pos));
                        }
                    }
                }

                Containers.dropContents(world, pos, furnace);
                if (world instanceof ServerLevel serverLevel) {
                    furnace.getRecipeAwardHandler().grantStoredRecipeExperience(serverLevel, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
                    OwnerRainbowContextHelper.markDirtyByOwnerUuid(serverLevel, furnace.getOwnerUuid());
                }
                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(oldState, world, pos, newState, movedByPiston);
        }
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return (BlockState) p_185499_1_.setValue(BlockStateProperties.HORIZONTAL_FACING, p_185499_2_.rotate((Direction) p_185499_1_.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation((Direction) p_185471_1_.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public boolean isSignalSource(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public int getSignal(BlockState p_180656_1_, BlockGetter p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
        return getDirectSignal(p_180656_1_, p_180656_2_, p_180656_3_, p_180656_4_);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter world, BlockPos pos, Direction direction) {
        if (world.getBlockEntity(pos) instanceof FurnacePatternBlockEntity furnace) {
            return furnace.getSideRedstoneSignal(direction);
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof FurnacePatternBlockEntity furnace) {
            return furnace.getComparatorLikeOutput();
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT, ModBlockState.HANDLING_RECIPE_TYPE, ModBlockState.JOVIAL_STATE);
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.PATTERN_HOLDER.create(pos, state);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {
            @Override
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                if (!(target instanceof BlockHitResult blockHit)) {
                    return false;
                }
                if (!(level instanceof ClientLevel clientLevel)) return false;

                BlockPos pos = blockHit.getBlockPos();
                BlockState reference = getReferenceStateOrNull(level, pos);
                if (reference == null) {
                    return false;
                }

                Direction face = blockHit.getDirection();
                VoxelShape shape = reference.getShape(level, pos);
                if (shape.isEmpty()) {
                    shape = Shapes.block();
                }

                AABB aabb = shape.bounds();
                RandomSource random = level.random;

                double x = pos.getX() + random.nextDouble() * (aabb.maxX - aabb.minX - 0.2D) + 0.1D + aabb.minX;
                double y = pos.getY() + random.nextDouble() * (aabb.maxY - aabb.minY - 0.2D) + 0.1D + aabb.minY;
                double z = pos.getZ() + random.nextDouble() * (aabb.maxZ - aabb.minZ - 0.2D) + 0.1D + aabb.minZ;

                switch (face) {
                    case DOWN -> y = pos.getY() + aabb.minY - 0.1D;
                    case UP -> y = pos.getY() + aabb.maxY + 0.1D;
                    case NORTH -> z = pos.getZ() + aabb.minZ - 0.1D;
                    case SOUTH -> z = pos.getZ() + aabb.maxZ + 0.1D;
                    case WEST -> x = pos.getX() + aabb.minX - 0.1D;
                    case EAST -> x = pos.getX() + aabb.maxX + 0.1D;
                }

                manager.add(
                        (new TerrainParticle(clientLevel, x, y, z, 0.0D, 0.0D, 0.0D, reference, pos))
                                .setPower(0.2F)
                                .scale(0.6F)
                );
                return true;
            }
        });
    }
}
