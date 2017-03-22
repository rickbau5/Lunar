package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.entity.Entity;
import me.vrekt.lunar.server.EntityRegistry;
import me.vrekt.lunar.server.Networking;
import me.vrekt.lunar.server.RemoteClient;
import me.vrekt.lunar.server.annotations.SerializationHelper;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Rick on 3/18/2017.
 */
public class HandshakePacket extends Packet {
    private RemoteClient remoteClient;
    private HashMap<Integer, String> entitiesMap;

    public HandshakePacket() {}

    public HandshakePacket(RemoteClient client, Set<Map.Entry<Integer, Class<? extends Entity>>> entities) {
        this.remoteClient = client;
        entitiesMap = new HashMap<>(entities.size());
        for (Map.Entry<Integer, Class<? extends Entity>> entry : entities) {
            entitiesMap.put(entry.getKey(), entry.getValue().getCanonicalName());
        }
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(stream);
        dos.writeInt(remoteClient.getId());
        dos.writeInt(Networking.GAME_INSTANCE.getWorld().getNextEntityIdAndInc());
        writeEntityRegistry(dos);
        writeEntities(dos);
        return stream.toByteArray();
    }

    private void writeEntityRegistry(DataOutputStream dos) throws IOException {
        dos.writeInt(entitiesMap.size());
        for (Map.Entry<Integer, String> entry : entitiesMap.entrySet()) {
            dos.writeInt(entry.getKey());
            byte[] bytes = entry.getValue().getBytes();
            dos.writeInt(bytes.length);
            dos.write(bytes);
        }
    }

    private void writeEntities(DataOutputStream dos) throws IOException {
        List<Entity> entities = Networking.GAME_INSTANCE.getWorld().getWorldEntities();
        List<Entity> serializable = new ArrayList<>(entities.size());
        for (Entity entity : entities) {
            if (SerializationHelper.isSerializable(entity)) {
                serializable.add(entity);
            }
        }

        dos.writeInt(serializable.size());

        for (Entity entity : serializable) {
            Optional<byte[]> bytes = SerializationHelper.serializeClass(entity, d -> {
                d.writeInt(EntityRegistry.getIdForEntity(entity.getClass()).get());
                d.writeInt(entity.getEntityID());
            });

            if (bytes.isPresent()) {
                dos.write(bytes.get());
            } else {
                throw new IllegalStateException("Couldn't serialize entity: " + entity.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        int id = dis.readInt();
        if (Networking.REMOTE_CLIENT != null) {
            Networking.REMOTE_CLIENT.setId(id);
            Networking.GAME_INSTANCE.getPlayer().setEntityID(dis.readInt());

            int numEntities = dis.readInt();
            for (int i = 0; i < numEntities; i++) {
                int eid = dis.readInt();
                int estrlen = dis.readInt();
                byte[] estrbytes = new byte[estrlen];
                dis.read(estrbytes, 0, estrlen);
                String estr = new String(estrbytes, 0, estrlen);
                EntityRegistry.updateEntityId(eid, estr);
            }
            EntityRegistry.printEntityRegistry();

            int num = dis.readInt();
            for (int i = 0; i < num; i++) {
                int regId = dis.readInt();
                Class<? extends Entity> entityClass = EntityRegistry.getEntityForId(regId);
                Optional<Object> opt = SerializationHelper.deserializeClass(entityClass, dis, (dataInputStream, aClass) -> {
                    Entity entity = (Entity)aClass;
                    entity.setEntityID(dataInputStream.readInt());
                });

                if (opt.isPresent()) {
                    Entity entity = (Entity)opt.get();
                    entity.setWorld(Networking.GAME_INSTANCE.getWorld());
                    Networking.GAME_INSTANCE.getWorld().queueEntityForAdd(entity);
                } else {
                    throw new IllegalStateException("Failed decoding entity!");
                }
            }

        }
    }

    @Override
    public String getName() {
        return "HandshakePacket";
    }

    @Override
    public byte getMarker() {
        return 0;
    }
}
