package ironfurnaces.registration;

import dev.anvilcraft.lib.v2.registrum.util.entry.BlockEntityEntry;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternBlockEntityRenderState;
import ironfurnaces.tileentity.furnaces.pattern.render.refactor.FurnacePatternBlockEntityRenderer;


import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModBlockEntities {
    public static final BlockEntityEntry<FurnacePatternBlockEntity> PATTERN_HOLDER =
            REGISTRATE
                    .<FurnacePatternBlockEntity, FurnacePatternBlockEntityRenderState>blockEntity("pattern_holder_block_entity", FurnacePatternBlockEntity::new)
                    .validBlock(ModBlocks.PATTERN_HOLDER)
                    .renderer(() -> FurnacePatternBlockEntityRenderer::new)
                    .register();



    public static void register() {

    }
}
