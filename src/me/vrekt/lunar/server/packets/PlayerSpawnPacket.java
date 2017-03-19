package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.entity.living.player.PlayerEntity;
import me.vrekt.lunar.server.Networking;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
public class PlayerSpawnPacket extends Packet {
    private PlayerEntity entity;

    public PlayerSpawnPacket() {}

    public PlayerSpawnPacket(PlayerEntity entity) {
        this.entity = entity;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(entity.getEntityID());
        dos.writeInt(entity.getX());
        dos.writeInt(entity.getY());
        dos.writeFloat(entity.getHealth());
        return bos.toByteArray();
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        PlayerEntity template = Networking.GAME_INSTANCE.getPlayer();
        int eid = dis.readInt();
        int x = dis.readInt();
        int y = dis.readInt();
        float health = dis.readFloat();

        Networking.GAME_INSTANCE.getWorld().addEntity(
            new PlayerEntity(Networking.GAME_INSTANCE.getWorld(), template.getTexture(), x, y,
                    template.getWidth(), template.getHeight(), eid, health, 0.0)
        );
    }

    @Override
    public String getName() {
        return "PlayerSpawnPacket";
    }

    @Override
    public byte getMarker() {
        return 2;
    }
}
