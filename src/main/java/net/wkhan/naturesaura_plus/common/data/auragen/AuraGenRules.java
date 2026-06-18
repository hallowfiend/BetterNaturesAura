package net.wkhan.naturesaura_plus.common.data.auragen;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class AuraGenRules {

    public record deMossedBlockAuraAmountPair(Block deMossedBlock, int auraAmount) {}
    public static final Map<Block, deMossedBlockAuraAmountPair> MOSS_GENERATIONS = new HashMap<>();

    public record flowerValues(int auraAmount, byte lucidity, byte obscurity, float obscurityScale) {}
    public static final Map<Block, flowerValues> FLOWER_GENERATIONS = new HashMap<>();

    public record slimeValues(int auraAmount, int slimeColor, int generationTimerModifier, float sizeModifier, boolean doSlimeSizeScaling, boolean doEntityDropLoot) {}
    public static final Map<EntityType<?>, slimeValues> SLIME_GENERATIONS = new HashMap<>();

    public static HashMap<String, Integer> auraRulesCount() {
        HashMap<String, Integer> rulesCount = new HashMap<>();
        rulesCount.put("Projectile Generations", NaturesAuraAPI.PROJECTILE_GENERATIONS.size());
        rulesCount.put("Moss Generations", MOSS_GENERATIONS.size());
        rulesCount.put("Flower Generations", FLOWER_GENERATIONS.size());
        rulesCount.put("Slime Generations", SLIME_GENERATIONS.size());
        return rulesCount;
    }

    public static void auraGenerationClear() {
        NaturesAuraAPI.PROJECTILE_GENERATIONS.clear();
        MOSS_GENERATIONS.clear();
        FLOWER_GENERATIONS.clear();
        SLIME_GENERATIONS.clear();
    }

    public static void addProjectileGeneration(ProjectileGenRule rule) {
        int auraAmount = rule.auraAmount();
        EntityType<?> projectile = rule.getProjectile();

        if (projectile != null) {
                NaturesAuraAPI.PROJECTILE_GENERATIONS.put(projectile, auraAmount);
                return;
        }

        TagKey<EntityType<?>> projectileTag = rule.getProjectileTag();
        if (projectileTag != null) {
                ForgeRegistries.ENTITY_TYPES.getValues().stream()
                        .filter(e -> e.is(projectileTag))
                        .forEach(e -> NaturesAuraAPI.PROJECTILE_GENERATIONS.put(e, auraAmount));
        }
    } //refactored

    public static void addMossGeneration(MossGenRule rule) {
        int auraAmount = rule.auraAmount();
        Block mossBlock = rule.getBlockInput();
        TagKey<Block> mossBlockTag = rule.getBlockInputTag();
        Block deMossedBlock = rule.getBlockOutput();

        if (mossBlock != null) {
            MOSS_GENERATIONS.put(mossBlock, new deMossedBlockAuraAmountPair(deMossedBlock, auraAmount));
            return;
        }

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.defaultBlockState().is(mossBlockTag))
                .forEach(b -> MOSS_GENERATIONS.put(b, new deMossedBlockAuraAmountPair(deMossedBlock, auraAmount)));
    }

    public static void addFlowerGeneration(FlowerGenRule rule) {
        Block flowerBlock = rule.getBlockInput();
        TagKey<Block> flowerBlockTag = rule.getBlockInputTag();
        if (flowerBlock == null & flowerBlockTag == null) return;
        int auraAmount = rule.auraAmount();
        byte lucidity = rule.lucidity();
        byte obscurity = rule.obscurity();
        float obscurityScale = rule.obscurityScale();

        if(flowerBlock != null) {
            FLOWER_GENERATIONS.put(flowerBlock, new flowerValues(auraAmount, lucidity, obscurity, obscurityScale));
            return;
        }

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(b -> b.defaultBlockState().is(flowerBlockTag))
                .forEach(b -> FLOWER_GENERATIONS.put(b, new flowerValues(auraAmount, lucidity, obscurity, obscurityScale)));
    } //refactored

    public static void addSlimeGeneration(SlimeGenRule rule) {
        int auraAmount = rule.auraAmount();
        int slimeColor = rule.slimeColor();
        int generationTimerModifier = rule.generationTimerModifier();
        float sizeModifier = rule.sizeModifier();
        boolean doSlimeSizeScaling = rule.doSlimeSizeScaling();
        boolean doEntityDropLoot = rule.doEntityDropLoot();
        EntityType<?> slime = rule.getEntity();

        if (slime != null) {
            SLIME_GENERATIONS.put(slime,
                    new slimeValues(auraAmount,slimeColor,generationTimerModifier,sizeModifier,doSlimeSizeScaling,doEntityDropLoot));
            return;
        }

        TagKey<EntityType<?>> slimeTag = rule.getEntityTag();
        if (slimeTag != null) {
            ForgeRegistries.ENTITY_TYPES.getValues().stream()
                    .filter(e -> e.is(slimeTag))
                    .forEach(e -> SLIME_GENERATIONS.put(e,
                            new slimeValues(auraAmount,slimeColor,generationTimerModifier, sizeModifier,doSlimeSizeScaling,doEntityDropLoot))
            );
        }
    } //refactored
}

