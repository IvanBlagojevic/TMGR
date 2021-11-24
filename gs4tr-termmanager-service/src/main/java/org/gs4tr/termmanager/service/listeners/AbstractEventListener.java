package org.gs4tr.termmanager.service.listeners;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gs4tr.foundation.modules.entities.model.NotifyingMessageListener;
import org.gs4tr.termmanager.dao.SubmissionLanguageDAO;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.dao.UserProfileDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.SubmissionUser;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.event.EventMessage;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.service.SubmissionTermService;
import org.gs4tr.termmanager.service.TermEntryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractEventListener implements NotifyingMessageListener<EventMessage> {

    @Autowired
    private SubmissionLanguageDAO _submissionLanguageDAO;

    @Autowired
    private SubmissionTermService _submissionTermService;

    @Autowired
    private SubmissionUserDAO _submissionUserDAO;

    @Autowired
    private TermEntryService _termEntryService;

    @Autowired
    private UserProfileDAO _userProfileDAO;

    protected final Log LOGGER = LogFactory.getLog(this.getClass());

    @Override
    public Class<EventMessage> getNotifyingMessageClass() {
	return EventMessage.class;
    }

    @Override
    public abstract void notify(EventMessage message);

    @Override
    public abstract boolean supports(EventMessage message);

    private SubmissionUserDAO getSubmissionUserDAO() {
	return _submissionUserDAO;
    }

    private UserProfileDAO getUserProfileDAO() {
	return _userProfileDAO;
    }

    protected void createNewSubmissionUser(Submission submission, String assignee) {
	TmUserProfile assigneeProfile = getUserProfileDAO().findUsersByUserNameNoFetch(assignee);

	Long count = getSubmissionUserDAO().findBySubmissionAndUserId(submission.getSubmissionId(),
		assigneeProfile.getUserProfileId());

	if (count == 0) {
	    SubmissionUser submissionUser = new SubmissionUser(assigneeProfile, submission);
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug(String.format(Messages.getString("AbstractEventListener.0"), //$NON-NLS-1$
			submission.getName(), submission.getSubmissionId(), TmUserProfile.getCurrentUserName()));
	    }
	    submissionUser = getSubmissionUserDAO().save(submissionUser);
	    Set<SubmissionUser> submissionUsers = submission.getSubmissionUsers();
	    if (submissionUsers == null) {
		submissionUsers = new HashSet<>();
		submission.setSubmissionUsers(submissionUsers);
	    }
	    submissionUsers.add(submissionUser);
	}
    }

    protected SubmissionLanguage createOrGetSubmissionLanguage(String languageId, String assignee,
	    Set<SubmissionLanguage> submissionLanguages, Submission submission, ProjectDetailInfo detailInfo) {
	for (SubmissionLanguage submissionLanguage : submissionLanguages) {
	    if (languageId.equals(submissionLanguage.getLanguageId())) {
		return submissionLanguage;
	    }
	}

	TmUserProfile submitterUserProfile = TmUserProfile.getCurrentUserProfile();
	TmUserProfile assigneeUserProfile = getUserProfileDAO().findUsersByUserNameNoFetch(assignee);

	detailInfo.incrementActiveSubmissionCount(languageId);

	Long submitterProfileId = submitterUserProfile.getUserProfileId();
	detailInfo.incrementActiveSubmissionCount(submitterProfileId, languageId);

	Long assingeeProfileId = assigneeUserProfile.getUserProfileId();
	if (!submitterProfileId.equals(assingeeProfileId)) {
	    detailInfo.incrementActiveSubmissionCount(assingeeProfileId, languageId);
	}

	SubmissionLanguage submissionLanguage = new SubmissionLanguage(languageId);
	submissionLanguage.setSubmission(submission);
	submissionLanguage.setAssignee(assignee);

	submissionLanguage = getSubmissionLanguageDAO().save(submissionLanguage);
	submissionLanguages.add(submissionLanguage);

	return submissionLanguage;
    }

    protected SubmissionLanguageDAO getSubmissionLanguageDAO() {
	return _submissionLanguageDAO;
    }

    protected SubmissionTermService getSubmissionTermService() {
	return _submissionTermService;
    }

    protected TermEntryService getTermEntryService() {
	return _termEntryService;
    }
}
