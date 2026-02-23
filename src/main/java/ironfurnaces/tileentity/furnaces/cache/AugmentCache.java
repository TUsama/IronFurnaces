package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.items.augments.ItemAugmentBlue;
import ironfurnaces.items.augments.ItemAugmentGreen;
import ironfurnaces.items.augments.ItemAugmentRed;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

public class AugmentCache extends CombinedInvWrapper {
    private final Consumer<FurnaceMode> updateFurnaceModeCallback;
    @Getter
    private RecipeType<?> currentRecipeType;
    private GreenAugmentModifier greenAugmentModifier;

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
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack itemStack = super.insertItem(slot, stack, simulate);
        refreshState();
        return itemStack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack itemStack = super.extractItem(slot, amount, simulate);
        refreshState();
        return itemStack;
    }

    private void refreshState() {


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
        } else if (item.getItem() instanceof ItemAugmentRed red) {
            this.currentRecipeType = red.getRecipeType();
        }

    }

    public GreenAugmentModifier.Modifiers getCurrentModifiers() {
        return this.greenAugmentModifier.modifiers;
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
