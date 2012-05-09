package HashTable;

public class QuadraticCollisionResolver implements CollisionResolver {	
	
	public QuadraticCollisionResolver() {
	}
	
	public int recalculateAddress(int collided, int previousAddress, int range)
	{	/*				
		return previousAddress + (int)Math.pow(collided, 2.0);
		*/
		
		return (previousAddress + collided) % range;
	}
}
