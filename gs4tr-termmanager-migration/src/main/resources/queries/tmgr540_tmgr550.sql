SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS TM_VERSION_REINDEX_INFO;

CREATE TABLE `TM_VERSION_REINDEX_INFO` (`ID` bigint(20) NOT NULL AUTO_INCREMENT, `ENABLE_REINDEX` bit(1), `FIRST_REINDEX` bit(1) COLLATE utf8_unicode_ci NOT NULL, PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO TM_VERSION_REINDEX_INFO (ENABLE_REINDEX, FIRST_REINDEX) VALUES (1, 1);

SET FOREIGN_KEY_CHECKS = 1;