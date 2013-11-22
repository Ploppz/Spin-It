package spinIt;

import game.essential.Vector2d;

import java.util.ArrayList;

public class Polygon {
	public final ArrayList<Vector2d> points = new ArrayList<Vector2d>();
	
	public void addPoint(double x, double y)
	{
		points.add(new Vector2d(x, y));
	}
	
	
}
