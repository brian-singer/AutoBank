package at.autobank.reader;

import java.util.List;
import java.util.Scanner;

import at.autobank.bean.RedmineMandate;
import at.autobank.exception.AccountNotFoundException;
import at.autobank.exception.UnexpectedFormatException;

/**
 * Interface class for reading the EUROLease flat file.
 */
public interface EuroLeaseReader {
	/**
	 * Read the header of the flat file.
	 * 
	 * @param scanner
	 *            the scanner containing the header
	 * @return the new Header string
	 * @throws AccountNotFoundException
	 */
	public String readHeader(Scanner scanner) throws AccountNotFoundException;

	/**
	 * Possible useful later for parsing multiple files.
	 */
	public void resetReader();

	/**
	 * Main reader method to parse the current line of a sequence.
	 * 
	 * @param requestString
	 *            the current string to be processed for the current sequence
	 * @return the transformed, same, or <code>null</code> <code>String</code>
	 *         is returned
	 * @throws UnexpectedFormatException
	 */
	public String parseRequestString(String requestString)
			throws UnexpectedFormatException, AccountNotFoundException;

	/**
	 * A helper method to indicate if only a partial field is returned by the
	 * method {@link #parseRequestString(String)}
	 * 
	 * @return true if the field should end with a ':'
	 */
	public boolean isEndOfField();

	/**
	 * A flag indicating if the sequence is completed.
	 * Helper method for logging individual sequences.
	 * @return true if the current sequence is complete
	 */
	public boolean isSequenceCompleted();

	/**
	 * Get missing accounts found when parsing EuroLease file.
	 * 
	 * @return null if all accounts were found or the list of missing accounts
	 */
	public List<RedmineMandate> getMissingAccounts();

	/**
	 * Helper method to format the accounts not found to a CSV file.
	 * 
	 * @return the string output
	 */
	public String formatCSVAccountsNotFoundList();

	/**
	 * @return the mandate for the current sequnce.
	 */
	public RedmineMandate getMandate();

	/**
	 * @return the FTX+PMD entry
	 */
	public String getBuchungstext();

	/**
	 * @return the sequence charged amount
	 */
	public String getBuchungsbetrag();
}
