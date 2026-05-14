package ironfurnaces.blocks;

import ironfurnaces.adaptor.energy.EnergyWrapper;
import ironfurnaces.capability.VanillaCapabilityHandler;
import ironfurnaces.container.BlockWirelessEnergyHeaterContainer;
import ironfurnaces.registration.ModBlocks;
import ironfurnaces.registration.ModMenus;
import ironfurnaces.tileentity.heater.BlockWirelessEnergyHeaterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
//? forge {
/*import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.network.NetworkHooks;
*///?} else {
import net.minecraft.core.component.DataComponents;
import ironfurnaces.registration.ModDataComponents;
import net.neoforged.neoforge.transfer.transaction.Transaction;
//?}
import javax.annotation.Nullable;

public class BlockWirelessEnergyHeater extends Block implements EntityBlock {

    public static final String HEATER = "heater";


    public BlockWirelessEnergyHeater(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
        return p_152134_ == p_152133_ ? (BlockEntityTicker<A>) p_152135_ : null;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level p_151988_, BlockEntityType<T> p_151989_, BlockEntityType<? extends BlockWirelessEnergyHeaterTile> p_151990_) {
        return p_151988_.isClientSide() ? null : createTickerHelper(p_151989_, p_151990_, BlockWirelessEnergyHeaterTile::tick);
    }

    private static void setNameIfCustomNameExist(BlockWirelessEnergyHeaterTile te, ItemStack stack) {
        if (te.hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME, te.getDisplayName());
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BlockWirelessEnergyHeaterTile(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, ModBlocks.asGenericBlockEntityType(ModBlocks.HEATER));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide()) {
            BlockWirelessEnergyHeaterTile te = (BlockWirelessEnergyHeaterTile) level.getBlockEntity(pos);
            ItemStack stack = new ItemStack(ModBlocks.HEATER.get());
            setNameIfCustomNameExist(te, stack);
            var wrapper = te.getEnergy();
            if (wrapper.getAmountAsInt() > 0) {
                VanillaCapabilityHandler.withItemEnergyStorage(stack, x -> {
                    try (Transaction tx = Transaction.openRoot()){
                        x.insert(wrapper.getAmountAsInt(), tx);
                        tx.commit();
                    }

                });
            }
            if (!player.isCreative())
                Containers.dropItemStack(level, te.getBlockPos().getX(), te.getBlockPos().getY(), te.getBlockPos().getZ(), stack);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }


    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            BlockWirelessEnergyHeaterTile te = (BlockWirelessEnergyHeaterTile) world.getBlockEntity(pos);
            setNameIfCustomNameExist(te, stack);
            VanillaCapabilityHandler.withItemEnergyStorage(stack, energy ->{
                VanillaCapabilityHandler.withBlockEnergyStorage(te, null, h -> {
                    try (Transaction tx = Transaction.openRoot()){
                        h.insert(energy.getAmountAsInt(), tx);
                        tx.commit();
                    }
                });
            });
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            this.interactWith(level, pos, player);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }



    private void interactWith(Level world, BlockPos pos, Player player) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof BlockWirelessEnergyHeaterTile tile) {
            ModMenus.HEATER_MENU.open((ServerPlayer) player, Component.translatable("container.ironfurnaces.wireless_energy_heater"), (window, playerinv, $) -> new BlockWirelessEnergyHeaterContainer(window, playerinv, tile), buf -> {
                buf.writeBlockPos(tileEntity.getBlockPos());
            });
        }
    }

}
