package spinIt;

import game.essential.Matrix2D;
import game.essential.Vector2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Satellite extends Circle {

	public Satellite(Vector2d pos, Game game) {
		super(50, game);
		position = pos.clone();
	}

	@Override
	public void update(long delta) {

	}

	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix) {
		Vector2d screenPos = matrix.transform(position);
		
		Graphics2D g = image.createGraphics();
		g.scale(matrix.a, matrix.d);
		g.drawImage(texture, (int) (screenPos.x - radius), (int) (screenPos.y - radius), null);
		
	}
	
	
	
	
	
	
	private static BufferedImage texture = null;
	static {
		try {
			texture = ImageIO.read(Earth.class.getResourceAsStream("assets/satellite.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
