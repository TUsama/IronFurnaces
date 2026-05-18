package ironfurnaces.tileentity.furnaces.handler;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import ironfurnaces.loaders.IronFurnaces;
import it.unimi.dsi.fastutil.objects.*;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
//? >1.20.1 {
import net.minecraft.world.item.crafting.RecipeHolder;
//?}

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责记录“recipeId -> 成功产出次数”，并提供 NBT 持久化。
 * <p>
 * 记录策略：
 * - 若存在经验流体(tag 非空)：不设上限（无限累加次数）
 * - 若不存在经验流体(tag 为空)：使用旧版上限逻辑限制次数增长
 */
public final class RecipeAwardHandler {


    private static final String NBT_KEY_RECIPES_USED = "RecipesUsed";

    @Getter
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap();

    public static int computeTotalXpToReachLevel(int level) {
        int xpNeeded = 0;

        // 0–14: 每级 + (7 + 2*(lvl-1))
        int n1 = Math.min(level, 15);
        if (n1 > 0) {
            xpNeeded += n1 * (7 + (7 + (n1 - 1) * 2)) / 2;
        }

        // 15–29: 每级 + (37 + 5*(lvl-1))
        int n2 = Math.max(0, Math.min(level, 30) - 15);
        if (n2 > 0) {
            xpNeeded += n2 * (37 + (37 + (n2 - 1) * 5)) / 2;
        }

        // 30+: 每级 + (112 + 9*(lvl-1))
        int n3 = Math.max(0, level - 30);
        if (n3 > 0) {
            xpNeeded += n3 * (112 + (112 + (n3 - 1) * 9)) / 2;
        }

        return xpNeeded;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 worldPosition, int craftedAmount, float experience) {
        int i = Mth.floor((float) craftedAmount * experience);
        float f = Mth.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }
        ExperienceOrb.award(level, worldPosition, i);

    }

    public void clear() {
        recipesUsed.clear();
    }


    public void record(@Nullable RecipeHolder<?> recipe, int maxXpLevelConfig) {
        if (recipe == null) return;
        if (!(recipe.value() instanceof AbstractCookingRecipe cookingRecipe)) {
            return;
        }
        var id = recipe.id();

        float xpPerRecipe = cookingRecipe.experience();
        int xpCap = computeTotalXpToReachLevel(maxXpLevelConfig) + 1;

        int current = recipesUsed.getInt(id);
        double nextXp = (double) (current + 1) * (double) xpPerRecipe;
        if (nextXp <= (double) xpCap) {
            recipesUsed.addTo(id, 1);
        }
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer player, List<ItemStack> outputs) {
        List<RecipeHolder<?>> recipesToAward = this.getRecipesToAwardAndPopExperience(player.level(), player.position());
        player.awardRecipes(recipesToAward);

        for(RecipeHolder<?> recipe : recipesToAward) {
            player.triggerRecipeCrafted(recipe, outputs);
        }

        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 position) {
        List<RecipeHolder<?>> recipesToAward = Lists.newArrayList();

        for (Reference2IntMap.Entry<ResourceKey<Recipe<?>>> resourceKeyEntry : this.recipesUsed.reference2IntEntrySet()) {
            level.recipeAccess().byKey(resourceKeyEntry.getKey()).ifPresent((recipe) -> {
                recipesToAward.add(recipe);
                createExperience(level, position, resourceKeyEntry.getIntValue(), ((AbstractCookingRecipe) recipe.value()).experience());
            });
        }

        return recipesToAward;
    }

    private static void createExperience(ServerLevel level, Vec3 position, int amount, float value) {
        int xpReward = Mth.floor((float)amount * value);
        float xpFraction = Mth.frac((float)amount * value);
        if (xpFraction != 0.0F && level.getRandom().nextFloat() < xpFraction) {
            ++xpReward;
        }

        ExperienceOrb.award(level, position, xpReward);
    }

    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC =
            Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    public void load(ValueInput input) {
        recipesUsed.clear();
        this.recipesUsed.putAll(input.read(NBT_KEY_RECIPES_USED, RECIPES_USED_CODEC).orElse(Map.of()));
    }

    public void save(ValueOutput output) {
        if (recipesUsed.isEmpty()) {
            return;
        }

        output.store(NBT_KEY_RECIPES_USED, RECIPES_USED_CODEC, this.recipesUsed);

    }
}