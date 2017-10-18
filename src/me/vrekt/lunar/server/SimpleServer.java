package me.vrekt.lunar.server;

import me.vrekt.lunar.entity.Entity;
import me.vrekt.lunar.server.annotations.SerializationHelper;
import me.vrekt.lunar.server.packets.EntitySpawnPacket;
import me.vrekt.lunar.server.packets.HandshakePacket;
import me.vrekt.lunar.server.packets.Packet;
import me.vrekt.lunar.server.packets.PlayerJoinPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by Rick on 3/18/2017.
 */
public class SimpleServer {
    class ClientListener implements Runnable {
        private final String address;
        private final int port;

        private ServerSocket serverSocket;
        private final Map<Integer, RemoteClient> clients;
        private int clientID;

        private boolean open = true;

        public ClientListener(String address, int port) {
            this.address = address;
            this.port = port;

            clients = Collections.synchronizedMap(new HashMap<Integer, RemoteClient>());
            clientID = 0;
        }

        private void initialize() {
            try {
                serverSocket = new ServerSocket(port);
                open = true;
            } catch (IOException e) {
                System.out.println("Couldn't initialize SERVER socket.");
                e.printStackTrace();
                throw new IllegalStateException("Server failed to initialize on port " + port);
            }
        }

        @Override
        public void run() {
            System.out.println("Server is accepting connections...");
            Socket clientSocket = null;
            while (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    System.out.println("Accept client interrupted.");
                    e.printStackTrace();
                    open = false;
                }

                if (clientSocket != null) {
                    RemoteClient newClient = new RemoteClient(game, clientID++, clientSocket);
                    newClient.addPacketToOutbound(new HandshakePacket(newClient, EntityRegistry.entityRegistry.entrySet()));
                    clients.put(newClient.getId(), newClient);
                    System.out.println("Accepted client: " + (clientID - 1));
                }
            }
        }

        public synchronized boolean isOpen() {
            return open;
        }

        public void stop() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Problem closing ClientListener socket.");
                e.printStackTrace();
            }
        }
    }

    private ClientListener clientListener;

    private NetworkedGame game;

    private final List<Packet> sendToClients;
    private final List<Integer> clientsToRemove;

    public SimpleServer(NetworkedGame game, String address, int port) {
        this.game = game;
        this.clientListener = new ClientListener(address, port);

        sendToClients = Collections.synchronizedList(new ArrayList<>());
        clientsToRemove = Collections.synchronizedList(new ArrayList<>());
    }

    public void sendPacketToClients(Packet packet) {
        sendToClients.add(packet);
    }

    public void start() {
        clientListener.initialize();
        new Thread(clientListener).start();

        ArrayList<Packet> packets = new ArrayList<>();
        while (clientListener.isOpen()) {
            synchronized (clientListener.clients) {
                if (!clientsToRemove.isEmpty()) {
                    clientsToRemove.forEach(id -> {
                        RemoteClient client = clientListener.clients.remove(id);
                        System.out.println("Removing client " + id);
                        if (client != null) {
                            client.destroy();
                        }
                    });
                    clientsToRemove.clear();
                }
                for (final RemoteClient client : clientListener.clients.values()) {
                    Packet packet = client.getNextInboundPacket();
                    if (packet != null) {
                        packets.add(packet);
                    }
                }
            }

            synchronized (sendToClients) {
                packets.addAll(sendToClients);
                sendToClients.clear();
            }

            if (packets.size() > 0) {
                synchronized (clientListener.clients) {
                    for (Packet packet : packets) {
                        if (packet instanceof PlayerJoinPacket) {
                            continue;
                        }
                        for (final RemoteClient client : clientListener.clients.values()) {
                            // packet.getClient() == null when the server is sending packets.
                            if (packet.getClient() == null || client.getId() != packet.getClient().getId()) {
                                client.addPacketToOutbound(packet);
                            }
                        }
                    }
                }
                packets.clear();
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
    }

    public void removeClientById(int clientId) {
        clientsToRemove.add(clientId);
    }

    public void stop() {
        int num = 0;
        synchronized (clientListener.clients) {
            for (final RemoteClient remoteClient : clientListener.clients.values()) {
                remoteClient.destroy();
                num++;
            }
        }
        System.out.println("Destroyed " + num + " clients.");
        clientListener.stop();
    }

    public void spawnEntity(Entity entity) {
        if (!EntityRegistry.getIdForEntity(entity.getClass()).isPresent()) {
            System.err.println("Cannot serialize entity, not registered in EntityRegistry: " + entity);
            return;
        }

        Packet spawnPacket = new EntitySpawnPacket(entity);
        try {
            spawnPacket.encode();
            Networking.sendToAll(spawnPacket);
            Networking.GAME_INSTANCE.getWorld().queueEntityForAdd(entity);
        } catch (IOException e) {
            System.err.println("Failed encoding EntitySpawnPacket.");
            e.printStackTrace();
        }
    }
}
