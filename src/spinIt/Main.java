package spinIt;

import game.essential.Drawable;
import game.essential.GameLoop;
import game.essential.Input;
import game.essential.Monitor;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;

import spinIt.menu.MainMenu;

public class Main extends GameLoop {
	
	
	
	Monitor display = new Monitor(Game.WIDTH, Game.HEIGHT);
	
	private int highScore = 0;
	public int getHighScore()
	{
		return highScore;
	}
	
	public Main()
	{
		JFrame window = new JFrame("Spin it!");
		window.add(display);
		window.setSize(Game.WIDTH + 6, Game.HEIGHT + 27);
		window.setVisible(true);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//
		
		display.models.add(new MainMenu());
	}
	
	@Override
	protected void update(long delta) {
		System.out.println(display.models.size());
		// Move if any tweening
		for (int i = tweenOuts.size() - 1; i >= 0; i --) {
			GameObject o = tweenOuts.get(i);
			// Make it go to the left yes... follow tweenIn??
			if (tweenIn != null) o.position.x = tweenIn.position.x - Game.WIDTH;
			// ++
			counters.set(i, counters.get(i) + 1);
			if (counters.get(i) > 200) {
				display.models.remove(o);
				tweenOuts.remove(o);
				counters.remove(i);
			}
		}
		if (tweenIn != null) {
			// Approach 0, 0
			tweenIn.velocity.x -= tweenIn.position.x / 20;
			tweenIn.velocity.x *= 0.8;
			tweenIn.position.x += tweenIn.velocity.x;
		}
		
		// UPDATE
		for (int i = display.models.size() - 1; i >= 0; i --) {
			Drawable a = display.models.get(i);
			a.update(delta);
		}
		
		// RENDER
		display.clear(Color.BLACK);
		display.render();
	}

	private ArrayList<Integer> counters = new ArrayList<Integer>();
	private ArrayList<GameObject> tweenOuts =new ArrayList<GameObject>();
	private GameObject tweenIn = null;
	
	private void addTweenOut(GameObject o)
	{
		tweenOuts.add(o);
		counters.add(40);
	}
	
	public void newGame(Earth earth, MainMenu menu)
	{
		Game g = new Game();
		Earth newEarth = new Earth(earth.radius, 0, g);
		for (Thruster t : earth.thrusters) {
			Thruster newT = new Thruster(g);
			newT.colour = t.colour;
			newT.angleOffset = t.angleOffset;
			newT.setKey(t.getKey());
			newEarth.thrusters.add(newT);
		}
		g.addEarth(newEarth);
		addTweenOut(menu);
		tweenIn = g;
		menu.locked = true;
		g.position.x = Game.WIDTH;
		display.models.add(g);
	}
	public void endGame(Game game, int score)
	{
		MainMenu menu = new MainMenu(score);
		menu.position.x = Game.WIDTH;
		addTweenOut(game);
		tweenIn = menu;
		display.models.add(menu);
		
		if (score > highScore) {
			highScore = score;
		}
	}
	
	
	
	
	public static Main gameloop = new Main();
	public static void main(String[] args)
	{
		Input.initialize(gameloop.display);
		gameloop.run();
	}
	
	public static Main getInstance()
	{
		return gameloop;
	}
	
	
	
}
