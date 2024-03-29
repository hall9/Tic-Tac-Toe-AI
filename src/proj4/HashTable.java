package proj4;

import proj4.AddressCalculator;
import proj4.CollisionResolver;
import proj4.Node;

public class HashTable {
	/*
	 * PUBLIC INTERFACE
	 */

	int counter = 0;
	
	int collisions = 0;

	private Node[] primaryArea;

	private Node[] overflowArea;

	private AddressCalculator addressCalculator = null;

	private CollisionResolver collisionResolver = null;
	
	private int Size;
	
	private int overFlowSize;

	/**
	 * Constructor for the hash table, craetes the arrays for the hash table
	 * 
	 * @param newAddressCalculator
	 * @param newCollisionResolver
	 * @param newSize
	 */
	public HashTable(AddressCalculator newAddressCalculator,
			CollisionResolver newCollisionResolver, int newSize) {
		addressCalculator = newAddressCalculator;
		collisionResolver = newCollisionResolver;
		Size = newSize;
		primaryArea = new Node[Size];
		overFlowSize = nextPrime(Size/2);
		overflowArea = new Node[ overFlowSize ];
		makeEmpty();
	}

	/**
	 * To add a entry to the hash table
	 * 
	 * @param key
	 * @param data
	 */
	public void addNode(Comparable key, Object data) {
		/*
		 * Add the object specified by the value parameter to the tree. The
		 * value of the key parameter is used to determine location of the new
		 * data within the tree.
		 */

		int homeAddress = addressCalculator.calculateAddress(key);
		int collided = 0;
		boolean overFlow = false;
		boolean collision = false;
		
		if (primaryArea[homeAddress] != null && findElement(key) == null) {
			int c = key.compareTo(primaryArea[homeAddress].key);
			
			if (c != 0 ) {
				collision = true;
			}
		}

		int overFlowAddress = -1;
		while (collision) {
			overFlowAddress = collisionResolver.recalculateAddress(++collided,
					homeAddress, overFlowSize);
			
			//System.out.println( homeAddress +  "  "+ (homeAddress +  + (int)Math.pow(collided, 2.0) ) );
			
			collision = overflowArea[overFlowAddress] != null;
			
			if (overflowArea[overFlowAddress] != null) {
				int c = key.compareTo(overflowArea[overFlowAddress].key);
				
				if (c == 0 ) {
					collision = false;
				}
			}
			
			collisions += 1;
			overFlow = true;
		}
		if (overFlow) {
			overflowArea[overFlowAddress] = new Node(key, data);
			counter += 1;
		} else {
			primaryArea[homeAddress] = new Node(key, data);
			counter += 1;
		}
	}
	
	/**
	 * To update a Node without deactiving it
	 * 
	 * @param key
	 * @param data
	 */
	public void setNode (Comparable key, Object data) {
		int homeAddress = addressCalculator.calculateAddress(key);
		int collided = 0;
		int overFlowAddress = collisionResolver.recalculateAddress(collided+1,
				homeAddress, overFlowSize);
		
		boolean collision = false;
		
		if (primaryArea[homeAddress] != null) {
			collision = primaryArea[homeAddress].data != data;
		}

	while (collision) {
		overFlowAddress = collisionResolver.recalculateAddress(++collided,
				homeAddress, overFlowSize);
		if ( overflowArea[overFlowAddress] != null ) {
			collision = overflowArea[overFlowAddress].data != data;
		}
	}
	if (!collision) {
		if (collided == 0) {
			primaryArea[homeAddress] = null;
		} else {
			overflowArea[overFlowAddress] = null;
		}
	}
	
	addNode(key, data);
		
	}

	/**
	 * To remove an entry from the hash table
	 * 
	 * @param key
	 * @param data
	 */
	public void removeNode(Comparable key, Object data) {
		int homeAddress = addressCalculator.calculateAddress(key);
		int collided = 0;
		int overFlowAddress = collisionResolver.recalculateAddress(collided+1,
				homeAddress, overFlowSize);

		boolean collision = false;
			
			if (primaryArea[homeAddress] != null) {
				collision = primaryArea[homeAddress].data != data;
			}

		while (collision) {
			overFlowAddress = collisionResolver.recalculateAddress(++collided,
					homeAddress, overFlowSize);
			if ( overflowArea[overFlowAddress] != null ) {
				collision = overflowArea[overFlowAddress].data != data;
			}
		}
		if (!collision) {
			if (collided == 0) {
				primaryArea[homeAddress] = null;

				if (overflowArea[overFlowAddress] != null) {
					primaryArea[homeAddress] = overflowArea[overFlowAddress];
					overflowArea[overFlowAddress] = null;
				}
			} else {
				overflowArea[overFlowAddress] = null;
			}
		}
	}

	/**
	 * To Find an element within the hash table
	 * 
	 * @param key
	 * @return
	 */
	public Object findElement(Comparable key) {
		/*
		 * Locates the specified key value within the tree and returns the
		 * corresponding data value.
		 */

		Object found = null;
		int homeAddress = addressCalculator.calculateAddress(key);

		if (primaryArea[homeAddress] != null) {
			int c = key.compareTo(primaryArea[homeAddress].key);

			if (c == 0) {
				found = primaryArea[homeAddress].data;
			} else {
				found = collisionSearch(key, homeAddress, 1);
			}
		}

		return found;
	}

	private Object collisionSearch(Comparable key, int homeAddress, int collided) {
		Object found = null;
		int overFlowAddress = collisionResolver.recalculateAddress(collided,
				homeAddress, overFlowSize);

		if (overflowArea[overFlowAddress] != null) {
			int c = key.compareTo(overflowArea[overFlowAddress].key);

			if (c == 0) {
				found = overflowArea[overFlowAddress].data;
			} else {
				collisionSearch(key, homeAddress, ++collided);
			}
		}

		return found;
	}
	
	/**
     * Internal method to find a prime number at least as large as n.
     * @param n the starting number (must be positive).
     * @return a prime number larger than or equal to n.
     */
    private static int nextPrime( int n )
    {
        if( n <= 0 )
            n = 3;

        if( n % 2 == 0 )
            n++;

        for( ; !isPrime( n ); n += 2 )
            ;

        return n;
    }

    /**
     * Internal method to test if a number is prime.
     * Not an efficient algorithm.
     * @param n the number to test.
     * @return the result of the test.
     */
    private static boolean isPrime( int n )
    {
        if( n == 2 || n == 3 )
            return true;

        if( n == 1 || n % 2 == 0 )
            return false;

        for( int i = 3; i * i <= n; i += 2 )
            if( n % i == 0 )
                return false;

        return true;
    }

	/*
	 * PRIVATE ATTRIBUTES AND HELPER METHODS
	 */
    
    public Object getNexttoDump () {
    	Object nextObj = null;
    	int location = 0;
    	
    	if (primaryArea.length != 0) {
    		while(primaryArea[location] == null && location != Size) {
    			if (location + 1 < primaryArea.length  ) {
    				location++;
    			}
    		}
    		
    		if (primaryArea[location] != null) {
				nextObj = primaryArea[location].data;
				removeNode(primaryArea[location].key, primaryArea[location].data);
				
				location++;
			}
    		else {
    		
    		}
    	}
    	
    	return nextObj;
    }

	public int getSize() {
		/* Returns the number of elements in the array. */
		return counter;
	}
	
	public int getNumCollisions() {
		/* Returns the number of collisions that happened. */
		return collisions;
	}
	
	 /**
     * Make the hash table logically empty.
     */
    public void makeEmpty( )
    {
        counter = 0;
        for( int i = 0; i < primaryArea.length; i++ ) {
        	primaryArea[ i ] = null;
        }
        
        for( int i = 0; i < overflowArea.length; i++ ) {
        	overflowArea[ i ] = null;
        }
    }

	public boolean isEmpty() {
		/* Returns true if the list is empty. Otherwise it returns false */
		return (counter == 0);
	}

	public boolean isfull() {
		/* Returns true if the list is full. Returns false to otherwise. */
		return (false);
	}
}
