package me.vrekt.lunar.server;

import me.vrekt.lunar.server.packets.Packet;
import me.vrekt.lunar.server.packets.PacketManager;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Rick on 3/18/2017.
 */
public class RemoteClient implements Runnable {
    private final NetworkedGame game;
    private int id;
    private Socket socket;

    private Queue<Packet> inboundPacketQueue;
    private Queue<Packet> outboundPacketQueue;

    public RemoteClient(NetworkedGame game, int id, Socket clientSocket) {
        this.game = game;
        this.id = id;
        this.socket = clientSocket;

        inboundPacketQueue = new ArrayBlockingQueue<>(100);
        outboundPacketQueue = new ArrayBlockingQueue<>(100);

        new Thread(this).start();

    }

    public void destroy() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                System.out.println("Couldn't close client connection " + id);
                e.printStackTrace();
            }
        }
    }

    public NetworkedGame getGame() {
        return game;
    }

    public synchronized boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public Packet getNextInboundPacket() {
        return inboundPacketQueue.poll();
    }

    public synchronized boolean addPacketToOutbound(Packet packet) {
        return outboundPacketQueue.offer(packet);
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            while (isConnected()) {
                if (inputStream.available() > 0) {
                    int marker = inputStream.read();
                    if (marker != -1) {
                        Packet packet = null;
                        try {
                            Class<? extends Packet> clazz = PacketManager.instance().getPacketForMarker((byte) marker);
                            if (clazz == null) {
                                System.err.println("Couldn't find class for packet with marker " + marker);
                            } else {
                                packet = clazz.newInstance();
                            }
                        } catch (InstantiationException e) {
                            System.out.println("Couldn't instantiate class for packet " + marker + ". Missing default constructor?");
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        if (packet != null) {
                            int size = inputStream.read();
                            byte[] bytes = new byte[size];
                            int read = 0;
                            while (read < size) {
                                read = inputStream.read(bytes, read, size - read);
                            }

                            packet.setClient(this);
                            packet.decode(new DataInputStream(new ByteArrayInputStream(bytes)));

                            inboundPacketQueue.offer(packet);
                            System.out.println(String.format("RemoteClient %d read packet %s.", id, packet.getName()));
                        } else {
                            System.out.println("No packet found for " + marker);
                            int size = inputStream.read();
                            byte[] garbage = new byte[size];
                            int read = 0;
                            while (read < size) {
                                read = inputStream.read(garbage, read, size - read);
                            }
                            System.out.printf("Discarded %d bytes.\n", read);
                        }
                    }
                }

                Packet outbound = outboundPacketQueue.poll();
                if (outbound != null) {
                    try {
                        byte[] bytes = outbound.encode();
                        outputStream.write(outbound.getMarker());
                        outputStream.write(bytes.length);
                        outputStream.write(bytes);
                        System.out.println(String.format("RemoteClient %d wrote packet %s.", id, outbound.getName()));
                    } catch (NullPointerException ex) {
                        System.out.printf("NPE for RemoteClient %d for packet %s.\n", id, outbound.getName());
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Exception encountered in RemoteClient loop of client " + id);
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
