package HashTable;

public interface CollisionResolver {

	public int recalculateAddress(int collided, int previousAddress, int range);
	
}
