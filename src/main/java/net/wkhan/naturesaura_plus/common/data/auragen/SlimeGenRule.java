package net.wkhan.naturesaura_plus.common.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record SlimeGenRule(
        Either<EntityType<?>,TagKey<EntityType<?>>> entityId,
        int auraAmount,
        int slimeColor,
        int generationTimerModifier,
        float sizeModifier,
        boolean doSlimeSizeScaling,
        boolean doEntityDropLoot
) {

    public static final Codec<SlimeGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.ENTITY_TYPES, Registries.ENTITY_TYPE)
                            .fieldOf("entity").forGetter(SlimeGenRule::entityId),
                    Codec.INT.fieldOf("aura_gain").forGetter(SlimeGenRule::auraAmount),
                    Codec.INT.optionalFieldOf("slime_color", 5089359).forGetter(SlimeGenRule::slimeColor),
                    Codec.INT.fieldOf("generation_timer_modifier").forGetter(SlimeGenRule::generationTimerModifier),
                    Codec.FLOAT.fieldOf("size_modifier").forGetter(SlimeGenRule::sizeModifier),
                    Codec.BOOL.optionalFieldOf("slime_size_scaling", false).forGetter(SlimeGenRule::doSlimeSizeScaling),
                    Codec.BOOL.optionalFieldOf("entity_drop_loot", false).forGetter(SlimeGenRule::doEntityDropLoot)
            ).apply(instance, SlimeGenRule::new)
    );

    public boolean isTag() {
        return this.entityId.right().isPresent();
    }

    public EntityType<?> getEntity() {
        return this.entityId.left().orElse(null);
    }

    public TagKey<EntityType<?>> getEntityTag() {
        return this.entityId.right().orElse(null);
    }

}
