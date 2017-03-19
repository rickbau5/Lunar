package me.vrekt.lunar.server;

import me.vrekt.lunar.entity.living.player.PlayerEntity;
import me.vrekt.lunar.world.World;

/**
 * Created by Rick on 3/18/2017.
 */
public abstract class NetworkedGame implements Networked {
    public abstract World getWorld();
    public abstract PlayerEntity getPlayer();
}
