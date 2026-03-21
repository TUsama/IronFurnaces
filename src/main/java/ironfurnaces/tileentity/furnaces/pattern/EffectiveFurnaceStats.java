package ironfurnaces.tileentity.furnaces.pattern;

public record EffectiveFurnaceStats(
        int smeltTickPerItem,
        int energyCapacity,
        int energyGenerationPerTick,
        int energyConsumerPerTick,
        int inputSlotAmount
) {
    public static EffectiveFurnaceStats fromBase(FurnacePattern pattern) {
        return new EffectiveFurnaceStats(
                pattern.smeltTickPerItem(),
                pattern.energyCapacity(),
                pattern.energyGenerationPerTick(),
                pattern.energyConsumerPerTick(),
                pattern.inputSlotAmount()
        );
    }

    public EffectiveFurnaceStats apply(RainbowBonus bonus) {
        return new EffectiveFurnaceStats(
                Math.max(1, smeltTickPerItem + bonus.smeltTickPerItemOffset()),
                Math.max(1, energyCapacity + bonus.energyCapacityOffset()),
                Math.max(1, energyGenerationPerTick + bonus.energyGenerationPerTickOffset()),
                Math.max(1, energyConsumerPerTick + bonus.energyConsumerPerTickOffset()),
                Math.max(1, inputSlotAmount + bonus.inputSlotAmountOffset())
        );
    }
}
