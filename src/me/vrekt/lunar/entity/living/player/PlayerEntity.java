package me.vrekt.lunar.entity.living.player;

import me.vrekt.lunar.entity.living.LivingEntity;
import me.vrekt.lunar.location.Location;
import me.vrekt.lunar.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Rick on 3/18/2017.
 */
public class PlayerEntity extends LivingEntity {
    private final World world;

    public PlayerEntity(World world, int x, int y, int width, int height, int entityID, float health, double speed) {
        super(x, y, width, height, entityID, health, speed);
        this.world = world;
    }

    public PlayerEntity(World world, BufferedImage image, int x, int y, int width, int height, int entityID, float health, double speed) {
        super(image, x, y, width, height, entityID, health, speed);
        this.world = world;
    }

    @Override
    public void drawEntity(Graphics graphics) {
        Location loc = world.worldToScreenLocation(x, y);
        graphics.drawImage(texture, loc.getX(), loc.getY(), null);
    }

    @Override
    public void updateEntity() {

    }
}
