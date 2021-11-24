package org.gs4tr.termmanager.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.event.Observable;
import org.gs4tr.termmanager.model.event.Observer;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo.SubmissionStateWrapper;

@NamedQueries({
	@NamedQuery(name = "Submission.findById", query = "select submission " + "from Submission submission "
		+ "where submission.submissionId = :entityId"),

	@NamedQuery(name = "Submission.findByIds", query = "select submission " + "from Submission submission "
		+ "where submission.submissionId in (:entityIds)"),

	@NamedQuery(name = "Submission.findByName", query = "select submission " + "from Submission submission "
		+ "where submission.name = :name"),

	@NamedQuery(name = "Submission.findByProjectId", query = "select submission " + "from Submission submission "
		+ "where submission.project.projectId = :projectId"),

	@NamedQuery(name = "Submission.findByProjectIds", query = "select submission " + "from Submission submission "
		+ "where submission.project.projectId in (:projectIds)"),

	@NamedQuery(name = "Submission.findAllSubmissionIds", query = "select submission.submissionId "
		+ "from Submission submission " + "inner join submission.project project "
		+ "where project.projectInfo.enabled is true"),

	@NamedQuery(name = "Submission.findAllSubmissions", query = "select submission " + "from Submission submission "
		+ "inner join submission.project project " + "where project.projectInfo.enabled is true"),

	@NamedQuery(name = "Submission.findProjectsBySubmissionIds", query = "select distinct project "
		+ "from Submission submission " + "inner join submission.project project "
		+ "where submission.submissionId in (:submissionIds)"),

	@NamedQuery(name = "Submission.updateLanguageByProjectId", query = "update Submission s set s.sourceLanguageId "
		+ "= :languageTo where s.sourceLanguageId = :languageFrom and s.project.projectId =:projectId") })
@Entity
@Table(name = "SUBMISSION")
public class Submission implements Observable, Identifiable<Long> {

    private static final Set<ItemStatusType> completedStatuses = new HashSet<>();

    private static final long serialVersionUID = -7921413348425119683L;

    static {
	completedStatuses.add(ItemStatusTypeHolder.COMPLETED);
	completedStatuses.add(ItemStatusTypeHolder.CANCELLED);
    }

    private String _assignees;

    private Date _dateModified;

    private Date _dateSubmitted;

    private EntityStatusPriority _entityStatusPriority;

    private String _markerId;

    private String _name;

    private TmProject _project;

    private String _sourceLanguageId;

    private Long _submissionId;

    private Set<SubmissionLanguage> _submissionLanguages;

    private Set<SubmissionUser> _submissionUsers;

    private String _submitter;

    private String _targetLanguageIds;

    private long _termEntryCount;

    public Submission() {
	_dateSubmitted = new Date();
	_dateModified = new Date();
	_termEntryCount = 0;
	setEntityStatusPriority(new EntityStatusPriority(Priority.NORMAL.getValue(), Priority.NORMAL.getValue(),
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW));
	setSubmissionLanguages(new HashSet<SubmissionLanguage>());
    }

    public Submission(String sourceLanguageId) {
	_sourceLanguageId = sourceLanguageId;
	_dateSubmitted = new Date();
	_dateModified = new Date();
	_termEntryCount = 0;
	setEntityStatusPriority(new EntityStatusPriority(Priority.NORMAL.getValue(), Priority.NORMAL.getValue(),
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW));
	setSubmissionLanguages(new HashSet<SubmissionLanguage>());
    }

    @Override
    public void addObserver(Observer o) {
	Set<SubmissionLanguage> submissionLanguages = getSubmissionLanguages();
	if (submissionLanguages == null) {
	    submissionLanguages = new HashSet<SubmissionLanguage>();
	    setSubmissionLanguages(submissionLanguages);
	}

	if (o instanceof SubmissionLanguage) {
	    SubmissionLanguage submissionLanguage = (SubmissionLanguage) o;
	    submissionLanguages.add(submissionLanguage);
	}
    }

    public void appendAssingees(String assignee) {
	String assignees = getAssignees();
	StringBuilder builder = new StringBuilder();
	if (StringUtils.isNotEmpty(assignees)) {
	    String[] values = assignees.split(StringConstants.SEMICOLON);
	    for (int i = 0; i < values.length; i++) {
		if (values[i].equals(assignee)) {
		    return;
		}
	    }
	    builder.append(assignees);
	    builder.append(StringConstants.SEMICOLON);
	}
	builder.append(assignee);
	setAssignees(builder.toString());
    }

    public void appendTargetLanguageIds(String languageId) {
	String targetLanguageIds = getTargetLanguageIds();
	StringBuilder builder = new StringBuilder();
	if (StringUtils.isNotEmpty(targetLanguageIds)) {
	    String[] values = targetLanguageIds.split(StringConstants.SEMICOLON);
	    for (int i = 0; i < values.length; i++) {
		if (values[i].equals(languageId)) {
		    return;
		}
	    }
	    builder.append(targetLanguageIds);
	    builder.append(StringConstants.SEMICOLON);
	}
	builder.append(languageId);
	setTargetLanguageIds(builder.toString());
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Submission other = (Submission) obj;
	if (_markerId == null) {
	    if (other._markerId != null)
		return false;
	} else if (!_markerId.equals(other._markerId))
	    return false;
	if (_name == null) {
	    if (other._name != null)
		return false;
	} else if (!_name.equals(other._name))
	    return false;
	if (_sourceLanguageId == null) {
	    if (other._sourceLanguageId != null)
		return false;
	} else if (!_sourceLanguageId.equals(other._sourceLanguageId))
	    return false;
	if (_submitter == null) {
	    return other._submitter == null;
	} else
	    return _submitter.equals(other._submitter);
    }

    // Values in this column are separated by ';' character
    @Transient
    public String getAssignees() {
	return _assignees;
    }

    @Column(name = "DATE_MODIFIED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateModified() {
	return _dateModified;
    }

    @Column(name = "DATE_SUBMITTED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateSubmitted() {
	return _dateSubmitted;
    }

    @Embedded
    public EntityStatusPriority getEntityStatusPriority() {
	return _entityStatusPriority;
    }

    @Transient
    @Override
    public Long getIdentifier() {
	return getSubmissionId();
    }

    @Column(name = "MARKER_ID", length = 40, nullable = false, updatable = false, unique = true)
    public String getMarkerId() {
	return _markerId;
    }

    @Column(name = "NAME", length = 50, nullable = false)
    public String getName() {
	return _name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    public TmProject getProject() {
	return _project;
    }

    @Column(name = "SOURCE_LANGUAGE_ID", length = 10, updatable = false, nullable = false)
    public String getSourceLanguageId() {
	return _sourceLanguageId;
    }

    @Id
    @Column(name = "SUBMISSION_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getSubmissionId() {
	return _submissionId;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "submission")
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<SubmissionLanguage> getSubmissionLanguages() {
	return _submissionLanguages;
    }

    @Transient
    public SubmissionUser getSubmissionUser(String username) {
	SubmissionUser submissionUser = null;
	Set<SubmissionUser> submissionUsers = getSubmissionUsers();
	if (CollectionUtils.isNotEmpty(submissionUsers)) {
	    for (SubmissionUser subUser : submissionUsers) {
		String userName = subUser.getUser().getUserName();
		if (username.equals(userName)) {
		    submissionUser = subUser;
		    break;
		}
	    }
	}

	return submissionUser;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "submission")
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    public Set<SubmissionUser> getSubmissionUsers() {
	return _submissionUsers;
    }

    @Column(name = "SUBMITTER", updatable = false, nullable = false, length = 128)
    public String getSubmitter() {
	return _submitter;
    }

    // Values in this column are separated by ';' character
    @Transient
    public String getTargetLanguageIds() {
	return _targetLanguageIds;
    }

    @Column(name = "TERMENTRY_COUNT", nullable = false)
    public long getTermEntryCount() {
	return _termEntryCount;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_markerId == null) ? 0 : _markerId.hashCode());
	result = prime * result + ((_name == null) ? 0 : _name.hashCode());
	result = prime * result + ((_sourceLanguageId == null) ? 0 : _sourceLanguageId.hashCode());
	return prime * result + ((_submitter == null) ? 0 : _submitter.hashCode());
    }

    @Transient
    public Boolean isCompleted() {
	return completedStatuses.contains(getEntityStatusPriority().getStatus());
    }

    @Override
    public void notifyObservers(Object arg) {
	if (arg instanceof SubmissionDetailInfo) {
	    SubmissionDetailInfo submissionInfo = (SubmissionDetailInfo) arg;

	    setTermEntryCount(calculateCounts(getTermEntryCount(), submissionInfo.getTermEntryCount().intValue()));

	    Set<SubmissionLanguage> subLangs = getSubmissionLanguages();
	    for (SubmissionLanguage subLang : subLangs) {
		String languageId = subLang.getLanguageId();
		SubmissionStateWrapper stateWrapper = submissionInfo.getSubmissionStatesWrapper().get(languageId);
		if (stateWrapper != null) {
		    subLang.update(stateWrapper);
		}
	    }
	    setDateModified(new Date());
	    changeSubmitterStatus();
	    changeAssigneeStatus();
	}
    }

    @Override
    public void removeObserver(Observer o) {
	// TODO Auto-generated method stub
    }

    public void setAssignees(String assignees) {
	_assignees = assignees;
    }

    public void setDateModified(Date dateModified) {
	_dateModified = dateModified;
    }

    public void setDateSubmitted(Date dateSubmitted) {
	_dateSubmitted = dateSubmitted;
    }

    public void setEntityStatusPriority(EntityStatusPriority entityStatusPriority) {
	_entityStatusPriority = entityStatusPriority;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setSubmissionId(identifier);
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setName(String name) {
	_name = name;
    }

    public void setProject(TmProject project) {
	_project = project;
    }

    public void setSourceLanguageId(String sourceLanguageId) {
	_sourceLanguageId = sourceLanguageId;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmissionLanguages(Set<SubmissionLanguage> submissionLanguages) {
	_submissionLanguages = submissionLanguages;
    }

    public void setSubmissionUsers(Set<SubmissionUser> submissionUsers) {
	_submissionUsers = submissionUsers;
    }

    public void setSubmitter(String submitter) {
	_submitter = submitter;
    }

    public void setTargetLanguageIds(String targetLanguageIds) {
	_targetLanguageIds = targetLanguageIds;
    }

    public void setTermEntryCount(long termEntryCount) {
	_termEntryCount = termEntryCount;
    }

    private long calculateCounts(long currentCount, long infoCount) {
	long count = currentCount + infoCount;
	return count >= 0 ? count : 0;
    }

    private void changeAssigneeStatus() {
	Set<SubmissionLanguage> submissionLanguages = getSubmissionLanguages();
	if (CollectionUtils.isNotEmpty(submissionLanguages)) {
	    Map<String, Set<SubmissionLanguage>> assigneeJobs = new HashMap<String, Set<SubmissionLanguage>>();

	    for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		String assignee = submissionLanguage.getAssignee();
		Set<SubmissionLanguage> jobs = assigneeJobs.get(assignee);
		if (jobs == null) {
		    jobs = new HashSet<SubmissionLanguage>();
		    assigneeJobs.put(assignee, jobs);
		}
		jobs.add(submissionLanguage);
	    }

	    ItemStatusType inTranslationReview = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;
	    ItemStatusType completed = ItemStatusTypeHolder.COMPLETED;
	    ItemStatusType cancelled = ItemStatusTypeHolder.CANCELLED;

	    Set<ItemStatusType> statuses = new HashSet<>();

	    for (Entry<String, Set<SubmissionLanguage>> entry : assigneeJobs.entrySet()) {

		for (SubmissionLanguage submissionLanguage : entry.getValue()) {
		    statuses.add(submissionLanguage.getStatusAssignee());
		}

		String assignee = entry.getKey();
		SubmissionUser submissionUser = getSubmissionUser(assignee);

		if (submissionUser != null) {
		    EntityStatusPriority statusPriority = submissionUser.getEntityStatusPriority();

		    if (statuses.contains(inTranslationReview)) {
			int high = Priority.HIGH.getValue();
			statusPriority.setStatus(inTranslationReview);
			statusPriority.setPriority(high);
			statusPriority.setPriorityAssignee(high);
		    } else if (statuses.contains(completed)) {
			int low = Priority.LOW.getValue();
			statusPriority.setStatus(completed);
			statusPriority.setPriority(low);
			statusPriority.setPriorityAssignee(low);
			statusPriority.setDateCompleted(new Date());
		    } else if (statuses.contains(cancelled)) {
			int lowest = Priority.LOWEST.getValue();
			statusPriority.setStatus(cancelled);
			statusPriority.setPriority(lowest);
			statusPriority.setPriorityAssignee(lowest);
			statusPriority.setDateCompleted(new Date());
		    }
		}

		statuses.clear();
	    }
	}
    }

    private void changeSubmitterStatus() {
	Set<SubmissionLanguage> submissionLanguages = getSubmissionLanguages();
	if (CollectionUtils.isNotEmpty(submissionLanguages)) {
	    Set<ItemStatusType> statuses = new HashSet<ItemStatusType>();
	    for (SubmissionLanguage submissionLanguage : submissionLanguages) {
		ItemStatusType status = submissionLanguage.getEntityStatusPriority().getStatus();
		statuses.add(status);
	    }

	    String submitter = getSubmitter();
	    SubmissionUser submissionUser = getSubmissionUser(submitter);
	    EntityStatusPriority userStatusPriority = submissionUser.getEntityStatusPriority();
	    EntityStatusPriority statusPriority = getEntityStatusPriority();

	    ItemStatusType inFinalReview = ItemStatusTypeHolder.IN_FINAL_REVIEW;
	    ItemStatusType inTranslationReview = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;
	    ItemStatusType completed = ItemStatusTypeHolder.COMPLETED;
	    ItemStatusType cancelled = ItemStatusTypeHolder.CANCELLED;

	    if (statuses.contains(inFinalReview)) {
		int high = Priority.HIGH.getValue();
		statusPriority.setStatus(inFinalReview);
		statusPriority.setPriority(high);
		userStatusPriority.setStatus(inFinalReview);
		userStatusPriority.setPriority(high);
	    } else if (statuses.contains(inTranslationReview)) {
		int normal = Priority.NORMAL.getValue();
		statusPriority.setStatus(inTranslationReview);
		statusPriority.setPriority(normal);
		userStatusPriority.setStatus(inTranslationReview);
		userStatusPriority.setPriority(normal);
		return;
	    } else if (statuses.contains(completed)) {
		int low = Priority.LOW.getValue();
		statusPriority.setStatus(completed);
		statusPriority.setPriority(low);
		userStatusPriority.setStatus(completed);
		userStatusPriority.setPriority(low);
	    } else if (statuses.contains(cancelled)) {
		int lowest = Priority.LOWEST.getValue();
		statusPriority.setStatus(cancelled);
		statusPriority.setPriority(lowest);
		userStatusPriority.setStatus(cancelled);
		userStatusPriority.setPriority(lowest);
	    }

	    userStatusPriority.setDateCompleted(new Date());
	    statusPriority.setDateCompleted(new Date());
	}
    }
}
