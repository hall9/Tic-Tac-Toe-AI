package proj4;

public class QuadraticCollisionResolver implements CollisionResolver {	
	
	public QuadraticCollisionResolver() {
	}
	
	public int recalculateAddress(int collided, int previousAddress, int range)
	{				
		//return (previousAddress + collided) % range;
		
		return (int) (( previousAddress + Math.pow(collided, 2.0) ) % range);
	}
}
