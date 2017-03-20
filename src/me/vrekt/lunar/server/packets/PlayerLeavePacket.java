package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.server.Networking;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Rick on 3/19/2017.
 */
public class PlayerLeavePacket extends Packet {
    @Override
    public byte[] encode() throws IOException {
        return PacketManager.withOutStreams((bos, dos) -> {
                    dos.writeInt(Networking.REMOTE_CLIENT.getId());
                    dos.writeInt(Networking.GAME_INSTANCE.getPlayer().getEntityID());
                });
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        int rid = dis.readInt();
        int eid = dis.readInt();

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
