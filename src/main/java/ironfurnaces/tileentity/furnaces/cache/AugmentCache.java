package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.items.augments.ItemAugmentBlue;
import ironfurnaces.items.augments.ItemAugmentGreen;
import ironfurnaces.items.augments.ItemAugmentRed;
import ironfurnaces.registration.ModCustomRecipe;
import ironfurnaces.tileentity.furnaces.FurnaceMode;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AugmentCache extends CombinedInvWrapper implements INBTSerializable<CompoundTag> {
    private final Consumer<FurnaceMode> updateFurnaceModeCallback;
    @Getter
    @Setter
    private HandlingRecipeType currentRecipeType;
    private GreenAugmentModifier greenAugmentModifier;
    private Runnable updateRecipeType;

    public AugmentCache(Consumer<FurnaceMode> updateFurnaceModeCallback, Runnable updateRecipeType) {
        super(new AugmentCacheHandler(),
                new AugmentCacheHandler(),
                new AugmentCacheHandler());
        this.updateFurnaceModeCallback = updateFurnaceModeCallback;
        this.greenAugmentModifier = GreenAugmentModifier.NONE;
        this.updateRecipeType = updateRecipeType;
        ((AugmentCacheHandler) getHandlerFromIndex(0))
                .onChange(i -> {
                    refreshState();
                })
                .validator((i, stack )-> stack.getItem() instanceof ItemAugmentRed);

        ((AugmentCacheHandler) getHandlerFromIndex(1))
                .onChange(i -> {
                    refreshState();
                })
                .validator((i, stack )-> stack.getItem() instanceof ItemAugmentGreen);

        ((AugmentCacheHandler) getHandlerFromIndex(2))
                .onChange(i -> {
                    refreshState();
                })
                .validator((i, stack)-> stack.getItem() instanceof ItemAugmentBlue);
    }




    public void refreshState() {
        ItemStack buff = this.itemHandler[1].getStackInSlot(0);
        if (buff.isEmpty()) {
            this.greenAugmentModifier = GreenAugmentModifier.NONE;
        } else if (buff.getItem() instanceof ItemAugmentGreen green) {
            this.greenAugmentModifier = green.getModifier();
        }

        ItemStack modeItem = this.itemHandler[2].getStackInSlot(0);
        FurnaceMode mode = null;
        if (modeItem.isEmpty()) {
            mode = FurnaceMode.FURNACE;
        } else if (modeItem.getItem() instanceof ItemAugmentBlue blue) {
            mode = blue.getMode();
        }
        this.updateFurnaceModeCallback.accept(mode);

        var item = this.itemHandler[0].getStackInSlot(0);
        if (item.isEmpty()) {
            this.currentRecipeType = HandlingRecipeType.NORMAL;
        } else if (item.getItem() instanceof ItemAugmentRed red) {
            this.currentRecipeType = red.getRecipeType();
            if (this.currentRecipeType.equals(HandlingRecipeType.BLAST) && mode.equals(FurnaceMode.GENERATOR)){
                this.currentRecipeType = HandlingRecipeType.GENERATE_BLAST;
            }
        }
        updateRecipeType.run();

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
                x -> x + 1,
                totalBurnTime -> totalBurnTime / 2,
                IntUnaryOperator.identity(),
                x -> x * 2,
                x -> 2 * x,
                x -> 2 * x
        )),
        FUEL_EFFICIENCY(new Modifiers(
                x -> x - 0.25f,
                totalBurnTime -> totalBurnTime * 2,
                IntUnaryOperator.identity(),
                x -> x / 2.0f,
                x -> x * 0.75f,
                x -> x / 2
        )),
        NONE(new Modifiers(
                FloatUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                IntUnaryOperator.identity(),
                x -> x,
                x -> x,
                IntUnaryOperator.identity()
        ));

        public final Modifiers modifiers;

        GreenAugmentModifier(Modifiers modifiers) {
            this.modifiers = modifiers;
        }

        public record Modifiers(
                FloatUnaryOperator normalWorkTimeModifier,
                IntUnaryOperator normalBurnTimeModifier,
                IntUnaryOperator energyBurnTimeModifier,
                Float2FloatFunction generateCurrentOutputModifier,
                Float2FloatFunction generatePerTickOutputModifier,
                IntUnaryOperator energyWorkCostModifier
        ) {
        }
    }

    public enum HandlingRecipeType implements StringRepresentable {
        NORMAL("normal", () -> RecipeType.SMELTING),
        SMOKE("smoke", () -> RecipeType.SMOKING),
        BLAST("blast", () -> RecipeType.BLASTING),
        GENERATE_BLAST("generate_blast", ModCustomRecipe.GENERATOR_RECIPE);
        public final String name;
        public final Supplier<RecipeType<?>> recipeType;

        HandlingRecipeType(String name, Supplier<RecipeType<?>> recipeType) {
            this.name = name;
            this.recipeType = recipeType;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private class AugmentSlotHandler extends ItemStackHandler {
        private final Predicate<ItemStack> validator;

        public AugmentSlotHandler(Predicate<ItemStack> validator) {
            super(1);
            this.validator = validator;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return validator.test(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            refreshState();
        }
    }
}
