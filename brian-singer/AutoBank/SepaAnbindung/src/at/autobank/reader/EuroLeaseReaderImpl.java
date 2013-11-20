package at.autobank.reader;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.autobank.exception.AccountNotFoundException;
import at.autobank.exception.UnexpectedFormatException;

public class EuroLeaseReaderImpl implements EuroLeaseReader {

	private Integer accountNumber;
	private boolean newRequest = true;
	private int sequence = 1;
	private boolean feld1159 = false;
	private boolean feld3194 = false;
	private boolean finished3194 = false;

	public String readHeader(final Scanner scanner) throws AccountNotFoundException {
		String line = scanner.findInLine("^UNA.*?1030'");
		System.out.println(line);
		Pattern pattern = Pattern.compile("(\\+)(MR)(\\+)(\\d)*(\\+)");
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			String value = matcher.group();
			System.out.println(value);
			pattern = Pattern.compile("(\\d+)");
			matcher = pattern.matcher(value);
			if (matcher.find()) {
				value = matcher.group();
				System.out.println("Dummy replacement for account: " + value);
				accountNumber = Integer.valueOf(value);
			} else {
				throw new AccountNotFoundException();
			}
		} else {
			// fail because we cant find the account information
			throw new AccountNotFoundException();
		}
		// add below code to helper function and lookup account to replace
		line = line.replaceFirst("(\\+)(MR)(\\+)(\\d)*(\\+)", "+MR+00XX00+");
		System.out.println("Replaced dummy account 00XX00");
		System.out.println(line);
		line = line.replaceFirst("(\\+)(OR)(\\+)(\\d)*(\\:)", "+OR+00XXX00:");
		System.out.println(line);
		System.out.println("Finished parsing header");
		return line;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public void resetReader() {
		accountNumber = null;
	}

	private void newSequence() {
		sequence++;
		feld1159 = false;
		feld3194 = false;
		finished3194 = false;
	}

	public String parseRequestString(final String requestString) throws UnexpectedFormatException {
		if (newRequest) {
			if (!requestString.contains("SEQ")) {
				throw new UnexpectedFormatException("The file does not contain a start sequence.");
			}
			newRequest = false;
			return requestString;
		}
		if (requestString.contains("SEQ")) {
			newSequence();
			System.out.println("Started parsing a new sequence.");
			return requestString;
		}
		if (!feld1159) {
			feld1159 = true;
			System.out.println("Possibly replacing Feld1159");
			return requestString;
		}
		if (!feld3194 && requestString.contains("FII+PH")) {
			return processFeld3194(requestString);
		}
		if (feld3194 && !finished3194 && requestString.contains("PRC+11")) {
			return processEndFeld3194(requestString);
		} else if (feld3194 && !finished3194) {
			return null;
		}
		return requestString;
	}

	private String processFeld3194(String field) throws UnexpectedFormatException {
		Pattern pattern = Pattern.compile("(\\d+)(\\')");
		Matcher matcher = pattern.matcher(field);
		String transformed;
		if (matcher.find()) {
			feld3194 = true;
			transformed = matcher.group();
		} else {
			throw new UnexpectedFormatException("Unexpected format parsing Feld3194");
		}
		// read account number
		pattern = Pattern.compile(".*?\\d+.*?(\\d+)");
		matcher = pattern.matcher(field);
		if (matcher.find()) {
			System.out.println("Feld3194 account number#" + matcher.group(1));
		}
		String dummyAccount = "AT666Dummy";
		return transformed + "FII+PH+" + dummyAccount + ":Witowetz::EUR'";
	}

	private String processEndFeld3194(String field) throws UnexpectedFormatException {
		Pattern pattern = Pattern.compile(".*?(PRC)*");
		Matcher matcher = pattern.matcher(field);
		if (matcher.find()) {
			finished3194 = true;
			return matcher.group();
		} else {
			throw new UnexpectedFormatException("could not format end of Feld3194");
		}
	}

	public int getCurrentSequenceNumber() {
		return sequence;
	}

}
