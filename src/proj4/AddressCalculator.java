package edu.dccc.dpr212.vehiclevanager;

public interface AddressCalculator {	
	
	/* Given an object, this class' job is to calculate an
	 * address in the hash table.
	 */
		
	public int calculateAddress(Object element);
	
}