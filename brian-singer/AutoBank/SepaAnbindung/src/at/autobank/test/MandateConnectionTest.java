package at.autobank.test;

import java.sql.SQLException;

import at.autobank.dao.MySqlDatabaseSingleTon;
import at.autobank.dto.Mandate;

public class MandateConnectionTest {
	public static void main(String[] args) {
		try {
			String testExistingMandate = "065282-01";
			MySqlDatabaseSingleTon database = new MySqlDatabaseSingleTon("localhost", "admin", "admin");
			Mandate mandate = database.getValidMandate(testExistingMandate, "AT08ZZZ00000004254");
			if (mandate == null) {
				System.out.println("Null mandate");
			} else {
				System.out.println(mandate.getMandateId());
			}
			database.closeDbConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
