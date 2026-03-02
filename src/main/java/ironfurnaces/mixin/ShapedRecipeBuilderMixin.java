package ironfurnaces.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeBuilder.class)
public interface ShapedRecipeBuilderMixin {

    @Accessor("category")
    RecipeCategory getCategory();

    @Accessor("result")
    Item getResult();

    @Accessor("count")
    int getCount();

    @Accessor("rows")
    List<String> getRows();

    @Accessor("key")
    Map<Character, Ingredient> getKey();

    @Accessor("advancement")
    Advancement.Builder getAdvancement();

    @Accessor("group")
    @Nullable
    String getGroup();

    @Accessor("showNotification")
    boolean isShowNotification();

    @Invoker("ensureValid")
    void callEnsureValid(ResourceLocation id);

}
