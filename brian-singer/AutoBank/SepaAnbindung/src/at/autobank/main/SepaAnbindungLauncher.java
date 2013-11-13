package at.autobank.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * The application launcher. Configuruation management and
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
		try {
			scanner.useDelimiter(":");
			scanner.skip("^UNA.*?1030'");
			while (scanner.hasNext()) {
				String scannerString = scanner.next();
				if (scannerString.trim().isEmpty()) {
					continue;
				}
				System.out.println(scannerString);
			}
			fis.close();
			scanner.close();
		} finally {
			fis.close();
			scanner.close();
		}
	}

	/**
	 * Helper method to print and exit.
	 * 
	 * @param error
	 */
	private static void exitAndOutputError(String error) {
		System.out.println(error);
		System.exit(0);
	}

}
