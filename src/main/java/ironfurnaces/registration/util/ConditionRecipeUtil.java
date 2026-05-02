package ironfurnaces.registration.util;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.conditions.ModLoadedCondition;
import net.minecraftforge.common.conditions.NotCondition;
import net.minecraftforge.common.conditions.TagEmptyCondition;
import net.minecraftforge.common.crafting.ConditionalRecipeOutput;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@UtilityClass
public class ConditionRecipeUtil {
    @SafeVarargs
    public static void whenHasTags(Consumer<RecipeOutput> consumer, DataGenContext<?, ?> ctx, RegistrumRecipeProvider provider, String path, String id, TagKey<Item>... tags) {
        //? 1.20.1 {
        ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
        for (TagKey<Item> itemTagKey : tags) {
            builder.addCondition(new NotCondition(new TagEmptyCondition(itemTagKey.location())));
        }
        if (path.isBlank()){
            consumer.accept(x -> builder.addRecipe(x).build(provider, IronFurnaces.id(id)));
        } else {
            consumer.accept(x -> builder.addRecipe(x).build(provider, IronFurnaces.id(path + "/" + id)));
        }
        //? } else {
        /*//~ if > 1.21.11 '(x.location())' -> '(x)'
        consumer.accept(new ConditionalRecipeOutput(provider, Arrays.stream(tags).map(x -> new NotCondition(new TagEmptyCondition(x.location()))).toArray(ICondition[]::new)));
        *///?}

    }

    public static void  whenHasMod(Consumer<RecipeOutput> consumer, DataGenContext<?, ?> ctx, String path, String id, String modId, RegistrumRecipeProvider provider) {
        //? 1.20.1 {
        consumer.accept(x -> ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(modId))
                .addRecipe(x).build(provider, IronFurnaces.id(path + "/" + id)));
        //? } else {
        /*consumer.accept(new ConditionalRecipeOutput(provider, Stream.of(new ModLoadedCondition(modId)).toArray(ICondition[]::new)));
         *///?}
    }

    public static void  whenAllthemodium(Consumer<RecipeOutput> consumer, DataGenContext<?, ?> ctx, String path, String id, RegistrumRecipeProvider provider) {
        whenHasMod(consumer, ctx, path, id, "allthemodium", provider);
    }
}
