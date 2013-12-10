package at.autobank.dto;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "EuroleaseSepaTransaction")
public class SepaTransformationTransaction {

	@DatabaseField(generatedId = true, columnName = "Tracking-ID")
	private int id;

	@DatabaseField(columnName = "Mandats-ID")
	private String mandateId;

	@DatabaseField(columnName = "Kontonummer")
	private Long kontonummer;

	@DatabaseField(columnName = "Bankleitzahl")
	private Integer bankleitzahl; 

	@DatabaseField(columnName = "Durchfuehrungsdatum")
	private Date durchfuehrungsdatum;

	@DatabaseField(columnName = "Buchungstext")
	private String buchungstext;

	@DatabaseField(columnName = "Buchungsbetrag")
	private String buchungsbetrag;

	@DatabaseField(columnName = "Valutadatum")
	private Date valutadatum;

	public int getId() {
		return id;
	}

	public String getMandateId() {
		return mandateId;
	}

	public void setMandateId(String mandateId) {
		this.mandateId = mandateId;
	}

	public Long getKontonummer() {
		return kontonummer;
	}

	public void setKontonummer(Long kontonummer) {
		this.kontonummer = kontonummer;
	}

	public Integer getBankleitzahl() {
		return bankleitzahl;
	}

	public void setBankleitzahl(Integer bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
	}

	public Date getDurchfuehrungsdatum() {
		return durchfuehrungsdatum;
	}

	public void setDurchfuehrungsdatum(Date durchfuehrungsdatum) {
		this.durchfuehrungsdatum = durchfuehrungsdatum;
	}

	public String getBuchungstext() {
		return buchungstext;
	}

	public void setBuchungstext(String buchungstext) {
		this.buchungstext = buchungstext;
	}

	public String getBuchungsbetrag() {
		return buchungsbetrag;
	}

	public void setBuchungsbetrag(String buchungsbetrag) {
		this.buchungsbetrag = buchungsbetrag;
	}

	public Date getValutadatum() {
		return valutadatum;
	}

	public void setValutadatum(Date valutadatum) {
		this.valutadatum = valutadatum;
	}

}
