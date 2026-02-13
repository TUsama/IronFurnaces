package ironfurnaces.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

public class RecipeSerializerEntry extends RegistryEntry<RecipeSerializer<?>> {
    public RecipeSerializerEntry(AbstractRegistrate<?> owner, RegistryObject<RecipeSerializer<?>> delegate) {
        super(owner, delegate);
    }
}
