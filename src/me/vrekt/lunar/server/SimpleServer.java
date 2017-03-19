package me.vrekt.lunar.server;

import me.vrekt.lunar.server.packets.HandshakePacket;
import me.vrekt.lunar.server.packets.Packet;
import me.vrekt.lunar.server.packets.PlayerJoinPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rick on 3/18/2017.
 */
public class SimpleServer {
    class ClientListener implements Runnable {
        private final String address;
        private final int port;

        private ServerSocket serverSocket;
        private final List<RemoteClient> clients;
        private int clientID;

        private boolean open = true;

        public ClientListener(String address, int port) {
            this.address = address;
            this.port = port;

            clients = Collections.synchronizedList(new ArrayList<RemoteClient>());
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
                    newClient.addPacketToOutbound(new HandshakePacket(newClient));
                    clients.add(newClient);
                    System.out.println("Accepted client: " + (clientID - 1));
                }
            }
        }

        public synchronized boolean isOpen() {
            return open;
        }
    }

    private ClientListener clientListener;

    private NetworkedGame game;

    private final List<Packet> sendToClients;

    public SimpleServer(NetworkedGame game, String address, int port) {
        this.game = game;
        this.clientListener = new ClientListener(address, port);

        sendToClients = Collections.synchronizedList(new ArrayList<>());
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
                for (final RemoteClient client : clientListener.clients) {
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
                        for (final RemoteClient client : clientListener.clients) {
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
}
