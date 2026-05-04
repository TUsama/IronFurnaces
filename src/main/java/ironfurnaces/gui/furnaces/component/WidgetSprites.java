package ironfurnaces.gui.furnaces.component;

import net.minecraft.resources.Identifier;

public record WidgetSprites(Identifier enabled, Identifier disabled, Identifier enabledFocused,
                            Identifier disabledFocused) {
    public WidgetSprites(Identifier p_295225_, Identifier p_294772_) {
        this(p_295225_, p_295225_, p_294772_, p_294772_);
    }

    public WidgetSprites(Identifier p_296152_, Identifier p_296020_, Identifier p_296073_) {
        this(p_296152_, p_296020_, p_296073_, p_296020_);
    }

    public Identifier get(boolean enabled, boolean focused) {
        if (enabled) {
            return focused ? this.enabledFocused : this.enabled;
        } else {
            return focused ? this.disabledFocused : this.disabled;
        }
    }
}
