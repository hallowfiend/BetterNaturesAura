package net.wkhan.naturesaura_plus.compat.curios;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.container.IAuraContainer;
import de.ellpeck.naturesaura.api.aura.item.IAuraRecharge;
import de.ellpeck.naturesaura.enchant.ModEnchantments;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import static net.wkhan.naturesaura_plus.common.event.PlayerTickEvent.handleItemTransfer;
import static net.wkhan.naturesaura_plus.common.item.ItemBreakPreventionAll.isTokenAppliedBroken;

public class NaturesAuraPlusCuriosUtil {

    private static final Capability<ICurio> CURIOS_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    // This method is only read by the JVM if Curios is active, preventing crashes
    public static void attachMergedCapability(AttachCapabilitiesEvent<ItemStack> event) {
        // An inline anonymous wrapper that implements ICurioItem instead of the base item class
        final ItemStack stack = event.getObject();

        ICurio curioWrapper = new ICurio() {
            @Override
            public ItemStack getStack() {
                return event.getObject();
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                Entity entity = slotContext.getWearer();
                if (entity.level().isClientSide || !(entity instanceof Player player) || !player.isShiftKeyDown() || stack.isEmpty()) return;
                LazyOptional<IAuraContainer> containerCap = stack.getCapability(NaturesAuraAPI.CAP_AURA_CONTAINER);
                if (!containerCap.isPresent()) return;
                int curioSlotIndex = slotContext.index();
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
                        if (recharge.resolve().get().rechargeFromContainer(container, curioSlotIndex, i, isSelectedItem)) {
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
        };

        // Standard Forge Capability injection logic using the inline wrapper
        ICapabilityProvider provider = new ICapabilityProvider() {
            private final LazyOptional<ICurio> curioCapOpt = LazyOptional.of(() -> curioWrapper);

            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == CURIOS_CAP) {
                    return curioCapOpt.cast();
                }
                return LazyOptional.empty();
            }
        };

        event.addCapability(ResourceLocation.fromNamespaceAndPath("naturesaura_plus", "curio_item"), provider);
    }

    public static void handleCuriosUnequip(Player player) {
        var optionalHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player);
        if (!optionalHandler.isPresent()) return;

        ICuriosItemHandler handler = optionalHandler.resolve().orElseThrow();
        var equipped = handler.getEquippedCurios();

        for (int i = 0; i < equipped.getSlots(); i++) {
            ItemStack stack = equipped.getStackInSlot(i);
            if (isTokenAppliedBroken(stack)) {
                ItemStack extracted = equipped.extractItem(i, 1, false);
                handleItemTransfer(player, extracted, "One of your curio broke!");
            }
        }
    }
}
