package at.autobank.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MandateView")
public class Mandate {

	@DatabaseField(columnName = "Mandats-ID", id = true)
	private String mandateId;

	@DatabaseField(columnName = "Creditor-ID")
	private String creditorId;

	@DatabaseField(columnName = "Kontonummer")
	private String kontonummer;

	@DatabaseField(columnName = "Bankleitzahl")
	private String bankleitzahl;

	@DatabaseField(columnName = "IBAN")
	private String iban;
	
	@DatabaseField(columnName = "Mandatsdatum")
	private String mandatsDatum;

	@DatabaseField(columnName = "Status")
	private String status;

	public String getMandatsDatum() {
		return mandatsDatum;
	}

	public void setMandatsDatum(String mandatsDatum) {
		this.mandatsDatum = mandatsDatum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMandateId() {
		return mandateId;
	}

	public void setMandateId(String mandateId) {
		this.mandateId = mandateId;
	}

	public String getCreditorId() {
		return creditorId;
	}

	public void setCreditorId(String creditorId) {
		this.creditorId = creditorId;
	}

	public String getKontonummer() {
		return kontonummer;
	}

	public void setKontonummer(String kontonummer) {
		this.kontonummer = kontonummer;
	}

	public String getBankleitzahl() {
		return bankleitzahl;
	}

	public void setBankleitzahl(String bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public void overrideMandateTable(String view) {

	}
}
