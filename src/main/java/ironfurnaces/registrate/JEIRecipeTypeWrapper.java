package ironfurnaces.registrate;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import lombok.experimental.UtilityClass;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class JEIRecipeTypeWrapper<T extends Recipe<?>> {
    @Nullable
    private NonNullSupplier<RecipeType<T>> factory = null;
    private RecipeType<T> recipeType;

    private JEIRecipeTypeWrapper(String key, String path, @NotNull Class<T> tagetClass) {
        this.factory = () -> RecipeType.create(key, path, tagetClass);
    }

    public static <T extends Recipe<?>> JEIRecipeTypeWrapper<T> create(String key, String path, Class<T> tagetClass){
        if (tagetClass != null){
            JEIRecipeTypeWrapper<T> tjeiRecipeTypeWrapper = new JEIRecipeTypeWrapper<>(key, path, tagetClass);
            return tjeiRecipeTypeWrapper;
        }
        return null;
    }

    public void register(){
        if (this.factory != null){
            this.recipeType = factory.get();
        }
    }

    public RecipeType<T> get(){
        return this.recipeType;
    }
}
