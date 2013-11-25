package at.autobank.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import at.autobank.exception.AccountNotFoundException;
import at.autobank.exception.UnexpectedFormatException;
import at.autobank.reader.EuroLeaseReader;
import at.autobank.reader.EuroLeaseReaderImpl;

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
		if (args.length == 0) {
			exitAndOutputError("Missing input file argument.");
		}
		File importFile = new File(args[0]);
		if (!importFile.exists()) {
			exitAndOutputError("Could not find: " + args[0]);
		}
		FileInputStream fis = new FileInputStream(importFile);
		Scanner scanner = new Scanner(fis);
		// the new mutated flat file
		StringBuilder transformedFile = new StringBuilder();
		// old format reading helper
		EuroLeaseReader euroLeaseReader = new EuroLeaseReaderImpl();
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
					transformedFile.append(scannerString + ":");
					continue;
				}
				String result = euroLeaseReader
						.parseRequestString(scannerString);
				if (result == null) {
					continue;
				}
				transformedFile.append(result + ":");
				// Check FII -> close with PRC
				// System.out.println(scannerString);
			}
			System.out.println(transformedFile);
			File output = new File(args[1]);
			if (!output.canWrite()) {
				// cancel transaction and must be rerun
				System.out.println("could not write: " + output.getPath());
			} else {
				fileOutput = new BufferedWriter(new FileWriter(output.getPath() + "/" + "testOutput.l"));
				fileOutput.write(transformedFile.toString());
				fileOutput.close();
			}
		} catch (UnexpectedFormatException e) {
			// TODO handle exception
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
