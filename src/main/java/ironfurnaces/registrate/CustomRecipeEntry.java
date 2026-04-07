package ironfurnaces.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

//? 1.20.1 {

import net.minecraftforge.registries.RegistryObject;
//? } else {
/*import net.minecraftforge.registries.DeferredHolder;
*///?}
//~ if >1.20.1 'RegistryEntry<RecipeType<?>>' -> 'RegistryEntry<RecipeType<?>, RecipeType<?>>'
public class CustomRecipeEntry<T extends Recipe<?>> extends RegistryEntry<RecipeType<?>> {
    private final RecipeSerializerEntry serializer;
    private JEIRecipeTypeEntry<T> jeiRecipeType;
    //~ if >1.20.1 'RegistryObject<RecipeType<?>> delegate' -> 'DeferredHolder<RecipeType<?>, RecipeType<?>> delegate'
    public CustomRecipeEntry(AbstractRegistrate<?> owner, RegistryObject<RecipeType<?>> delegate, RecipeSerializerEntry serializer, JEIRecipeTypeEntry<T> jeiRecipeType) {
        super(owner, delegate);
        this.serializer = serializer;
        this.jeiRecipeType = jeiRecipeType;
    }

    public RecipeSerializer<T> asSerializer() {
        return (RecipeSerializer<T>) serializer.get();
    }

    public JEIRecipeTypeEntry<T> asJEIRecipeType(){
        if (jeiRecipeType != null){
            return jeiRecipeType;
        }
        throw new NullPointerException("You can't access the JEIRecipeTypeWrapper without create it!");
    }

    protected void registerJEIRecipeType(){
        if (this.jeiRecipeType != null) {
            this.jeiRecipeType.register();
        }
    }


    @Override
    public @NonnullType RecipeType<T> get() {
        return (RecipeType<T>) super.get();
    }
}
