SET FOREIGN_KEY_CHECKS = 0;

INSERT IGNORE INTO TM_POLICY (POLICY_ID, CATEGORY, POLICY_TYPE) VALUES ('POLICY_TM_TERM_ON_HOLD_TERM_STATUS', 'Task', 0);
INSERT IGNORE INTO TM_POLICY (POLICY_ID, CATEGORY, POLICY_TYPE) VALUES ('POLICY_TM_TERM_ADD_ON_HOLD_TERM', 'Task', 0);
INSERT IGNORE INTO TM_POLICY (POLICY_ID, CATEGORY, POLICY_TYPE) VALUES ('POLICY_TM_TERM_ADD_BLACKLIST_TERM', 'Task', 0);

INSERT IGNORE INTO TM_ROLE_POLICY (ROLE_ID, POLICY_ID) SELECT ROLE_ID, 'POLICY_TM_TERM_ADD_BLACKLIST_TERM' FROM TM_ROLE_POLICY WHERE TM_ROLE_POLICY.POLICY_ID IN ('POLICY_TM_TERM_ADD_APPROVED_TERM','POLICY_TM_TERM_ADD_PENDING_TERM');
INSERT IGNORE INTO TM_ROLE_POLICY (ROLE_ID, POLICY_ID) SELECT ROLE_ID, 'POLICY_TM_TERM_ADD_ON_HOLD_TERM' FROM TM_ROLE_POLICY WHERE TM_ROLE_POLICY.POLICY_ID IN ('POLICY_TM_TERM_ADD_APPROVED_TERM','POLICY_TM_TERM_ADD_PENDING_TERM');
INSERT IGNORE INTO TM_ROLE_POLICY (ROLE_ID, POLICY_ID) SELECT ROLE_ID, 'POLICY_TM_TERM_ON_HOLD_TERM_STATUS' FROM TM_ROLE_POLICY WHERE TM_ROLE_POLICY.POLICY_ID IN ('POLICY_TM_TERM_APPROVE_TERM_STATUS','POLICY_TM_TERM_DEMOTE_TERM_STATUS');

ALTER TABLE `TM_PROJECT_DETAIL` ADD COLUMN `PENDING_APPROVAL_TERM_COUNT`  INTEGER DEFAULT 0;
ALTER TABLE `TM_PROJECT_DETAIL` ADD COLUMN `ON_HOLD_TERM_COUNT`  INTEGER DEFAULT 0;

ALTER TABLE `TM_PROJECT_LANGUAGE_DETAIL` ADD COLUMN `PENDING_APPROVAL_TERM_COUNT`  INTEGER DEFAULT 0;
ALTER TABLE `TM_PROJECT_LANGUAGE_DETAIL` ADD COLUMN `ON_HOLD_TERM_COUNT`  INTEGER DEFAULT 0;

CREATE INDEX PROJECT_STATUS_IDX ON TM_TERM (PROJECT_ID, STATUS);
CREATE INDEX PROJECT_LANGUAGE_STATUS_IDX ON TM_TERM (PROJECT_ID, LANGUAGE_ID, STATUS);

UPDATE TM_PROJECT_DETAIL pd SET pd.PENDING_APPROVAL_TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.`STATUS` = 'WAITING' AND t.DISABLED = 0);
UPDATE TM_PROJECT_DETAIL pd SET pd.APPROVE_TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.`STATUS` = 'PROCESSED' AND t.DISABLED = 0);
UPDATE TM_PROJECT_DETAIL pd SET pd.FORBIDDEN_TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.`STATUS` = 'BLACKLISTED' AND t.DISABLED = 0);
UPDATE TM_PROJECT_DETAIL pd SET pd.TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.DISABLED = 0);

UPDATE TM_PROJECT_LANGUAGE_DETAIL pld INNER JOIN TM_PROJECT_DETAIL pd ON pd.PROJECT_DETAIL_ID = pld.PROJECT_DETAIL_ID SET pld.PENDING_APPROVAL_TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.`STATUS` = 'WAITING' AND t.LANGUAGE_ID = pld.LANGUAGE_ID AND t.DISABLED = 0);
UPDATE TM_PROJECT_LANGUAGE_DETAIL pld INNER JOIN TM_PROJECT_DETAIL pd ON pd.PROJECT_DETAIL_ID = pld.PROJECT_DETAIL_ID SET pld.APPROVE_TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.`STATUS` = 'PROCESSED' AND t.LANGUAGE_ID = pld.LANGUAGE_ID AND t.DISABLED = 0);
UPDATE TM_PROJECT_LANGUAGE_DETAIL pld INNER JOIN TM_PROJECT_DETAIL pd ON pd.PROJECT_DETAIL_ID = pld.PROJECT_DETAIL_ID SET pld.FORBIDDEN_TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.`STATUS` = 'BLACKLISTED' AND t.LANGUAGE_ID = pld.LANGUAGE_ID AND t.DISABLED = 0);
UPDATE TM_PROJECT_LANGUAGE_DETAIL pld INNER JOIN TM_PROJECT_DETAIL pd ON pd.PROJECT_DETAIL_ID = pld.PROJECT_DETAIL_ID SET pld.TERM_COUNT = (SELECT COUNT(t.TERM_ID) FROM TM_TERM t WHERE t.PROJECT_ID = pd.PROJECT_ID AND t.LANGUAGE_ID = pld.LANGUAGE_ID AND t.DISABLED = 0);

UPDATE TM_PROJECT_DETAIL pd SET pd.ACTIVE_SUBMISSION_COUNT = (SELECT COUNT(t.SUBMISSION_ID) FROM TM_SUBMISSION t WHERE t.PROJECT_ID = pd.PROJECT_ID AND (t.`STATUS` = 'INTRANSLATIONREVIEW' OR t.`STATUS` = 'INFINALREVIEW'));
UPDATE TM_PROJECT_DETAIL pd SET pd.COMPLETED_SUBMISSION_COUNT = (SELECT COUNT(t.SUBMISSION_ID) FROM TM_SUBMISSION t WHERE t.PROJECT_ID = pd.PROJECT_ID AND (t.`STATUS` = 'COMPLETED' OR t.`STATUS` = 'CANCELLED'));

UPDATE TM_PROJECT_LANGUAGE_DETAIL pld INNER JOIN TM_PROJECT_DETAIL pd ON pd.PROJECT_DETAIL_ID = pld.PROJECT_DETAIL_ID SET pld.ACTIVE_SUBMISSION_COUNT = (SELECT COUNT(sl.SUBMISSION_ID) FROM TM_SUBMISSION_LANGUAGE sl INNER JOIN TM_SUBMISSION t ON sl.SUBMISSION_ID = t.SUBMISSION_ID WHERE t.PROJECT_ID = pd.PROJECT_ID AND (t.`STATUS` = 'INTRANSLATIONREVIEW' OR t.`STATUS` = 'INFINALREVIEW') AND sl.LANGUAGE_ID = pld.LANGUAGE_ID);
UPDATE TM_PROJECT_LANGUAGE_DETAIL pld INNER JOIN TM_PROJECT_DETAIL pd ON pd.PROJECT_DETAIL_ID = pld.PROJECT_DETAIL_ID SET pld.COMPLETED_SUBMISSION_COUNT = (SELECT COUNT(sl.SUBMISSION_ID) FROM TM_SUBMISSION_LANGUAGE sl INNER JOIN TM_SUBMISSION t ON sl.SUBMISSION_ID = t.SUBMISSION_ID WHERE t.PROJECT_ID = pd.PROJECT_ID AND (t.`STATUS` = 'COMPLETED' OR t.`STATUS` = 'CANCELLED') AND sl.LANGUAGE_ID = pld.LANGUAGE_ID);

ALTER TABLE TM_TERM DROP INDEX PROJECT_STATUS_IDX;
ALTER TABLE TM_TERM DROP INDEX PROJECT_LANGUAGE_STATUS_IDX;

ALTER TABLE `TM_PROJECT` ADD COLUMN `SHARE_PENDING_TERMS` BOOLEAN DEFAULT TRUE;

ALTER TABLE TM_STATISTICS ADD COLUMN `ADDED_ON_HOLD_COUNT` INTEGER DEFAULT 0;
ALTER TABLE TM_STATISTICS ADD COLUMN `ON_HOLD_COUNT` INTEGER DEFAULT 0;

ALTER TABLE TM_STATISTICS ADD COLUMN `ADDED_BLACKLISTED_COUNT` INTEGER DEFAULT 0;
ALTER TABLE TM_STATISTICS ADD COLUMN `BLACKLISTED_COUNT` INTEGER DEFAULT 0;

SET FOREIGN_KEY_CHECKS = 1;