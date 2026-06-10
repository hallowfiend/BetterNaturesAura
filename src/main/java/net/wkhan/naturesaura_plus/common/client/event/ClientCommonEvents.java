package net.wkhan.naturesaura_plus.common.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.client.ClientDualBarTooltipComponent;
import net.wkhan.naturesaura_plus.common.item.ItemAuraManaHolder;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientCommonEvents {

    @SubscribeEvent
    public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        // Tells Minecraft: When you see DualResourceData, use this factory constructor to create the client renderer!
        event.register(ItemAuraManaHolder.DualAuraManaItemImpl.RecordDualAuraMana.class, data -> new ClientDualBarTooltipComponent(data.aura(), data.max_aura(), data.mana(), data.max_mana()));
    }
}
