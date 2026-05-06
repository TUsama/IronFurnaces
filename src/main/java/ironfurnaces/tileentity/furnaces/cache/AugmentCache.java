package ironfurnaces.tileentity.furnaces.cache;

import ironfurnaces.items.augments.ItemAugmentBlue;
import ironfurnaces.items.augments.ItemAugmentGreen;
import ironfurnaces.items.augments.ItemAugmentRed;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;

public class AugmentCache extends CombinedResourceHandler<ItemResource> implements ValueIOSerializable {
    BiConsumer<AbstractFurnaceModeHandler, IRecipeTypeHandler> stateChangedCallback;
    @Getter
    @Setter
    private IRecipeTypeHandler currentRecipeType;
    private GreenAugmentModifier greenAugmentModifier;

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
                .validator((i, stack) -> stack.getItem() instanceof ItemAugmentRed);

        ((AugmentCacheHandler) getHandlerFromIndex(1))
                .onChange(i -> {
                    refreshState();
                })
                .validator((i, stack) -> stack.getItem() instanceof ItemAugmentGreen);

        ((AugmentCacheHandler) getHandlerFromIndex(2))
                .onChange(i -> {
                    refreshState();
                })
                .validator((i, stack) -> stack.getItem() instanceof ItemAugmentBlue);
    }


    public void refreshState() {
        ItemStack buff = ItemUtil.getStack(getHandlerFromIndex(1), 0);
        if (buff.isEmpty()) {
            this.greenAugmentModifier = GreenAugmentModifier.NONE;
        } else if (buff.getItem() instanceof ItemAugmentGreen green) {
            this.greenAugmentModifier = green.getModifier();
        }

        ItemStack modeItem = ItemUtil.getStack(getHandlerFromIndex(2), 0);
        AbstractFurnaceModeHandler mode = null;
        if (modeItem.isEmpty()) {
            mode = VanillaFurnaceModeHandler.INSTANCE;
        } else if (modeItem.getItem() instanceof ItemAugmentBlue blue) {
            mode = blue.getModeHandler();
        }

        var item = ItemUtil.getStack(getHandlerFromIndex(0), 0);
        if (!mode.isInternal()) {
            IRecipeTypeHandler attachRecipeTypeHandler = ((CompatModeHandler) mode).getAttachRecipeTypeHandler();
            if (!attachRecipeTypeHandler.getRecipeType().equals(this.currentRecipeType.getRecipeType())) {
                this.currentRecipeType = attachRecipeTypeHandler;
            }

        } else if (item.isEmpty()) {
            this.currentRecipeType = SmeltRecipeTypeHandler.INSTANCE;
        } else if (item.getItem() instanceof ItemAugmentRed red) {
            this.currentRecipeType = red.getRecipeTypeHandler();
            if (this.currentRecipeType.equals(BlastRecipeTypeHandler.INSTANCE) && mode.isGenerator()) {
                this.currentRecipeType = GeneratorBlastRecipeTypeHandler.INSTANCE;
            }
        }
        stateChangedCallback.accept(mode, this.currentRecipeType);
    }

    public GreenAugmentModifier.Modifiers getCurrentModifiers() {
        return this.greenAugmentModifier.modifiers;
    }

    @Override
    public void serialize(ValueOutput output) {
        ((AugmentCacheHandler) getHandlerFromIndex(0)).serialize(output);
        ((AugmentCacheHandler) getHandlerFromIndex(1)).serialize(output);
        ((AugmentCacheHandler) getHandlerFromIndex(2)).serialize(output);
        this.getCurrentRecipeType().serialize(output);
    }

    @Override
    public void deserialize(ValueInput input) {
        ((AugmentCacheHandler) getHandlerFromIndex(0)).deserialize(input);
        ((AugmentCacheHandler) getHandlerFromIndex(1)).deserialize(input);
        ((AugmentCacheHandler) getHandlerFromIndex(2)).deserialize(input);
        this.getCurrentRecipeType().deserialize(input);
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
