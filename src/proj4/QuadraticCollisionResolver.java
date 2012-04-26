package edu.dccc.dpr212.vehiclevanager;


public class QuadraticCollisionResolver implements CollisionResolver {	

	public QuadraticCollisionResolver() {
	}
	
	public int recalculateAddress(int collided, int previousAddress)
	{					
		return previousAddress + (int)Math.pow(collided, 2.0);
	}
}
