package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.entity.Entity;
import me.vrekt.lunar.server.EntityRegistry;
import me.vrekt.lunar.server.Networking;
import me.vrekt.lunar.server.annotations.SerializationHelper;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by Rick on 3/18/2017.
 */
public class EntitySpawnPacket extends Packet {
    private Entity entity;

    public EntitySpawnPacket(Entity entity) {
        this.entity = entity;
    }

    public EntitySpawnPacket() {}

    @Override
    public byte[] encode() throws IOException {
        Optional<byte[]> bytes = SerializationHelper.serializeClass(entity, dos -> {
            dos.writeInt(EntityRegistry.getIdForEntity(entity.getClass()).get());
            dos.writeInt(entity.getEntityID());
        });
        if (bytes.isPresent()) {
            this.bytes = bytes.get();
            return this.bytes;
        } else {
            System.err.println("Could not serialize entity: " + entity.getClass().getCanonicalName());
            return new byte[0];
        }
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        int registryId = dis.readInt();
        int eId = dis.readInt();

        Class<? extends Entity> entityClass = EntityRegistry.getEntityForId(registryId);
        Optional<Entity> entityOpt = SerializationHelper.deserializeClass(entityClass, dis);

        if (entityOpt.isPresent()) {
            Entity entity = entityOpt.get();
            entity.setEntityID(eId);
            entity.setWorld(Networking.GAME_INSTANCE.getWorld());
            Networking.GAME_INSTANCE.getWorld().queueEntityForAdd(entity);
        }
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
