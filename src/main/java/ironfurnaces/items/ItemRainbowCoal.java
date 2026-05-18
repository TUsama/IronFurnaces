package ironfurnaces.items;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.FuelValues;
import org.jetbrains.annotations.Nullable;

public class ItemRainbowCoal extends Item {
    private static final int DURABILITY = 5120;
    private static final int BURN_TIME = 200;

    public ItemRainbowCoal(Properties properties) {
        super(properties
                .durability(DURABILITY)
                .setNoCombineRepair()
        );
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * (1.0F - (float) stack.getDamageValue() / (float) getDurability()));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float durabilityLeft = Math.max(
                0.0F,
                ((float) getDurability() - (float) stack.getDamageValue()) / (float) getDurability()
        );

        return Mth.hsvToRgb(durabilityLeft / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public int getBurnTime(
            ItemStack stack,
            @Nullable RecipeType<?> recipeType,
            FuelValues fuelValues
    ) {
        return BURN_TIME;
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        int nextDamage = instance.getOrDefault(DataComponents.DAMAGE, 0) + 1;

        if (nextDamage >= getDurability()) {
            return null;
        }

        ItemStack remainder = new ItemStack(this);
        remainder.set(DataComponents.DAMAGE, nextDamage);

        return ItemStackTemplate.fromNonEmptyStack(remainder);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return false;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return false;
    }

    private int getDurability() {
        return DURABILITY;
    }
}