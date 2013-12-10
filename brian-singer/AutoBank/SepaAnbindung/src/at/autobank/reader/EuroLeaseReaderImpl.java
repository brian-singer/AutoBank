package at.autobank.reader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.autobank.bean.RedmineMandate;
import at.autobank.dao.MySqlDatabaseSingleTon;
import at.autobank.dto.Mandate;
import at.autobank.dto.SepaTransformationTransaction;
import at.autobank.exception.AccountNotFoundException;
import at.autobank.exception.UnexpectedFormatException;

/**
 * The main implementation for the flat file parsing for the EUROLease system.
 * 
 * @version 1.0
 * 
 */
public class EuroLeaseReaderImpl implements EuroLeaseReader {

	/** the database controller. */
	MySqlDatabaseSingleTon database;
	/** temporary holder for the mandates until database connection is available. */
	private List<RedmineMandate> mandates;
	/** flag indicating a new request is being parsed. */
	private boolean newRequest = true;
	/** flag indicating if the current sequence is completed. */
	private boolean sequenceCompleted = false;
	/** the current sequence being processed. */
	private int sequence = 1;
	/** sequence transformation. */
	private boolean sequenceTransformed = false;
	/** a flag indicating if the current sequence parsed field 3194. */
	private boolean feld3194 = false;
	/** a flag inidicating field 3194 is finished. */
	private boolean finished3194 = false;
	/** a flag inidicating the bankleitzahl was found. */
	private boolean blzFound = false;
	/** the account number for the current sequence. */
	private Long accountNumber;
	/** the BLZ for the current sequence. */
	private Integer blz;
	/** a flag indicating if the contact from the (FII) field has been set. */
	private boolean contactSet = false;
	/** the contact for the payment (FII). */
	private String contact;
	/** the current mandate being processed. */
	private Mandate mandate = null;
	/** a flag to indicate if a mandate is recurring. */
	private boolean isMandateRecurring;
	/** Preserve the old (FTX+PMD). */
	private boolean ftxPmd = false;
	/** the booking amount. */
	private String buchungsbetrag;
	/** the buchungstext. */
	private StringBuilder buchungstext;

	/**
	 * a flag indicating if the field ends or its partially returned (E.g.
	 * REF+CR number).
	 */
	private boolean isEndOfField = true;
	/** Accounts not found. */
	private List<Mandate> missingAccounts = null;

	/**
	 * Default constructor. Note: temporary constructor until DB connection.
	 * 
	 * @param mandateList
	 *            the list of mandates in the CSV file
	 */
	@Deprecated
	public EuroLeaseReaderImpl(List<RedmineMandate> mandateList) {
		mandates = mandateList;
	}

	/**
	 * The new default constructor.
	 * 
	 * @param databaseToSet
	 */
	public EuroLeaseReaderImpl(MySqlDatabaseSingleTon databaseToSet) {
		database = databaseToSet;
	}

	/**
	 * {@inheritDoc}
	 */
	public String readHeader(final Scanner scanner)
			throws AccountNotFoundException {
		String line = scanner.findInLine("^UNA.*?1030'");
		Pattern pattern = Pattern.compile("(\\+)(MR)(\\+)(\\d)*(\\+)");
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			String value = matcher.group();
			pattern = Pattern.compile("(\\d+)");
			matcher = pattern.matcher(value);
			if (matcher.find()) {
				value = matcher.group();
				accountNumber = Long.valueOf(value);
			} else {
				throw new AccountNotFoundException();
			}
		} else {
			// fail because we cant find the account information
			throw new AccountNotFoundException();
		}
		// add below code to helper function and lookup account to replace
		line = line.replaceFirst("(\\+)(MR)(\\+)(\\d)*(\\+)",
				"+MR+AT591967500301001400+");
		line = line.replaceFirst("(\\+)(OR)(\\+)(\\d)*(\\:)",
				"+OR+AT591967500301001400:");
		System.out.println("Finished parsing header");
		return line;
	}

	/**
	 * {@inheritDoc}
	 */
	public void resetReader() {
		// TODO
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSequenceCompleted() {
		return sequenceCompleted;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Mandate> getMissingAccounts() {
		return missingAccounts;
	}

	/**
	 * {@inheritDoc}
	 */
	public String formatCSVAccountsNotFoundList() {
		String header = "KontoNummer;Bankleitzahl";
		StringBuilder csv = new StringBuilder(header);
		for (Mandate account : missingAccounts) {
			csv.append("\n");
			csv.append(account.getKontonummer());
			csv.append(";");
			csv.append(account.getBankleitzahl());
		}
		return csv.toString();
	}

	/**
	 * new sequence resets.
	 */
	private void newSequence() {
		sequence++;
		sequenceTransformed = false;
		feld3194 = false;
		finished3194 = false;
		blzFound = false;
		contactSet = false;
		ftxPmd = false;
		isMandateRecurring = false;
		isEndOfField = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String parseRequestString(final String requestString)
			throws UnexpectedFormatException, AccountNotFoundException, SQLException {
		// System.out.println("DEBUG----" + requestString);
		if (newRequest) {
			if (!requestString.contains("SEQ")) {
				throw new UnexpectedFormatException(
						"The file does not contain a start sequence.");
			}
			System.out.println("Started parsing a new sequence#" + sequence);
			newRequest = false;
			return requestString;
		}
		if (requestString.contains("SEQ")) {
			sequenceCompleted = true;
			newSequence();
			System.out.println("Inserting Feld 4440.");
			String seqenceString = insertFeld4440(requestString, true);
			System.out.println("Started parsing a new sequence#" + sequence);
			return seqenceString;
		}
		if (requestString.contains("CNT+1")) {
			sequenceCompleted = true;
			return insertFeld4440(requestString, false);
		}
		sequenceCompleted = false;
		if (!sequenceTransformed) {
			sequenceTransformed = true;
			if (requestString.equals("83")) {
				return "82";
			} else {
				return requestString;
			}
		}
		if (!feld3194 && requestString.contains("FII+PH")) {
			feld3194 = true;
			isEndOfField = false;
			return processFeld3194(requestString);
		} else if (!feld3194) {
			return requestString;
		}
		isEndOfField = true;
		if (!contactSet) {
			contactSet = true;
			contact = requestString;
			return null;
		}
		if (!finished3194) {
			if (requestString.contains("EUR")) {
				// next string is BLZ
				blzFound = true;
				return null;
			} else if (blzFound) {
				finished3194 = true;
				blz = Integer.valueOf(requestString);
				mandate = requestMandate();
				if (mandate == null) {
					System.out.println("KN: " + accountNumber + " BLZ:" + blz
							+ " not found.");
					if (missingAccounts == null) {
						missingAccounts = new ArrayList<Mandate>();
					}
					mandate = new Mandate();
					mandate.setKontonummer(accountNumber.toString());;
					mandate.setBankleitzahl(blz.toString());
					if (!missingAccounts.contains(mandate)) {
						missingAccounts.add(mandate);
					}
				}
				isEndOfField = false;
				return createFeld3194();
			} else {
				return null;
			}
		}
		isEndOfField = true;
		if (finished3194 && requestString.contains("FTX+PMD")) {
			ftxPmd = true;
			buchungstext = new StringBuilder("FTX+PMD+++INTNR?");
			return "FTX+PMD+++INTNR?";
		} else if (finished3194 && !ftxPmd) {
			return null;
		}
		if (!requestString.contains("EUR")) {
			buchungstext.append(":");
			buchungstext.append(requestString);
		}
		return requestString;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEndOfField() {
		return isEndOfField;
	}

	/**
	 * {@inheritDoc}
	 */
	public Mandate getMandate() {
		return mandate;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getBuchungsbetrag() {
		return buchungsbetrag;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getBuchungstext() {
		return buchungstext.toString();
	}

	public Long getKontonummer() {
		return accountNumber; 
	}

	public Integer getBankleitzahl() {
		return blz;
	}

	/**
	 * Parse the field 3194. Add account number to this sequence.
	 * 
	 * @param field
	 *            the field to be parsed
	 * @throws UnexpectedFormatException
	 */
	private String processFeld3194(String field)
			throws UnexpectedFormatException {
		Pattern pattern = Pattern.compile("(\\d+)(\\')");
		Matcher matcher = pattern.matcher(field);
		String transformed;
		if (matcher.find()) {
			transformed = matcher.group();
		} else {
			throw new UnexpectedFormatException(
					"Unexpected format parsing Feld3194");
		}
		// read account number
		pattern = Pattern.compile(".*?\\d+.*?(\\d+)");
		matcher = pattern.matcher(field);
		if (matcher.find()) {
			System.out.println("Feld3194 account number#" + matcher.group(1));
			accountNumber = Long.valueOf(removeLeadingZeroes(matcher.group(1)));
		} else {
			throw new UnexpectedFormatException(
					"Could not find account number for sequence#" + sequence);
		}
		// REF+CR number
		return transformed;
	}

	/**
	 * @return the transformed (FII+PH) field3194
	 */
	private String createFeld3194() {
		StringBuilder field = new StringBuilder("FII+PH+");
		field.append(mandate.getIban());
		field.append(":");
		field.append(contact);
		field.append("::EUR'");
		field.append("PRC+11'");
		return field.toString();
	}

	/**
	 * Helper function to remove 0's preceding account numbers for Long parsing.
	 * 
	 * @param accountNumber
	 *            the string to parse
	 * @return the transformed string
	 */
	private String removeLeadingZeroes(String accountNumber) {
		Pattern p = Pattern.compile("^(0+)(\\d+)"); // leading zeroes
		Matcher m = p.matcher(accountNumber);
		if (m.find()) {
			accountNumber = m.group(2);
		}
		return accountNumber;
	}

	/**
	 * Insert the field 4440.
	 * 
	 * @param field
	 *            the field to transform
	 * @param sequence
	 *            if sequence or else close of file (CNT)
	 * @return the transformed field
	 * @throws UnexpectedFormatException
	 */
	private String insertFeld4440(String field, boolean sequence)
			throws UnexpectedFormatException {
		String transformedField;
		Pattern pattern = Pattern.compile("(\\d+)(,)(\\d+)");
		Matcher matcher = pattern.matcher(field);
		if (matcher.find()) {
			transformedField = matcher.group();
			buchungsbetrag = transformedField;
			StringBuilder fieldTransformer = new StringBuilder(transformedField);
			fieldTransformer.append("'FTX+PMD+++MID/");
			fieldTransformer.append(mandate.getMandateId());
			fieldTransformer.append(":CI/");
			fieldTransformer.append(mandate.getCreditorId());
			fieldTransformer.append(":SIGN/");
			fieldTransformer.append(mandate.getMandatsDatum());
			fieldTransformer.append("/SEQT/");
			if (isMandateRecurring) {
				fieldTransformer.append("RCUR");
			} else {
				fieldTransformer.append("FRST'");
			}
			if (sequence) {
				fieldTransformer.append("SEQ++" + this.sequence);
			} else {
				fieldTransformer.append("CNT+1");
			}
			return fieldTransformer.toString();
		} else {
			throw new UnexpectedFormatException("Inserting Feld 4440 failed.");
		}
	}

	/**
	 * Scan the list of mandates. Note: temporary helper until DB connection is
	 * available.
	 * 
	 * @param kontoNummer
	 *            the account number
	 * @param bank
	 *            the BLZ
	 * @return the mandate from the Redmine system
	 */
	@Deprecated
	private RedmineMandate scanForMandate() {
		for (RedmineMandate mandate : mandates) {
			if (mandate.getKontoNummber().equals(accountNumber)
					&& mandate.getBankleitzahl().equals(blz)) {
				return mandate;
			}
		}
		return null;
	}

	/**
	 * Find a valid mandate for the current sequence.
	 * 
	 * @return the mandate or null if no valid mandate was found
	 * @throws SQLException
	 */
	private Mandate requestMandate() throws SQLException {
		Mandate mandate = null;
		SepaTransformationTransaction transaction = database.getLastMandate(accountNumber, blz);
		if (transaction != null) {
			mandate = database.getValidMandate(transaction.getMandateId());
			if (mandate != null) {
				isMandateRecurring = true;
				return mandate;
			}
		}
		return database.selectMandate(accountNumber, blz);
	}

}
