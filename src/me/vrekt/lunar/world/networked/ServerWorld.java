package me.vrekt.lunar.world.networked;

import me.vrekt.lunar.entity.Entity;
import me.vrekt.lunar.server.Networked;
import me.vrekt.lunar.server.NetworkedEntity;
import me.vrekt.lunar.server.Networking;
import me.vrekt.lunar.server.packets.EntitySpawnPacket;
import me.vrekt.lunar.world.World;

/**
 * Created by Rick on 3/18/2017.
 */
public abstract class ServerWorld extends World implements Networked {
    public ServerWorld(String name, int width, int height, int tileWidth, int tileHeight) {
        super(name, width, height, tileWidth, tileHeight);
    }

    @Override
    public void addEntity(Entity entity) {
        if (entity instanceof NetworkedEntity) {
            super.addEntity(entity);
            Networking.sendToAllClients(new EntitySpawnPacket(((NetworkedEntity) entity)));
        } else {
            System.err.printf("Ignored non network entity id %d.", entity.getEntityID());
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public boolean isServer() {
        return true;
    }
}
