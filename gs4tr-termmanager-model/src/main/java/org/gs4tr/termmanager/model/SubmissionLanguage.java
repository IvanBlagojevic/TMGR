package org.gs4tr.termmanager.model;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
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

import org.gs4tr.foundation.modules.entities.model.Identifiable;
import org.gs4tr.foundation.modules.entities.model.types.ItemStatusType;
import org.gs4tr.termmanager.model.event.Observer;
import org.gs4tr.termmanager.model.event.SubmissionDetailInfo.SubmissionStateWrapper;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@NamedQueries({
	@NamedQuery(name = "SubmissionLanguage.findById", query = "select subLang " + "from SubmissionLanguage subLang "
		+ "where subLang.submissionLanguageId = :entityId"),

	@NamedQuery(name = "SubmissionLanguage.findByIds", query = "select subLang "
		+ "from SubmissionLanguage subLang " + "where subLang.submissionLanguageId in (:entityIds)"),

	@NamedQuery(name = "SubmissionLanguage.findBySubmissionId", query = "select subLang "
		+ "from SubmissionLanguage subLang " + "where subLang.submission.submissionId = :submissionId "
		+ "order by subLang.languageId asc"),

	@NamedQuery(name = "SubmissionLanguage.updateLanguageByProjectId", query = "update SubmissionLanguage sl "
		+ "set sl.languageId =:languageTo where sl.languageId =:languageFrom and sl.submission.submissionId "
		+ "in(select s.submissionId from Submission s where s.project.projectId =:projectId)") })
@Entity
@Table(name = "SUBMISSION_LANGUAGE")
public class SubmissionLanguage implements Observer, Identifiable<Long> {

    private static final long serialVersionUID = -8306830997058943475L;

    private String _assignee;

    private Date _dateModified;

    private Date _dateSubmitted;

    private EntityStatusPriority _entityStatusPriority;

    private String _languageId;

    private String _markerId;

    private ItemStatusType _statusAssignee;

    private Submission _submission;

    private Set<SubmissionLanguageComment> _submissionLanguageComments;

    private Long _submissionLanguageId;

    private long _termCanceledCount;

    private long _termCompletedCount;

    private long _termCount;

    private long _termInFinalReviewCount;

    private long _termInTranslationCount;

    public SubmissionLanguage() {
	_termInTranslationCount = 0;
	_termCanceledCount = 0;
	_termCompletedCount = 0;
	_termCount = 0;
	_termInFinalReviewCount = 0;
	_dateSubmitted = new Date();
	_dateModified = new Date();
	_statusAssignee = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;
	_markerId = UUID.randomUUID().toString();
	setEntityStatusPriority(new EntityStatusPriority(Priority.NORMAL.getValue(), Priority.NORMAL.getValue(),
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW));
    }

    public SubmissionLanguage(String languageId) {
	_termInTranslationCount = 0;
	_termCanceledCount = 0;
	_termCompletedCount = 0;
	_termCount = 0;
	_termInFinalReviewCount = 0;
	_dateSubmitted = new Date();
	_dateModified = new Date();
	_languageId = languageId;
	_statusAssignee = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;
	_markerId = UUID.randomUUID().toString();
	setEntityStatusPriority(new EntityStatusPriority(Priority.NORMAL.getValue(), Priority.NORMAL.getValue(),
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW));
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	SubmissionLanguage other = (SubmissionLanguage) obj;
	if (_assignee == null) {
	    if (other._assignee != null)
		return false;
	} else if (!_assignee.equals(other._assignee))
	    return false;
	if (_languageId == null) {
	    if (other._languageId != null)
		return false;
	} else if (!_languageId.equals(other._languageId))
	    return false;
	if (_markerId == null) {
	    return other._markerId == null;
	} else
	    return _markerId.equals(other._markerId);
    }

    @Column(name = "ASSIGNEE", nullable = false, updatable = false, length = 128)
    public String getAssignee() {
	return _assignee;
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
	return getSubmissionLanguageId();
    }

    @Column(name = "LANGUAGE_ID", length = 10, updatable = false, nullable = false)
    public String getLanguageId() {
	return _languageId;
    }

    @Column(name = "MARKER_ID", length = 40, nullable = false, updatable = false, unique = true)
    public String getMarkerId() {
	return _markerId;
    }

    @Column(name = "STATUS_ASSIGNEE", length = 30, nullable = false)
    @Type(type = "org.gs4tr.foundation.modules.dao.hibernate.usertype.ItemStatusTypeUserType", parameters = @Parameter(name = "typeInstanceClassName", value = "org.gs4tr.foundation.modules.entities.model.types.ItemStatusType"))
    public ItemStatusType getStatusAssignee() {
	return _statusAssignee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMISSION_ID", nullable = false)
    public Submission getSubmission() {
	return _submission;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "submissionLanguage", cascade = CascadeType.ALL)
    @BatchSize(size = 50)
    public Set<SubmissionLanguageComment> getSubmissionLanguageComments() {
	return _submissionLanguageComments;
    }

    @Id
    @Column(name = "SUBMISSION_LANGUAGE_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getSubmissionLanguageId() {
	return _submissionLanguageId;
    }

    @Column(name = "CANCELED_COUNT", nullable = false)
    public long getTermCanceledCount() {
	return _termCanceledCount;
    }

    @Column(name = "COMPLETED_COUNT", nullable = false)
    public long getTermCompletedCount() {
	return _termCompletedCount;
    }

    @Column(name = "TERM_COUNT", nullable = false)
    public long getTermCount() {
	return _termCount;
    }

    @Column(name = "IN_FINAL_REVIEW_COUNT", nullable = false)
    public long getTermInFinalReviewCount() {
	return _termInFinalReviewCount;
    }

    @Column(name = "IN_TRANSLATION_COUNT", nullable = false)
    public long getTermInTranslationCount() {
	return _termInTranslationCount;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_assignee == null) ? 0 : _assignee.hashCode());
	result = prime * result + ((_languageId == null) ? 0 : _languageId.hashCode());
	return prime * result + ((_markerId == null) ? 0 : _markerId.hashCode());
    }

    public void setAssignee(String assignee) {
	_assignee = assignee;
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
	setSubmissionLanguageId(identifier);
    }

    public void setLanguageId(String languageId) {
	_languageId = languageId;
    }

    public void setMarkerId(String markerId) {
	_markerId = markerId;
    }

    public void setStatusAssignee(ItemStatusType statusAssignee) {
	_statusAssignee = statusAssignee;
    }

    public void setSubmission(Submission submission) {
	_submission = submission;
    }

    public void setSubmissionLanguageComments(Set<SubmissionLanguageComment> submissionLanguageComments) {
	_submissionLanguageComments = submissionLanguageComments;
    }

    public void setSubmissionLanguageId(Long submissionLanguageId) {
	_submissionLanguageId = submissionLanguageId;
    }

    public void setTermCanceledCount(long termCanceledCount) {
	_termCanceledCount = termCanceledCount;
    }

    public void setTermCompletedCount(long termCompletedCount) {
	_termCompletedCount = termCompletedCount;
    }

    public void setTermCount(long termCount) {
	_termCount = termCount;
    }

    public void setTermInFinalReviewCount(long termInFinalReviewCount) {
	_termInFinalReviewCount = termInFinalReviewCount;
    }

    public void setTermInTranslationCount(long termInTranslationCount) {
	_termInTranslationCount = termInTranslationCount;
    }

    @Override
    public void update(Object o) {
	if (o instanceof SubmissionStateWrapper) {
	    SubmissionStateWrapper stateWrapper = (SubmissionStateWrapper) o;
	    changeTermCount(stateWrapper);
	    changeTermInTranslationCount(stateWrapper);
	    changeTermCanceledCount(stateWrapper);
	    changeTermCompletedCount(stateWrapper);
	    changeTermInFinalReviewCount(stateWrapper);
	    setDateModified(new Date());
	    changeStatus();
	}
    }

    private void changeStatus() {
	long termCount = getTermCount();
	long termCanceledCount = getTermCanceledCount();
	long termInFinalReviewCount = getTermInFinalReviewCount();
	long termInTranslationCount = getTermInTranslationCount();

	EntityStatusPriority statusPriority = getEntityStatusPriority();

	int lowest = Priority.LOWEST.getValue();
	int low = Priority.LOW.getValue();
	int normal = Priority.NORMAL.getValue();
	int high = Priority.HIGH.getValue();

	ItemStatusType cancelled = ItemStatusTypeHolder.CANCELLED;
	ItemStatusType completed = ItemStatusTypeHolder.COMPLETED;
	ItemStatusType inFinalReview = ItemStatusTypeHolder.IN_FINAL_REVIEW;
	ItemStatusType inTranslationReview = ItemStatusTypeHolder.IN_TRANSLATION_REVIEW;

	if (termCount == termCanceledCount) {
	    statusPriority.setStatus(cancelled);
	    setStatusAssignee(cancelled);
	    statusPriority.setPriority(lowest);
	    statusPriority.setPriorityAssignee(lowest);
	    statusPriority.setDateCompleted(new Date());
	} else if (termInTranslationCount == 0) {
	    setStatusAssignee(completed);
	    if (termInFinalReviewCount == 0) {
		statusPriority.setStatus(completed);
		statusPriority.setPriority(low);
		statusPriority.setPriorityAssignee(low);
	    } else {
		statusPriority.setStatus(inFinalReview);
		statusPriority.setPriority(high);
	    }
	    statusPriority.setDateCompleted(new Date());
	} else if (termInFinalReviewCount == termCount) {
	    statusPriority.setStatus(inFinalReview);
	    statusPriority.setPriority(high);
	} else if (termInTranslationCount > 0) {
	    setStatusAssignee(inTranslationReview);
	    statusPriority.setPriorityAssignee(high);
	    statusPriority.setStatus(inTranslationReview);
	    statusPriority.setPriority(normal);
	}
    }

    private void changeTermCanceledCount(SubmissionStateWrapper state) {
	long totalCount = getTermCount();
	int canceledCount = state.getCanceledCount().intValue();
	long count = getTermCanceledCount() + canceledCount;
	long finalCount = count >= 0 ? count : 0;
	finalCount = finalCount > totalCount ? totalCount : finalCount;
	setTermCanceledCount(finalCount);
    }

    private void changeTermCompletedCount(SubmissionStateWrapper state) {
	long totalCount = getTermCount();
	int completedCount = state.getCompletedCount().intValue();
	long count = getTermCompletedCount() + completedCount;
	long finalCount = count >= 0 ? count : 0;
	finalCount = finalCount > totalCount ? totalCount : finalCount;
	setTermCompletedCount(finalCount);
    }

    private void changeTermCount(SubmissionStateWrapper state) {
	long count = getTermCount() + state.getTotalCount().intValue();
	setTermCount(count);
    }

    private void changeTermInFinalReviewCount(SubmissionStateWrapper state) {
	long totalCount = getTermCount();
	int inFinalReviewCount = state.getInFinalReviewCount().intValue();
	long count = getTermInFinalReviewCount() + inFinalReviewCount;
	long finalCount = count >= 0 ? count : 0;
	finalCount = finalCount > totalCount ? totalCount : finalCount;
	setTermInFinalReviewCount(finalCount);
    }

    private void changeTermInTranslationCount(SubmissionStateWrapper state) {
	long totalCount = getTermCount();
	int inTranslationCount = state.getInTranslationCount().intValue();
	long count = getTermInTranslationCount() + inTranslationCount;
	long finalCount = count >= 0 ? count : 0;
	finalCount = finalCount > totalCount ? totalCount : finalCount;
	setTermInTranslationCount(finalCount);
    }
}
