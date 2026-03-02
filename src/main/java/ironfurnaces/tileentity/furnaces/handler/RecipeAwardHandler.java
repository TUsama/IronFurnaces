package ironfurnaces.tileentity.furnaces.handler;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * 负责记录“recipeId -> 成功产出次数”，并提供 NBT 持久化。
 * <p>
 * 记录策略：
 * - 若存在经验流体(tag 非空)：不设上限（无限累加次数）
 * - 若不存在经验流体(tag 为空)：使用旧版上限逻辑限制次数增长
 */
public final class RecipeAwardHandler {

    public static final TagKey<Fluid> EXPERIENCE_FLUID_TAG =
            TagKey.create(BuiltInRegistries.FLUID.key(), new ResourceLocation("forge", "experience"));

    private static final String NBT_KEY_RECIPES_USED = "RecipesUsed";

    @Getter
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();


    public void clear() {
        recipesUsed.clear();
    }


    public void record(@Nullable Recipe<?> recipe, int maxXpLevelConfig) {
        if (!(recipe instanceof AbstractCookingRecipe cookingRecipe)) {
            return;
        }

        ResourceLocation id = cookingRecipe.getId();

        float xpPerRecipe = cookingRecipe.getExperience();
        int xpCap = computeTotalXpToReachLevel(maxXpLevelConfig) + 1;

        int current = recipesUsed.getInt(id);
        double nextXp = (double) (current + 1) * (double) xpPerRecipe;
        if (nextXp <= (double) xpCap) {
            recipesUsed.addTo(id, 1);
        }
    }


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

    public void unlockRecipes(ServerPlayer player) {
        List<Recipe<?>> list = this.grantStoredRecipeExperience(player.serverLevel(), player.position());
        player.awardRecipes(list);
        recipesUsed.clear();
    }

    public List<Recipe<?>> grantStoredRecipeExperience(ServerLevel level, Vec3 worldPosition) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((h) -> {
                list.add(h);
                splitAndSpawnExperience(level, worldPosition, entry.getIntValue(), ((AbstractCookingRecipe) h).getExperience());
            });
        }


        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 worldPosition, int craftedAmount, float experience) {
        int i = Mth.floor((float) craftedAmount * experience);
        float f = Mth.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }
        ExperienceOrb.award(level, worldPosition, i);

    }

    /* -----------------------------
     * NBT 序列化（你后面写 unlockRecipes 时会用到）
     * ----------------------------- */

    public void loadFromTag(CompoundTag tag) {
        recipesUsed.clear();
        if (!tag.contains(NBT_KEY_RECIPES_USED, CompoundTag.TAG_COMPOUND)) {
            return;
        }

        CompoundTag recipesTag = tag.getCompound(NBT_KEY_RECIPES_USED);
        for (String key : recipesTag.getAllKeys()) {
            ResourceLocation id = new ResourceLocation(key);
            recipesUsed.put(id, recipesTag.getInt(key));
        }
    }

    public void saveToTag(CompoundTag tag) {
        CompoundTag recipesTag = new CompoundTag();
        for (Object2IntMap.Entry<ResourceLocation> e : recipesUsed.object2IntEntrySet()) {
            recipesTag.putInt(e.getKey().toString(), e.getIntValue());
        }
        tag.put(NBT_KEY_RECIPES_USED, recipesTag);
    }
}