package net.wkhan.naturesaura_plus.mixin.auragen;

import de.ellpeck.naturesaura.blocks.tiles.BlockEntityChorusGenerator;
import de.ellpeck.naturesaura.blocks.tiles.BlockEntityImpl;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules;
import net.wkhan.naturesaura_plus.common.tag.ModTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Deque;
import java.util.Set;

import static net.wkhan.naturesaura_plus.Config.chorusGenRange;
import static net.wkhan.naturesaura_plus.NaturesAuraPlusUtils.crawlConnectedBlocks;
import static net.wkhan.naturesaura_plus.common.data.auragen.AuraGenRules.CHORUS_GENERATIONS;

@Mixin(BlockEntityChorusGenerator.class)
public abstract class ChorusGenMixin extends BlockEntityImpl {
    public ChorusGenMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow @Final private Deque<BlockPos> currentlyBreaking;
    @Shadow private int auraPerBlock;
    @Unique private String naturesaura_plus$chorusGenSoilBlock;

    //Customize tick method, and add the appropriate blocks to tags in tag generator

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$chorusGenTick(CallbackInfo ci) {
        ci.cancel();
        if (this.level.isClientSide()) return;
        if (this.level.getGameTime() % 5L != 0L) return;
        if (this.currentlyBreaking.isEmpty()) return;
        BlockPos pos = this.currentlyBreaking.removeLast();
        Block block = this.level.getBlockState(pos).getBlock();
        AuraGenRules.chorusValues chorusValues = CHORUS_GENERATIONS.get(ForgeRegistries.BLOCKS
                .getValue(ResourceLocation.parse(this.naturesaura_plus$chorusGenSoilBlock))); //might be laggy
        if (block != chorusValues.stemBlock() && block != chorusValues.capBlock()) {
            this.currentlyBreaking.clear();
            this.auraPerBlock = 0;
            this.naturesaura_plus$chorusGenSoilBlock = null;
            return;
        }
        PacketHandler.sendToAllAround(this.level, this.worldPosition, 32,
                new PacketParticles((float)this.worldPosition.getX(), (float)this.worldPosition.getY(), (float)this.worldPosition.getZ(),
                        PacketParticles.Type.CHORUS_GENERATOR, pos.getX(), pos.getY(), pos.getZ()));
        this.level.removeBlock(pos, false);
        this.level.playSound((Player)null, (double)this.worldPosition.getX() + (double)0.5F,
                (double)this.worldPosition.getY() + (double)0.5F, (double)this.worldPosition.getZ() + (double)0.5F,
                chorusValues.soundEvent(), SoundSource.BLOCKS, chorusValues.soundVolume(), chorusValues.soundPitch());
        this.generateAura(this.auraPerBlock);
        this.setChanged();
    }

    @Inject(
            method = "onRedstonePowerChange",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$chorusGenRedstonePower(int newPower, CallbackInfo ci) {
        ci.cancel();
        if (this.redstonePower > 0 || newPower <= 0 || !this.currentlyBreaking.isEmpty()) {
            super.onRedstonePowerChange(newPower);
            return;
        }
        int range = chorusGenRange;
        chorusClimb:
        for(int x = -range; x <= range; ++x) {
            for(int y = -range; y <= range; ++y) {
                for(int z = -range; z <= range; ++z) {
                    BlockPos offset = this.worldPosition.offset(x, y, z);
                    Block soil = this.level.getBlockState(offset).getBlock(); //npe potential?
                    System.out.println("soil: " + soil);
                    if (!CHORUS_GENERATIONS.containsKey(soil)) continue;
                    BlockState shoot = this.level.getBlockState(offset.above());
                    System.out.println("shoot: " + shoot);
                    if (!shoot.is(ModTags.Blocks.TOWERING_PLANT_STEM) || !shoot.is(ModTags.Blocks.TOWERING_PLANT_CAP)) continue;
                    AuraGenRules.chorusValues chorusValues = CHORUS_GENERATIONS.get(soil);
                    Set<BlockPos> plants = crawlConnectedBlocks(this.level,offset.above(),1000, //make cap a config
                            stem -> stem.getBlock() == chorusValues.stemBlock(),
                            cap -> cap.getBlock() == chorusValues.capBlock());
                    if (plants.size() <= 1) continue;
                    System.out.println("big plant");
                    this.currentlyBreaking.addAll(plants);
                    this.currentlyBreaking.addFirst(offset);
                    if (chorusValues.isSizeScaled()) this.auraPerBlock = plants.size() * chorusValues.auraGainPerBlock();
                    else this.auraPerBlock = chorusValues.auraGainPerBlock();
                    System.out.println(this.auraPerBlock);
                    this.naturesaura_plus$chorusGenSoilBlock = ForgeRegistries.BLOCKS.getKey(soil).toString();
                    this.setChanged();
                    break chorusClimb;
                }
            }
        }
    }

    @Inject(
            method = "writeNBT",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$writeNBTToChorusGen(CompoundTag compound, SaveType type, CallbackInfo ci) {
        ci.cancel();
        super.writeNBT(compound, type);
        if (type == SaveType.TILE) {
            ListTag list = new ListTag();

            for(BlockPos pos : this.currentlyBreaking) {
                list.add(NbtUtils.writeBlockPos(pos));
            }

            compound.put("breaking", list);
            compound.putInt("aura", this.auraPerBlock);
            if (!currentlyBreaking.isEmpty() &&
                    !compound.getString("soil_block").equals(this.naturesaura_plus$chorusGenSoilBlock))
                compound.put("soil_block", StringTag.valueOf(this.naturesaura_plus$chorusGenSoilBlock)); //check
        }
    }

    @Inject(
            method = "readNBT",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void naturesaura_plus$readNBTFromChorusGen(CompoundTag compound, SaveType type, CallbackInfo ci) {
        ci.cancel();
        super.readNBT(compound, type);
        if (type == SaveType.TILE) {
            this.currentlyBreaking.clear();
            ListTag list = compound.getList("breaking", 10);

            for(int i = 0; i < list.size(); ++i) {
                this.currentlyBreaking.add(NbtUtils.readBlockPos(list.getCompound(i)));
            }

            this.auraPerBlock = compound.getInt("aura");
            this.naturesaura_plus$chorusGenSoilBlock = compound.getString("soil_block"); //check
        }
    }

}
