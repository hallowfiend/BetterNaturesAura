package net.wkhan.naturesaura_plus.common.item;

import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemRecallCoffee extends Item {
    public ItemRecallCoffee(Properties p_41383_) {
        super(p_41383_);
    }

    public record ServerLevelVec3SpawnAnglePack(ServerLevel serverLevel, Vec3 pos, float angle) {}

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity) { //particles no work, no bueno
        if(level.isClientSide) return super.finishUsingItem(stack, level, entity);
        if(!(entity instanceof Player player)) return super.finishUsingItem(stack, level, entity);
        ServerLevelVec3SpawnAnglePack levelNVec3 = simulateRespawnCheck(player);
        Vec3 spawnPos = levelNVec3.pos;
        ServerLevel serverLevel = levelNVec3.serverLevel;
        float spawnAngle = levelNVec3.angle;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        int petTpRange = 2; //make config
        List<LivingEntity> pets = serverLevel.getNearbyEntities(LivingEntity.class,
                TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().range(petTpRange).selector(
                        target -> {
                            if (target instanceof TamableAnimal pet)
                                return serverPlayer.getUUID() == pet.getOwnerUUID();
                            else if (target instanceof AbstractHorse horse)
                                return serverPlayer.getUUID() == horse.getOwnerUUID();
                            return false;
                        }
                ), serverPlayer, serverPlayer.getBoundingBox().inflate(petTpRange));

        pets.forEach(pet -> {
            PacketHandler.sendToAllAround(serverLevel, BlockPos.containing(pet.position()), 32,
                    new PacketParticles((float)pet.getX(), (float)pet.getY(), (float)pet.getZ(), PacketParticles.Type.PET_REVIVER));
            pet.teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
        });

        PacketHandler.sendToAllAround(serverLevel, BlockPos.containing(serverPlayer.position()), 32,
                new PacketParticles((float)serverPlayer.getX(), (float)serverPlayer.getY(), (float)serverPlayer.getZ(), PacketParticles.Type.PET_REVIVER));
        serverPlayer.teleportTo(serverLevel, spawnPos.x, spawnPos.y, spawnPos.z, spawnAngle, 0.0F);

        PacketHandler.sendToAllAround(serverLevel, BlockPos.containing(spawnPos), 32,
                new PacketParticles((float) spawnPos.x,(float) spawnPos.y,(float) spawnPos.z, PacketParticles.Type.PET_REVIVER));

        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> toolTip, TooltipFlag p_41424_) {
        toolTip.add(Component.translatable("info.naturesaura_plus.aura_coffee")
                .setStyle(Style.EMPTY.withItalic(true).applyFormat(ChatFormatting.GRAY)));
    }

    public static ServerLevelVec3SpawnAnglePack simulateRespawnCheck(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return null;
        BlockPos respawnPos = serverPlayer.getRespawnPosition();
        float respawnAngle = serverPlayer.getRespawnAngle();
        boolean isSpawnForced = serverPlayer.isRespawnForced();
        ServerLevel defaultDimension = serverPlayer.getServer().overworld();
        ServerLevel targetDimension = serverPlayer.server.getLevel(serverPlayer.getRespawnDimension());

        if (respawnPos == null || targetDimension == null) return new ServerLevelVec3SpawnAnglePack
                (defaultDimension,Vec3.atBottomCenterOf(defaultDimension.getSharedSpawnPos()),respawnAngle);

        Optional<Vec3> actualSpawnPoint = Player.findRespawnPositionAndUseSpawnBlock
                (targetDimension, respawnPos, respawnAngle, isSpawnForced, true);
        return actualSpawnPoint.map(vec3 -> new ServerLevelVec3SpawnAnglePack(targetDimension, vec3, respawnAngle))
                .orElseGet(() -> new ServerLevelVec3SpawnAnglePack
                        (defaultDimension, Vec3.atBottomCenterOf(defaultDimension.getSharedSpawnPos()), respawnAngle));
    }
}
