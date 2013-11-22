package spinIt;

import game.essential.Drawable;
import game.essential.Vector2d;

public abstract class GameObject implements Drawable {

	protected Game game = null;
	
	/**
	 * @param game The game that this GameObject belongs to. If you don't need the GameObject to know it, just pass null.
	 */
	public GameObject(Game game)
	{
		this.game = game;
	}
	public Vector2d position = new Vector2d();
	public Vector2d velocity = new Vector2d();
	public Vector2d size = new Vector2d();
	
}
