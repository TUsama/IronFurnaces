package ironfurnaces.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeTypeBuilder<T extends Recipe<?>, P> extends AbstractBuilder<RecipeType<?>, RecipeType<?>, P, RecipeTypeBuilder<T, P>> {

    private RecipeSerializerBuilder recipeSerializerBuilder;
    private Class<T> jeiRecipeTypeClass;

    public RecipeTypeBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<RecipeSerializer<T>> serializerFactory) {
        super(owner, parent, name, callback, ForgeRegistries.Keys.RECIPE_TYPES);
        this.recipeSerializerBuilder = new RecipeSerializerBuilder(owner, parent, name, callback, serializerFactory);
    }

    public RecipeTypeBuilder<T, P> jei(Class<T> recipeClass){
        this.jeiRecipeTypeClass = recipeClass;
        return this;
    }

    @Override
    protected @NonnullType RecipeType<?> createEntry() {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return getName();
            }
        };
    }


    @Override
    protected RegistryEntry<RecipeType<?>> createEntryWrapper(RegistryObject<RecipeType<?>> delegate) {
        return new RecipeTypeEntry<>(getOwner(), delegate, recipeSerializerBuilder.register(), JEIRecipeTypeWrapper.create(IronFurnaces.MOD_ID, getName(), jeiRecipeTypeClass));
    }

    @Override
    public RecipeTypeEntry<T> register() {
        RecipeTypeEntry<T> register = (RecipeTypeEntry<T>) super.register();
        register.registerJEIRecipeType();
        return register;
    }

    public class RecipeSerializerBuilder extends AbstractBuilder<RecipeSerializer<?>, RecipeSerializer<?>, P, RecipeSerializerBuilder> {

        private NonNullSupplier<RecipeSerializer<T>> serializerFactory;

        public RecipeSerializerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<RecipeSerializer<T>> serializerFactory) {
            super(owner, parent, name, callback, ForgeRegistries.Keys.RECIPE_SERIALIZERS);
            this.serializerFactory = serializerFactory;
        }

        @Override
        protected RegistryEntry<RecipeSerializer<?>> createEntryWrapper(RegistryObject<RecipeSerializer<?>> delegate) {
            return new RecipeSerializerEntry(getOwner(), delegate);
        }

        @Override
        protected @NonnullType RecipeSerializer<?> createEntry() {
            return serializerFactory.get();
        }

        @Override
        public RecipeSerializerEntry register() {
            return ((RecipeSerializerEntry) super.register());
        }
    }
}
