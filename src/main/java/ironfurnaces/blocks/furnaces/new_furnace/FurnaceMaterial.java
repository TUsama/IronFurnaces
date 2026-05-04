package ironfurnaces.blocks.furnaces.new_furnace;

import com.clefal.nirvana_lib.relocated.io.vavr.control.Either;
import ironfurnaces.tileentity.furnaces.pattern.FurnacePattern;
import net.minecraft.world.level.block.Block;

public class FurnaceMaterial implements Either<Block, FurnacePattern> {
    private Either<Block, FurnacePattern> delegate;

    public FurnaceMaterial(Either<Block, FurnacePattern> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Block getLeft() {
        return delegate.getLeft();
    }

    @Override
    public boolean isLeft() {
        return delegate.isLeft();
    }

    @Override
    public boolean isRight() {
        return delegate.isRight();
    }

    @Override
    public FurnacePattern get() {
        return delegate.get();
    }

    @Override
    public String stringPrefix() {
        return delegate.stringPrefix();
    }


}
