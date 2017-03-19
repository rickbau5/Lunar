package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.utilities.CheckedBiConsumer;
import me.vrekt.lunar.utilities.CheckedConsumer;

import java.io.*;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Rick on 3/18/2017.
 */
public class PacketManager {
    private HashMap<Byte, Class<? extends Packet>> registeredPackets;

    private static byte nextPacketMarker = 0b0;

    private static PacketManager instance;

    public PacketManager() {
        registeredPackets = new HashMap<>();

        registerPackets();
    }

    private void registerPackets() {
        registerPacket(HandshakePacket.class, new HandshakePacket().getMarker());
        registerPacket(PlayerJoinPacket.class, new PlayerJoinPacket().getMarker());
        registerPacket(PlayerSpawnPacket.class, new PlayerSpawnPacket().getMarker());
        registerPacket(EntityMovementPacket.class, new EntityMovementPacket().getMarker());
        registerPacket(EntitySpawnPacket.class, new EntitySpawnPacket().getMarker());

        nextPacketMarker = (byte)registeredPackets.size();
    }

    public boolean registerPacket(Class<? extends Packet> clazz, byte marker) {
        if (registeredPackets.containsKey(marker)) {
            return false;
        }
        registeredPackets.put(marker, clazz);
        return true;
    }

    public Class<? extends Packet> getPacketForMarker(byte marker) {
        return registeredPackets.get(marker);
    }

    public static byte getNextAvailablePacketMarker() {
        byte ret = nextPacketMarker;
        nextPacketMarker++;
        return ret;
    }

    public static PacketManager instance() {
        if (instance == null) {
            instance = new PacketManager();
        }
        return instance;
    }

    public static byte[] withOutStreams(CheckedBiConsumer<ByteArrayOutputStream, DataOutputStream> consumer) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        consumer.accept(bos, dos);

        return bos.toByteArray();
    }

    public static void withInStreams(byte[] bytes, CheckedConsumer<DataInputStream> consumer) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        consumer.accept(new DataInputStream(bis));
    }
}
