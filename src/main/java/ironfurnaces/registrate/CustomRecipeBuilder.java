package ironfurnaces.registrate;

import com.clefal.nirvana_lib.utils.ModUtils;
import dev.anvilcraft.lib.v2.registrum.AbstractRegistrum;
import dev.anvilcraft.lib.v2.registrum.builders.AbstractBuilder;
import dev.anvilcraft.lib.v2.registrum.builders.BuilderCallback;
import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.ProviderType;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import dev.anvilcraft.lib.v2.registrum.util.entry.RegistryEntry;
import dev.anvilcraft.lib.v2.registrum.util.nullness.NonNullBiConsumer;
import dev.anvilcraft.lib.v2.registrum.util.nullness.NonNullSupplier;
import dev.anvilcraft.lib.v2.registrum.util.nullness.NonnullType;
import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

//? 1.20.1 {
/*import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;
*///? } else {
import net.neoforged.neoforge.registries.DeferredHolder;
//?}


public class CustomRecipeBuilder<T extends Recipe<?>, P> extends AbstractBuilder<RecipeType<?>, RecipeType<?>, P, CustomRecipeBuilder<T, P>> {

    private RecipeSerializerBuilder recipeSerializerBuilder;
    private Class<T> jeiRecipeTypeClass;

    public CustomRecipeBuilder(AbstractRegistrum<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<RecipeSerializer<T>> serializerFactory) {
        //~ if >1.20.1 'ForgeRegistries.Keys.RECIPE_TYPES' -> 'BuiltInRegistries.RECIPE_TYPE.key()'
        super(owner, parent, name, callback, BuiltInRegistries.RECIPE_TYPE.key());
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
    protected RegistryEntry<RecipeType<?>, RecipeType<?>> createEntryWrapper(DeferredHolder<RecipeType<?>, RecipeType<?>> delegate) {
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

        public RecipeSerializerBuilder(AbstractRegistrum<?> owner, P parent, String name, BuilderCallback callback, NonNullSupplier<RecipeSerializer<T>> serializerFactory) {
            //~ if >1.20.1 'ForgeRegistries.Keys.RECIPE_SERIALIZERS' -> 'BuiltInRegistries.RECIPE_SERIALIZER.key()'
            super(owner, parent, name, callback, BuiltInRegistries.RECIPE_SERIALIZER.key());
            this.serializerFactory = serializerFactory;
        }

        @Override
                //~ if >1.20.1 'RegistryEntry<RecipeSerializer<?>> createEntryWrapper(RegistryObject<RecipeSerializer<?>> delegate)' -> 'RegistryEntry<RecipeSerializer<?>, RecipeSerializer<?>> createEntryWrapper(DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> delegate)'
        protected RegistryEntry<RecipeSerializer<?>, RecipeSerializer<?>> createEntryWrapper(DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> delegate) {
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
