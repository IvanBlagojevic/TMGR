package org.gs4tr.termmanager.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.migration.AbstractMigrations;
import org.springframework.jdbc.core.JdbcTemplate;

public class Migrations extends AbstractMigrations {

    private static final String CREATE_TABLE_TM_VERSION = "CREATE TABLE `TM_VERSION` (`VERSION_ID` bigint(20) NOT NULL AUTO_INCREMENT, `VERSION_LABEL` varchar(30) COLLATE utf8_unicode_ci NOT NULL, PRIMARY KEY (`VERSION_ID`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";

    private static final String CREATE_TABLE_TM_VERSION_REINDEX_INFO = "CREATE TABLE `TM_VERSION_REINDEX_INFO` (`ID` bigint(20) NOT NULL AUTO_INCREMENT, `ENABLE_REINDEX` bit(1), `FIRST_REINDEX` bit(1) COLLATE utf8_unicode_ci NOT NULL, PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS `%s`;";

    private static final String GET_FIRST_REINDEX_INFO_QUERY = "SELECT FIRST_REINDEX FROM TM_VERSION_REINDEX_INFO;";

    private static final String INSERT_INTO_TM_VERSION_REINDEX_INFO = "INSERT INTO TM_VERSION_REINDEX_INFO (ENABLE_REINDEX, FIRST_REINDEX) VALUES (%d, %d);";

    private static final String INSERT_INTO_TM_VERSION_VERSION = "INSERT INTO TM_VERSION (VERSION_LABEL) VALUES ('%s');";

    private static final Log LOGGER = LogFactory.getLog(Migrations.class);

    private static final String TM_VERSION = "TM_VERSION";

    private static final String TM_VERSION_REINDEX_INFO = "TM_VERSION_REINDEX_INFO";

    private void createVersionReindexInfoTable() {
	Boolean firstReindex = isFirstStartup();
	if (firstReindex) {
	    recreateVersionReindexInfoTable();
	}
    }

    private Boolean isFirstStartup() {
	Boolean isFirstReindex;
	try {
	    isFirstReindex = getJdbcTemplate().queryForObject(GET_FIRST_REINDEX_INFO_QUERY, Boolean.class);
	} catch (Exception e) {
	    LOGGER.warn(Messages.getString("Migrations.1"));
	    isFirstReindex = false;
	}
	return isFirstReindex;
    }

    private void recreateVersionReindexInfoTable() {
	JdbcTemplate jdbcTemplate = getJdbcTemplate();
	jdbcTemplate.execute(String.format(DROP_TABLE, TM_VERSION_REINDEX_INFO));
	jdbcTemplate.execute(CREATE_TABLE_TM_VERSION_REINDEX_INFO);
	jdbcTemplate.execute(String.format(INSERT_INTO_TM_VERSION_REINDEX_INFO, 1, 1));

    }

    @Override
    protected String fetchSourceVersion() throws Exception {
	String sourceVersion;

	try {
	    sourceVersion = super.fetchSourceVersion();
	    createVersionReindexInfoTable();
	} catch (Exception e) {
	    sourceVersion = Version.getVersion();
	    LOGGER.warn(Messages.getString("Migrations.0")); //$NON-NLS-1$
	}

	return sourceVersion;
    }

    @Override
    protected String fetchTargetVersion() {
	return Version.getVersion();
    }

    @Override
    protected void updateCurrentVersion(String currentVersion) throws Exception {
	JdbcTemplate jdbcTemplate = getJdbcTemplate();
	jdbcTemplate.execute(String.format(DROP_TABLE, TM_VERSION));
	jdbcTemplate.execute(CREATE_TABLE_TM_VERSION);
	jdbcTemplate.execute(String.format(INSERT_INTO_TM_VERSION_VERSION, currentVersion));
	super.updateCurrentVersion(currentVersion);
    }
}
