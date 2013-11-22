package spinIt;

import game.essential.Matrix2D;
import game.essential.Vector2d;

import java.awt.image.BufferedImage;

public class Particle extends GameObject {

	public Colour colour = new Colour(0xFF0000);
	
	public double friction = 1;
	
	public Particle(Colour colour)
	{
		super(null);
		this.colour = colour;
	}
	public Particle(Colour colour, double x, double y)
	{
		super(null);
		this.colour = colour;
		position.x = x;
		position.y = y;
	}
	
	@Override
	public void update(long delta) {
		position.x += velocity.x;
		position.y += velocity.y;
	}

	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix) {
		Vector2d screenPos = matrix.transform(position);
		int screenX = (int) screenPos.x;
		int screenY = (int) screenPos.y;
		

		if (screenX >= 1 && screenY >= 1 && screenX < image.getWidth() - 1 && screenY < image.getHeight() - 1) {

			int col = image.getRGB(screenX, screenY);
			
			image.setRGB(screenX, screenY, colour.clone().blend(col).getRGB());
			
			Colour b = colour.clone().setAlpha((float) (colour.getAlpha() * 0.5));
			
			screenX -= 1;
			col = image.getRGB(screenX, screenY);
			image.setRGB(screenX, screenY, b.clone().blend(col).getRGB());
			
			screenX += 2;
			col = image.getRGB(screenX, screenY);
			image.setRGB(screenX, screenY, b.clone().blend(col).getRGB());
			
			screenX -= 1;
			screenY -= 1;
			col = image.getRGB(screenX, screenY);
			image.setRGB(screenX, screenY, b.clone().blend(col).getRGB());
			
			screenY += 2;

			col = image.getRGB(screenX, screenY);
			image.setRGB(screenX, screenY, b.clone().blend(col).getRGB());
		}
		
	}
	

}
