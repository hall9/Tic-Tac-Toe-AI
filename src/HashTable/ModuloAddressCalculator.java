package HashTable;

public class ModuloAddressCalculator implements AddressCalculator {

	int range;

	public ModuloAddressCalculator(int newRange) {
		range = newRange;
	}

	public int calculateAddress(Object element) {

		Integer inElement = (Integer) element;

		return inElement.intValue() % range;
	}

}
