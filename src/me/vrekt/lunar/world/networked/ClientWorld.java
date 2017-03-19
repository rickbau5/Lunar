package me.vrekt.lunar.world.networked;

import me.vrekt.lunar.server.Networked;
import me.vrekt.lunar.world.World;

/**
 * Created by Rick on 3/19/2017.
 */
public abstract class ClientWorld extends World implements Networked {
    public ClientWorld(String name, int width, int height, int tileWidth, int tileHeight) {
        super(name, width, height, tileWidth, tileHeight);
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
