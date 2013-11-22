package spinIt;

import game.essential.Drawable;
import game.essential.Matrix2D;
import game.essential.Vector2d;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.adonax.starfield.StarField;

public class Background1 implements Drawable {

	public Background1()
	{
		long a = System.nanoTime();
		System.out.println("STAR TIME: " + (System.nanoTime() - a));
	}
	
	
	@Override
	public void update(long delta) {
		
	}

	private StarBackground bg = new StarBackground();
	
	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix) {
		Vector2d screenPos = matrix.transform(0, 0);
		Point pos = new Point((int) screenPos.x, (int) screenPos.y);
		/*Vector2d step = new Vector2d(matrix.scaledX(40), matrix.scaledY(40));
		
		if (step.x < 1) step.x = 1;
		if (step.y < 1) step.y = 1;
		System.out.println(step);
		for (int y = (int)(screenPos.y % step.y); y < image.getHeight(); y += step.y) {
			for (int x = (int)(screenPos.x % step.x); x < image.getWidth(); x += step.x) {
				if (x >= 0 && y >= 0 && y < image.getHeight() && x < image.getWidth()) {
					image.setRGB(x, y, 0xFFFFFF);
				}
			}
		}*/
		Graphics2D g = image.createGraphics();
	}
	private int[] getImageData(BufferedImage image)
	{
		return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
}
