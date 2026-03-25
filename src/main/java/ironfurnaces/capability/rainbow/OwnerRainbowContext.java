package ironfurnaces.capability.rainbow;

import com.clefal.nirvana_lib.relocated.io.vavr.Tuple;
import com.clefal.nirvana_lib.relocated.io.vavr.Tuple2;
import ironfurnaces.capability.IPlayerFurnacesList;
import ironfurnaces.capability.ModCapabilities;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.*;
import lombok.Getter;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OwnerRainbowContext {

    private final Map<ResourceLocation, EffectiveFurnaceStats> resolvedStats = new LinkedHashMap<>();
    private final Map<ResourceLocation, Set<ResourceLocation>> contributors = new LinkedHashMap<>();
    @Getter
    private long revision = 0L;
    @Getter
    private boolean dirty = true;

    public void markDirty() {
        this.dirty = true;
    }

    public void invalidateAll() {
        this.resolvedStats.clear();
        this.contributors.clear();
        this.revision = 0L;
        this.dirty = true;
    }

    /**
     * 对外统一入口。
     * 非彩虹 pattern 直接返回 base。
     * 彩虹 pattern 返回当前玩家上下文解析后的 stats。
     */
    public EffectiveFurnaceStats getResolvedStats(FurnacePattern pattern) {
        if (pattern instanceof RainbowFurnacePattern rainbowPattern) {
            System.out.println("current resolve stats are " + resolvedStats);
            return resolvedStats.getOrDefault(rainbowPattern.id(), toBaseStats(rainbowPattern));
        }
        return EffectiveFurnaceStats.fromBase(((NormalFurnacePattern) pattern));
    }

    public Set<ResourceLocation> getContributors(ResourceLocation rainbowPatternId) {
        return contributors.getOrDefault(rainbowPatternId, Set.of());
    }

    public Map<ResourceLocation, EffectiveFurnaceStats> snapshotResolvedStats() {
        return Map.copyOf(resolvedStats);
    }

    public void tick(ServerPlayer player) {
        if (!dirty) {
            return;
        }
        refreshNow(player);
    }

    public void refreshNow(ServerPlayer player) {
        Tuple2<LinkedHashSet<ResourceLocation>, List<FurnacePatternBlockEntity>> linkedHashSetListTuple2 = collectActiveNormalKinds(player);
        Set<ResourceLocation> activeNormalKinds = linkedHashSetListTuple2._1;
        //System.out.println("refresh now!");
        Map<ResourceLocation, EffectiveFurnaceStats> newResolvedStats = new LinkedHashMap<>();
        Map<ResourceLocation, Set<ResourceLocation>> newContributors = new LinkedHashMap<>();

        for (FurnacePattern pattern : FurnacePatternManager.allPossiblePattern()) {
            if (!(pattern instanceof RainbowFurnacePattern rainbowPattern)) {
                continue;
            }

            RainbowBonus totalBonus = RainbowBonus.ZERO;
            LinkedHashSet<ResourceLocation> currentContributors = new LinkedHashSet<>();

            for (ResourceLocation activeKind : activeNormalKinds) {
                RainbowBonus bonus = rainbowPattern.config().bonusFor(activeKind);
                if (isZeroBonus(bonus)) {
                    continue;
                }
                totalBonus = totalBonus.add(bonus);
                currentContributors.add(activeKind);
            }

            if (rainbowPattern.config().isAllKindsActivated(activeNormalKinds.size())) {
                totalBonus = totalBonus.add(rainbowPattern.config().allKindsBonus());
            }

            EffectiveFurnaceStats resolved = toBaseStats(rainbowPattern).apply(totalBonus);
            newResolvedStats.put(rainbowPattern.id(), resolved);
            newContributors.put(rainbowPattern.id(), Set.copyOf(currentContributors));

        }

        boolean changed =
                !this.resolvedStats.equals(newResolvedStats)
                        || !this.contributors.equals(newContributors);

        this.resolvedStats.clear();
        this.resolvedStats.putAll(newResolvedStats);

        this.contributors.clear();
        this.contributors.putAll(newContributors);

        if (changed) {
            this.revision++;
        }
        for (FurnacePatternBlockEntity furnacePatternBlockEntity : linkedHashSetListTuple2._2) {
            EffectiveFurnaceStats effectiveFurnaceStats = this.resolvedStats.get(furnacePatternBlockEntity.getPattern().id());
            if (effectiveFurnaceStats != null){
                furnacePatternBlockEntity.updateRainbowStats(effectiveFurnaceStats, revision);
            }
        }

        this.dirty = false;
    }


    private Tuple2<LinkedHashSet<ResourceLocation>, List<FurnacePatternBlockEntity>> collectActiveNormalKinds(ServerPlayer player) {

        LinkedHashSet<ResourceLocation> result = new LinkedHashSet<>();
        List<FurnacePatternBlockEntity> rainbows = new ArrayList<>();

        var capability = player.getCapability(ModCapabilities.FURNACES_LIST);
        capability.ifPresent(list -> {
            for (GlobalPos globalPos : list.get()) {
                if (globalPos == null) {
                    continue;
                }

                ServerLevel level = player.server.getLevel(globalPos.dimension());
                if (level == null) {
                    continue;
                }

                if (!level.isLoaded(globalPos.pos())) {
                    if (GameplayConfig.config.force_load_chunk_when_check_furnace_kind_for_rainbow_furnace) {
                        level.getChunkAt(globalPos.pos()).setLoaded(true);
                    }
                    continue;
                }

                if (!(level.getBlockEntity(globalPos.pos()) instanceof FurnacePatternBlockEntity other)) {
                    continue;
                }
                if (!player.getUUID().equals(other.getOwnerUuid())) {
                    continue;
                }
                FurnacePattern otherPattern = other.getPattern();

                if (otherPattern.isRainbow()){
                    rainbows.add(other);
                }
                if (!other.isActiveForRainbowCount()) {
                    continue;
                }
                result.add(otherPattern.id());
            }
        });

        return Tuple.of(result, rainbows);
    }

    private boolean isZeroBonus(RainbowBonus bonus) {
        return bonus.smeltTickPerItemOffset() == 0
                && bonus.energyCapacityOffset() == 0
                && bonus.energyGenerationPerTickOffset() == 0
                && bonus.energyConsumerPerTickOffset() == 0
                && bonus.inputSlotAmountOffset() == 0;
    }

    /**
     * 第二层建议不要持久化 resolvedStats 这种派生缓存。
     * 重新登录或重载后直接重算，避免脏缓存。
     */
    public CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("Revision", revision);
        tag.putBoolean("Dirty", true);
        return tag;
    }

    public void loadFromTag(@Nullable CompoundTag tag) {
        this.resolvedStats.clear();
        this.contributors.clear();
        this.revision = tag == null ? 0L : tag.getLong("Revision");
        this.dirty = true;
    }

    private static EffectiveFurnaceStats toBaseStats(RainbowFurnacePattern pattern) {
        return new EffectiveFurnaceStats(
                pattern.baseSmeltTickPerItem(),
                pattern.baseEnergyCapacity(),
                pattern.baseEnergyGenerationPerTick(),
                pattern.baseEnergyConsumerPerTick(),
                pattern.baseInputSlotAmount()
        );
    }
}