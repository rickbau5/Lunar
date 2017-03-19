package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.server.NetworkedEntity;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
public class EntitySpawnPacket extends Packet {
    private NetworkedEntity entity;

    public EntitySpawnPacket(NetworkedEntity entity) {
        this.entity = entity;
    }

    public EntitySpawnPacket() {}

    @Override
    public byte[] encode() throws IOException {
        return entity.serialze();
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        entity.deserialize(bytes);
    }

    @Override
    public String getName() {
        return "EntitySpawnPacket";
    }

    @Override
    public byte getMarker() {
        return 4;
    }
}
