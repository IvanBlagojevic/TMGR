package org.gs4tr.termmanager.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.foundation.modules.entities.model.PagedList;
import org.gs4tr.foundation.modules.entities.model.PagedListInfo;
import org.gs4tr.foundation.modules.entities.model.Task;
import org.gs4tr.foundation.modules.entities.model.TaskPagedList;
import org.gs4tr.foundation.modules.entities.model.UserTypeEnum;
import org.gs4tr.foundation.modules.entities.model.types.EntityType;
import org.gs4tr.termmanager.dao.SubmissionDAO;
import org.gs4tr.termmanager.dao.SubmissionDetailViewDAO;
import org.gs4tr.termmanager.dao.SubmissionUserDAO;
import org.gs4tr.termmanager.model.EntityTypeHolder;
import org.gs4tr.termmanager.model.Submission;
import org.gs4tr.termmanager.model.TmUserProfile;
import org.gs4tr.termmanager.model.search.command.SubmissionSearchRequest;
import org.gs4tr.termmanager.model.view.SubmissionDetailView;
import org.gs4tr.termmanager.service.SubmissionDetailViewService;
import org.gs4tr.termmanager.service.utils.AdminTasksHolderHelper;
import org.gs4tr.termmanager.service.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("submissionDetailViewService")
public class SubmissionDetailViewServiceImpl implements SubmissionDetailViewService {

    @Autowired
    private SubmissionDAO _submissionDAO;

    @Autowired
    private SubmissionDetailViewDAO _submissionDetailViewDAO;

    @Autowired
    private SubmissionUserDAO _submissionUserDAO;

    @Autowired
    private AdminTasksHolderHelper _tasksHolderHelper;

    @Override
    @Transactional(readOnly = true)
    public TaskPagedList<SubmissionDetailView> search(SubmissionSearchRequest command, PagedListInfo info) {
	preProcessSearchRequest(command);

	if (CollectionUtils.isEmpty(command.getSubmissionIds())) {
	    TaskPagedList<SubmissionDetailView> taskPagedList = createEmptyPagedList(info);
	    return taskPagedList;
	}

	PagedList<SubmissionDetailView> pagedList = getSubmissionDetailViewDAO().getEntityPagedList(command, info);

	TaskPagedList<SubmissionDetailView> taskPagedList = new TaskPagedList<SubmissionDetailView>(pagedList);

	List<Task> tasks = ServiceUtils.postProcessEntityPagedList(taskPagedList, command.getFolder(), null,
		new EntityType[] { EntityTypeHolder.SUBMISSIONDETAIL }, getTasksHolderHelper());

	taskPagedList.setTasks(tasks.toArray(new Task[tasks.size()]));

	return taskPagedList;
    }

    private TaskPagedList<SubmissionDetailView> createEmptyPagedList(PagedListInfo pagedListInfo) {
	TaskPagedList<SubmissionDetailView> taskPagedList = new TaskPagedList<SubmissionDetailView>(
		new PagedList<SubmissionDetailView>());
	taskPagedList.setTasks(new Task[0]);
	taskPagedList.setPagedListInfo(pagedListInfo);
	return taskPagedList;
    }

    private SubmissionDAO getSubmissionDAO() {
	return _submissionDAO;
    }

    private SubmissionDetailViewDAO getSubmissionDetailViewDAO() {
	return _submissionDetailViewDAO;
    }

    private SubmissionUserDAO getSubmissionUserDAO() {
	return _submissionUserDAO;
    }

    private AdminTasksHolderHelper getTasksHolderHelper() {
	return _tasksHolderHelper;
    }

    private void preProcessSearchRequest(SubmissionSearchRequest command) {
	TmUserProfile user = TmUserProfile.getCurrentUserProfile();
	command.setUserName(user.getUserName());

	if (UserTypeEnum.POWER_USER == user.getUserType()) {
	    Set<Long> submissionIds = getSubmissionDAO().findAllSubmissionIds();
	    command.setSubmissionIds(submissionIds);
	    command.setSubmitterView(true);
	    command.setPowerUser(true);
	    return;
	}

	command.setUserId(user.getUserProfileId());

	if (CollectionUtils.isEmpty(command.getSubmissionIds())) {
	    Set<Long> submissionIds = new HashSet<Long>();
	    List<Submission> submissions = getSubmissionUserDAO().findSubmissionsByUserId(user.getUserProfileId());
	    for (Submission submission : submissions) {
		submissionIds.add(submission.getSubmissionId());
	    }

	    command.setSubmissionIds(submissionIds);
	}

	boolean submitterView = ServiceUtils.isSubmitterUser(user, 0L);
	command.setSubmitterView(submitterView);
    }
}
