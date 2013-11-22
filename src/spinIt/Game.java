package spinIt;

import game.essential.Input;
import game.essential.KeyListener;
import game.essential.Matrix2D;
import game.essential.ModelList;
import game.essential.Vector2d;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import spinIt.menu.MainMenu;

public class Game extends GameObject implements KeyListener {
	
	
	final public static int WIDTH = 1000;
	final public static int HEIGHT = 700;
	
	
	private int countDown = 60 * 80;
	
	public Game()
	{
		super(null);
		Input.addKeyListener(this);
	}
	
	public void addEarth(Earth earth)
	{
		earth.game = this;
		planets.add(earth);
		mainCamera.followList.add(earth);
		nextSatellite(earth);
	}

////////// MODEL COLLECTIONS (those are the game objects) { \\\\\\\\\\\
	//public ModelList<SomeGameObject> gameObjects = new ModelList<SomeGameObject>();
	
	ModelList<Planet> planets = new ModelList<Planet>();
	
	ModelList<Satellite> satellites = new ModelList<Satellite>();
	
	FireEngine fireEngine = new FireEngine();
	
	FpsCounter fpsC = new FpsCounter(10, 10);
	
	StarBackground bg = new StarBackground();
	
	
///////////// Update and rendering \\\\\\\\\\\\\\\
	

	public boolean locked = false;
	
	private boolean enterDown = false;
	private int enterCounter = 0;
	
	@Override
	public void update(long delta) {
		if (locked) return;
		countDown --;
		if (countDown == 0) {
			locked = true;
			Main.getInstance().endGame(this, ((Earth)planets.get(0)).score);
		}

		
		// End game if enter is down for more than 1 sec.
		if (enterDown) {
			enterCounter ++;
			if (enterCounter > 60) {
				// END GAME!!
				locked = true;
				Main.getInstance().endGame(this, ((Earth)planets.get(0)).score);
			}
		} else {
			enterCounter = 0;
		}
		
		planets.update(delta);
		satellites.update(delta);
		fireEngine.update(delta);
		
		// Check for collision between earth and satellites
		if (planets.size() > 0) {
			Earth earth = (Earth) planets.get(0);
			for (int i = satellites.size() - 1; i >= 0; i --) {
				Satellite s = satellites.get(i);
				Vector2d dist = earth.position.clone().substract(s.position);
				double radial = Math.sqrt(dist.x*dist.x + dist.y*dist.y);
				if (radial < earth.radius + s.radius + 20) {
					nextSatellite(s, earth);
				}
			}
		}
		// Cameras
		mainCamera.update();
		
		
		// Fps counter

		fpsC.update(delta);
		
	}
	
	public void nextSatellite(Satellite pickedUp, Earth earth)
	{
		earth.score += 10;
		satellites.remove(pickedUp);
		nextSatellite(earth);
	}
	private double satelliteDistance = 2000;
	private void nextSatellite(Earth earth)
	{
		// Add satellite some distance from the earth
		double angle = Math.random() * Math.PI * 2;
		Vector2d pos = new Vector2d(Math.cos(angle), Math.sin(angle)).times(satelliteDistance).add(earth.position);
		
		Satellite satellite = new Satellite(pos, this);
		satellites.add(satellite);
	}
	
	
	
	
	// Cameras
	Camera mainCamera = new Camera(WIDTH, HEIGHT);
	
	
	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix)
	{
		BufferedImage temp = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D tempG = temp.createGraphics();
		tempG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		matrix = mainCamera.getMatrix();
		
		bg.renderTo(temp, matrix);
		planets.renderTo(temp, matrix);
		satellites.renderTo(temp, matrix);
		fireEngine.renderTo(temp, matrix);
		fpsC.renderTo(temp, matrix);
		
		
		// Additional GUI stuff man
		{
			// Exit
			{
				drawString("Press and hold enter to end game", Game.WIDTH - 230, Game.HEIGHT - 40, tempG, MainMenu.font, true);
				// Make initial arrows.
				BufferedImage temp2 = new BufferedImage(100, 60, BufferedImage.TYPE_INT_ARGB);
				Graphics2D tempg = temp2.createGraphics();
				
				int offset = 0;
				// Arrow 1
				tempg.drawLine(10, 10, 20, 20);
				tempg.drawLine(20, 20, 10, 30);
				// Arrow 2
				offset += 5;
				tempg.drawLine(10 + offset, 10, 20 + offset, 20);
				tempg.drawLine(20 + offset, 20, 10 + offset, 30);
				
				applyOutline(temp2, 0xFF008020);
				applyOutline(temp2, 0xFF007060);
				applyOutline(temp2, 0xCC006050);
				applyOutline(temp2, 0x90005040);
				applyOutline(temp2, 0x60004030);
				applyOutline(temp2, 0x60003020);
				
				tempG.drawImage(temp2, Game.WIDTH - 50, Game.HEIGHT - 47, null);
			}
		}
		// ARROW
		{
			if (planets.size() > 0) {
				for (int i = satellites.size() - 1; i >= 0; i --) {
					Satellite s = satellites.get(i);
					renderArrow(temp, matrix, (Earth) planets.get(0), s);
				}
			}
		}
		
		// SCORE and countdown
		{
			if (planets.size() > 0) {
				Earth e = (Earth) planets.get(0);
				drawString("Score: " + e.score + "!", Game.WIDTH / 2, 10, tempG, font_b, true);
				drawString((int) (countDown / 60) + " seconds left!", Game.WIDTH / 2, 50, tempG, MainMenu.font, true);
				
			}
		}
		
		Graphics2D mainG = image.createGraphics();
		mainG.drawImage(temp, (int) position.x, (int) position.y, null);
	}
	private Font font_b = MainMenu.font_b.deriveFont(30f);
	
	public void addFireParticle(FireParticle p)
	{
		fireEngine.addParticle(p);
	}
	
	
	// DRAW
	private void renderArrow(BufferedImage image, Matrix2D matrix, Earth from, Satellite to)
	{
		Vector2d earthPos = matrix.transform(from.position);
		
		// render ummm like 500 px away from the earth?
		Vector2d distance = to.position.clone().substract(from.position);
		double angle = distance.angle();
		BufferedImage temp = new BufferedImage(300, 60, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = temp.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.drawLine(0, 30, 180, 30);
		g2.drawLine(180, 30, 170, 20);
		g2.drawLine(180, 30, 170, 40);
		
		if (angle < - Math.PI / 2 || angle > Math.PI / 2) {
			g2.translate(temp.getWidth() - 40, 0);
			g2.scale(-1, 1);
		}
		drawString("Satellite - " + (int)distance.length() + " km", 0, 0, g2, MainMenu.font, false);
		applyOutline(temp, 0xFF000000);
		applyOutline(temp, 0xFFBA0000);
		
		
		Graphics2D g = image.createGraphics();
		g.translate(earthPos.x, earthPos.y);
		g.rotate(angle);
		if (angle < - Math.PI / 2 || angle > Math.PI / 2) {
			g.scale(1, -1);
		}
		g.drawImage(temp, 0, 0, null);
	}
	
	
	
	private Rectangle2D drawString(String str, int x, int y, Graphics2D g,  Font f, boolean center)
	{
		Rectangle2D r = getBounds(str, f);
		double newWidth = r.getWidth();
		double newHeight= r.getHeight();

		g.setFont(f);
		int yOffset = 0;
		int beginning = 0;
		int end = 0;
		while (beginning < str.length()) {
			// New line.
			end = str.indexOf('\n', end + 1);
			if (end == -1) end = str.length();
			String stringToRender = str.substring(beginning, end);
			Rectangle2D lineRect = getBounds(stringToRender, f);
			newWidth = Math.max(newWidth, lineRect.getWidth());
			newHeight += lineRect.getHeight();
			g.drawString(stringToRender, (float) (x - r.getX() - (center? lineRect.getWidth()/2 : 0)), (float) (y - r.getY() + yOffset));
			yOffset += lineRect.getHeight();
			beginning = end;
		}
		r.setRect(x, y, newWidth, newHeight);
		return r;
	}
	private Rectangle2D getBounds(String str, Font f)
	{
		return f.getStringBounds(str, new FontRenderContext(null, false, false));
	}
	private void applyOutline(BufferedImage image, int ARGB)
	{
		int[] data = getImageData(image);
		int[] preData = new int[data.length];
		System.arraycopy(data, 0, preData, 0, data.length);
		int w = image.getWidth();
		int h = image.getHeight();
		int i = 0;
		
		// Algorithm: Loop through pixels. If the pixel has any horizontal/vertical neighbors, fill it with color
		for (int y = 0; y < h; y ++) {
			for (int x = 0; x < w; x ++) {
				if (preData[i] == 0) {
					// If top neighbor
					if (i > w && preData[i - w] != 0) {
						data[i] = ARGB;
					}
					// If bottom neighbor
					else if (i < data.length - w && preData[i + w] != 0) {
						data[i] = ARGB;
					}
					// If left neighbor
					else if (x != 0 && preData[i - 1] != 0) {
						data[i] = ARGB;
					}
					// If right neighbor
					else if (x != w - 1 && preData[i + 1] != 0) {
						data[i] = ARGB;
					}
				}

				i ++;
			}
		}
	}
	private int[] getImageData(BufferedImage image)
	{
		return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
	@Override
	public void keyDown(KeyEvent e) {
		if (e.getKeyCode() == Input.ENTER) {
			enterDown = true;
		}
	}

	@Override
	public void keyUp(KeyEvent e) {
		if (e.getKeyCode() == Input.ENTER) {
			enterDown = false;
		}
	}
	
}
