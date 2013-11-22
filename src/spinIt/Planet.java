package spinIt;

import game.essential.Vector2d;



public class Planet extends Circle {
	
	protected Vector2d force = new Vector2d();
	
	protected double torque = 0;
	
	protected double rotationSpeed = 0;
	
	protected double rotation = 0;
	
	public Planet(double radius, Game game)
	{
		super(radius, game);
	}
	
	@Override
	public void update(long delta)
	{
		size.x = diameter();
		size.y = diameter();
		// Apply forces
		velocity.x += force.x / area();
		velocity.y += force.y / area();
		rotationSpeed += torque / (area()/2 * radius * radius);
		
		force.x = 0; force.y = 0; torque = 0;
		
		// 
		
		rotation += rotationSpeed;
		rotationSpeed *= 0.999;
		position.x += velocity.x;
		velocity.x *= 0.999;
		position.y += velocity.y;
		velocity.y *= 0.999;
	}
	
	public double getRotation()
	{
		return rotation;
	}
	
	public void applyForce(Force force)
	{
		applyForce(force.point, force.force);
	}
	public void applyForce(Vector2d point, Vector2d force)
	{
		this.force.x += force.x;
		this.force.y += force.y;
		
		// Distance to the center
		Vector2d distance = point.clone().substract(position);
		
		torque += (distance.x * force.y - distance.y * force.x);
		
	}
}