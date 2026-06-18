package net.wkhan.naturesaura_plus.common.data.auragen;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class SlimeGenRule {

    @SerializedName("entity")
    private String entityId;

    @SerializedName("aura")
    private int auraAmount;
    
    @SerializedName("color")
    private int slimeColor;
    
    @SerializedName("size_modifier")
    private float sizeModifier;
    
    @SerializedName("generation_timer_modifier")
    private int generationTimerModifier;
    
    @SerializedName("slime_size_scaling")
    private boolean slimeSizeScaling;

    @SerializedName("drop_loot")
    private boolean entityDropLoot;

    private transient EntityType<?> cachedSlimeEntity;
    private transient TagKey<EntityType<?>> cachedSlimeEntityTag;
    private transient boolean rulesResolved = false;
    private transient String sourceFile;

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public boolean resolve() {
        if (rulesResolved) return true;

        if (!slimeResolve()) {
            logError("Failed to resolve entity ID: '" + entityId + "'");
            return false;
        }

        this.rulesResolved = true;
        return true;
    }

    private boolean slimeResolve(){
        if (entityId == null || entityId.isEmpty()) {
            System.err.println("SlimeGen Rule Error: Missing Projectile ID'" + entityId + "'");
            return false;
        }
        if (entityId.startsWith("#")) {
            String tagId = entityId.substring(1);
            this.cachedSlimeEntityTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tagId));
            return true;
        }
        ResourceLocation loc = ResourceLocation.tryParse(entityId);
        if (loc != null && ForgeRegistries.ENTITY_TYPES.containsKey(loc)) {
            this.cachedSlimeEntity = ForgeRegistries.ENTITY_TYPES.getValue(loc);
            return true;
        } else {
            System.err.println("SlimeGen Rule Error: Invalid Entity ID '" + entityId + "'");
            return false;
        }
    }


    private void logError(String message) {
        if (sourceFile != null) {
            System.err.println("SlimeGen Rule Error in " + sourceFile + ": " + message);
        } else {
            System.err.println("SlimeGen Rule Error: (Invalid SourceFile? <- Seen when sourceFile resolves to null) " + message);
        }
    }


    public EntityType<?> getSlimeEntity() {
        return this.cachedSlimeEntity;
    }
    public TagKey<EntityType<?>> getSlimeEntityTag() {
        return this.cachedSlimeEntityTag;
    }
    public int getAuraAmount() {
        return auraAmount;
    }
    public int getColor() {
        return slimeColor;
    }
    public float getSizeModifier() {
        return sizeModifier;
    }
    public int getGenerationTimerModifier() {
        return generationTimerModifier;
    }
    public boolean isSlimeSizeScaling() {
        return slimeSizeScaling;
    }
    public boolean isEntityDropLoot() {
        return entityDropLoot;
    }

}
