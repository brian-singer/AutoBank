package at.autobank.dto;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "eurolease_sepa_transaction")
public class SepaTransformationTransaction {

	@DatabaseField(generatedId = true, columnName = "transaction_id")
	private int id;

//	@DatabaseField(columnName = "MANDATEID", canBeNull = false, foreign = true)
//	private RedmineMandate redmineMandate;
	@DatabaseField(columnName = "mandate_id")
	private String mandateId;

	@DatabaseField(columnName = "durchfuehrungsdatum")
	private Date durchfuehrungsdatum;

	@DatabaseField(columnName = "buchungstext")
	private String buchungstext;

	@DatabaseField(columnName = "buchungsbetrag")
	private String buchungsbetrag;

	@DatabaseField(columnName = "valutadatum")
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
