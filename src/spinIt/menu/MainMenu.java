package spinIt.menu;

import game.essential.Input;
import game.essential.KeyListener;
import game.essential.Matrix2D;
import game.essential.Vector2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.InputStream;
import java.util.ArrayList;

import spinIt.Colour;
import spinIt.Earth;
import spinIt.Game;
import spinIt.GameObject;
import spinIt.Key;
import spinIt.Main;
import spinIt.Thruster;

public class MainMenu extends GameObject implements  KeyListener {

	
	private Earth earth = new Earth(100, 0, null);
	
	public MainMenu()
	{
		super(null);
		Input.addKeyListener(this);
	}
	
	boolean missingThurstersWarning = false;
	
	boolean scored = false;
	int score = 0;
	public MainMenu(int score)
	{
		super(null);
		Input.addKeyListener(this);
		this.score = score;
		scored = true;
	}
	
	private int[] colours = new int[]{0x880022, 0xDD6610, 0x654367, 0xaa0080};
	private int c = 0;
	
	private void togglePlayer(Key control) {
		Thruster thruster = null;
		for (int i = earth.thrusters.size() - 1; i >= 0; i --) {
			thruster = earth.thrusters.get(i);
			
			if (thruster.getKey() == control) {
				earth.thrusters.remove(i);
				return;
			}
		}
		// If code reaches this point, no thruster exists with the Key control.
		thruster = new Thruster(null);
		thruster.setKey(control);
		thruster.angleOffset = (Math.random() * Math.PI / 2) - (Math.PI / 4);
		
		// Create new colour
		thruster.colour = new Colour(colours[c % (colours.length)]);
		c++;
		earth.thrusters.add(thruster);
		earth.placeThrusters();
	}
	
	public boolean locked = false;
	
	@Override
	public void update(long delta) {
		if (locked) return;
		// Handle key input since last update.
		for (int i = keyDownBuffer.size() - 1; i >= 0; i --) {
			int keyCode = keyDownBuffer.get(i);
			if (keyCode == Input.ENTER) {
				if (earth.thrusters.size() > 0) {
					Main.getInstance().newGame(earth, this);
				} else {
					missingThurstersWarning = true;
				}
			} else if (keyCode == Input.ESCAPE) {
				earth.thrusters.clear();
			} else {
				for (Key key : Key.values()) {
					if(key.code == keyCode) {
						togglePlayer(key);
					}
				}
			}
		}
		keyDownBuffer.clear();
		
		if (earth.thrusters.size() > 0) {
			missingThurstersWarning = false;
		}
		
		earth.placeThrusters();
		
		for (int i = earth.thrusters.size() - 1; i >= 0; i --) {
			Thruster t = earth.thrusters.get(i);
			t.colour.setRGB(colours[i % colours.length]);
		}
		// Set radius depending on number of thrusters
		earth.radius = 100 + earth.thrusters.size()*2;
	}

	private final double GOLDEN_RATIO = (1 + Math.sqrt(5)) / 2;
	
	private final String description = "To add a thruster, press a letter on your keyboard.\nYou will use that letter to toggle the thruster.\nYou can decide if you want to play multiplayer with one thruster each,"
			+ "\nor go solo with several thrusters. (Or any combination of course) Your choice!\nPress |escape| to reset setup.";
	
	
	@Override
	public void renderTo(BufferedImage mainImage, Matrix2D matrix) {

		BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Vector2d screenPos = matrix.transform(0, 0);
		
		// RENDER DESCRIPTION
		{
			drawString(description, Game.WIDTH / 2, 20, g, font, true);
		}
		
		// PLAYERS REGISTERED: (view the controls)
		{
			int num = earth.thrusters.size();
			int expectedWidth = (int) (Math.sqrt(GOLDEN_RATIO * num) + 0.5);
			int actualWidth = Math.min(expectedWidth, num - 1);
			int x = 0;
			int y = 0;
			
			Thruster thruster;
			for (int i = 0; i < num; i ++) {
				thruster = earth.thrusters.get(i);
				renderButton(image.getWidth() / 2 + (actualWidth)*20 - (actualWidth - x)*40, y * 40 + 150, thruster.getKey(), g);
				//
				x ++;
				if (x == expectedWidth) {
					actualWidth = Math.min(expectedWidth, num - 1 - i);
					x = 0;
					y ++;
				}
			}
		}
		// PREVIEW
		{
			earth.position.x = Game.WIDTH / 2;
			earth.position.y = Game.HEIGHT - earth.radius - 30;
			earth.renderTo(image, matrix);
		}
		
		// START BUTTON
		{
			// Make initial arrows.
			BufferedImage temp = new BufferedImage(100, 60, BufferedImage.TYPE_INT_ARGB);
			Graphics2D tempg = temp.createGraphics();
			
			int offset = 0;
			// Arrow 1
			tempg.drawLine(10, 10, 20, 20);
			tempg.drawLine(20, 20, 10, 30);
			// Arrow 2
			offset += 5;
			tempg.drawLine(10 + offset, 10, 20 + offset, 20);
			tempg.drawLine(20 + offset, 20, 10 + offset, 30);
			
			applyOutline(temp, 0xFF008020);
			applyOutline(temp, 0xFF007060);
			applyOutline(temp, 0xCC006050);
			applyOutline(temp, 0x90005040);
			applyOutline(temp, 0x60004030);
			applyOutline(temp, 0x60003020);
			
			g.drawImage(temp, Game.WIDTH - 50, Game.HEIGHT - 47, null);
			
			drawString("Press enter to start", Game.WIDTH - 150, Game.HEIGHT - 40, g, font, true);
		}
		
		// MISSING THRUSTER WARNING
		{
			if (missingThurstersWarning) {
				Color temp = g.getColor();
				g.setColor(new Color(0xBB0000));
				drawString("You need to set up at least\none thruster before playing!", Game.WIDTH - 150, Game.HEIGHT - 120, g, font, true);
				// Underline
				g.drawLine(Game.WIDTH/2 - 250, 44, Game.WIDTH/2 + 250, 44);
				g.setColor(new Color(0x550000));
				g.drawLine(Game.WIDTH/2 - 251, 45, Game.WIDTH/2 + 251, 45);
				
				
				g.setColor(temp);
			}
		}
		
		// SCORE
		{
			if (scored) {
				drawString("Last score: " + score, 15, 500, g, font, false);
			}
			drawString("Best score: " + Main.getInstance().getHighScore(), 15, 540, g, font, false);
		}
		

		Graphics2D mainG = mainImage.createGraphics();
		mainG.drawImage(image, (int) position.x, (int) position.y, null);
	}
	
	private void renderButton(int x, int y, Key key, Graphics2D g)
	{
		// Draw the key.
		String str = "";
		if (key == null) {
			str = "?";
		} else {
			str = key.getName();
		}
		// Draw rounded rect
		Point controlPoint = new Point(x, y);
		Rectangle2D strRect = getBounds(str, font);
		
		g.setColor(new Color(0x3333333));
		g.fillRoundRect(controlPoint.x, controlPoint.y, 30, 30, 3, 3);
		g.setColor(Color.white);
		g.drawRoundRect(controlPoint.x, controlPoint.y, 30, 30, 3, 3);
		
		// Draw string
		drawString(str, (int) (x + (30 - strRect.getWidth())/2), (int) (y + (30 - strRect.getHeight())/2), g, font, false);
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
	private ArrayList<Integer> keyDownBuffer = new ArrayList<Integer>();
	@Override
	public void keyDown(KeyEvent e) {
		int code = e.getKeyCode();
		keyDownBuffer.add(code);
	}
	
	@Override
	public void keyUp(KeyEvent e) {
	}
	
	
	// STATIC!
	
	public static Font font = getFont("res/CD.ttf").deriveFont(20f);
	public static Font font_b = getFont("res/CD_BOLD.ttf").deriveFont(14f);
	private static Font getFont(String name)
    {
        Font font = null;
	    try {
	      InputStream is = MainMenu.class.getResourceAsStream(name);
	      font = Font.createFont(Font.TRUETYPE_FONT, is);
	    } catch (Exception ex) {
	      ex.printStackTrace();
	      System.err.println(name + " not loaded.  Using serif font.");
	      font = new Font("serif", Font.PLAIN, 150);
	    }
	    return font;
    }

}
