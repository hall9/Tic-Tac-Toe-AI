package HashTable;

public class ModuloAddressCalculator implements AddressCalculator {

	int range;

	public ModuloAddressCalculator(int newRange) {
		range = newRange;
	}

	public int calculateAddress(Comparable element) {

		Long inElement = (Long) element;

		return (int) (inElement.longValue() % range);
	}

}
