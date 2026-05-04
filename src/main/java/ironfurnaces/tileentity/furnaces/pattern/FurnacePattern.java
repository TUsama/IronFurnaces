
package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.util.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public sealed abstract class FurnacePattern permits NormalFurnacePattern, RainbowFurnacePattern {

    public static final Codec<FurnacePattern> CODEC =
            PatternKind.CODEC.dispatch(
                    "pattern_type",
                    FurnacePattern::kind,
                    //~ if 1.20.1 '.CODEC' -> '.CODEC.codec()'{
                    kind -> switch (kind) {
                        case NORMAL -> NormalFurnacePattern.CODEC;
                        case RAINBOW -> RainbowFurnacePattern.CODEC;
                    }
                    //~ }

            );
    public static final String NBT_KEY = "ir_current_pattern";
    public static final NormalFurnacePattern FALLBACK = new NormalFurnacePattern(
            IronFurnaces.id("fallback_pattern"),
            1000,
            1,
            20_000,
            40,
            20,
            1,
            Optional.empty()
    );
    public static final Codec<FurnacePattern> REF_CODEC = Identifier.CODEC.xmap(
            FurnacePatternManager::getOrFallback,
            FurnacePattern::id
    );

    public static Component toDisplayName(Identifier id) {
        if (id == null) {
            return Component.literal("null");
        }

        FurnacePattern pattern = FurnacePatternManager.get(id);
        if (pattern != null) {
            return Component.translatable("block.ironfurnaces." + pattern.id().getPath());
        }

        if (BuiltInRegistries.BLOCK.containsKey(id)) {
            return Component.translatable(Util.makeDescriptionId("block", id));
        }

        return Component.literal(id.toString());
    }

    public abstract Identifier id();

    public abstract Optional<Identifier> referenceBlock();

    public abstract PatternKind kind();

    public final boolean isRainbow() {
        return kind() == PatternKind.RAINBOW;
    }

    public Optional<RainbowFurnaceConfig> rainbowConfig() {
        return Optional.empty();
    }


    public enum PatternKind implements StringRepresentable {
        NORMAL("normal"),
        RAINBOW("rainbow");

        public static final EnumCodec<PatternKind> CODEC =
                StringRepresentable.fromEnum(PatternKind::values);

        private final String name;

        PatternKind(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}