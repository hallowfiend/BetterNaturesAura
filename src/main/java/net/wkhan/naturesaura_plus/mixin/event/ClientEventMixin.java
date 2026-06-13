package net.wkhan.naturesaura_plus.mixin.event;

import de.ellpeck.naturesaura.events.ClientEvents;
import de.ellpeck.naturesaura.items.ItemAuraCache;
import net.minecraft.world.item.ItemStack;
import net.wkhan.naturesaura_plus.compat.botania.ItemAuraManaHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ClientEvents.class)
public class ClientEventMixin {

    @ModifyArg(
            method = "onClientTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lde/ellpeck/naturesaura/Helper;getEquippedItem(Ljava/util/function/Predicate;Lnet/minecraft/world/entity/player/Player;Z)Lnet/minecraft/world/item/ItemStack;",
                    ordinal = 0
            ),
            index = 0,
            remap = false
    )
    private Predicate<ItemStack> naturesaura_plus$modifyCacheCheck (Predicate<ItemStack> predicate) {
        return (s) -> s.getItem() instanceof ItemAuraCache || s.getItem() instanceof ItemAuraManaHolder;
    }
}
