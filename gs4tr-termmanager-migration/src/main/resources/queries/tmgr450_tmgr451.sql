SET FOREIGN_KEY_CHECKS = 0;
INSERT IGNORE INTO TM_POLICY (POLICY_ID, CATEGORY, POLICY_TYPE) VALUES ('POLICY_TM_TERM_ADD_APPROVED_TERM', 'Task',	0);
INSERT IGNORE INTO TM_POLICY (POLICY_ID, CATEGORY, POLICY_TYPE) VALUES ('POLICY_TM_TERM_ADD_PENDING_TERM', 'Task',	0);
UPDATE TM_ROLE_POLICY SET POLICY_ID='POLICY_TM_TERM_ADD_APPROVED_TERM' WHERE POLICY_ID='POLICY_TM_TERMENTRY_ADD';
DELETE FROM TM_ROLE_POLICY WHERE POLICY_ID = 'POLICY_TM_TERMENTRY_EDIT';
DELETE FROM TM_POLICY WHERE POLICY_ID = 'POLICY_TM_TERMENTRY_EDIT';
DELETE FROM TM_POLICY WHERE POLICY_ID = 'POLICY_TM_TERMENTRY_ADD';
DELETE FROM TM_USER_PROFILE_METADATA;
SET FOREIGN_KEY_CHECKS = 1;
