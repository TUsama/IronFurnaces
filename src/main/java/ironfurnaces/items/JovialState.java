package ironfurnaces.items;

import net.minecraft.util.StringRepresentable;

public enum JovialState implements StringRepresentable {
    NONE("none"),
    SPOOKY("spooky"),
    XMAS("xmas");

    public final String name;

    JovialState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
