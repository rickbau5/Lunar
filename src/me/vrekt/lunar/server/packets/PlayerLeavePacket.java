package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.entity.Entity;
import me.vrekt.lunar.entity.living.player.NetworkedPlayer;
import me.vrekt.lunar.server.Networking;
import me.vrekt.lunar.server.RemoteClient;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Rick on 3/19/2017.
 */
public class PlayerLeavePacket extends Packet {
    private int clientId;
    private int playerId;

    public PlayerLeavePacket(RemoteClient client, NetworkedPlayer player) {
        this.clientId = client.getId();
        this.playerId = player.getEntityID();
    }

    public PlayerLeavePacket() {}

    @Override
    public byte[] encode() throws IOException {
        return PacketManager.withOutStreams((bos, dos) -> {
                    dos.writeInt(clientId);
                    dos.writeInt(playerId);
                });
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        this.clientId = dis.readInt();
        this.playerId = dis.readInt();

        Entity removePlayer = Networking.GAME_INSTANCE.getWorld().getEntity(playerId);
        Networking.GAME_INSTANCE.getWorld().queueEntityForRemoval(removePlayer);

        if (Networking.SERVER != null) {
            Networking.SERVER.removeClientById(clientId);
            Networking.sendToAllClients(this);
        }
    }

    @Override
    public String getName() {
        return "PlayerLeavePacket";
    }

    @Override
    public byte getMarker() {
        return 5;
    }
}
