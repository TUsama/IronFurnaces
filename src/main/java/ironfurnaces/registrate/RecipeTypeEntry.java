package ironfurnaces.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;


public class RecipeTypeEntry<T extends Recipe<?>> extends RegistryEntry<RecipeType<?>> {
    private final RecipeSerializerEntry serializer;

    public RecipeTypeEntry(AbstractRegistrate<?> owner, RegistryObject<RecipeType<?>> delegate, RecipeSerializerEntry serializer) {
        super(owner, delegate);
        this.serializer = serializer;
    }


    public RecipeSerializer<T> asSerializer() {

        return (RecipeSerializer<T>) serializer.get();
    }


    @Override
    public @NonnullType RecipeType<T> get() {
        return (RecipeType<T>) super.get();
    }
}
