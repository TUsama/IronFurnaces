
package ironfurnaces.tileentity.furnaces.pattern.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePatternManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record PatternUpgradeRule(
        ResourceLocation id,
        ResourceLocation from,
        ResourceLocation to
) {
    public static final String KEY = "ir_upgrade";

    public static final Codec<PatternUpgradeRule> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(PatternUpgradeRule::id),
            ResourceLocation.CODEC.fieldOf("from").forGetter(PatternUpgradeRule::from),
            ResourceLocation.CODEC.fieldOf("to").forGetter(PatternUpgradeRule::to)
    ).apply(inst, PatternUpgradeRule::new));

    private static final ResourceLocation PLACEHOLDER = IronFurnaces.id("placeholder");

    public static final PatternUpgradeRule backup =
            new PatternUpgradeRule(IronFurnaces.id("backup_upgrade"), PLACEHOLDER, IronFurnaces.id("iron_furnace"));

    public boolean isFrom(BlockState state, @Nullable BlockEntity blockEntity) {
        return matches(this.from, state, blockEntity);
    }

    public boolean isTo(BlockState state, @Nullable BlockEntity blockEntity) {
        return matches(this.to, state, blockEntity);
    }

    private static boolean matches(ResourceLocation target, BlockState state, @Nullable BlockEntity blockEntity) {
        if (target == null) return false;

        // 先判 pattern
        if (blockEntity instanceof FurnacePatternBlockEntity patternBlockEntity) {
            FurnacePattern pattern = patternBlockEntity.getPattern();
            if (pattern != null && target.equals(pattern.id())) {
                return true;
            }
        }

        // 再判 block
        if (isBlockId(target)) {
            return state.is(BuiltInRegistries.BLOCK.get(target));
        }

        return false;
    }

    public static boolean isPatternId(ResourceLocation id) {
        return id != null && FurnacePatternManager.get(id) != null;
    }

    public static boolean isBlockId(ResourceLocation id) {
        return id != null && BuiltInRegistries.BLOCK.containsKey(id);
    }

    @Nullable
    public FurnacePattern getToPattern() {
        return FurnacePatternManager.get(this.to);
    }

    @Nullable
    public Block getToBlock() {
        return BuiltInRegistries.BLOCK.getOptional(this.to).orElse(null);
    }

    @Nullable
    public FurnacePattern getFromPattern() {
        return FurnacePatternManager.get(this.from);
    }

    @Nullable
    public Block getFromBlock() {
        return BuiltInRegistries.BLOCK.getOptional(this.from).orElse(null);
    }

}