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
	 * 
	 * @param Expects the String to be in the form: "id;value"
	 */
	public Transaction(String summary){
		String[] split = summary.split(";");
		key = Integer.parseInt(split[0]);
		value = Integer.parseInt(split[1]);
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
