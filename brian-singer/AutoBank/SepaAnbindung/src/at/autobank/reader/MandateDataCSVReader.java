package at.autobank.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import at.autobank.bean.RedmineMandate;

/**
 * A temporary reader for CSV parsing mandate data.
 */
public class MandateDataCSVReader {
	/** the CSV mandate file. */
	private File mandateData;

	public MandateDataCSVReader(File file) {
		mandateData = file;
	}

	/**
	 * Reads the mandate CSV file.
	 * 
	 * @return the list of mandates.
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public List<RedmineMandate> getMandateList()
			throws JsonProcessingException, IOException {
		CsvMapper csvMapper = new CsvMapper();
		CsvSchema bootstrap = csvMapper.schemaFor(RedmineMandate.class).withColumnSeparator(';');
		MappingIterator<Entry<?,?>> mappingIterator = csvMapper
				.reader(RedmineMandate.class).with(bootstrap)
				.readValues(mandateData);
		if (!mappingIterator.hasNext()) {
			return new ArrayList<RedmineMandate>();
		}
		List<RedmineMandate> mandates = new ArrayList<RedmineMandate>();
		while (mappingIterator.hasNextValue()) {
			mandates.add((RedmineMandate)mappingIterator.nextValue());
		}
		return mandates;
	}
}
