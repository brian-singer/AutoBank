package at.autobank.exception;

/**
 * Exception class for cases where an account number is not found.
 */
public class AccountNotFoundException extends Throwable {

	/** sid. */
	private static final long serialVersionUID = 2666571898235486558L;

	/**
	 * Default exception. Header account was not found.
	 */
	public AccountNotFoundException() {
		super("Header account not found.");
	}

	/**
	 * Detailed exception.
	 * 
	 * @param accountInformation
	 *            the account not found.
	 */
	public AccountNotFoundException(String accountInformation) {
		super(accountInformation);
	}
}
