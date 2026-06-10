package net.wkhan.naturesaura_plus.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

public class BotaniaModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NaturesAuraPlus.MODID);

    public static final RegistryObject<Item> AURA_MANA_HOLDER = ITEMS.register("aura_mana_holder",
            () -> new ItemAuraManaHolder(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
