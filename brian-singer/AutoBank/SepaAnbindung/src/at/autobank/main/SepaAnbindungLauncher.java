package at.autobank.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Scanner;

import at.autobank.dao.MySqlDatabaseSingleTon;
import at.autobank.dto.SepaTransformationTransaction;
import at.autobank.exception.AccountNotFoundException;
import at.autobank.exception.UnexpectedFormatException;
import at.autobank.reader.EuroLeaseReader;
import at.autobank.reader.EuroLeaseReaderImpl;

import com.j256.ormlite.logger.LocalLog;

/**
 * The application launcher. Configuration management and command line launcher.
 */
public class SepaAnbindungLauncher {

	/**
	 * Main application entry.
	 * 
	 * @param args
	 *            Input file directory
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// optional logging levels for db: debug, info, error
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "info");
		if (args.length == 0) {
			exitAndOutputError("Missing input file argument.");
		}
		File importFile = new File(args[0]);
		if (!importFile.exists()) {
			exitAndOutputError("Could not find: " + args[0]);
		}
		FileInputStream fis = new FileInputStream(importFile);
		Scanner scanner = new Scanner(fis);
		MySqlDatabaseSingleTon database = null;
		try {
			database = new MySqlDatabaseSingleTon(args[2], args[3], args[4]);
		} catch (SQLException e) {
			e.printStackTrace();
			exitAndOutputError("Could not initialise database.");
		}
		EuroLeaseReader euroLeaseReader = new EuroLeaseReaderImpl(database);
		// the new mutated flat file
		StringBuilder transformedFile = new StringBuilder();
		try {
			transformedFile.append(euroLeaseReader.readHeader(scanner));
		} catch (AccountNotFoundException e) {
			exitAndOutputError("Could not read account from Header.");
		}
		BufferedWriter fileOutput = null;
		try {
			scanner.useDelimiter(":");
			while (scanner.hasNext()) {
				String scannerString = scanner.next();
				if (scannerString.trim().isEmpty()) {
					if (!transformedFile.toString().endsWith("'")) {
						transformedFile.append(scannerString + ":");
					}
					continue;
				}
				String result = euroLeaseReader
						.parseRequestString(scannerString);
				if (result == null) {
					continue;
				}
				if (!euroLeaseReader.isEndOfField()) {
					transformedFile.append(result);
				} else if (scanner.hasNext()) {
					transformedFile.append(result + ":");
				} else {
					transformedFile.append(result);
				}
				if (euroLeaseReader.isSequenceCompleted()) {
					if (euroLeaseReader.getMissingAccounts() == null) {
						writeSequenceTransaction(euroLeaseReader, database);
					}
				}
			}
			System.out.println("Finished parsing sequences.");
			// System.out.println("DEBUG:" + transformedFile.toString());
			if (euroLeaseReader.getMissingAccounts() == null) {
				File output = new File(args[1]);
				if (!output.canWrite()) {
					// cancel transaction and must be rerun
					System.out.println("Could not write: " + output.getPath());
				} else {
					String outputFileName = importFile.getName() + "_"
							+ Calendar.getInstance().getTimeInMillis();
					fileOutput = new BufferedWriter(new FileWriter(
							output.getPath() + "/" + outputFileName));
					fileOutput.write(transformedFile.toString());
				}
			} else {
				database.rollback();
				File output = new File(args[1]);
				if (!output.canWrite()) {
					// cancel transaction and must be rerun
					System.out.println("could not write: " + output.getPath());
				} else {
					String accountNotFoundFileName = "accounts_not_found_" + Calendar.getInstance().getTimeInMillis()
							+ ".csv";
					fileOutput = new BufferedWriter(new FileWriter(
							output.getPath() + "/" + accountNotFoundFileName));
					String csvOutput = euroLeaseReader.formatCSVAccountsNotFoundList();
					fileOutput.write(csvOutput);
				}
			}
			database.closeDbConnection();
		} catch (UnexpectedFormatException e) {
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			fis.close();
			scanner.close();
			if (fileOutput != null) {
				fileOutput.close();
			}
		}
	}

	/**
	 * Write the sequence details to the tracking database.
	 * 
	 * @param reader
	 * @param database
	 * @throws SQLException
	 */
	private static void writeSequenceTransaction(EuroLeaseReader reader, MySqlDatabaseSingleTon database) throws SQLException {
		SepaTransformationTransaction transaction = new SepaTransformationTransaction();
		transaction.setMandateId(reader.getMandate().getMandateId());
		transaction.setKontonummer(reader.getKontonummer());
		transaction.setBankleitzahl(reader.getBankleitzahl());
		transaction.setBuchungstext(reader.getBuchungstext());
		transaction.setBuchungsbetrag(reader.getBuchungsbetrag());
		Calendar calendar = Calendar.getInstance();
		transaction.setDurchfuehrungsdatum(calendar.getTime());
		calendar.add(Calendar.DATE, 2);
		transaction.setValutadatum(calendar.getTime());
		database.insert(transaction);
	}

	/**
	 * Helper method to print and exit.
	 * 
	 * @param error
	 *            the error to print
	 */
	private static void exitAndOutputError(String error) {
		System.out.println(error);
		System.exit(0);
	}

}
