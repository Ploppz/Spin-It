package spinIt;

import game.essential.Matrix2D;
import game.essential.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;


public class Circle extends GameObject {
	
	public double radius = 1;
	
	public Circle(double radius, Game game)
	{
		super(game);
		this.radius = radius;
	}
	
	public double diameter()
	{
		return radius + radius;
	}
	
	public double area()
	{
		return radius * radius * Math.PI;
	}

	@Override
	public void update(long delta) {
		
	}
	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix) {
		Vector2d screenPos = matrix.transform(position);
		
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Rendering
		int x = (int) (screenPos.x - matrix.scaledX(radius));
		int y = (int) (screenPos.y - matrix.scaledY(radius));
		int w = (int) matrix.scaledX(diameter());
		int h = (int) matrix.scaledY(diameter());
		g.setColor(Color.BLACK);
		g.fillOval(x, y, w, h);
		g.setColor(Color.WHITE);
		g.drawOval( x, y, w, h);
	}

	
}
