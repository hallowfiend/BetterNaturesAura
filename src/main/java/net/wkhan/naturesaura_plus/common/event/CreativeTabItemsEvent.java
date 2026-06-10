package net.wkhan.naturesaura_plus.common.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.item.BotaniaModItems;
import net.wkhan.naturesaura_plus.common.item.ModItems;

import static net.wkhan.naturesaura_plus.NaturesAuraPlus.isBotaniaLoaded;

@Mod.EventBusSubscriber(modid = NaturesAuraPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabItemsEvent {

    @SubscribeEvent
    public static void addCustomItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("naturesaura", "tab"))) {
            event.accept(ModItems.BREAK_PREVENTION.get());
            if (isBotaniaLoaded) {
                event.accept(BotaniaModItems.AURA_MANA_HOLDER.get());
            }
        }
    }

}
