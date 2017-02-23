package me.vrekt.lunar.tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tile {

	private BufferedImage texture;
	private int width, height;

	private boolean isSolid = false;
	private int ID;

	/**
	 * Initialize the tile.
	 * 
	 * @param ID
	 * @param texture
	 */
	public Tile(BufferedImage texture, int ID, boolean isSolid) {
		this.texture = texture;

		this.width = texture.getWidth();
		this.height = texture.getHeight();

		this.isSolid = isSolid;
		this.ID = ID;
	}

	/**
	 * Initialize the tile.
	 * 
	 * @param ID
	 * @param texture
	 * @param width
	 * @param height
	 */
	public Tile(BufferedImage texture, int ID, int width, int height, boolean isSolid) {
		this.texture = texture;

		this.width = width;
		this.height = height;

		this.isSolid = isSolid;
		this.ID = ID;

	}

	/**
	 * Get the texture.
	 * 
	 * @return texture
	 */
	public BufferedImage getTexture() {
		return texture;
	}

	/**
	 * Get the width
	 * 
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height.
	 * 
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the ID.
	 * 
	 * @return
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Return if the tile is solid.
	 * 
	 * @return
	 */
	public boolean isSolid() {
		return isSolid;
	}

	/**
	 * Set if the tile is solid or not.
	 * 
	 * @param isSolid
	 */
	public void setSolid(boolean isSolid) {
		this.isSolid = isSolid;
	}

	/**
	 * Draw the tile.
	 * 
	 * @param graphics
	 * @param x
	 * @param y
	 */
	public void drawTile(Graphics graphics, int x, int y) {
		graphics.drawImage(texture, x, y, null);
	}

}
