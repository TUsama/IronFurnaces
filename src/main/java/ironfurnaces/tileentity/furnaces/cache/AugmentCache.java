//~ replace_serialization
package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.items.augments.ItemAugmentBlue;
import ironfurnaces.items.augments.ItemAugmentGreen;
import ironfurnaces.items.augments.ItemAugmentRed;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.BlastRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.GeneratorBlastRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.cache.recipe_type_handlers.SmeltRecipeTypeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.AbstractFurnaceModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.compat.CompatModeHandler;
import ironfurnaces.tileentity.furnaces.pattern.mode.internal.VanillaFurnaceModeHandler;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
//? 1.20.1 {

//? } else {
import net.minecraft.core.HolderLookup;
//?}

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;

public class AugmentCache extends CombinedInvWrapper implements INBTSerializable<CompoundTag> {
    @Getter
    @Setter
    private IRecipeTypeHandler currentRecipeType;
    private GreenAugmentModifier greenAugmentModifier;
    BiConsumer<AbstractFurnaceModeHandler, IRecipeTypeHandler> stateChangedCallback;
    public AugmentCache(BiConsumer<AbstractFurnaceModeHandler, IRecipeTypeHandler> stateChangedCallback) {
        super(new AugmentCacheHandler(),
                new AugmentCacheHandler(),
                new AugmentCacheHandler());
        this.greenAugmentModifier = GreenAugmentModifier.NONE;
        this.currentRecipeType = SmeltRecipeTypeHandler.INSTANCE;
        this.stateChangedCallback = stateChangedCallback;
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


    public void updateAllStacks(List<ItemStack> stacks){
        if (stacks.size() != 3) {
            IronFurnaces.LOGGER.error("Found a Itemstack list with size {} want to sync to Augment cache!", stacks.size());
            return;
        }
        for (int i = 0; i < this.getSlots(); i++) {
            this.setStackInSlot(i, stacks.get(i));
        }
    }

    public void displayMessages(){

    }

    public void refreshState() {
        ItemStack buff = this.itemHandler[1].getStackInSlot(0);
        if (buff.isEmpty()) {
            this.greenAugmentModifier = GreenAugmentModifier.NONE;
        } else if (buff.getItem() instanceof ItemAugmentGreen green) {
            this.greenAugmentModifier = green.getModifier();
        }

        ItemStack modeItem = this.itemHandler[2].getStackInSlot(0);
        AbstractFurnaceModeHandler mode = null;
        if (modeItem.isEmpty()) {
            mode = VanillaFurnaceModeHandler.INSTANCE;
        } else if (modeItem.getItem() instanceof ItemAugmentBlue blue) {
            mode = blue.getModeHandler();
        }

        var item = this.itemHandler[0].getStackInSlot(0);
        if (!mode.isInternal()) {
            IRecipeTypeHandler attachRecipeTypeHandler = ((CompatModeHandler) mode).getAttachRecipeTypeHandler();
            if (!attachRecipeTypeHandler.getRecipeType().equals(this.currentRecipeType.getRecipeType())){
                this.currentRecipeType = attachRecipeTypeHandler;
            }

        } else if (item.isEmpty()) {
            this.currentRecipeType = SmeltRecipeTypeHandler.INSTANCE;
        } else if (item.getItem() instanceof ItemAugmentRed red) {
            this.currentRecipeType = red.getRecipeTypeHandler();
            if (this.currentRecipeType.equals(BlastRecipeTypeHandler.INSTANCE) && mode.isGenerator()){
                this.currentRecipeType = GeneratorBlastRecipeTypeHandler.INSTANCE;
            }
        }
        stateChangedCallback.accept(mode, this.currentRecipeType);
    }

    public GreenAugmentModifier.Modifiers getCurrentModifiers() {
        return this.greenAugmentModifier.modifiers;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();

        for (IItemHandler handler : this.itemHandler) {
            if (handler instanceof ItemStackHandler stackHandler) {
                list.add(stackHandler.serializeNBT(registries));
            }
        }

        root.put("Handlers", list);
        root.put("RecipeTypeHandlerExtraData", this.getCurrentRecipeType().serializeNBT(registries));
        return root;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
        if (nbt.contains("Handlers", Tag.TAG_LIST)){
            ListTag list = nbt.getList("Handlers", Tag.TAG_COMPOUND);

            for (int i = 0; i < list.size() && i < this.itemHandler.length; i++) {
                if (this.itemHandler[i] instanceof ItemStackHandler handler) {
                    //~ if >1.20.1 'list.getCompound(i)' -> 'registries, list.getCompound(i)'
                    handler.deserializeNBT(registries, list.getCompound(i));
                }
            }
        }

        if (nbt.contains("RecipeTypeHandlerExtraData", Tag.TAG_COMPOUND)){
            this.currentRecipeType.deserializeNBT(registries, nbt);
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

}
