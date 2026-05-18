package ironfurnaces.capability;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;
//? forge {
/*import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
*///?} else {
import net.neoforged.neoforge.capabilities.Capabilities;
//?}
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;


import java.util.function.Function;

public final class VanillaCapabilityHandler {
    private VanillaCapabilityHandler() {
    }

    /*
     * ----------------------------
     * Item capability helpers
     * ----------------------------
     */

    public static @Nullable ResourceHandler<ItemResource> getBlockItemHandler(BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }

        //? if <=1.20.1 {
        /*return blockEntity.getCapability(net.neoforged.neoforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, side)
                .resolve()
                .orElse(null);
        *///?} else {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }

        return level.getCapability(
                Capabilities.Item.BLOCK,
                blockEntity.getBlockPos(),
                blockEntity.getBlockState(),
                blockEntity,
                side
        );
        //?}
    }

    public static @Nullable ResourceHandler<ItemResource> getBlockItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
        if (level == null || pos == null) {
            return null;
        }

        //? if <=1.20.1 {
        /*BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }
        return blockEntity.getCapability(net.neoforged.neoforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, side)
                .resolve()
                .orElse(null);
        *///?} else {
        return level.getCapability(
                Capabilities.Item.BLOCK,
                pos,
                side
        );
        //?}
    }

    public static void withBlockItemHandler(BlockEntity blockEntity, @Nullable Direction side, Consumer<ResourceHandler<ItemResource>> consumer) {
        ResourceHandler<ItemResource> handler = getBlockItemHandler(blockEntity, side);
        if (handler != null) {
            consumer.accept(handler);
        }
    }

    public static void withBlockItemHandler(Level level, BlockPos pos, @Nullable Direction side, Consumer<ResourceHandler<ItemResource>> consumer) {
        ResourceHandler<ItemResource> handler = getBlockItemHandler(level, pos, side);
        if (handler != null) {
            consumer.accept(handler);
        }
    }

    public static <R> @Nullable R mapBlockItemHandler(BlockEntity blockEntity, @Nullable Direction side, Function<ResourceHandler<ItemResource>, R> mapper) {
        ResourceHandler<ItemResource> handler = getBlockItemHandler(blockEntity, side);
        return handler == null ? null : mapper.apply(handler);
    }

    public static <R> @Nullable R mapBlockItemHandler(Level level, BlockPos pos, @Nullable Direction side, Function<ResourceHandler<ItemResource>, R> mapper) {
        ResourceHandler<ItemResource> handler = getBlockItemHandler(level, pos, side);
        return handler == null ? null : mapper.apply(handler);
    }

    /*
     * ----------------------------
     * Energy capability helpers
     * ----------------------------
     */

    public static @Nullable EnergyHandler getBlockEnergyStorage(BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }

        //? if <=1.20.1 {
        /*return blockEntity.getCapability(net.neoforged.neoforge.common.capabilities.ForgeCapabilities.ENERGY, side)
                .resolve()
                .orElse(null);
        *///?} else {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }

        return level.getCapability(
                Capabilities.Energy.BLOCK,
                blockEntity.getBlockPos(),
                blockEntity.getBlockState(),
                blockEntity,
                side
        );
        //?}
    }

    public static @Nullable EnergyHandler getItemEnergyStorage(ItemStack itemStack) {
        return itemStack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(itemStack));

    }

    public static @Nullable EnergyHandler getBlockEnergyStorage(Level level, BlockPos pos, @Nullable Direction side) {
        if (level == null || pos == null) {
            return null;
        }

        return level.getCapability(
                Capabilities.Energy.BLOCK,
                pos,
                side
        );

    }

    public static void withBlockEnergyStorage(BlockEntity blockEntity, @Nullable Direction side, Consumer<EnergyHandler> consumer) {
        EnergyHandler storage = getBlockEnergyStorage(blockEntity, side);
        if (storage != null) {
            consumer.accept(storage);
        }
    }

    public static void withItemEnergyStorage(ItemStack itemStack, Consumer<EnergyHandler> consumer) {
        EnergyHandler storage = getItemEnergyStorage(itemStack);
        if (storage != null) {
            consumer.accept(storage);
        }
    }

    public static void withBlockEnergyStorage(Level level, BlockPos pos, @Nullable Direction side, Consumer<EnergyHandler> consumer) {
        EnergyHandler storage = getBlockEnergyStorage(level, pos, side);
        if (storage != null) {
            consumer.accept(storage);
        }
    }

    public static <R> @Nullable R mapBlockEnergyStorage(BlockEntity blockEntity, @Nullable Direction side, Function<EnergyHandler, R> mapper) {
        EnergyHandler storage = getBlockEnergyStorage(blockEntity, side);
        return storage == null ? null : mapper.apply(storage);
    }

    public static <R> @Nullable R mapBlockEnergyStorage(Level level, BlockPos pos, @Nullable Direction side, Function<EnergyHandler, R> mapper) {
        EnergyHandler storage = getBlockEnergyStorage(level, pos, side);
        return storage == null ? null : mapper.apply(storage);
    }
}
