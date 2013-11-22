package spinIt;

import game.essential.Drawable;
import game.essential.Matrix2D;
import game.essential.ModelList;
import game.essential.Vector2d;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class FireEngine implements Drawable {
	
	private final double removeWhenAlpha = 0.01;//0.02;
	private final double frictionConstant = 0.8; //0.56;
	private final double frictionRandom = 0.1; //0.3;
	
	
	
	public FireEngine() {}

	ModelList<FireParticle> particles = new ModelList<FireParticle>();
	
	public void addParticle(FireParticle p)
	{
		p.friction = frictionConstant + Math.random() * frictionRandom;
		particles.add(p);
	}
	
	
	@Override
	public void update(long delta) {
		for (int i = particles.size() - 1; i >= 0; i --) {
			FireParticle p = particles.get(i);
			p.update(delta);
			
			
			// Eventually remove
			if (p.colour.getAlpha() < removeWhenAlpha) {
				particles.remove(p);
			}
			
		}
	}
	
	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix) {
		Graphics2D g = image.createGraphics();
		
		Colour col = new Colour();
		Colour alphaCol = new Colour();
		Vector2d screenPos;
		int screenX;
		int screenY;
		int dataIndex;
		int[] data = getImageData(image);
		
		for (FireParticle p : particles) {
			screenPos = matrix.transform(p.position);
			screenX = (int) screenPos.x;
			screenY = (int) screenPos.y;
			if (!(screenX < 0 || screenY < 0 || screenX >= image.getWidth() || screenY >= image.getHeight())) {
			
				// Render the praticle
				
				dataIndex = screenY * image.getWidth() + screenX;
				if (dataIndex >= 0 && dataIndex < data.length) {
					col.setRGB(data[dataIndex]);
					data[dataIndex] = p.colour.clone().blend(col).getRGB();
					//data[dataIndex] = p.colour.clone().setRGB(0xFF0000).blend(col).getRGB();
					//data[dataIndex] = 0xFF0000;
				}
				
				// Around the pixel
				alphaCol = p.colour.clone().setAlpha(0.5f);
				
				
				dataIndex -= image.getWidth();
				if (dataIndex >= 0 && dataIndex < data.length) {
					col.setRGB(data[dataIndex]);
					data[dataIndex] = alphaCol.clone().blend2(col).getRGB();
				}
	
				dataIndex += image.getWidth() + image.getWidth();
				if (dataIndex >= 0 && dataIndex < data.length) {
					col.setRGB(data[dataIndex]);
					data[dataIndex] = alphaCol.clone().blend2(col).getRGB();
				}
	
				dataIndex -= image.getWidth();
				dataIndex -= 1;
				if (dataIndex >= 0 && dataIndex < data.length) {
					col.setRGB(data[dataIndex]);
					data[dataIndex] = alphaCol.clone().blend2(col).getRGB();
				}
	
				dataIndex += 2;
				if (dataIndex >= 0 && dataIndex < data.length) {
					col.setRGB(data[dataIndex]);
					data[dataIndex] = alphaCol.clone().blend2(col).getRGB();
				}
				
				// TESTING THE ANGLE
				//Vector2d from = matrix.transform(p.direction.point.clone());
				//Vector2d to = matrix.transform(p.direction.point.clone().add(p.direction.force.clone().times(10)));
				//g.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
			}
		}
	}
	
	private int[] getImageData(BufferedImage image)
	{
		return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
}
