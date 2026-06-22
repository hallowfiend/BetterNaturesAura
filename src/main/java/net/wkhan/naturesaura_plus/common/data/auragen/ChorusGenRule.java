package net.wkhan.naturesaura_plus.common.data.auragen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;

public record ChorusGenRule(
        Either<Block, TagKey<Block>> soilBlockId,
        Either<Block, TagKey<Block>> stemBlockId,
        Either<Block, TagKey<Block>> capBlockId,
        int auraGainPerBlock,
        boolean isSizeScaled,
        SoundEvent soundEvent,
        float soundVolume,
        float soundPitch
) {

    public static final Codec<ChorusGenRule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.BLOCKS, Registries.BLOCK)
                            .fieldOf("block_plant_soil").forGetter(ChorusGenRule::soilBlockId),
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.BLOCKS, Registries.BLOCK)
                            .fieldOf("block_plant_stem").forGetter(ChorusGenRule::stemBlockId),
                    NaturesAuraPlusUtils.elementOrTagCodec(ForgeRegistries.BLOCKS, Registries.BLOCK)
                            .fieldOf("block_plant_cap").forGetter(ChorusGenRule::capBlockId),
                    Codec.INT.fieldOf("aura_gain_per_block").forGetter(ChorusGenRule::auraGainPerBlock),
                    Codec.BOOL.optionalFieldOf("isSizeScaled", true).forGetter(ChorusGenRule::isSizeScaled),
                    ForgeRegistries.SOUND_EVENTS.getCodec().optionalFieldOf("break_sound", SoundEvents.CHORUS_FRUIT_TELEPORT).forGetter(ChorusGenRule::soundEvent),
                    Codec.FLOAT.optionalFieldOf("break_sound_volume", 0.5F).forGetter(ChorusGenRule::soundVolume),
                    Codec.FLOAT.optionalFieldOf("break_sound_pitch", 1F).forGetter(ChorusGenRule::soundPitch)
            ).apply(instance, ChorusGenRule::new)
    );

    public boolean isSoilTag() {
        return this.soilBlockId.right().isPresent();
    }
    public Block getBlockSoil() {
        return this.soilBlockId.left().orElse(null);
    }
    public TagKey<Block> getBlockSoilTag() {
        return this.soilBlockId.right().orElse(null);
    }
}
