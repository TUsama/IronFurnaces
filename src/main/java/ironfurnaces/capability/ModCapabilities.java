package ironfurnaces.capability;

import ironfurnaces.capability.rainbow.OwnerRainbowContext;
import static ironfurnaces.loaders.IronFurnaces.MOD_ID;
import java.util.function.Supplier;
//? forge {
/*import ironfurnaces.capability.rainbow.PlayerRainbowContextCapability;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;
*///? } else {
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;

//?}
public class ModCapabilities {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public static final Supplier<AttachmentType<PlayerFurnacesList>> FURNACES_LIST =
            ATTACHMENTS.register("furnaces_list",
                    () -> AttachmentType.serializable(PlayerFurnacesList::new)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<OwnerRainbowContext>> PLAYER_RAINBOW_CONTEXT =
            ATTACHMENTS.register("player_rainbow_context",
                    () -> AttachmentType.serializable(OwnerRainbowContext::new)
                            .copyOnDeath()
                            .build());


}
