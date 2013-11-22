package spinIt;

import game.essential.Vector2d;

public class FireParticle extends Particle {

	/**
	 * Unit vector.
	 */
	public Force direction = new Force();
	private Vector2d directionSpeed;
	
	/**
	 * @return Returns the direction of the flame.
	 */
	public Force getDirection()
	{
		return direction;
	}
	
	public FireParticle(Colour colour, double x, double y, Force direction, Vector2d directionSpeed)
	{
		super(colour, x, y);
		this.direction = direction;
		
		this.directionSpeed = directionSpeed;
	}
	
	@Override
	public void update(long delta)
	{
		super.update(delta);
		colour.setAlpha(colour.getAlpha() * (float) friction);
		//colour.setGreen(colour.getGreen() + 4);

	// ACCELERATION TOWARDS CENTER VECTOR OF FLAME

		// This is the distance from position to the direction point.
		Vector2d dist = direction.point.clone().substract(position);
		// Distance from position to hte direction line:
		Vector2d distance = (dist.substract(direction.force.clone().times(dist.dot(direction.force))));
		
		// Convert speed vector to normal and tangential speed (to the direction)
		//System.out.println("Force: " + direction.force);
		Vector2d unitTangent = direction.force.clone();
		Vector2d unitNormal = unitTangent.clone().rotate90CC();
		double normalDistance = dist.dot(unitNormal);
		
		double tangentSpeed = unitTangent.clone().dot(velocity);
		double normalSpeed = unitNormal.clone().dot(velocity);
		
		// we want the particle to approach a distance = 0. normalSpeed is the speed in this direction.
		
		normalSpeed += normalDistance / 30;
		//normalSpeed *= 0.98;
		
		// Convert back to vectors
		velocity = unitTangent.times(tangentSpeed).add(unitNormal.times(normalSpeed));

	// SPEED OF THE DIRECTION FORCE
		direction.point.x += directionSpeed.x;
		direction.point.y += directionSpeed.y;

		//directionSpeed.x *= 0.9;
		//directionSpeed.y *= 0.9;
		
	}

}
