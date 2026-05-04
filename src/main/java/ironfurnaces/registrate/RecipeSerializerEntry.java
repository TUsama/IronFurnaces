package ironfurnaces.registrate;

import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

//? 1.20.1 {
/*import net.neoforged.neoforge.registries.RegistryObject;
*///? } else {
import net.neoforged.neoforge.registries.DeferredHolder;
//?}

//~ if >1.20.1 'RegistryEntry<RecipeSerializer<?>>' -> 'RegistryEntry<RecipeSerializer<?>, RecipeSerializer<?>>'
public class RecipeSerializerEntry extends RegistryEntry<RecipeSerializer<?>, RecipeSerializer<?>> {
    //~ if >1.20.1 'RegistryObject<RecipeSerializer<?>> delegate' -> 'DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> delegate'
    public RecipeSerializerEntry(AbstractRegistrum<?> owner, DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> delegate) {
        super(owner, delegate);
    }
}
