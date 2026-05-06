package ironfurnaces.registration.util;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import ironfurnaces.loaders.IronFurnaces;
import lombok.experimental.UtilityClass;
//? forge{
/*import net.minecraft.data.recipes.FinishedRecipe;
*///?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
//? >=1.21.1{
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.ConditionalRecipeOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.conditions.ICondition;
//?} else {
/*import net.neoforged.neoforge.common.crafting.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.conditions.NotCondition;
import net.neoforged.neoforge.common.crafting.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.ConditionalRecipe;

*///?}


*///?}

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@UtilityClass
public class ConditionRecipeUtil {
    //~ if >1.20.1 'Consumer<Consumer<FinishedRecipe>>' -> 'Consumer<RecipeOutput>' {
    @SafeVarargs
    public static void whenHasTags(Consumer<Consumer<FinishedRecipe>> consumer, DataGenContext<?, ?> ctx, RegistrateRecipeProvider provider, String path, String id, TagKey<Item>... tags) {
        //? 1.20.1 {
        /*ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
        for (TagKey<Item> itemTagKey : tags) {
            builder.addCondition(new NotCondition(new TagEmptyCondition(itemTagKey.location())));
        }
        if (path.isBlank()){
            consumer.accept(x -> builder.addRecipe(x).build(provider, IronFurnaces.id(id)));
        } else {
            consumer.accept(x -> builder.addRecipe(x).build(provider, IronFurnaces.id(path + "/" + id)));
        }
        *///? } else {
        //~ if > 1.21.11 '(x.location())' -> '(x)'
        consumer.accept(new ConditionalRecipeOutput(provider, Arrays.stream(tags).map(x -> new NotCondition(new TagEmptyCondition(x))).toArray(ICondition[]::new)));
        //?}

    }

    public static void  whenHasMod(Consumer<Consumer<FinishedRecipe>> consumer, DataGenContext<?, ?> ctx, String path, String id, String modId, RegistrateRecipeProvider provider) {
        //? 1.20.1 {
        /*consumer.accept(x -> ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(modId))
                .addRecipe(x).build(provider, IronFurnaces.id(path + "/" + id)));
        *///? } else {
        consumer.accept(new ConditionalRecipeOutput(provider, Stream.of(new ModLoadedCondition(modId)).toArray(ICondition[]::new)));
         //?}
    }

    public static void  whenAllthemodium(Consumer<Consumer<FinishedRecipe>> consumer, DataGenContext<?, ?> ctx, String path, String id, RegistrateRecipeProvider provider) {
        whenHasMod(consumer, ctx, path, id, "allthemodium", provider);
    }
    //~}
}
