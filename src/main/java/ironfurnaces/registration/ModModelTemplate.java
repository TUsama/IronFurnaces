//? >1.21.11 {
/*package ironfurnaces.registration;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class ModModelTemplate {

    private static final ModelTemplate PATTERN_HOLDER_BASE_TEMPLATE =
            ModelTemplates.create(TextureSlot.PARTICLE);

    protected static void createPatternHolderBaseModel(Item patternHolder, ItemModelGenerators prov) {
        PATTERN_HOLDER_BASE_TEMPLATE.create(
                patternHolder,
                TextureMapping.particleFromItem(patternHolder),
                prov.modelOutput
        );
    }
}
*///?}