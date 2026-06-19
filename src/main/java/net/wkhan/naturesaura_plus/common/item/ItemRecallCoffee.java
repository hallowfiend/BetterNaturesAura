package net.wkhan.naturesaura_plus.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemRecallCoffee extends Item {
    public ItemRecallCoffee(Properties p_41383_) {
        super(p_41383_);
        MinecraftForge.EVENT_BUS.register(new ItemAuraCoffeeEventListener());
    }

    public record ServerLevelVec3SpawnAnglePack(ServerLevel serverLevel, Vec3 pos, float angle) {}

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity entity) {
        if(level.isClientSide) return super.finishUsingItem(stack, level, entity);
        if(!(entity instanceof Player player)) return super.finishUsingItem(stack, level, entity);
        ServerLevelVec3SpawnAnglePack levelNVec3 = simulateRespawnCheck(player);
        Vec3 spawnPos = levelNVec3.pos;
        ServerLevel serverLevel = levelNVec3.serverLevel; //(serverLevel, spawnPos.x, spawnPos.y, spawnPos.z)
        float spawnAngle = levelNVec3.angle;
        ServerPlayer serverPlayer = (ServerPlayer) player;
        serverPlayer.teleportTo(serverLevel, spawnPos.x, spawnPos.y, spawnPos.z, spawnAngle, 0.0F);
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public @NotNull SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    public static class ItemAuraCoffeeEventListener {
        public ItemAuraCoffeeEventListener() {}

        @SubscribeEvent
        public void onTooltip(ItemTooltipEvent event) {
            if (event.getEntity() == null || !event.getEntity().level().isClientSide) return;
            ItemStack stack = event.getItemStack();
            if (stack.isEmpty()) return;
            if (!(stack.getItem() instanceof ItemRecallCoffee)) return;
            List<Component> tooltip = event.getToolTip();
            tooltip.add(Component.translatable("info.naturesaura_plus.aura_coffee").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        }

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
