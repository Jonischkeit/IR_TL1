package creditcard;

/**
 * Repraesentiert eine Zeile der Eingabedatei, also eine Transaktion bestehend
 * aus Kreditkartennummer und Umsatz.
 * 
 * @author Daniel Blank
 *
 */
public class Transaction {

	// Kreditkartennummer
	private int key;
	// Geldbetrag (Umsatz)
	private int value;

	/**
	 * Constructor.
	 * 
	 * @param keyString Kreditkartennummer.
	 * @param valString Geldbetrag (Umsatz).
	 */
	public Transaction(String idString, String valString) {
		this.key = new Integer(idString).intValue();
		this.value = new Integer(valString).intValue();
	}

	/**
	 * Returns key.
	 * @return Kreditkartennummer.
	 */
	public int getKey() {
		return key;
	}

	/**
	 * Return Value.
	 * @return Geldbetrag (Umsatz).
	 */
	public int getValue() {
		return value;
	}
}
