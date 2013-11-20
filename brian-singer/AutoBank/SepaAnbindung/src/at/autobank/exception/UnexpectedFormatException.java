package at.autobank.exception;

/**
 * Exception class for unhandled formatting.
 */
public class UnexpectedFormatException extends Throwable {

	/** sid. */
	private static final long serialVersionUID = 6587684998798921411L;

	/**
	 * Default constructor.
	 * 
	 * @param exception
	 */
	public UnexpectedFormatException(final String exception) {
		super(exception);
	}
}
