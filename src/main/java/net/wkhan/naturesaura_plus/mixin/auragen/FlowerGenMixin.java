package net.wkhan.naturesaura_plus.mixin.auragen;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityFlowerGenerator;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticleStream;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;
import net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.in;
import static net.wkhan.naturesaura_plus.Config.*;
import static net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules.FLOWER_GENERATIONS;
import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.circularBuffer;

@Mixin(BlockEntityFlowerGenerator.class)
public class FlowerGenMixin extends BlockEntityImpl {
    public FlowerGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique private int naturesaura_plus$vitality = 100;

    @Unique private final circularBuffer<Block> naturesaura_plus$flowerMemory = new circularBuffer<>(flowerGenMemorySize) {};

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$mossAuraGenerator(CallbackInfo ci) {
        ci.cancel();
        Level level = this.level;
        if (level == null) return;
        if (level.isClientSide || level.getGameTime() % 10 != 0L) return;
        List<BlockPos> possible = new ArrayList<>();
        int range = flowerGenRange;

        for(int x = -range; x <= range; ++x) {
            for(int y = -1; y <= 1; ++y) {
                for(int z = -range; z <= range; ++z) {
                    BlockPos offset = this.worldPosition.offset(x, y, z);
                    Block block = level.getBlockState(offset).getBlock();
                    if (FLOWER_GENERATIONS.containsKey(block)) {
                        possible.add(offset);
                    }
                }
            }
        }

        if (possible.isEmpty()) {
            return;
        }

        BlockPos pos = possible.get(level.random.nextInt(possible.size()));
        Block block = level.getBlockState(pos).getBlock();
        AuraGenRules.flowerValues stats = FLOWER_GENERATIONS.get(block);
        int lucidity = stats.lucidity();
        int repeatFlower = naturesaura_plus$flowerMemory.countObject(block);
        if (this.naturesaura_plus$vitality != 100 && lucidity != 0 && repeatFlower == 0) {
            this.naturesaura_plus$vitality = Math.min(this.naturesaura_plus$vitality + lucidity,100);
        }
        else if (this.naturesaura_plus$vitality != 0) {
            int obscurity = (int) (stats.obscurity() * Math.pow(stats.obscurityScale(),repeatFlower));
            this.naturesaura_plus$vitality = Math.max(this.naturesaura_plus$vitality - obscurity, 0);
        }
        naturesaura_plus$flowerMemory.writeObject(block);
        int auraAmount = (int) (stats.auraAmount() * (1 - Math.pow(((double) this.naturesaura_plus$vitality/flowerGenVitalityFloor),flowerGenPowFactor)));
        int toAdd = Math.max(0, auraAmount);
        if (toAdd > 0) {
            if (IAuraType.forLevel(level).isSimilar(NaturesAuraAPI.TYPE_OVERWORLD) && this.canGenerateRightNow(toAdd)) {
                this.generateAura(toAdd);
            } else {
                toAdd = 0;
            }
        }
        level.removeBlock(pos, false);
        int color = Helper.blendColors(6081584, 15023126, (float)toAdd / (float)auraAmount);
        if (toAdd > 0) {
            for(int i = level.random.nextInt(5) + 5; i >= 0; --i) {
                PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticleStream((float)pos.getX() + 0.25F + level.random.nextFloat() * 0.5F, (float)pos.getY() + 0.25F + level.random.nextFloat() * 0.5F, (float)pos.getZ() + 0.25F + level.random.nextFloat() * 0.5F, (float)this.worldPosition.getX() + 0.25F + level.random.nextFloat() * 0.5F, (float)this.worldPosition.getY() + 0.25F + level.random.nextFloat() * 0.5F, (float)this.worldPosition.getZ() + 0.25F + level.random.nextFloat() * 0.5F, level.random.nextFloat() * 0.02F + 0.1F, color, 1.0F));
            }

            PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticles((float)this.worldPosition.getX(), (float)this.worldPosition.getY(), (float)this.worldPosition.getZ(), PacketParticles.Type.FLOWER_GEN_AURA_CREATION, new int[0]));
        }

        PacketHandler.sendToAllAround(level, this.worldPosition, 32, new PacketParticles((float)pos.getX(), (float)pos.getY(), (float)pos.getZ(), PacketParticles.Type.FLOWER_GEN_CONSUME, new int[]{color}));

    }
}
