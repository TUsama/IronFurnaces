package ironfurnaces.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

//? 1.20.1 {
import net.minecraftforge.registries.RegistryObject;
//? } else {
/*import net.minecraftforge.registries.DeferredHolder;
*///?}

//~ if >1.20.1 'RegistryEntry<RecipeSerializer<?>>' -> 'RegistryEntry<RecipeSerializer<?>, RecipeSerializer<?>>'
public class RecipeSerializerEntry extends RegistryEntry<RecipeSerializer<?>> {
    //~ if >1.20.1 'RegistryObject<RecipeSerializer<?>> delegate' -> 'DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> delegate'
    public RecipeSerializerEntry(AbstractRegistrate<?> owner, RegistryObject<RecipeSerializer<?>> delegate) {
        super(owner, delegate);
    }
}
