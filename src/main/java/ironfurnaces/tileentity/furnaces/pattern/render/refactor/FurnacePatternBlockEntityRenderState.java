package ironfurnaces.tileentity.furnaces.pattern.render.refactor;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public final class FurnacePatternBlockEntityRenderState extends BlockEntityRenderState {

    public FurnacePatternRenderData data;

    public void clear() {
        this.data = null;
    }
}
