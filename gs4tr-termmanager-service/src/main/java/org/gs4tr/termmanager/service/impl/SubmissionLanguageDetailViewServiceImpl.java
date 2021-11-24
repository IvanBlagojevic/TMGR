package org.gs4tr.termmanager.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.dao.SubmissionLanguageDetailViewDAO;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.SubmissionLanguage;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.command.SubmissionLanguageDetailRequest;
import org.gs4tr.termmanager.model.view.SubmissionLanguageDetailView;
import org.gs4tr.termmanager.service.SubmissionLanguageDetailViewService;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("submissionLanguageDetailViewService")
public class SubmissionLanguageDetailViewServiceImpl implements SubmissionLanguageDetailViewService {

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Autowired
    private SubmissionLanguageDetailViewDAO _submissionLanguageDetailViewDAO;

    @Override
    @Transactional(readOnly = true)
    public TaskPagedList<SubmissionLanguageDetailView> search(SubmissionLanguageDetailRequest command,
	    PagedListInfo info) {
	preProcessSearchRequest(command);

	PagedList<SubmissionLanguageDetailView> pagedList = getSubmissionLanguageDetailViewDAO()
		.getEntityPagedList(command, info);

	TaskPagedList<SubmissionLanguageDetailView> taskPagedList = new TaskPagedList<SubmissionLanguageDetailView>(
		pagedList);
	taskPagedList.setTasks(new Task[0]);

	return taskPagedList;
    }

    private SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    private SubmissionLanguageDetailViewDAO getSubmissionLanguageDetailViewDAO() {
	return _submissionLanguageDetailViewDAO;
    }

    private void preProcessSearchRequest(SubmissionLanguageDetailRequest command) {
	Long submissionId = command.getSubmissionId();
	Submission submission = getSubmissionDAO().load(submissionId);
	String submitter = submission.getSubmitter();

	Long projectId = submission.getProject().getProjectId();

	TmUserProfile user = TmUserProfile.getCurrentUserProfile();

	boolean submitterView = ServiceUtils.isSubmitterUser(user, projectId);
	command.setSubmitterView(submitterView);

	Set<SubmissionLanguage> submissionLanguages = submission.getSubmissionLanguages();

	if (CollectionUtils.isNotEmpty(submissionLanguages)) {
	    String userName = user.getUserName();
	    Set<String> languageIds = new HashSet<String>();

	    for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		String assignee = submissionLanguage.getAssignee();
		String languageId = submissionLanguage.getLanguageId();

		if (userName.equals(submitter)) {
		    languageIds.add(languageId);
		} else if (userName.equals(assignee)) {
		    languageIds.add(languageId);
		} else if (user.isPowerUser()) {
		    languageIds.add(languageId);
		}
	    }

	    command.setLanguageIds(languageIds);
	}
    }
}
