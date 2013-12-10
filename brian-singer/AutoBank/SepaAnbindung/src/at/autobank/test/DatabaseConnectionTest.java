package at.autobank.test;

import java.sql.SQLException;
import java.util.Date;

import at.autobank.dao.MySqlDatabaseSingleTon;
import at.autobank.dto.SepaTransformationTransaction;

public class DatabaseConnectionTest {
	public static void main(String[] args) {
		try {
			MySqlDatabaseSingleTon database = new MySqlDatabaseSingleTon("localhost", "admin", "admin");
			SepaTransformationTransaction transaction = new SepaTransformationTransaction();
			transaction.setDurchfuehrungsdatum(new Date());
			transaction.setBuchungstext("Test");
			transaction.setBuchungsbetrag("1,34");
			transaction.setValutadatum(new Date());
			database.insert(transaction);
			database.closeDbConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
