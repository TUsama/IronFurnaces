package ironfurnaces.capability.rainbow;

import com.clefal.nirvana_lib.relocated.io.vavr.Tuple;
import com.clefal.nirvana_lib.relocated.io.vavr.Tuple2;
import ironfurnaces.capability.PlayerDataHandler;
import ironfurnaces.config.GameplayConfig;
import ironfurnaces.config.RainbowConfig;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.*;
import lombok.Getter;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OwnerRainbowContext implements ValueIOSerializable {

    private final Map<Identifier, EffectiveFurnaceStats> resolvedStats = new LinkedHashMap<>();
    private final Map<Identifier, Set<Identifier>> contributors = new LinkedHashMap<>();
    @Getter
    private long revision = 0L;
    @Getter
    private boolean dirty = true;

    private static EffectiveFurnaceStats toBaseStats(RainbowFurnacePattern pattern) {
        return new EffectiveFurnaceStats(
                pattern.baseSmeltTickPerItem(),
                pattern.baseBatchHandle(),
                pattern.baseEnergyCapacity(),
                pattern.baseEnergyGenerationPerTick(),
                pattern.baseEnergyConsumerPerTick(),
                pattern.baseInputSlotAmount()
        );
    }

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
            return resolvedStats.getOrDefault(rainbowPattern.id(), toBaseStats(rainbowPattern));
        }
        return EffectiveFurnaceStats.fromBase(((NormalFurnacePattern) pattern));
    }

    public Set<Identifier> getContributors(Identifier rainbowPatternId) {
        return contributors.getOrDefault(rainbowPatternId, Set.of());
    }

    public Map<Identifier, EffectiveFurnaceStats> snapshotResolvedStats() {
        return Map.copyOf(resolvedStats);
    }

    public void tick(ServerPlayer player) {
        if (!dirty) {
            return;
        }
        refreshNow(player);
    }

    public void refreshNow(ServerPlayer player) {
        Tuple2<LinkedHashSet<Identifier>, List<FurnacePatternBlockEntity>> linkedHashSetListTuple2 = collectActiveNormalKinds(player);
        Set<Identifier> activeNormalKinds = linkedHashSetListTuple2._1;
        //System.out.println("refresh now!");
        Map<Identifier, EffectiveFurnaceStats> newResolvedStats = new LinkedHashMap<>();
        Map<Identifier, Set<Identifier>> newContributors = new LinkedHashMap<>();

        for (FurnacePattern pattern : FurnacePatternManager.allPossiblePattern()) {
            if (!(pattern instanceof RainbowFurnacePattern rainbowPattern)) {
                continue;
            }

            RainbowBonus totalBonus = RainbowBonus.ZERO;
            LinkedHashSet<Identifier> currentContributors = new LinkedHashSet<>();
            if (RainbowConfig.config.enable_per_kind_bonus) {
                for (Identifier activeKind : activeNormalKinds) {
                    RainbowBonus bonus = rainbowPattern.config().bonusFor(activeKind);
                    if (isZeroBonus(bonus)) {
                        continue;
                    }
                    totalBonus = totalBonus.add(bonus);
                    currentContributors.add(activeKind);
                }
            }

            if (RainbowConfig.config.enable_all_kind_bonus) {
                if (rainbowPattern.config().isAllKindsActivated(activeNormalKinds)) {
                    totalBonus = totalBonus.add(rainbowPattern.config().allKindsBonus());
                }
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
            if (effectiveFurnaceStats != null) {
                furnacePatternBlockEntity.updateRainbowStats(effectiveFurnaceStats, revision);
            }
        }

        this.dirty = false;
    }

    private Tuple2<LinkedHashSet<Identifier>, List<FurnacePatternBlockEntity>> collectActiveNormalKinds(ServerPlayer player) {
        return PlayerDataHandler.readFurnacesList(player, list -> {
            LinkedHashSet<Identifier> result = new LinkedHashSet<>();
            List<FurnacePatternBlockEntity> rainbows = new ArrayList<>();
            //System.out.println("current size is " + list.get().size());
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

                if (otherPattern.isRainbow()) {
                    rainbows.add(other);
                }
                if (!other.isActiveForRainbowCount()) {
                    continue;
                }
                result.add(otherPattern.id());
                System.out.println("add to result");
            }
            return Tuple.of(result, rainbows);
        });

    }

    private boolean isZeroBonus(RainbowBonus bonus) {
        return bonus.smeltTickPerItemOffset() == 0
                && bonus.energyCapacityOffset() == 0
                && bonus.energyGenerationPerTickOffset() == 0
                && bonus.energyConsumerPerTickOffset() == 0
                && bonus.inputSlotAmountOffset() == 0;
    }

    private static final String KEY_REVISION = "Revision";
    private static final String KEY_DIRTY = "Dirty";

    @Override
    public void serialize(ValueOutput output) {
        output.putLong(KEY_REVISION, this.revision);

        // 保留旧 saveToTag 的语义：写出时永远标记 Dirty=true
        output.putBoolean(KEY_DIRTY, true);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.resolvedStats.clear();
        this.contributors.clear();

        this.revision = input.getLongOr(KEY_REVISION, 0L);

        // 旧 loadFromTag 没有读取 Dirty，而是无条件设为 true
        this.dirty = true;
    }
}