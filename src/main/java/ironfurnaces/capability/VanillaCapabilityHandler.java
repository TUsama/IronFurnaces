package ironfurnaces.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;


import java.util.function.Function;

public final class VanillaCapabilityHandler {
    private VanillaCapabilityHandler() {
    }

    /*
     * ----------------------------
     * Item capability helpers
     * ----------------------------
     */

    public static @Nullable IItemHandler getBlockItemHandler(BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }

        //? if <=1.20.1 {
        return blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, side)
                .resolve()
                .orElse(null);
        //?} else {
        /*Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }

        return level.getCapability(
                net.minecraftforge.capabilities.Capabilities.ItemHandler.BLOCK,
                blockEntity.getBlockPos(),
                blockEntity.getBlockState(),
                blockEntity,
                side
        );
        *///?}
    }

    public static @Nullable IItemHandler getBlockItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
        if (level == null || pos == null) {
            return null;
        }

        //? if <=1.20.1 {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }
        return blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, side)
                .resolve()
                .orElse(null);
        //?} else {
        /*return level.getCapability(
                net.minecraftforge.capabilities.Capabilities.ItemHandler.BLOCK,
                pos,
                side
        );
        *///?}
    }

    public static void withBlockItemHandler(BlockEntity blockEntity, @Nullable Direction side, Consumer<IItemHandler> consumer) {
        IItemHandler handler = getBlockItemHandler(blockEntity, side);
        if (handler != null) {
            consumer.accept(handler);
        }
    }

    public static void withBlockItemHandler(Level level, BlockPos pos, @Nullable Direction side, Consumer<IItemHandler> consumer) {
        IItemHandler handler = getBlockItemHandler(level, pos, side);
        if (handler != null) {
            consumer.accept(handler);
        }
    }

    public static <R> @Nullable R mapBlockItemHandler(BlockEntity blockEntity, @Nullable Direction side, Function<IItemHandler, R> mapper) {
        IItemHandler handler = getBlockItemHandler(blockEntity, side);
        return handler == null ? null : mapper.apply(handler);
    }

    public static <R> @Nullable R mapBlockItemHandler(Level level, BlockPos pos, @Nullable Direction side, Function<IItemHandler, R> mapper) {
        IItemHandler handler = getBlockItemHandler(level, pos, side);
        return handler == null ? null : mapper.apply(handler);
    }

    /*
     * ----------------------------
     * Energy capability helpers
     * ----------------------------
     */

    public static @Nullable IEnergyStorage getBlockEnergyStorage(BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }

        //? if <=1.20.1 {
        return blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY, side)
                .resolve()
                .orElse(null);
        //?} else {
        /*Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }

        return level.getCapability(
                net.minecraftforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                blockEntity.getBlockPos(),
                blockEntity.getBlockState(),
                blockEntity,
                side
        );
        *///?}
    }

    public static @Nullable IEnergyStorage getBlockEnergyStorage(Level level, BlockPos pos, @Nullable Direction side) {
        if (level == null || pos == null) {
            return null;
        }

        //? if <=1.20.1 {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }
        return blockEntity.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY, side)
                .resolve()
                .orElse(null);
        //?} else {
        /*return level.getCapability(
                net.minecraftforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                pos,
                side
        );
        *///?}
    }

    public static void withBlockEnergyStorage(BlockEntity blockEntity, @Nullable Direction side, Consumer<IEnergyStorage> consumer) {
        IEnergyStorage storage = getBlockEnergyStorage(blockEntity, side);
        if (storage != null) {
            consumer.accept(storage);
        }
    }

    public static void withBlockEnergyStorage(Level level, BlockPos pos, @Nullable Direction side, Consumer<IEnergyStorage> consumer) {
        IEnergyStorage storage = getBlockEnergyStorage(level, pos, side);
        if (storage != null) {
            consumer.accept(storage);
        }
    }

    public static <R> @Nullable R mapBlockEnergyStorage(BlockEntity blockEntity, @Nullable Direction side, Function<IEnergyStorage, R> mapper) {
        IEnergyStorage storage = getBlockEnergyStorage(blockEntity, side);
        return storage == null ? null : mapper.apply(storage);
    }

    public static <R> @Nullable R mapBlockEnergyStorage(Level level, BlockPos pos, @Nullable Direction side, Function<IEnergyStorage, R> mapper) {
        IEnergyStorage storage = getBlockEnergyStorage(level, pos, side);
        return storage == null ? null : mapper.apply(storage);
    }
}
