package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.item.BotaniaModItems;
import net.wkhan.naturesaura_plus.common.item.ModItems;

import static net.wkhan.naturesaura_plus.NaturesAuraPlus.isBotaniaLoaded;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NaturesAuraPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.BREAK_PREVENTION);
        if(isBotaniaLoaded) simpleItem(BotaniaModItems.AURA_MANA_HOLDER);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(NaturesAuraPlus.MODID, "item/" + item.getId().getPath()));
    }
}
