package ironfurnaces.tileentity.furnaces.cache.stat;

import net.minecraft.util.Mth;

public final class FillStats {
    public static final FillStats EMPTY = new FillStats();
    public float fill_sum = 0.0f;
    public int non_empty = 0;
    public int slot_count = 0;

    public float fillRatio() {
        return slot_count <= 0 ? 0.0f : (fill_sum / (float) slot_count);
    }

    public int toRedstoneStrength() {
        if (slot_count <= 0) return 0;
        int signal = Mth.floor(fillRatio() * 14.0f) + (non_empty > 0 ? 1 : 0);
        return Mth.clamp(signal, 0, 15);
    }

    public FillStats add(FillStats other) {
        this.fill_sum += other.fill_sum;
        this.non_empty += other.non_empty;
        this.slot_count += other.slot_count;
        return this;
    }
}
