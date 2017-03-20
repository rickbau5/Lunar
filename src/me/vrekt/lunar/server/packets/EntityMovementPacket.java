package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.entity.Entity;
import me.vrekt.lunar.server.Networking;

import java.io.*;

/**
 * Created by Rick on 3/18/2017.
 */
public class EntityMovementPacket extends Packet {
    private Entity entity;

    public EntityMovementPacket(Entity entity) {
        this.entity = entity;
    }

    public EntityMovementPacket() {}

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(entity.getEntityID());
        dos.writeInt(entity.getX());
        dos.writeInt(entity.getY());
        return bos.toByteArray();
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        int eid = dis.readInt();
        int x = dis.readInt();
        int y = dis.readInt();
        Entity entity = Networking.GAME_INSTANCE.getWorld().getEntity(eid);

        if (entity != null) {
            entity.setX(x);
            entity.setY(y);
            this.entity = entity;
        } else {
            System.err.printf("Couldn't find entity with id %d for %s.", eid, getName());
        }
    }

    @Override
    public byte getMarker() {
        return 3;
    }

    @Override
    public String getName() {
        return "EntityMovementPacket";
    }
}
