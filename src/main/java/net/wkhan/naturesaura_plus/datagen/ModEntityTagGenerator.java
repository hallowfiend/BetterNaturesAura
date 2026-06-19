package net.wkhan.naturesaura_plus.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlus;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagGenerator extends EntityTypeTagsProvider {
    public ModEntityTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NaturesAuraPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(e -> e.getCategory() != MobCategory.MONSTER && e.getCategory() != MobCategory.MISC )
                .forEach(e -> this.tag(ModTags.Entities.ANIMAL).add(e));
    }

}
