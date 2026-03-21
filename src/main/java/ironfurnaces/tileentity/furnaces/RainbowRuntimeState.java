package ironfurnaces.tileentity.furnaces;

import ironfurnaces.capability.CapabilityPlayerFurnacesList;
import ironfurnaces.config.IronFurnacesConfig;
import ironfurnaces.tileentity.furnaces.pattern.EffectiveFurnaceStats;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import ironfurnaces.tileentity.furnaces.pattern.RainbowBonus;
import ironfurnaces.tileentity.furnaces.pattern.RainbowFurnaceConfig;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class RainbowRuntimeState {

    private final FurnacePatternBlockEntity owner;

    private EffectiveFurnaceStats cachedStats = EffectiveFurnaceStats.fromBase(FurnacePattern.FALLBACK);
    private final Set<ResourceLocation> cachedActiveKinds = new LinkedHashSet<>();
    private long nextRefreshGameTime = 0L;

    public RainbowRuntimeState(FurnacePatternBlockEntity owner) {
        this.owner = owner;
    }

    public EffectiveFurnaceStats getCachedStats() {
        FurnacePattern pattern = owner.getPattern();
        if (pattern == null) return EffectiveFurnaceStats.fromBase(FurnacePattern.FALLBACK);
        return cachedStats;
    }

    public Set<ResourceLocation> getCachedActiveKinds() {
        return Set.copyOf(cachedActiveKinds);
    }

    public void onPatternChanged() {
        FurnacePattern pattern = owner.getPattern();
        this.cachedStats = EffectiveFurnaceStats.fromBase(pattern == null ? FurnacePattern.FALLBACK : pattern);
        this.cachedActiveKinds.clear();
        this.nextRefreshGameTime = 0L;
    }

    public void tickServer() {
        if (owner.getLevel() == null || owner.getLevel().isClientSide) return;

        FurnacePattern pattern = owner.getPattern();
        if (pattern == null || !pattern.isRainbow()) return;

        RainbowFurnaceConfig config = pattern.rainbowConfigOrEmpty();
        long gameTime = owner.getLevel().getGameTime();

        if (gameTime < nextRefreshGameTime) return;

        refreshNow();
        nextRefreshGameTime = gameTime + Math.max(1, config.refreshInterval());
    }

    public void refreshNow() {
        FurnacePattern selfPattern = owner.getPattern();
        if (selfPattern == null || !selfPattern.isRainbow()) {
            this.cachedStats = EffectiveFurnaceStats.fromBase(FurnacePattern.FALLBACK);
            this.cachedActiveKinds.clear();
            return;
        }

        RainbowFurnaceConfig config = selfPattern.rainbowConfigOrEmpty();
        EffectiveFurnaceStats stats = EffectiveFurnaceStats.fromBase(selfPattern);
        Set<ResourceLocation> activeKinds = new LinkedHashSet<>();

        ServerPlayer serverPlayer = resolveOwnerPlayer();
        if (serverPlayer == null) {
            this.cachedStats = stats;
            this.cachedActiveKinds.clear();
            owner.setChanged();
            return;
        }

        serverPlayer.getCapability(CapabilityPlayerFurnacesList.FURNACES_LIST).ifPresent(list -> {
            for (GlobalPos globalPos : list.get()) {
                if (globalPos == null) continue;

                if (!config.countSelf()
                        && globalPos.dimension().equals(owner.getLevel().dimension())
                        && globalPos.pos().equals(owner.getBlockPos())) {
                    continue;
                }

                if (owner.getLevel().getServer() == null) continue;
                ServerLevel targetLevel = owner.getLevel().getServer().getLevel(globalPos.dimension());
                if (targetLevel == null) continue;
                if (!targetLevel.isLoaded(globalPos.pos())) {
                    if (IronFurnacesConfig.config.force_load_chunk_when_check_furnace_kind_for_rainbow_furnace) {
                        targetLevel.getChunkAt(globalPos.pos()).setLoaded(true);
                    }
                    continue;
                }

                if (!(targetLevel.getBlockEntity(globalPos.pos()) instanceof FurnacePatternBlockEntity other)) continue;
                if (!other.isActiveForRainbowCount()) continue;

                FurnacePattern otherPattern = other.getPattern();
                if (otherPattern == null) continue;
                if (otherPattern.isRainbow()) continue;
                if (config.ignorePatterns().contains(otherPattern.id())) continue;

                activeKinds.add(otherPattern.id());
            }
        });

        RainbowBonus totalBonus = RainbowBonus.ZERO;
        for (ResourceLocation id : activeKinds) {
            totalBonus = totalBonus.add(config.bonusFor(id));
        }

        this.cachedStats = stats.apply(totalBonus);
        this.cachedActiveKinds.clear();
        this.cachedActiveKinds.addAll(activeKinds);
        owner.setChanged();
    }

    @Nullable
    private ServerPlayer resolveOwnerPlayer() {
        if (owner.getLevel() == null || owner.getLevel().isClientSide || owner.getLevel().getServer() == null) {
            return null;
        }
        UUID ownerUuid = owner.getOwnerUuid();
        if (ownerUuid == null) return null;
        return owner.getLevel().getServer().getPlayerList().getPlayer(ownerUuid);
    }

    public void saveToTag(CompoundTag tag) {
        tag.putLong("RainbowNextRefresh", this.nextRefreshGameTime);
    }

    public void loadFromTag(CompoundTag tag) {
        this.nextRefreshGameTime = tag.getLong("RainbowNextRefresh");
        FurnacePattern pattern = owner.getPattern();
        this.cachedStats = EffectiveFurnaceStats.fromBase(pattern == null ? FurnacePattern.FALLBACK : pattern);
        this.cachedActiveKinds.clear();
    }
}