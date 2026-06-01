package net.wkhan.naturesaura_plus.common.data.auragen;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class FlowerGenRule {

    @SerializedName("block_to_convert")
    private String blockInputId;

    @SerializedName("aura_gain")
    private int auraAmount;

    @SerializedName("lucidity")
    private int lucidity;

    @SerializedName("obscurity")
    private int obscurity;

    @SerializedName("obscurity_scale")
    private int obscurityScale;

    private transient Block cachedBlockInput;
    private transient TagKey<Block> cachedBlockInputTag;
    private transient boolean rulesResolved = false;
    private transient String sourceFile;

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public boolean resolve() {
        if (rulesResolved) return true;

        if (!blockInputResolve()) {
            logError("Failed to resolve Block (Input Conversion By MossGen) ID: '" + blockInputId + "'");
            return false;
        }

        this.rulesResolved = true;
        return true;
    }

    private boolean blockInputResolve() {
        if (blockInputId == null || blockInputId.isEmpty()) {
            System.err.println("FlowerGen Rule Error: Missing Block ID'" + blockInputId + "'");
            return false;
        }
        if (blockInputId.startsWith("#")) {
            String tagId = blockInputId.substring(1);
            this.cachedBlockInputTag = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(tagId));
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(blockInputId);
        if (loc != null && ForgeRegistries.BLOCKS.containsKey(loc)) {
            this.cachedBlockInput = ForgeRegistries.BLOCKS.getValue(loc);
            return true;
        } else {
            System.err.println("FlowerGen Rule Error: Invalid Block ID '" + blockInputId + "'");
            return false;
        }
    }

    private void logError(String message) {
        if (sourceFile != null) {
            System.err.println("MossGen Rule Error in " + sourceFile + ": " + message);
        } else {
            System.err.println("MossGen Rule Error: (Invalid SourceFile? <- Seen when sourceFile resolves to null) " + message);
        }
    }


    public Block getBlockInput() {
        return this.cachedBlockInput;
    }
    public TagKey<Block> getBlockInputTag() {
        return this.cachedBlockInputTag;
    }
    public int getAuraAmount() {
        return auraAmount;
    }
    public int getLucidity() {
        return lucidity;
    }
    public int getObscurity() {
        return obscurity;
    }
    public int getObscurityScale() {
        return obscurityScale;
    }
}

