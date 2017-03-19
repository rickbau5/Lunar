package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.server.Networking;
import me.vrekt.lunar.server.RemoteClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
public class HandshakePacket extends Packet {
    private RemoteClient remoteClient;

    public HandshakePacket() {}

    public HandshakePacket(RemoteClient client) {
        this.remoteClient = client;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(stream);
        dos.writeInt(remoteClient.getId());
        dos.writeInt(Networking.GAME_INSTANCE.getWorld().getNextEntityIdAndInc());
        return stream.toByteArray();
    }

    @Override
    public void decode(DataInputStream dis) throws IOException {
        int id = dis.readInt();
        if (Networking.REMOTE_CLIENT != null) {
            int id2 = Networking.REMOTE_CLIENT.getId();
            Networking.REMOTE_CLIENT.setId(id);
            Networking.GAME_INSTANCE.getPlayer().setEntityID(dis.readInt());
            System.out.printf("Id was %d, now is %d & eid is %d.\n", id2, id, Networking.GAME_INSTANCE.getPlayer().getEntityID());
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
