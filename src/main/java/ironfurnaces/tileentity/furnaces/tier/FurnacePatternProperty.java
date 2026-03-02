package ironfurnaces.tileentity.furnaces.tier;

import ironfurnaces.loaders.IronFurnaces;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FurnacePatternProperty extends Property<FurnacePattern> {
    public static final FurnacePatternProperty INSTANCE = new FurnacePatternProperty("pattern", FurnacePattern.class);

    private FurnacePatternProperty(String name, Class<FurnacePattern> clazz) {
        super(name, clazz);
    }

    @Override
    public Collection<FurnacePattern> getPossibleValues() {
        return FurnacePatternManager.allPossiblePattern();
    }

    @Override
    public String getName(FurnacePattern value) {
        return value.id().getPath();
    }


    @Override
    public Optional<FurnacePattern> getValue(String value) {
        return Optional.ofNullable(FurnacePatternManager.get(IronFurnaces.id(value)));
    }
}
