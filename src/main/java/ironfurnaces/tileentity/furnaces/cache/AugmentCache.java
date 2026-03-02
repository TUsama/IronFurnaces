package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.items.augments.ItemAugmentBlue;
import ironfurnaces.items.augments.ItemAugmentGreen;
import ironfurnaces.items.augments.ItemAugmentRed;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

public class AugmentCache extends CombinedInvWrapper implements INBTSerializable<CompoundTag> {
    private final Consumer<FurnaceMode> updateFurnaceModeCallback;
    @Getter
    private RecipeType<?> currentRecipeType;
    private GreenAugmentModifier greenAugmentModifier;
    @Getter
    private int currentType;

    public AugmentCache(Consumer<FurnaceMode> updateFurnaceModeCallback) {
        super(new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemAugmentRed;
            }
        }, new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemAugmentGreen;
            }
        }, new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemAugmentBlue;
            }
        });
        this.updateFurnaceModeCallback = updateFurnaceModeCallback;
        this.greenAugmentModifier = GreenAugmentModifier.NONE;
        refreshState();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack itemStack = super.insertItem(slot, stack, simulate);
        if (ItemStack.matches(stack, itemStack)) refreshState();
        return itemStack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack itemStack = super.extractItem(slot, amount, simulate);
        if (!itemStack.isEmpty()) refreshState();
        return itemStack;
    }

    public void refreshState() {


        ItemStack buff = this.itemHandler[1].getStackInSlot(0);
        if (buff.isEmpty()) {
            this.greenAugmentModifier = GreenAugmentModifier.NONE;
        } else if (buff.getItem() instanceof ItemAugmentGreen green) {
            this.greenAugmentModifier = green.getModifier();
        }

        ItemStack mode = this.itemHandler[2].getStackInSlot(0);
        if (mode.isEmpty()) {
            this.updateFurnaceModeCallback.accept(FurnaceMode.FURNACE);
        } else if (mode.getItem() instanceof ItemAugmentBlue blue) {
            FurnaceMode mode1 = blue.getMode();
            this.updateFurnaceModeCallback.accept(mode1);
        }

        var item = this.itemHandler[0].getStackInSlot(0);
        if (item.isEmpty()) {
            this.currentRecipeType = RecipeType.SMELTING;
            this.currentType = 0;
        } else if (item.getItem() instanceof ItemAugmentRed red) {
            this.currentRecipeType = red.getRecipeType();
            this.currentType = red.getType();
        }

    }

    public GreenAugmentModifier.Modifiers getCurrentModifiers() {
        return this.greenAugmentModifier.modifiers;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();

        for (IItemHandler handler : this.itemHandler) {
            if (handler instanceof ItemStackHandler stackHandler) {
                list.add(stackHandler.serializeNBT());
            }
        }

        root.put("Handlers", list);
        return root;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (!nbt.contains("Handlers", Tag.TAG_LIST)) return;

        ListTag list = nbt.getList("Handlers", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size() && i < this.itemHandler.length; i++) {
            if (this.itemHandler[i] instanceof ItemStackHandler handler) {
                handler.deserializeNBT(list.getCompound(i));
            }
        }
    }


    public enum GreenAugmentModifier {
        SPEED(new Modifiers(
                x -> x / 2,
                totalBurnTime -> totalBurnTime / 2,
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                x -> 2 * x,
                x -> 2 * x
        )),
        FUEL_EFFICIENCY(new Modifiers(
                x -> (int) (x * 1.25f),
                totalBurnTime -> totalBurnTime * 2,
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                x -> (int) (x * 0.75f),
                x -> x / 2
        )),
        NONE(new Modifiers(
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity()
        ));

        public final Modifiers modifiers;

        GreenAugmentModifier(Modifiers modifiers) {
            this.modifiers = modifiers;
        }

        public record Modifiers(
                IntUnaryOperator normalWorkTimeModifier,
                IntUnaryOperator normalBurnTimeModifier,
                IntUnaryOperator energyWorkTimeModifier,
                IntUnaryOperator energyBurnTimeModifier,
                IntUnaryOperator generateOutputModifier,
                IntUnaryOperator energyWorkCostModifier
        ) {
        }
    }
}
