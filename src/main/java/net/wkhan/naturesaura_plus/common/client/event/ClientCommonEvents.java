package net.wkhan.naturesaura_plus.common.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.client.ClientDualBarTooltipComponent;
import net.wkhan.naturesaura_plus.compat.botania.ItemAuraManaHolder;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientCommonEvents {

    @SubscribeEvent
    public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(
                ItemAuraManaHolder.DualAuraManaItemImpl.RecordDualAuraMana.class,
                data -> new ClientDualBarTooltipComponent(data.aura(), data.max_aura(), data.mana(), data.max_mana()));
    }
}
