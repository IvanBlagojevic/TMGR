package org.gs4tr.termmanager.model;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.gs4tr.foundation.modules.entities.model.Identifiable;

@NamedQueries({ @NamedQuery(name = "SubmissionUser.findSubmissionsByUserId", query = "select submission "
	+ "from SubmissionUser subUser " + "inner join subUser.submission submission "
	+ "inner join submission.project project " + "where subUser.user.userProfileId = :userId "
	+ "and project.projectInfo.enabled is true"),

	@NamedQuery(name = "SubmissionUser.findBySubmissionAndUserId", query = "select count(subUser.submissionUserId) "
		+ "from SubmissionUser subUser " + "where subUser.user.userProfileId = :userId "
		+ "and subUser.submission.submissionId = :submissionId") })
@Entity
@Table(name = "SUBMISSION_USER")
public class SubmissionUser implements Identifiable<Long> {

    private static final long serialVersionUID = 6241705897784215274L;

    private EntityStatusPriority _entityStatusPriority;

    private Submission _submission;

    private Long _submissionUserId;

    private TmUserProfile _user;

    public SubmissionUser() {

    }

    public SubmissionUser(TmUserProfile user, Submission submission) {
	_user = user;
	_submission = submission;
	setEntityStatusPriority(new EntityStatusPriority(Priority.NORMAL.getValue(), Priority.NORMAL.getValue(),
		ItemStatusTypeHolder.IN_TRANSLATION_REVIEW));
    }

    @Embedded
    public EntityStatusPriority getEntityStatusPriority() {
	return _entityStatusPriority;
    }

    @Transient
    @Override
    public Long getIdentifier() {
	return getSubmissionUserId();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMISSION_ID", nullable = false, updatable = false)
    public Submission getSubmission() {
	return _submission;
    }

    @Id
    @Column(name = "SUBMISSION_USER_ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getSubmissionUserId() {
	return _submissionUserId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PROFILE_ID", nullable = false, updatable = false)
    public TmUserProfile getUser() {
	return _user;
    }

    public void setEntityStatusPriority(EntityStatusPriority entityStatusPriority) {
	_entityStatusPriority = entityStatusPriority;
    }

    @Override
    public void setIdentifier(Long identifier) {
	setSubmissionUserId(identifier);
    }

    public void setSubmission(Submission submission) {
	_submission = submission;
    }

    public void setSubmissionUserId(Long submissionUserId) {
	_submissionUserId = submissionUserId;
    }

    public void setUser(TmUserProfile user) {
	_user = user;
    }
}
