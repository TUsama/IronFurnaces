package ironfurnaces.registration.util;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumRecipeProvider;
import lombok.experimental.UtilityClass;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.ConditionalRecipeOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.common.conditions.ICondition;


import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

@UtilityClass
public class ConditionRecipeUtil {

    @SafeVarargs
    public static void whenHasTags(Consumer<RecipeOutput> consumer, DataGenContext<?, ?> ctx, RegistrumRecipeProvider provider, String path, String id, TagKey<Item>... tags) {
        consumer.accept(new ConditionalRecipeOutput(provider, Arrays.stream(tags).map(x -> new NotCondition(new TagEmptyCondition(x))).toArray(ICondition[]::new)));
    }

    public static void  whenHasMod(Consumer<RecipeOutput> consumer, DataGenContext<?, ?> ctx, String path, String id, String modId, RegistrumRecipeProvider provider) {
        consumer.accept(new ConditionalRecipeOutput(provider, Stream.of(new ModLoadedCondition(modId)).toArray(ICondition[]::new)));
    }

    public static void  whenAllthemodium(Consumer<RecipeOutput> consumer, DataGenContext<?, ?> ctx, String path, String id, RegistrumRecipeProvider provider) {
        whenHasMod(consumer, ctx, path, id, "allthemodium", provider);
    }
}
