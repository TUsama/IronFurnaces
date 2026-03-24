package ironfurnaces.tileentity.furnaces.pattern;

import com.mojang.serialization.Codec;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class FurnacePattern {

    public static final Codec<FurnacePattern> CODEC =
            PatternKind.CODEC.dispatch(
                    "pattern_type",
                    FurnacePattern::kind,
                    kind -> switch (kind) {
                        case NORMAL -> NormalFurnacePattern.CODEC;
                        case RAINBOW -> RainbowFurnacePattern.CODEC;
                    }
            );
    public static final String NBT_KEY = "ir_current_pattern";
    public static final NormalFurnacePattern FALLBACK = new NormalFurnacePattern(
            IronFurnaces.id("fallback_pattern"),
            1000,
            20_000,
            40,
            20,
            1,
            Optional.empty()
    );
    public static final Codec<FurnacePattern> REF_CODEC = ResourceLocation.CODEC.xmap(
            FurnacePatternManager::getOrFallback,
            FurnacePattern::id
    );

    public abstract ResourceLocation id();

    public abstract Optional<ResourceLocation> referenceBlock();

    public abstract PatternKind kind();

    public final boolean isRainbow() {
        return kind() == PatternKind.RAINBOW;
    }

    public Optional<RainbowFurnaceConfig> rainbowConfig() {
        return Optional.empty();
    }

    public RainbowFurnaceConfig rainbowConfigOrEmpty() {
        return rainbowConfig().orElseGet(() -> new RainbowFurnaceConfig(Map.of(), Set.of()));
    }

    public void broadcastChanges(FurnacePatternBlockEntity blockEntity) {
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