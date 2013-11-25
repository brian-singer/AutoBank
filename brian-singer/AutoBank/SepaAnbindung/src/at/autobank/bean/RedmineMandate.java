package at.autobank.bean;

import java.util.Date;

/**
 * Data Transaction object for accessing the mandate database. Version 1.0 is
 * with a dynamic csv file
 * 
 * @version 1.0
 * 
 */
public class RedmineMandate {
	String mandateId;
	String creditorId;
	Long kontoNummber;
	Integer bankleitzahl;
	String iban;
	String bic;
	Integer kontaktNummer;
	String mandateDate;
	Integer valid;
	Date validUntilDate;
	String subject;
	String name;
	String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getValidUntilDate() {
		return validUntilDate;
	}

	public void setValidUntilDate(Date validUntilDate) {
		this.validUntilDate = validUntilDate;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public String getMandateDate() {
		return mandateDate;
	}

	public void setMandateDate(String mandateDate) {
		this.mandateDate = mandateDate;
	}

	public Integer getKontaktNummer() {
		return kontaktNummer;
	}

	public void setKontaktNummer(Integer kontaktNummer) {
		this.kontaktNummer = kontaktNummer;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public Integer getBankleitzahl() {
		return bankleitzahl;
	}

	public void setBankleitzahl(Integer bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
	}

	public Long getKontoNummber() {
		return kontoNummber;
	}

	public void setKontoNummber(Long kontoNummber) {
		this.kontoNummber = kontoNummber;
	}

	public String getCreditorId() {
		return creditorId;
	}

	public void setCreditorId(String creditorId) {
		this.creditorId = creditorId;
	}

	public String getMandateId() {
		return mandateId;
	}

	public void setMandateId(String id) {
		mandateId = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof RedmineMandate))
			return false;
		RedmineMandate mandateToCompare = (RedmineMandate) obj;
		if (kontoNummber.equals(mandateToCompare.getKontoNummber())
				&& bankleitzahl.equals(mandateToCompare.getBankleitzahl())) {
			return true;
		}
		return false;
	}
}
