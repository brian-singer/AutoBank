package at.autobank.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import at.autobank.bean.RedmineMandate;
import at.autobank.reader.MandateDataCSVReader;

public class EuroLeaseReaderTest {

	/**
	 * @param args the CSV path/location/file.csv
	 */
	public static void main(String[] args) {
		File mandateData = new File(args[0]);
		if (!mandateData.exists()) {
			System.out.println("Missing mandate CSV file.");
		}
		MandateDataCSVReader csvReader = new MandateDataCSVReader(mandateData);
		List<RedmineMandate> mandateList = null;
		try {
			mandateList = csvReader.getMandateList();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mandateList != null && mandateList.isEmpty()) {
			System.out.println("Mandate list is empty.");
		} else {
			System.out.println("Success!");
		}
	}

}
