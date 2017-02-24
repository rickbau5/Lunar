# Getting Started!
To start you want to initialize the Lunar class.
```java
Lunar lunar = new Lunar();
```

This class holds everything you will need for your game.
Alternatively you can create a direct object for Game aswell as the SoundManager and AssetManager.

```java
Game game = new Game(title, width, height, tickRate);
Game game = new Game(title, width, height, GameState, tickRate);

SoundManager sm = new SoundManager();
AssetManager am = new AssetManager();
```

Using the ` lunar ` object starting the game is simple.
```java
lunar.initializeGame(title, width, height, tickRate);
lunar.initializeGame(title, width, height, GameState, tickRate);

lunar.getGame().start();
```

Using the ` game ` object starting the game is just as easy.
```java
game.start();
```

# Parameters
title - This indicates the title of the window.
width, height - the dimensions of the window.

tickRate:
tickRate indicates how fast the game is drawn/updated. A good tickrate is 64 or above.

gameState:
this is not required when starting the game. Choosing to add this will not require you to add a gameState manually.

# GameStates

GameStates are the base of your game. Each GameState will hold two methods ` onTick(); ` and ` onDraw(Graphics graphics); `.
In your gameState you may keep track of worlds, players and other entities.
Heres an example:

```java
public class MainState extends GameState {
        private Player player;
	private Level1 world;

	public MainState(int priority) {
		super(priority);

		player = new Player(params);
		world = new Level1("world1", 600, 400);
		world.addEntity(player);

	}

	@Override
	public void onDraw(Graphics graphics) {
		world.onDraw(graphics);
	}

	@Override
	public void onTick() {
		player.updateEntity();
		player.updateBoundingBox();

		world.onTick();

	}
}
```

` priority ` indicates which GameState to draw/update first, this is useful for multiple GameStates.
0 will be the first to draw/update and then everything next.

# Entities

Entities are for example your player or an enemy. To create a custom entity start by extending ` LivingEntity `. LivingEntity indicates its 'living' and requires health/speed values.

Entity classes have the option to use a sprite, althought not required be sure to remember using built in draw functions for entities use the sprite.

```java
public class MyPlayer extends LivingEntity {
        public Player(int x, int y, int width, int height, int entityID, float health, double speed) {
		super(x, y, width, height, entityID, health, speed);
	}

	@Override
	public void drawEntity(Graphics graphics) {
	// lets draw a simple red box for our player.
		graphics.setColor(Color.red);
		graphics.fillRect(x, y, width, height);
	}

	/** Basic example of movement for our player! **
	@Override
	public void updateEntity() {
		boolean w, a, s, d;
		w = InputListener.isKeyDown(KeyEvent.VK_W);
		a = InputListener.isKeyDown(KeyEvent.VK_A);
		s = InputListener.isKeyDown(KeyEvent.VK_S);
		d = InputListener.isKeyDown(KeyEvent.VK_D);

		if (w) {x
			y -= speed;
		}

		if (a) {
			x -= speed;
		}

		if (s) {
			y += speed;
		}

		if (d) {
			x += speed;
		}
	}
}
```

Lets go over the fields.

x, y - the position of the player.
width, height - dimensions of the player.

entityID - in each world entities are managed by their ID. Think of this as a unique number for each entity.
If we have a player and a mob its up to you which ID theyre assigned.

health - the health of the entity.
speed - the speed of the entity.

# Sounds

To play a sound you want to create a new ` Sound ` object.
Each sound has an ID and the audio file.

```java
Sound sound = new Sound(int ID, File audio);
```

To play the sound simply get your SoundManager instance. Either via your ` lunar ` object or making a new instance.
```java
SoundManager sm = lunar.getSoundManager();

sm.playAudio(sound);
```
You can also keep track of all your game sounds within the SoundManager.
Alternatively if you don't want to create a new Sound object you can play the file directly.
```java
sm.playAudio(file);
```

# Input
Every game needs input and Lunar provides Mouse and Keyboard input.
For example, to check if a certain key is pressed:

```java
InputListener.isKeyPressed(KeyEvent.KEY);
```

MouseInput has many useful methods, as documented here:

```java
         /**
	 * Get the click coordinates.
	 * 
	 * @return
	 */
	public static Point getLastClick() {
		return lastClick;
	}

	/**
	 * Returns if the mouse is down.
	 * 
	 * @return
	 */
	public static boolean isMouseDown() {
		return isMouseDown;
	}

	/**
	 * Get the component the mouse entered. This can return null if the mouse
	 * exited the component.
	 * 
	 * @return
	 */
	public static Component getEnteredComponent() {
		return enteredComponent;
	}
```

# Worlds

Creating a custom world is very easy.
Start by extending ` World `.

```java
public class MyWorld extends World {

	public Level1(String name, int width, int height) {
		super(name, width, height);
	}
	
        @Override
	public void onDraw(Graphics graphics) {

	}

	@Override
	public void onTick() {

	}
}
```

Each class extending World can use many useful methods within the World class to make things easier.
These include:

adding entities
adding tiles, adding multile tiles at once in one method.
drawing all entities and tiles
getting an entity via their ID.
and many more!

# Tiles, Sprites
Tiles are essential for a textured game.

Each tile holds an ID, the texture, if its solid or not and their width/height.
If we want a tile for a Wall this can be done easily.

To start lets load our SpriteSheet.

```java
SpriteManager sm = new SpriteManager(SpriteManager.load("pathToSheet.png"));
```

Now we can easily get certain textures at certain points in our spritesheet.
For example if we have the Wall texture at 0, 0 and its 64x64 we can get it using:

```java
BufferedImage wall = sm.getSectionAt(0, 0, 64, 64);
```

Now we have our wall texture.
Lets now create the tile.

```java
Tile tile = new Tile(wall, 0, true);
```
As you can see we created the tile with our wall texture, an ID of 0 and the isSolid flag set to true.
Now we can add this to our world

```java
world.addTile(tile, x, y);
```
The AssetManager holds all of our game tiles. Simply get it using the ` lunar ` object or creating a new instance.

```java
AssetManager am = new AssetManager();
```

Lets store our tile:

```java
am.addTile(tile);
```

AssetManager includes the ability to get a tile via ID aswell as remove and add tiles.

# RayTracing

RayTracing can be used to find for example a wall or a certain tile.
If we wanted to check if there is a solid tile infront of the player we can!
Start by making new instance of the RayTracing.

```java
RayTracing rayTrace = new RayTracing();
```

Now lets check if there are any solid blocks infront of us. (right)

```java
TileInfo t = rayTrace.getNextSolidTile(myWorld, player.posX(), player.posY(), Direction.RIGHT, 64, 64);
```

If the TileInfo is NULL there is no solid tile found.
```java
if(t == null) {
  System.out.println("No tile found!);
} else {
   // do something
}
```

If rayTracing is taking too long you can cancel it via: ` rayTrace.stopRayTrace(); `.

