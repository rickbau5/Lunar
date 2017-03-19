package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.entity.Entity;
import me.vrekt.lunar.entity.living.player.PlayerEntity;
import me.vrekt.lunar.server.Networking;

import java.io.*;
import java.util.List;

/**
 * Created by Rick on 3/18/2017.
 */
public class PlayerJoinPacket extends Packet {
    private PlayerEntity entityPlayer;

    public PlayerJoinPacket() {}

    public PlayerJoinPacket(PlayerEntity rogueLikePlayer) {
        this.entityPlayer = rogueLikePlayer;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        dos.writeInt(entityPlayer.getX());
        dos.writeInt(entityPlayer.getY());
        dos.writeInt(entityPlayer.getEntityID());

        return bos.toByteArray();
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        int x = dis.readInt();
        int y = dis.readInt();
        int eId = dis.readInt();
        PlayerEntity template = Networking.GAME_INSTANCE.getPlayer();
        List<Entity> worldEntities = Networking.GAME_INSTANCE.getWorld().getWorldEntities();
        for (Entity worldEntity : worldEntities) {
            if (worldEntity instanceof PlayerEntity) {
                getClient().addPacketToOutbound(new PlayerSpawnPacket((PlayerEntity) worldEntity));
            }
        }
        getClient().addPacketToOutbound(new PlayerSpawnPacket(Networking.GAME_INSTANCE.getPlayer()));
        PlayerEntity newPlayer = new PlayerEntity(Networking.GAME_INSTANCE.getWorld(), template.getTexture(), x, y, template.getWidth(), template.getHeight(), eId, 100, 0.0);

        Packet packet = new PlayerSpawnPacket(newPlayer);
        packet.setClient(getClient());
        Networking.sendToAllClients(packet);
        Networking.GAME_INSTANCE.getWorld().addEntity(newPlayer);
    }

    @Override
    public String getName() {
        return "PlayerJoinPacket";
    }

    @Override
    public byte getMarker() {
        return 1;
    }
}
