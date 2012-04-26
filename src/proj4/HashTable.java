package edu.dccc.dpr212.containers;

import edu.dccc.dpr212.vehiclevanager.AddressCalculator;
import edu.dccc.dpr212.vehiclevanager.CollisionResolver;

public class HashTable {
	/*
	 * PUBLIC INTERFACE
	 */

	int counter = 0;

	private Node[] primaryArea = new Node[200];

	private Node[] overflowArea = new Node[100];

	private AddressCalculator addressCalculator = null;

	private CollisionResolver collisionResolver = null;

	public HashTable(AddressCalculator newAddressCalculator,
			CollisionResolver newCollisionResolver) {
		addressCalculator = newAddressCalculator;
		collisionResolver = newCollisionResolver;
	}

	public void addNode(Comparable key, Object data) {
		/*
		 * Add the object specified by the value parameter to the tree. The
		 * value of the key parameter is used to determine location of the new
		 * data within the tree.
		 */

		int homeAddress = addressCalculator.calculateAddress(key);
		int collided = 0;
		boolean overFlow = false;
		boolean collision = primaryArea[homeAddress] != null;

		int overFlowAddress = -1;
		while (collision) {
			overFlowAddress = collisionResolver.recalculateAddress(++collided,
					homeAddress);
			collision = overflowArea[overFlowAddress] != null;

			overFlow = true;
		}
		if (overFlow) {
			overflowArea[overFlowAddress] = new Node(key, data);
		} else {
			primaryArea[homeAddress] = new Node(key, data);
		}
	}

	public void removeNode(Comparable key, Object data) {
		int homeAddress = addressCalculator.calculateAddress(key);
		int collided = 0;
		int overFlowAddress = collisionResolver.recalculateAddress(++collided,
				homeAddress);

		boolean collision = primaryArea[homeAddress].data != data;

		while (collision) {
			overFlowAddress = collisionResolver.recalculateAddress(collided++,
					homeAddress);
			collision = overflowArea[overFlowAddress].data != data;
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
		int overFlowAddress = collisionResolver.recalculateAddress(collided++,
				homeAddress);

		if (overflowArea[overFlowAddress] != null) {
			int c = key.compareTo(overflowArea[overFlowAddress].key);

			if (c == 0) {
				found = overflowArea[overFlowAddress].data;
			} else {
				collisionSearch(key, homeAddress, collided);
			}
		}

		return found;
	}

	/*
	 * PRIVATE ATTRIBUTES AND HELPER METHODS
	 */

	public int getSize() {
		/* Returns the number of elements in the array. */
		return counter;
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
