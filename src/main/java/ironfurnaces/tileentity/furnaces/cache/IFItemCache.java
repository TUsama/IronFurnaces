package ironfurnaces.tileentity.furnaces.cache;

import lombok.With;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class IFItemCache extends ItemStackHandler {
    protected final Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback;

    public IFItemCache(Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback) {
        this.grabRecipeCallback = grabRecipeCallback;
    }

    public IFItemCache(int size, Function<ItemStack, Optional<? extends Recipe>> grabRecipeCallback) {
        super(size);
        this.grabRecipeCallback = grabRecipeCallback;
    }

}
