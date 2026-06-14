package ironfurnaces.util;

import dev.anvilcraft.lib.v2.registrum.providers.DataGenContext;
import dev.anvilcraft.lib.v2.registrum.providers.generators.RegistrumItemModelGenerator;
import ironfurnaces.blocks.furnaces.new_furnace.FurnacePatternHolderItem;
import ironfurnaces.items.upgrades.furnace_upgrade.ItemUpgradeTool;
import ironfurnaces.items.upgrades.furnace_upgrade.render.UpgradeToolSpecialRenderer;
import ironfurnaces.loaders.IronFurnaces;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternHolderSpecialRenderer;
import lombok.experimental.UtilityClass;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

@UtilityClass
public class ClientUtil {
    public static void genModel(DataGenContext<Item, FurnacePatternHolderItem> ctx, RegistrumItemModelGenerator prov) {
        Identifier baseModel = IronFurnaces.id("item/pattern_holder_base");
        prov.itemModelOutput.accept(
                ctx.get(),
                ItemModelUtils.specialModel(
                        baseModel,
                        new FurnacePatternHolderSpecialRenderer.Unbaked()
                )
        );
    }

    public static void genModels(DataGenContext<Item, ItemUpgradeTool> ctx, RegistrumItemModelGenerator prov) {
        Identifier baseModel = IronFurnaces.id("item/upgrade_tool_base");

        ModelTemplates.PARTICLE_ONLY.create(
                baseModel,
                TextureMapping.particle(TextureMapping.getBlockTexture(Blocks.STONE)),
                prov.modelOutput
        );

        prov.itemModelOutput.accept(
                ctx.get(),
                ItemModelUtils.specialModel(
                        baseModel,
                        new UpgradeToolSpecialRenderer.Unbaked()
                )
        );
    }
}
