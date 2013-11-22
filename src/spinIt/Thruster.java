package spinIt;

import game.essential.KeyListener;
import game.essential.Matrix2D;
import game.essential.Vector2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


/**
 * This is what the player controls.
 */
public class Thruster extends GameObject implements KeyListener {

	/** Key code to toggle the thruster. */
	private Key toggleKey = Key.NO_KEY;
	
	public Colour colour = new Colour(0x440000);
	
	
	/** In radians. */
	public double angle = 0;
	
	public double angleOffset = 0;
	
	/**
	 * Unit vector for the angle
	 */
	private Vector2d unitVector = new Vector2d();
	
	public Thruster(Game game) {
		super(game);
	}
	
	private boolean toggled = false;
	public boolean toggled()
	{
		return toggled;
	}
	
	

	@Override
	public void update(long delta) {
		unitVector.x = Math.cos(angle);
		unitVector.y = Math.sin(angle);
		if (toggled && game != null) {
			
			Force direction = new Force(getTurretTip(0), unitVector.clone());
			
			for (int i = 0; i < 60; i ++) {
				if (Math.random() > 0.5) {
					double rand = Math.random() * 2 - 1;
					double rAngle = this.angle + rand;
					
					Vector2d tip = getTurretTip(i/6 - 5);
					FireParticle p = new FireParticle(new Colour(0xFF0000), tip.x - velocity.x, tip.y - velocity.y, direction.clone(), velocity.clone());
					p.velocity.x = Math.cos(rAngle) * 2 + velocity.x;
					p.velocity.y = Math.sin(rAngle) * 2 + velocity.y;
					game.addFireParticle(p);
				}
			}
		}
	}
	


	@Override
	public void keyDown(KeyEvent e) {
		if (e.getKeyCode() == toggleKey.code)
		{
			toggled = true;
		}
	}


	@Override
	public void keyUp(KeyEvent e) {
		if (e.getKeyCode() == toggleKey.code)
		{
			toggled = false;
		}
	}
	
	/*private Vector2d getTurretTip()
	{
		return new Vector2d(position.x + Math.cos(angle) * turretLength, position.y + Math.sin(angle) * turretLength);
	}*/
	/** 1/4 of Math.PI * 2 radians = Math.PI / 2 */
	private final double fourth = Math.PI / 2;
	private Vector2d getTurretTip(double offset)
	{
		return new Vector2d(position.x + Math.cos(angle + fourth) * offset + Math.cos(angle) * turretLength, position.y +  + Math.sin(angle + fourth) * offset + Math.sin(angle) * turretLength);
	}
	
	
	// RENDERING
	
	private Font font = new Font("Arial", Font.PLAIN, 12);
	
	private double circleSize = 21;
	private double turretLength = 15;
	
	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix) {
		Vector2d screenPos = matrix.transform(position);
		
		int size = (int) (circleSize * matrix.a);
		double turretLength = this.turretLength * matrix.a;
		
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Turret
		Matrix2D rot = new Matrix2D();
		rot.translate(screenPos).rotate(angle);

		drawRect(0, -4, turretLength, 8, rot, g);
		
		// CIRCLE
		g.setColor(new Color(colour.getRGB()));
		g.fillOval((int) screenPos.x - size/2, (int) screenPos.y - size/2, size, size);
		g.setColor(Color.WHITE);
		g.drawOval((int) screenPos.x - size/2, (int) screenPos.y - size/2, size, size);
		
		// TEXT
		g.setFont(font);
		g.setColor(Color.WHITE);
		
		/*FontRenderContext frc = new FontRenderContext(null, true, true);
        TextLayout layout = new TextLayout(toggleKey.getName(), font, frc);
		
		layout.draw(g, (float) screenPos.x - 4, (float) screenPos.y + 4);*/
		
		
	}
	
	private void drawRect(double x, double y, double width, double height, Matrix2D matrix, Graphics2D g)
	{
		Vector2d lineStart = matrix.transform(new Vector2d(x, y));
		Vector2d lineEnd = matrix.transform(new Vector2d(x + width, y));
		g.drawLine((int) lineStart.x, (int) lineStart.y, (int) lineEnd.x, (int) lineEnd.y);
		lineStart = lineEnd.clone();
		lineEnd = matrix.transform(new Vector2d(x + width, y + height));
		g.drawLine((int) lineStart.x, (int) lineStart.y, (int) lineEnd.x, (int) lineEnd.y);
		lineStart = lineEnd.clone();
		lineEnd = matrix.transform(new Vector2d(x, y + height));
		g.drawLine((int) lineStart.x, (int) lineStart.y, (int) lineEnd.x, (int) lineEnd.y);
		lineStart = lineEnd.clone();
		lineEnd = matrix.transform(new Vector2d(x, y));
		g.drawLine((int) lineStart.x, (int) lineStart.y, (int) lineEnd.x, (int) lineEnd.y);
	}

	
	public void setKey(Key key)
	{
		toggleKey = key;
	}
	/**
	 * @return Returns the toggle key.
	 */
	public Key getKey()
	{
		return toggleKey;
	}
}
