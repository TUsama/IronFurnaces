
package ironfurnaces.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
//? 1.20.1 {

//? } else {
/*import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.advancements.Criterion;
*///?}
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeBuilder.class)
@MixinEnvironment
public interface ShapedRecipeBuilderMixin {

    @Accessor("category")
    RecipeCategory getCategory();

    @Accessor("result")
    Item getResult();
    //? > 1.20.1 {
    /*@Accessor("resultStack")
    ItemStack getResultStack();
    *///?}

    @Accessor("count")
    int getCount();

    @Accessor("rows")
    List<String> getRows();

    @Accessor("key")
    Map<Character, Ingredient> getKey();

    //? 1.20.1 {
    @Accessor("advancement")
    Advancement.Builder getAdvancement();
    //? } else {
    /*@Accessor("criteria")
    Map<String, Criterion<?>> getCriteria();
    *///?}


    @Accessor("group")
    @Nullable
    String getGroup();

    @Accessor("showNotification")
    boolean isShowNotification();
    //? 1.20.1 {
        
    @Invoker("ensureValid")
    void callEnsureValid(ResourceLocation id);
    //? } else {
    /*@Invoker("ensureValid")
    ShapedRecipePattern callEnsureValid(ResourceLocation id);
    *///?}


}
