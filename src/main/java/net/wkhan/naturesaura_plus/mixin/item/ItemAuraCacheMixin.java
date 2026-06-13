package net.wkhan.naturesaura_plus.mixin.item;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import de.ellpeck.naturesaura.items.ItemAuraCache;
import de.ellpeck.naturesaura.items.ItemImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemAuraCache.class)
public class ItemAuraCacheMixin extends ItemImpl {
    public ItemAuraCacheMixin(String baseName) {
        super(baseName);
    }

    @Inject(
            method = "inventoryTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void naturesaura_plus$invalidateItemAuraCacheTick(ItemStack stackIn, Level levelIn, Entity entityIn, int itemSlot, boolean isSelected, CallbackInfo ci) {
        ci.cancel();
        if (levelIn.isClientSide || !(entityIn instanceof Player player) || !player.isShiftKeyDown()) return;
        LazyOptional<IAuraContainer> containerCap = stackIn.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER);
        if (!containerCap.isPresent()) return;
        Inventory inventory = player.getInventory();
        IAuraContainer container = containerCap.resolve().get();
        int[] slotsToRecharge = new int[]{
                inventory.selected, // Mainhand-slot
                40,                 // Offhand-slot
                36, 37, 38, 39      // Armor-slots (Boots, Leggings, Chestplate, Helmet)
        };

        for (int i : slotsToRecharge) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            LazyOptional<IAuraRecharge> recharge = stack.getCapability(NaturesAuraAPI.CAP_AURA_RECHARGE);
            if (recharge.isPresent()) {
                boolean isSelectedItem = (i == inventory.selected || i == 40);
                if (recharge.resolve().get().rechargeFromContainer(container, itemSlot, i, isSelectedItem)) {
                    break;
                }
            }
            else if (stack.getEnchantmentLevel(ModEnchantments.AURA_MENDING) > 0) {
                boolean isArmor = (i >= 36 && i <= 39);
                boolean isHand = (i == inventory.selected || i == 40);

                if ((isArmor || isHand) && Helper.rechargeAuraItem(stack, container, 1000)) {
                    break;
                }
            }
        }
    }
}
