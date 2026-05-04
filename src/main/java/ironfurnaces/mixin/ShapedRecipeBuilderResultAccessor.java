//? 1.20.1 {
/*package ironfurnaces.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ShapedRecipeBuilder.Result.class)
@MixinEnvironment
public interface ShapedRecipeBuilderResultAccessor {

    @Accessor("id")
    Identifier getId();

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
    Identifier getAdvancementId();

    @Accessor("showNotification")
    boolean isShowNotification();
}
*///? }