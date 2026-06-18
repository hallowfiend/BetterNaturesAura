package net.wkhan.naturesaura_plus.common.network.packets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public record PacketFlowerGenUpdate(byte vitality, Block flower) {

    public PacketFlowerGenUpdate(FriendlyByteBuf buf) {
        this(buf.readByte(), BuiltInRegistries.BLOCK.byId(buf.readVarInt()));
    }

    public void encodePacket(FriendlyByteBuf buf) {
        buf.writeByte(vitality);
        buf.writeVarInt(BuiltInRegistries.BLOCK.getId(flower));
    }

    public void handlePacket(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(
                () -> {} //do stuff
        );
        context.setPacketHandled(true);
    }

}
