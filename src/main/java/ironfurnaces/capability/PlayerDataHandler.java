package ironfurnaces.capability;

import ironfurnaces.capability.rainbow.OwnerRainbowContext;
import net.minecraft.world.entity.player.Player;

//? 1.20.1 {
/*import net.neoforged.neoforge.common.util.LazyOptional;
*///? } else {

//?}
import java.util.function.Consumer;
import java.util.function.Function;

public final class PlayerDataHandler {
    public static void editFurnacesList(Player player, Consumer<PlayerFurnacesList> consumer) {
        //? forge {
        /*player.getCapability(ModCapabilities.FURNACES_LIST).ifPresent(consumer::accept);
        *///?} else {
        consumer.accept(player.getData(ModCapabilities.FURNACES_LIST));
        //?}

    }

    public static <R> R readFurnacesList(Player player, Function<PlayerFurnacesList, R> fn) {
        //? forge {
        /*LazyOptional<PlayerFurnacesList> capability = player.getCapability(ModCapabilities.FURNACES_LIST);
        if (capability.isPresent()){
            return fn.apply(capability.orElseGet(() -> new PlayerFurnacesList()));
        }
        return null;
        *///?} else {
        return fn.apply(player.getData(ModCapabilities.FURNACES_LIST));
        //?}
    }

    public static void editRainbowContext(Player player, Consumer<OwnerRainbowContext> consumer) {
        //? forge {
        /*player.getCapability(ModCapabilities.PLAYER_RAINBOW_CONTEXT).ifPresent(x -> consumer.accept(x));
        *///?} else {
        consumer.accept(player.getData(ModCapabilities.PLAYER_RAINBOW_CONTEXT));
        //?}
    }

    public static <R> R readRainbowContext(Player player, Function<OwnerRainbowContext, R> fn) {
        //? forge {
        /*LazyOptional<OwnerRainbowContext> capability = player.getCapability(ModCapabilities.PLAYER_RAINBOW_CONTEXT);
        if (capability.isPresent()){
            return fn.apply(capability.orElseGet(() -> new OwnerRainbowContext()));
        }
        return null;
        *///?} else {
        return fn.apply(player.getData(ModCapabilities.PLAYER_RAINBOW_CONTEXT));
        //?}
    }
}
