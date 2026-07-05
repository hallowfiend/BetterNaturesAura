package net.wkhan.naturesaura_plus.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraftforge.fluids.*;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record FluidGenRule(
        Either<Fluid<?>,TagKey<Fluid<?>>> fluidType,
        int auraAmount,
        int fluidAmount,
        int burnTime
) {

    public static final Codec<FluidGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.FLUIDS, Registries.FLUIDS)
                            .fieldOf("fluid").forGetter(FluidGenRule::fluidType),
                    Codec.INT.fieldOf("aura_gain").forGetter(FluidGenRule::auraAmount),
                    Codec.INT.fieldOf("mb_consumed").forGetter(FluidGenRule::fluidAmount),
                    Codec.INT.optionalFieldOf("burn_time_in_ticks", 20).forGetter(FluidGenRule::burnTime)
            ).apply(instance, FluidGenRule::new)
    );

    public boolean isTag() {
        return this.fluidType.right().isPresent();
    }

    public Fluid<?> getFluid() {
        return this.fluidType.left().orElse(null);
    }

    public TagKey<FluidType<?>> getFluidTag() {
        return this.fluidType.right().orElse(null);
    }

}
