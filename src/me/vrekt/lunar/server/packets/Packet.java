package me.vrekt.lunar.server.packets;

import me.vrekt.lunar.server.RemoteClient;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
public abstract class Packet {
    protected RemoteClient client;

    public Packet() {}

    public abstract byte[] encode() throws IOException;
    public abstract void decode(DataInputStream dis) throws IOException;

    public void setClient(RemoteClient client) {
        this.client = client;
    }

    public RemoteClient getClient() {
        return client;
    }

    public abstract String getName();

    public abstract byte getMarker();
}
