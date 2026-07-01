package net.wkhan.naturesaura_plus.mixin.misc.treeritual;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Codec;
import de.ellpeck.naturesaura.blocks.multi.Multiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.wkhan.naturesaura_plus.common.data.MushroomTreeRitualTracker;
import net.wkhan.naturesaura_plus.common.data.duckfaces.AbstractWoodStand;
import net.wkhan.naturesaura_plus.common.data.duckfaces.MultiBlockUtil;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashSet;
import java.util.Set;

@Mixin(AbstractHugeMushroomFeature.class)
public abstract class AbstractHugeMushroomFeatureMixin extends Feature<HugeMushroomFeatureConfiguration> {
    public AbstractHugeMushroomFeatureMixin(Codec<HugeMushroomFeatureConfiguration> p_65786_) {
        super(p_65786_);
    }

    @WrapMethod(method = "place")
    private boolean naturesaura_plus$takeMushroomCache(FeaturePlaceContext<HugeMushroomFeatureConfiguration> context, Operation<Boolean> original) {
        BlockPos saplingPos = context.origin();
        Level level = context.level().getLevel();
        ((MultiBlockUtil) Multiblocks.TREE_RITUAL).naturesaura_plus$allowAirInRitual();
        boolean isRitual = Multiblocks.TREE_RITUAL.isComplete(context.level().getLevel(), saplingPos);
        if (!isRitual)
            return original.call(context);

        Set<BlockPos> capturedStems = new HashSet<>();
        Set<BlockPos> capturedCaps = new HashSet<>();
        MushroomTreeRitualTracker.STEM_CACHE.set(capturedStems);
        MushroomTreeRitualTracker.CAP_CACHE.set(capturedCaps);

        boolean grewSuccessfully;
        try {
            grewSuccessfully = original.call(context);
        } finally {
            MushroomTreeRitualTracker.STEM_CACHE.remove();
            MushroomTreeRitualTracker.CAP_CACHE.remove();
        }

        if (!grewSuccessfully || capturedStems.isEmpty()) return grewSuccessfully;
        Multiblocks.TREE_RITUAL.forEach(saplingPos, 'W', (standPos, matcher) -> {
            BlockEntity tile = level.getBlockEntity(standPos);
            if (tile instanceof AbstractWoodStand woodStand) {
                woodStand.naturesaura_plus$setTreeStemCache(capturedStems);
                woodStand.naturesaura_plus$setTreeLeafCache(capturedCaps.isEmpty() ? null : capturedCaps);
            }
            return true;
        });
        return true;
    }
}
