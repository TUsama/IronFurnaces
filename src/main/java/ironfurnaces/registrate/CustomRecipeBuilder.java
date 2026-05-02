package ironfurnaces.registrate;

import com.clefal.nirvana_lib.utils.ModUtils;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

//? 1.20.1 {
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
//? } else {
/*import net.minecraftforge.registries.DeferredHolder;
*///?}


public class CustomRecipeBuilder<T extends Recipe<?>, P> extends AbstractBuilder<RecipeType<?>, RecipeType<?>, P, CustomRecipeBuilder<T, P>> {

    private RecipeSerializerBuilder recipeSerializerBuilder;
    private Class<T> jeiRecipeTypeClass;

    public CustomRecipeBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<RecipeSerializer<T>> serializerFactory) {
        //~ if >1.20.1 'ForgeRegistries.Keys.RECIPE_TYPES' -> 'BuiltInRegistries.RECIPE_TYPE.key()'
        super(owner, parent, name, callback, ForgeRegistries.Keys.RECIPE_TYPES);
        this.recipeSerializerBuilder = new RecipeSerializerBuilder(owner, parent, name, callback, serializerFactory);
    }

    public CustomRecipeBuilder<T, P> jei(Class<T> recipeClass) {
        if (ModUtils.isModLoaded("jei")){
            this.jeiRecipeTypeClass = recipeClass;
        }
        return this;
    }

    public CustomRecipeBuilder<T, P> recipe(NonNullBiConsumer<DataGenContext<RecipeType<?>, RecipeType<?>>, RegistrumRecipeProvider> cons) {
        this.<RegistrumRecipeProvider>setData(ProviderType.RECIPE, cons);
        return this;
    }

    @Override
    protected @NonnullType RecipeType<T> createEntry() {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return getName();
            }
        };
    }


    @Override
            //~ if >1.20.1 'RegistryEntry<RecipeType<?>> createEntryWrapper(RegistryObject<RecipeType<?>> delegate)' -> 'RegistryEntry<RecipeType<?>, RecipeType<?>> createEntryWrapper(DeferredHolder<RecipeType<?>, RecipeType<?>> delegate)'
    protected RegistryEntry<RecipeType<?>> createEntryWrapper(RegistryObject<RecipeType<?>> delegate) {
        return new CustomRecipeEntry<>(getOwner(), delegate, recipeSerializerBuilder.register(), JEIRecipeTypeEntry.create(IronFurnaces.MOD_ID, getName(), jeiRecipeTypeClass));
    }

    @Override
    public CustomRecipeEntry<T> register() {
        CustomRecipeEntry<T> register = (CustomRecipeEntry<T>) super.register();
        register.registerJEIRecipeType();
        return register;
    }

    public class RecipeSerializerBuilder extends AbstractBuilder<RecipeSerializer<?>, RecipeSerializer<?>, P, RecipeSerializerBuilder> {

        private NonNullSupplier<RecipeSerializer<T>> serializerFactory;

        public RecipeSerializerBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<RecipeSerializer<T>> serializerFactory) {
            //~ if >1.20.1 'ForgeRegistries.Keys.RECIPE_SERIALIZERS' -> 'BuiltInRegistries.RECIPE_SERIALIZER.key()'
            super(owner, parent, name, callback, ForgeRegistries.Keys.RECIPE_SERIALIZERS);
            this.serializerFactory = serializerFactory;
        }

        @Override
                //~ if >1.20.1 'RegistryEntry<RecipeSerializer<?>> createEntryWrapper(RegistryObject<RecipeSerializer<?>> delegate)' -> 'RegistryEntry<RecipeSerializer<?>, RecipeSerializer<?>> createEntryWrapper(DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> delegate)'
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
