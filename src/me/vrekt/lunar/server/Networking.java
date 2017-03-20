package me.vrekt.lunar.server;

import me.vrekt.lunar.server.packets.Packet;

/**
 * Created by Rick on 3/18/2017.
 */
public final class Networking {
    public static NetworkedGame GAME_INSTANCE;

    /**
     * RemoteClient is null on game host (server). For clients
     */
    public static RemoteClient REMOTE_CLIENT;

    /**
     * Server is not null on the game host, null on all clients.
     */
    public static SimpleServer SERVER;

    public static boolean sendToAllClients(Packet packet) {
        if (REMOTE_CLIENT != null) {
            REMOTE_CLIENT.addPacketToOutbound(packet);
            return true;
        } else if (GAME_INSTANCE != null && GAME_INSTANCE.isServer() && SERVER != null) {
            SERVER.sendPacketToClients(packet);
            return true;
        }
        return false;
    }
}
