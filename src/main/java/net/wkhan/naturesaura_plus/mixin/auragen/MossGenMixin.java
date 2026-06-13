package net.wkhan.naturesaura_plus.mixin.auragen;

import de.ellpeck.naturesaura.api.misc.ILevelData;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityMossGenerator;
import de.ellpeck.naturesaura.misc.LevelData;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.wkhan.naturesaura_plus.NaturesAuraPlusUtils;
import net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static net.wkhan.naturesaura_plus.Config.*;
import static net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules.MOSS_GENERATIONS;

@Mixin(BlockEntityMossGenerator.class)
public abstract class MossGenMixin extends BlockEntityImpl {
    public MossGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Unique
    private final NaturesAuraPlusUtils.circularBuffer<Block> naturesaura_plus$mossMemory = new NaturesAuraPlusUtils.circularBuffer<>(mossGenMemorySize) {};

    //Magic Particles straight from Elpeck's code.
    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void naturesaura_plus$mossAuraGenerator(CallbackInfo ci) {
        ci.cancel();

        if (this.level.isClientSide) return;

        if (this.level.getGameTime() % 20L != 0L) {
            return;
        }

        LevelData data = (LevelData) ILevelData.getLevelData(this.level);
        List<BlockPos> possibleOffsets = new ArrayList<>();
        int range = mossGenRange;

        for(int x = -range; x <= range; ++x) {
            for(int y = -range; y <= range; ++y) {
                for(int z = -range; z <= range; ++z) {
                    BlockPos offset = this.worldPosition.offset(x, y, z);
                    boolean isRecent = data.recentlyConvertedMossStones.contains(offset);
                    Block block = this.level.getBlockState(offset).getBlock();
                    if (naturesaura_plus$mossMemory.countObject(block) != 0) return; //Maybe make this != 0 check data driven too
                    if (isRecent) {
                        data.recentlyConvertedMossStones.remove(offset);
                        continue;
                    }
                    if (MOSS_GENERATIONS.containsKey(block)) {
                        possibleOffsets.add(offset);
                    }
                }
            }
        }

        if (possibleOffsets.isEmpty()) {
            return;
        }

        BlockPos offset = possibleOffsets.get(this.level.random.nextInt(possibleOffsets.size()));
        BlockState state = this.level.getBlockState(offset);
        Block block = state.getBlock();
        naturesaura_plus$mossMemory.writeObject(block);
        AuraGenRules.deMossedBlockAuraAmountPair resultAuraAmountPair = MOSS_GENERATIONS.get(block);

        Block result = resultAuraAmountPair.deMossedBlock();
        int auraAmount = resultAuraAmountPair.auraAmount();
        if (!this.canGenerateRightNow(auraAmount)) { //The "cannot generate" particles don't actually exist in this version yet. I could add them in, but for now I haven't
//                PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PacketParticles.Type.CANNOT_GENERATE));
            return;
        }

        this.generateAura(auraAmount);
        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32, new PacketParticles((float)offset.getX(), (float)offset.getY(), (float)offset.getZ(), PacketParticles.Type.MOSS_GENERATOR));

        this.level.levelEvent(2001, offset, Block.getId(state));
        this.level.setBlockAndUpdate(offset, result.defaultBlockState());

    }

}
