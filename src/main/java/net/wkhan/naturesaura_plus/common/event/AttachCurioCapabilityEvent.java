package net.wkhan.naturesaura_plus.common.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.compat.botania.ItemAuraManaHolder;

import static net.wkhan.naturesaura_plus.NaturesAuraPlus.isBotaniaLoaded;
import static net.wkhan.naturesaura_plus.NaturesAuraPlus.isCuriosLoaded;
import static net.wkhan.naturesaura_plus.compat.curios.NaturesAuraPlusCuriosUtil.attachMergedCapability;

@Mod.EventBusSubscriber(modid = "naturesaura_plus", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCurioCapabilityEvent {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
//        ResourceLocation key = getId(event.getObject().getItem());
        if (!isBotaniaLoaded || !(event.getObject().getItem() instanceof ItemAuraManaHolder)) return; //make it a safe curio attachment
        if (isCuriosLoaded) attachMergedCapability(event);
    }
}
