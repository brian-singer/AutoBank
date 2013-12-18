package at.autobank.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "IbanMapping")
public class IbanMapping {
	
	@DatabaseField(columnName = "ID", id = true)
	private Long id;
	
	@DatabaseField(columnName = "Kontonummer")
	private Long kontonummer;

	@DatabaseField(columnName = "IBAN")
	private String iban; 
	
	@DatabaseField(columnName = "Creditor-ID")
	private String creditorID; 

	public Long getKontonummer() {
		return kontonummer;
	}

	public String getIban() {
		return iban;
	}
	
	public String getCreditorId() {
		return creditorID;
	}

}
