package spinIt;

import game.essential.Matrix2D;
import game.essential.ModelList;
import game.essential.Vector2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import spinIt.menu.MainMenu;

public class Earth extends Planet {
	
	public ModelList<Thruster> thrusters = new ModelList<Thruster>();
	
	public int score = 0;
	
	/**
	 * @param numThrusters	Number of thrusters.
	 */
	public Earth(double radius, int numThrusters, Game game) {
		super(radius, game);
		// Make new thrusters
		Thruster newT;
		for (int i = 0; i < numThrusters; i ++) {
			newT = new Thruster(game);
			newT.angleOffset = (Math.random() * Math.PI / 2) - (Math.PI / 4);
			newT.setKey(Key.values()[i]);
			thrusters.add(newT);
		}
		placeThrusters();
		
		//imageOffset.x = Math.random() * (Earth.texture.getWidth() - diameter()) + radius;
		//imageOffset.y = Math.random() * (Earth.texture.getHeight() - diameter()) + radius;
		//System.out.println("OFFSET: " + imageOffset);
		graphics = new BufferedImage((int) diameter(), (int) diameter(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = graphics.createGraphics();
		g2.scale(diameter() / Earth.texture.getWidth(), diameter() / Earth.texture.getHeight());
		g2.drawImage(Earth.texture, 0, 0, null);
	}
	
	/**
	 * Is called in update(long)!
	 */
	public void placeThrusters()
	{
		double singleRad = (Math.PI * 2) / thrusters.size();
		double rad = rotation;
		

		for (int i = 0; i < thrusters.size(); i ++) {
			Thruster thruster = thrusters.get(i);

			Vector2d localPos = new Vector2d(Math.cos(rad) * radius, Math.sin(rad) * radius);
			thruster.position.x = position.x + localPos.x;
			thruster.position.y = position.y + localPos.y;
			
			// Speed
			Vector2d normal = localPos.clone().rotate90CC().normalize();
			// The additional speed derived from this angular speed
			Vector2d additionalSpeed = normal.times(rotationSpeed * localPos.length());
			//
			thruster.velocity.x = velocity.x + additionalSpeed.x;
			thruster.velocity.y = velocity.y + additionalSpeed.y;
			thruster.angle = thruster.angleOffset + rad;
			rad +=singleRad;
		}
	}
	
	@Override
	public void update(long delta)
	{
		super.update(delta);
		placeThrusters();
		thrusters.update(delta);
		
		for (int i = thrusters.size() - 1; i >= 0; i --) {
			Thruster thruster = thrusters.get(i);
			if (thruster.toggled()) {
				applyForce(thruster.position, new Vector2d(Math.cos(thruster.angle + Math.PI) * 3000, Math.sin(thruster.angle + Math.PI) * 3000));
			}
		}
	}
	

	Vector2d imageOffset = new Vector2d();
	private BufferedImage graphics = null;
	
	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix)
	{
		thrusters.renderTo(image, matrix);
		//super.renderTo(image, matrix);
		// render image??
		BufferedImage temp = new BufferedImage((int) matrix.scaledX(diameter()), (int) matrix.scaledY(diameter()), BufferedImage.TYPE_INT_ARGB);
		
		Vector2d screenPos =  matrix.transform(position.clone());
		
		
		Graphics2D tempG = temp.createGraphics();

		tempG.scale(matrix.a * diameter() / Earth.texture.getWidth(), matrix.d * diameter() / Earth.texture.getHeight());
		tempG.translate(matrix.scaledX(radius), matrix.scaledY(radius));
		tempG.drawImage(Earth.texture, (int) -matrix.scaledX(radius), (int) -matrix.scaledX(radius), null);
		
		// mask..
		BufferedImage mask = new BufferedImage(temp.getWidth(), temp.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D maskG = mask.createGraphics();
		maskG.setColor(Color.WHITE);
		maskG.fillOval(0, 0, mask.getWidth(), mask.getHeight());
		mask(temp, mask);
		
		Graphics2D g2 = image.createGraphics();
		g2.translate(screenPos.x, screenPos.y);
		g2.rotate(rotation);
		g2.drawImage(temp, (int) ( - matrix.scaledX(radius)), (int) ( - matrix.scaledY(radius)), null);
		g2.dispose();
		g2 = image.createGraphics();
		// Render the controls of the thrusters!
		Font f30 =  MainMenu.font.deriveFont(30f);
		for (int i = thrusters.size() - 1; i >= 0; i --) {
			Thruster thruster = thrusters.get(i);
			if (thruster.getKey() != Key.NO_KEY) {
				// Rendeeeer
				Rectangle2D rect = getBounds(thruster.getKey().getName(), f30);
				Vector2d rectSize = new Vector2d(rect.getWidth(), rect.getHeight());
				Vector2d localPoint = matrix.getScaled(thruster.position.substract(position).times(0.8));
				//localPoint.x -= Math.cos(localPoint.angle()) * rectSize.x;
				//localPoint.y -= Math.sin(localPoint.angle()) * rectSize.y;
				// Try to center the text near the edge of the earth
				temp = new BufferedImage((int) rect.getWidth()+8, (int) rect.getHeight()+8, BufferedImage.TYPE_INT_ARGB);
				tempG = temp.createGraphics();
				tempG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				tempG.setColor(Color.white);
				drawString(thruster.getKey().getName(), 1, 1, tempG, f30);
				applyOutline(temp, 0xDD000000);
				Colour col = thruster.colour.clone();
				applyOutline(temp, col.getARGB());
				col.multiply(0.7).setAlpha(0.98f);
				applyOutline(temp, col.getARGB());
				col.multiply(0.7).setAlpha(0.95f);
				applyOutline(temp, col.getARGB());
				
				g2.drawImage(temp, (int) (screenPos.x + localPoint.x - rect.getWidth()/2), (int) (screenPos.y + localPoint.y - rect.getHeight()/2), null);
			}
		}
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
	
	private void mask(BufferedImage target, BufferedImage mask)
	{
		
		int[] target_d = getImageData(target);
		int[] mask_d = getImageData(mask);
		int w = target.getWidth();
		int h = target.getHeight();
		if (w != mask.getWidth() || h != mask.getHeight()) return;
		int i = 0;
		for (int y = 0; y < h; y ++) {
			for (int x = 0; x < w; x ++) {
				if (mask_d[i] == 0) {
					target_d[i] = 0;
				}
				i ++;
			}
		}
	}
	
	private Rectangle2D drawString(String str, int x, int y, Graphics2D g,  Font f)
	{
		Rectangle2D r = getBounds(str, f);
		g.setFont(f);
		g.drawString(str, (float) (x - r.getX()), (float) (y - r.getY()));
		r.setRect(x, y, r.getWidth(), r.getHeight());
		return r;
	}
	private Rectangle2D getBounds(String str, Font f)
	{
		return f.getStringBounds(str, new FontRenderContext(null, false, false));
	}
	
	
	
	
	private static BufferedImage texture = null;
	static {
		try {
			texture = ImageIO.read(Earth.class.getResourceAsStream("assets/earth.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int[] getImageData(BufferedImage image)
	{
		return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
}
