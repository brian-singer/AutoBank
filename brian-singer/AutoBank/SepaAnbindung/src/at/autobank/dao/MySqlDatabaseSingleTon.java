package at.autobank.dao;

import java.sql.SQLException;

import at.autobank.dto.SepaTransformationTransaction;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MySqlDatabaseSingleTon {

	// the db connection source
	private ConnectionSource connectionSource = null;
	private Dao<SepaTransformationTransaction, Integer> sepaDao;

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
		connectionSource = new JdbcConnectionSource("jdbc:mysql://" + url, username,
				password);
		sepaDao = DaoManager.createDao(connectionSource,
				SepaTransformationTransaction.class);
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
