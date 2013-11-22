package spinIt;

import game.essential.Vector2d;

public class Force {
	public Vector2d point = new Vector2d();
	public Vector2d force = new Vector2d();
	
	public Force()
	{
		
	}
	public Force(double x, double y, double forceX, double forceY)
	{
		point.x = x;
		point.y = y;
		force.x = forceX;
		force.y = forceY;
	}
	public Force(double x, double y, Vector2d force)
	{
		point.x = x;
		point.y = y;
		this.force.x = force.x;
		this.force.y = force.y;
	}
	public Force(Vector2d point, Vector2d force)
	{
		this.point.x = point.x;
		this.point.y = point.y;
		this.force.x = force.x;
		this.force.y = force.y;
	}
	
	public Force clone()
	{
		return new Force(point.x, point.y, force.x, force.y);
	}
}
