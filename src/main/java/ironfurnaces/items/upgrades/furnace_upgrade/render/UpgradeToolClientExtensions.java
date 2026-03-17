package ironfurnaces.items.upgrades.furnace_upgrade.render;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public final class UpgradeToolClientExtensions implements IClientItemExtensions {
    private static final UpgradeToolItemRenderer RENDERER = new UpgradeToolItemRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return RENDERER;
    }
}