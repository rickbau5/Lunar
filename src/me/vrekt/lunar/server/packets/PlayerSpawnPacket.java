package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.entity.living.player.NetworkedPlayer;
import me.vrekt.lunar.entity.living.player.ServerPlayer;
import me.vrekt.lunar.server.Networking;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
public class PlayerSpawnPacket extends Packet {
    private NetworkedPlayer entity;

    public PlayerSpawnPacket() {}

    public PlayerSpawnPacket(NetworkedPlayer entity) {
        this.entity = entity;
    }

    @Override
    public byte[] encode() throws IOException {
        return entity.serialize();
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        NetworkedPlayer template = Networking.GAME_INSTANCE.getPlayer();
        ServerPlayer newPlayer = new ServerPlayer(
                Networking.GAME_INSTANCE.getWorld(), template.getTexture(), 0, 0,
                template.getWidth(), template.getHeight(), -1, 100, 0.0);

        newPlayer.deserialize(this.bytes);

        Networking.GAME_INSTANCE.getWorld().addEntity(newPlayer);
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
