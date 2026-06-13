package net.wkhan.naturesaura_plus.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NaturesAuraPlus.MODID);

    public static final RegistryObject<Item> BREAK_PREVENTION = ITEMS.register("break_prevention_token",
            () -> new ItemBreakPreventionAll(new Item.Properties().stacksTo(64).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> COFFEE = ITEMS.register("coffee",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat()
                    .nutrition(4)
                    .saturationMod(0.8f)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1), 1)
                    .build())
                    .stacksTo(16).rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> AURA_COFFEE = ITEMS.register("aura_coffee",
            () -> new ItemRecallCoffee(new Item.Properties().food(new FoodProperties.Builder()
                            .alwaysEat()
                            .nutrition(1)
                            .saturationMod(0.2f)
                            .build())
                    .stacksTo(16).rarity(Rarity.RARE)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
