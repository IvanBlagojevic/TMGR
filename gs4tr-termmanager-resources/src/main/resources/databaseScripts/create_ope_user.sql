SET FOREIGN_KEY_CHECKS=0;

INSERT IGNORE INTO TM_ROLE (ROLE_ID, GENERIC, ROLE_TYPE) VALUES ('generic_ope_user', 0, 0);

INSERT IGNORE INTO TM_ROLE_POLICY (ROLE_ID, POLICY_ID) VALUES ('generic_ope_user', 'POLICY_TM_READ');

INSERT IGNORE INTO TM_USER_PROFILE (ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CREDENTIALS_NON_EXPIRED, DATE_PASSWORD_CHANGED, EMAIL_NOTIFICATION, ENABLED, `PASSWORD`, USERNAME, USER_TYPE, GENERIC_PASSWORD, CHANGED_TERMS, HIDDEN, ITEMS_PER_PAGE, ORGANIZATION_ID, GENERIC, OPE) VALUES ( 1, 1, 1, NOW(), 0, 1, sha('!0P3us3r@'), 'OPE_user', 0, '!0P3us3r@', 0, 0, 50, 1, 1, 1);

UPDATE TM_USER_PROFILE u SET OPE = 1 WHERE u.USERNAME = 'OPE_user';

DELETE u2 FROM TM_USER_PROFILE u1, TM_USER_PROFILE u2 WHERE u1.USER_PROFILE_ID < u2.USER_PROFILE_ID AND u1.USERNAME = u2.USERNAME;

INSERT IGNORE INTO TM_PROJECT_USER_LANGUAGE (LANGUAGE_ID, PROJECT_ID, USER_PROFILE_ID ) SELECT DISTINCT pl.LANGUAGE_ID AS LANGUAGE_ID, pl.PROJECT_ID AS PROJECT_ID, ( SELECT u.USER_PROFILE_ID FROM TM_USER_PROFILE AS u WHERE u.USERNAME = 'OPE_user') AS USER_PROFILE_ID FROM TM_PROJECT_LANGUAGE AS pl;

INSERT IGNORE INTO TM_USER_PROJECT_ROLE (PROJECT_ID, ROLE_ID, USER_PROFILE_ID ) SELECT DISTINCT p.PROJECT_ID AS PROJECT_ID, 'generic_ope_user' AS ROLE_ID, ( SELECT u.USER_PROFILE_ID FROM TM_USER_PROFILE AS u WHERE u.USERNAME = 'OPE_user') AS USER_PROFILE_ID FROM TM_PROJECT AS p;

SET FOREIGN_KEY_CHECKS=1;