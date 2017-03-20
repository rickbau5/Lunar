package me.vrekt.lunar.entity.living.player;

import me.vrekt.lunar.server.NetworkedEntity;
import me.vrekt.lunar.server.packets.PacketManager;
import me.vrekt.lunar.world.World;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Rick on 3/19/2017.
 */
public abstract class NetworkedPlayer extends PlayerEntity implements NetworkedEntity {
    public NetworkedPlayer(World world, BufferedImage image, int x, int y, int width, int height, int entityID, float health, double speed) {
        super(world, image, x, y, width, height, entityID, health, speed);
    }

    @Override
    public byte[] serialize() throws IOException {
        return PacketManager.withOutStreams((bos, dos) -> {
            dos.writeInt(entityID);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeFloat(health);
        });
    }

    @Override
    public void deserialize(byte[] bytes) throws IOException {
        PacketManager.withInStreams(bytes, dis -> {
            setEntityID(dis.readInt());
            setX(dis.readInt());
            setY(dis.readInt());
            setHealth(dis.readFloat());
        });
    }
}
