package net.wkhan.naturesaura_plus.common.data;

import net.minecraft.core.BlockPos;

import java.util.Set;

public class MushroomTreeRitualTracker {
    public static final ThreadLocal<Set<BlockPos>> STEM_CACHE = new ThreadLocal<>();
    public static final ThreadLocal<Set<BlockPos>> CAP_CACHE = new ThreadLocal<>();
}
