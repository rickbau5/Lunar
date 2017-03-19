package me.vrekt.lunar.entity.living.player;

import me.vrekt.lunar.world.World;

import java.awt.image.BufferedImage;

/**
 * Created by Rick on 3/18/2017.
 */
public class ServerPlayer extends NetworkedPlayer {
    public ServerPlayer(World world, BufferedImage image, int x, int y, int width, int height, int entityID, float health, double speed) {
        super(world, image, x, y, width, height, entityID, health, speed);
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public boolean isServer() {
        return true;
    }

}
