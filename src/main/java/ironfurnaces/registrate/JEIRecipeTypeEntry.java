package ironfurnaces.registrate;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import ironfurnaces.loaders.IronFurnaces;
import mezz.jei.api.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class JEIRecipeTypeEntry<T> {
    @Nullable
    private NonNullSupplier<RecipeType<T>> factory;
    private RecipeType<T> recipeType;

    private JEIRecipeTypeEntry(String key, String path, @NotNull Class<T> tagetClass) {
        this.factory = () -> RecipeType.create(key, path, tagetClass);
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

    public RecipeType<T> get() {
        return this.recipeType;
    }
}
