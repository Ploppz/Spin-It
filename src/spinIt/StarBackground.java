package spinIt;

import game.essential.Drawable;
import game.essential.Matrix2D;
import game.essential.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Random;

import com.adonax.starfield.GalacticCloud;
import com.adonax.starfield.StarSet;
import com.adonax.starfield.TwinklingStar;

public class StarBackground implements Drawable {

	
	private BufferedImage image;
	private BufferedImage temp;
	
	/** Contains a map of only the default colour (defaultColour). */
	private int[] emptyMap;
	
	private final int defaultColour = 0x060015;
	
	private final int margin = 30;
	
	public StarBackground()
	{
		image = new BufferedImage(Game.WIDTH + margin * 2, Game.HEIGHT + margin * 2, BufferedImage.TYPE_INT_RGB);
		
		temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		// Render the empty map
		emptyMap = new int[image.getWidth() * image.getHeight()];
		for (int i = 0; i < emptyMap.length; i ++) {
			emptyMap[i] = defaultColour;
		}
		
		//////
		
		init();
	}
	
	private Random rand = new Random();
	
	private void init()
	{
		
		// COLOUR WITH DEFAULT COLOUR
		int[] data = getData(image);
		for (int i = 0; i < data.length; i ++) {
			data[i] = defaultColour;
		}
		render(0, 0, image.getWidth(), image.getHeight());
	}
	

	// STARS
	StarSet starsRed = new StarSet(new Color(255, 224, 224));
	StarSet starsWhite = new StarSet();
	StarSet starsBlue = new StarSet(new Color(232, 232, 255));
	StarSet currStars = starsRed;
	
	/** Render a part (rectangle) of the image. */
	private void render(int startX, int startY, int endX, int endY) {
		
		// number of 100x100 chunks
		int width = Math.abs(endX - startX);
		int height = Math.abs(endY - startY);
		int area = (width * height) / 10000;
		Graphics2D g = image.createGraphics();
		// Draw the initial picture

		// Draw some clouds I guess
		/*int numClouds = rand.nextInt((int) Math.ceil((double)area / 10d));
		for (int i = 0; i < numClouds; i ++) {
			int w = rand.nextInt(Math.min(300, width - 10)) + 10;
			int h = rand.nextInt(Math.min(300, height - 10)) + 10;
			GalacticCloud gc = new GalacticCloud(w, h);
			g.drawImage(gc.getCloud(), startX + rand.nextInt(width - w), startY + rand.nextInt(height - h), null);
		}*/
		
		
		TwinklingStar s;
		int numStars = 13 * area;
		for (int i = 0; i < numStars; i ++) {
			int color = rand.nextInt(5);

			switch(color) {
				case 0:
					currStars = starsRed;
					break;
				case 1:
				case 2:
					currStars = starsWhite;
					break;
				case 3:
				case 4:
					currStars = starsBlue;
			}
			// choose size (five sizes possible)
			int size = 4 - (int)Math.log1p(rand.nextInt(148));
				
			// chose brightness (five brightnesses possible)
			int brightness = rand.nextInt(5);
			
			
			s = new TwinklingStar(startX + rand.nextInt(width - 5), startY + rand.nextInt(height - 12), currStars.getStar(size, brightness), currStars.getStar(size, brightness));
			
			s.draw(g);
		}
	}
	
	
	@Override
	public void update(long delta) {
		
	}
	
	/** Will offset the image by moving it inside itself. */
	private void offset(int xOffset, int yOffset)
	{
		int[] data = getData(image);
		// old image
		int[] cache = new int[data.length];
		System.arraycopy(data, 0, cache, 0, data.length);
		
		int i = 0;
		int w = image.getWidth(); int h = image.getHeight(); 
		
		int cacheX = -xOffset;
		int cacheY = -yOffset;
		int cacheI = cacheY * image.getWidth() + cacheX;
		for (int y = 0; y < h; y ++, cacheY ++) {
			for (int x = 0; x < w; x ++, cacheX ++) {
				cacheX = x + xOffset;
				cacheY = y + yOffset;
				if (cacheI >= 0 && cacheI < cache.length) {
					// use existing pixel
					data[i] = cache[cacheI];
				} else {
					// make a new pixel
					data[i] = defaultColour;
				}
				
				i ++;
				cacheI ++;
			}
		}
		
		
		/*int[] data = getData(image);
		int[] tempData = getData(temp);
		System.arraycopy(data, 0, tempData, 0, data.length);
		//Graphics2D g2 = temp.createGraphics();
		//g2.drawImage(image, 0, 0, null);
		
		System.arraycopy(emptyMap, 0, data, 0, emptyMap.length);
		
		Graphics2D g = image.createGraphics();
		g.drawImage(temp, xOffset, yOffset, null);*/
	}
	private void offset(Vector2d offset)
	{
		offset((int) offset.x, (int) offset.y);
	}

	/** The offset when we last did the internal rendering update. */
	private Vector2d lastUpdated = null;
	
	/** The offset when we last moved the image to scroll it. */
	private Vector2d lastMoved = null;
	
	// error because int
	private final Vector2d error = new Vector2d();
	
	@Override
	public void renderTo(BufferedImage image, Matrix2D matrix)
	{
		Vector2d screenPos = matrix.transform(0, 0);
		if (lastMoved == null) {
			lastMoved = screenPos.clone();
		}
		if (lastUpdated == null) {
			lastUpdated = screenPos.clone();
		}
		// OFFSETTING (scrolling)
		Vector2d offset = screenPos.clone().substract(lastMoved).add(error);
		if ( !((int) offset.x == 0 && (int) offset.y == 0)) {
			error.x = offset.x % 1;
			error.y = offset.y % 1;
			// Move:
			offset(offset);
			lastMoved = screenPos;
		}
		
		
		// Drawing new stars to the edges!
		
		//Distance from the last rendering
		Vector2d updateDelta = screenPos.clone().substract(lastUpdated);
		// Check if anything needs to be re-rendered.
		if (updateDelta.x > margin) {
			// Render left side
			render(0, 0, (int) updateDelta.x, (int) this.image.getHeight());
			lastUpdated.x = screenPos.x;
			
		} else if (updateDelta.x < - margin) {
			// Render right side
			
			render((int) (this.image.getWidth() - Math.abs(updateDelta.x)), 0, (int) Math.abs(updateDelta.x), (int) this.image.getHeight());
			lastUpdated.x = screenPos.x;
		}
		
		if (updateDelta.y > margin) {
			// Render top
			render(0, 0, this.image.getWidth(), (int) updateDelta.y);
			lastUpdated.y = screenPos.y;
			
		} else if (updateDelta.y < - margin) {
			// Render bottom
			render(0, (int) (this.image.getHeight() - Math.abs(updateDelta.y)), this.image.getWidth(), this.image.getHeight());
			lastUpdated.y = screenPos.y;
			
		}
		
		// Drawing the final result to the screen!
		Graphics2D g = image.createGraphics();
		g.drawImage(this.image, - margin, - margin, null);
	}
	
	private int[] getData(BufferedImage image)
	{
		return getData(image.getRaster());
	}
	
	private int[] getData(WritableRaster raster)
	{
		return  ((DataBufferInt) raster.getDataBuffer()).getData();
	}

}
