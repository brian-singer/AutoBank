package at.autobank.reader;

import java.util.Scanner;

import at.autobank.exception.AccountNotFoundException;
import at.autobank.exception.UnexpectedFormatException;

/**
 * Interface class for reading the EUROLease flat file.
 */
public interface EuroLeaseReader {
	/**
	 * 
	 * @param scanner
	 * @return
	 * @throws AccountNotFoundException
	 */
	public String readHeader(Scanner scanner) throws AccountNotFoundException;

	/**
	 * 
	 */
	public void resetReader();

	public int getAccountNumber();

	public String parseRequestString(String requestString) throws UnexpectedFormatException;
}
