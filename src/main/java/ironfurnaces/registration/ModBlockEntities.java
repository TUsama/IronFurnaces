package ironfurnaces.registration;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import ironfurnaces.tileentity.furnaces.FurnacePatternBlockEntity;
import ironfurnaces.tileentity.furnaces.pattern.render.PatternHolderBlockEntityRenderer;

import static ironfurnaces.loaders.IronFurnaces.REGISTRATE;

public class ModBlockEntities {
    public static final BlockEntityEntry<FurnacePatternBlockEntity> PATTERN_HOLDER =
            REGISTRATE
                    .<FurnacePatternBlockEntity>blockEntity("pattern_holder_block_entity", FurnacePatternBlockEntity::new)
                    .validBlock(ModBlocks.PATTERN_HOLDER)
                    .renderer(() -> context -> new PatternHolderBlockEntityRenderer())
                    .register();



    public static void register() {

    }
}
