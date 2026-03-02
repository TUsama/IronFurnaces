package ironfurnaces.mixin;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeBuilder.Result.class)
public interface ShapedRecipeBuilderResultAccessor {

    @Accessor("id")
    ResourceLocation getId();

    @Accessor("result")
    Item getResult();

    @Accessor("count")
    int getCount();

    @Accessor("group")
    String getGroup();

    @Accessor("pattern")
    List<String> getPattern();

    @Accessor("key")
    Map<Character, Ingredient> getKey();

    @Accessor("advancement")
    Advancement.Builder getAdvancement();

    @Accessor("advancementId")
    ResourceLocation getAdvancementId();

    @Accessor("showNotification")
    boolean isShowNotification();
}
