package at.autobank.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import at.autobank.dto.Mandate;
import at.autobank.dto.SepaTransformationTransaction;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MySqlDatabaseSingleTon {

	/** the db connection source. */
	private ConnectionSource connectionSource = null;
	/** the tracking system dao. */
	private Dao<SepaTransformationTransaction, Integer> sepaDao;
	/** the mandate dao. */
	private Dao<Mandate, String> mandateDao;

	/** the mandate date formatter. */
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Default constructor.
	 * 
	 * @throws SQLException
	 */
	public MySqlDatabaseSingleTon(String url, String username, String password)
			throws SQLException {
		initDbConnection(url, username, password);
	}

	/**
	 * Insert the object.
	 * 
	 * @param transaction
	 *            the transaction object to insert
	 * @throws SQLException
	 */
	public void insert(SepaTransformationTransaction transaction)
			throws SQLException {
		sepaDao.create(transaction);
	}

	public SepaTransformationTransaction getLastMandate(Long kontonummer, Integer bankleitzahl) throws SQLException {
		QueryBuilder<SepaTransformationTransaction, Integer> qBuilder = sepaDao.queryBuilder();
		qBuilder.where().eq("Kontonummer", kontonummer).and().eq("Bankleitzahl", bankleitzahl);
		qBuilder.orderBy("Durchfuehrungsdatum", false);
		qBuilder.limit(Long.valueOf(1));
		List<SepaTransformationTransaction> transaction = qBuilder.query();
		if (transaction.size() == 0) {
			return null;
		} else {
			return transaction.get(0);
		}
	}

	public Mandate selectMandate(Long kontonummer, Integer bankleitzahl)
			throws SQLException {
		List<Mandate> mandates = mandateDao.queryBuilder().where()
				.eq("Kontonummer", kontonummer.toString()).and()
				.eq("Bankleitzahl", bankleitzahl.toString()).and()
				.eq("Status", "Gültig").query();
		if (mandates.size() > 1) {
			Mandate latestMandate = null;
			for (Mandate mandate : mandates) {
				if (latestMandate == null) {
					latestMandate = mandate;
				}
				try {
					Date mandateDate = dateFormatter.parse(mandate.getMandatsDatum());
					if (mandateDate.after(dateFormatter.parse(latestMandate.getMandatsDatum()))) {
						latestMandate = mandate;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return latestMandate;
		} else if (mandates.size() == 1) {
			return mandates.get(0);
		}
		return null;
	}

	public Mandate getValidMandate(String mandateId) throws SQLException {
		List<Mandate> mandates = mandateDao.queryBuilder().where()
				.eq("Mandats-ID", mandateId).and()
				.eq("Status", "Gültig").query();
		if (mandates.size() >= 1) {
			return mandates.get(0);
		}
		return null;
	}

	/**
	 * Initialise the db.
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @throws SQLException
	 */
	private void initDbConnection(String url, String username, String password)
			throws SQLException {
		// create our data-source for the database
		connectionSource = new JdbcConnectionSource("jdbc:mysql://" + url,
				username, password);
		sepaDao = DaoManager.createDao(connectionSource,
				SepaTransformationTransaction.class);
		mandateDao = DaoManager.createDao(connectionSource, Mandate.class);
		if (!sepaDao.isTableExists()) {
			TableUtils.createTable(connectionSource,
					SepaTransformationTransaction.class);
		}
	}

	/**
	 * Close the database connection.
	 * 
	 * @throws SQLException
	 */
	public void closeDbConnection() throws SQLException {
		connectionSource.close();
	}

	/**
	 * Rollback the current transaction.
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		sepaDao.rollBack(connectionSource.getReadWriteConnection());
	}
}
