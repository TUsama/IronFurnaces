package ironfurnaces.registrate;

import dev.anvilcraft.lib.v2.registrum.util.nullness.NonNullSupplier;
import ironfurnaces.loaders.IronFurnaces;
import mezz.jei.api.recipe.types.IRecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class JEIRecipeTypeEntry<T> {
    @Nullable
    private NonNullSupplier<IRecipeType<T>> factory;
    private IRecipeType<T> recipeType;

    private JEIRecipeTypeEntry(String key, String path, @NotNull Class<T> tagetClass) {
        this.factory = () -> IRecipeType.create(key, path, tagetClass);
    }

    public static <T> JEIRecipeTypeEntry<T> create(String key, String path, Class<T> tagetClass) {
        if (tagetClass != null) {
            JEIRecipeTypeEntry<T> tjeiRecipeTypeEntry = new JEIRecipeTypeEntry<>(key, path, tagetClass);
            return tjeiRecipeTypeEntry;
        }
        return null;
    }

    public static <T> JEIRecipeTypeEntry<T> create(String path, Class<T> tagetClass) {
        if (tagetClass != null) {
            JEIRecipeTypeEntry<T> tjeiRecipeTypeEntry = new JEIRecipeTypeEntry<>(IronFurnaces.MOD_ID, path, tagetClass);
            return tjeiRecipeTypeEntry;
        }
        return null;
    }

    public void register() {
        if (this.factory != null) {
            this.recipeType = factory.get();
        }
    }

    public IRecipeType<T> get() {
        return this.recipeType;
    }
}
